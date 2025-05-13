package edu.brown.cs.termproject.draft.Handlers;

import java.util.*;

import com.google.gson.Gson;

import edu.brown.cs.termproject.draft.PaletteCreator;
import edu.brown.cs.termproject.draft.Piece;
import edu.brown.cs.termproject.draft.RecommendationCreator;
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

        if(!storage.userExists(uid)){
            try {
            storage.createUser(uid);
            } catch (Exception e){
                System.out.println("Error creating the user in Firebase: " + e.getMessage());
            }
        }

        try {
            // Get user data
            List<Piece> savedPieces = storage.getSavedPieces(uid);
            List<Piece> clickedPieces = storage.getClickedPieces(uid);
            List<Piece> onboardingPieces = storage.getOnboardingResponses(uid);
            List<Piece> allPieces = storage.getAllPieces();

            System.out.println("Saved pieces: " + savedPieces);
            System.out.println("Clicked pieces: " + clickedPieces);
            System.out.println("Onboarding pieces: " + onboardingPieces);

            // Extract onboarding keywords
            List<String> onboardingKeywords = new ArrayList<>();
            for (Piece p : onboardingPieces) {
                onboardingKeywords.addAll(p.getTags());
            }
            System.out.println("Onboarding keywords: " + onboardingKeywords);

            // Build the palette
            Map<String, Double> palette = PaletteCreator.createPalette(savedPieces, onboardingKeywords, clickedPieces);
            System.out.println("Generated palette: " + palette);

            // Avoid recommending things the user already saved
            Set<String> alreadySavedIds = new HashSet<>();
            for (Piece p : savedPieces) {
                alreadySavedIds.add(p.getId());
            }
            System.out.println("Already saved IDs: " + alreadySavedIds);

            // Get top recommendations
            List<Piece> recs = RecommendationCreator.recommendPieces(allPieces, palette, alreadySavedIds, 12);
            // System.out.println("Top recommendations: " + recs);

            // Send the response
            String jsonResponse = GSON.toJson(Map.of("recommendations", recs));
            // System.out.println("JSON Response: " + jsonResponse);

            response.status(200);
            return jsonResponse;

        } catch (Exception e) {
            // Log the error and send a 500 error response
            e.printStackTrace();
            System.err.println("Error occurred during recommendation generation: " + e.getMessage());
            response.status(500);
            return GSON.toJson(Map.of("error", "Internal server error"));
        }
    }

}