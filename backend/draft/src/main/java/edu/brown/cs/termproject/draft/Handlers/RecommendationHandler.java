package edu.brown.cs.termproject.draft.Handlers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.termproject.draft.PaletteCreator;
import edu.brown.cs.termproject.draft.Piece;
import edu.brown.cs.termproject.draft.RecommendationCreator;
import edu.brown.cs.termproject.draft.API.eBayFetcher;
import edu.brown.cs.termproject.draft.Utilities.Storage.StorageInterface;
import spark.*;

public class RecommendationHandler implements Route {

    private final StorageInterface storage;
    private static final Gson GSON = new Gson();

    public RecommendationHandler(StorageInterface storage) {
        this.storage = storage;
    }

    @Override
    public Object handle(Request request, Response response) {
        String uid = request.queryParams("uid");
        if (uid == null) {
            response.status(400);
            return GSON.toJson(Map.of("error", "Missing user ID"));
        }

        System.out.println("Received uid: " + uid); // Log the uid

        // if it's a new user, create their database collection
        if (!storage.userExists(uid)) {
            try {
                storage.createUser(uid);
            } catch (Exception e) {
                System.out.println("Error creating the user in Firebase: " + e.getMessage());
            }
        }

        try {
            // get user data
            List<Piece> savedPieces = storage.getSavedPieces(uid);
            List<Piece> clickedPieces = storage.getClickedPieces(uid);
            List<Piece> onboardingPieces = storage.getOnboardingResponses(uid);
            eBayFetcher fetcher = new eBayFetcher();

            System.out.println("Saved pieces: " + savedPieces);
            System.out.println("Clicked pieces: " + clickedPieces);
            System.out.println("Onboarding pieces: " + onboardingPieces);

            // extract onboarding keywords
            List<String> onboardingKeywords = new ArrayList<>();
            for (Piece p : onboardingPieces) {
                onboardingKeywords.addAll(p.getTags());
            }
            System.out.println("Onboarding keywords: " + onboardingKeywords);

            // build the user palette
            Map<String, Double> palette = PaletteCreator.createPalette(savedPieces, onboardingKeywords, clickedPieces);
            System.out.println("Generated palette: " + palette);

            // compile a search query from top-weighted palette tags
            String searchQuery = palette.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(5) // get top 5 tags
                    .map(Map.Entry::getKey)
                    .collect(Collectors.joining(" "));
            String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
            System.out.println("eBay search query: " + encodedQuery);

            // fetch results from eBay API (wip)
            JsonObject ebayResponse = fetcher.searchEbay(searchQuery);
            List<Piece> ebayPieces = eBayFetcher.parseEbayResults(ebayResponse);
            System.out.println("eBay Pieces retrieved: " + ebayPieces.size());

            // avoid recommending things the user already saved
            Set<String> alreadySavedIds = new HashSet<>();
            for (Piece p : savedPieces) {
                alreadySavedIds.add(p.getId());
            }
            System.out.println("Already saved IDs: " + alreadySavedIds);

            // get top recommendations
            List<Piece> recs = RecommendationCreator.recommendPieces(ebayPieces, palette, alreadySavedIds, 12);
            // System.out.println("Top recommendations: " + recs);

            // send out the response
            String jsonResponse = GSON.toJson(Map.of("recommendations", recs));
            // System.out.println("JSON Response: " + jsonResponse);

            response.status(200);
            return jsonResponse;

        } catch (Exception e) {
            // log the error and send a 500 error response
            e.printStackTrace();
            System.err.println("Error occurred during recommendation generation: " + e.getMessage());
            response.status(500);
            return GSON.toJson(Map.of("error", "[RecommendationHandler] Server error"));
        }
    }

}