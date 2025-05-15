package edu.brown.cs.termproject.draft.Handlers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import edu.brown.cs.termproject.draft.PaletteCreator;
import edu.brown.cs.termproject.draft.Piece;
import edu.brown.cs.termproject.draft.RecommendationCreator;
import edu.brown.cs.termproject.draft.Exceptions.RecommenderException;
import edu.brown.cs.termproject.draft.Utilities.APIUtilities;
import edu.brown.cs.termproject.draft.Utilities.Storage.StorageInterface;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Spark Java route handler responsible for generating recommendations in
 * response to HTTP requests.
 * 
 */
public class RecommendationHandler implements Route {

    private final StorageInterface storage;
    private static final Gson GSON = new Gson();
    private static final int DEFAULT_RECOMMENDATION_LIMIT = 50;
    private static final String FALLBACK_SEARCH_TERM = "vintage clothing";
    private static final int MAX_TAGS_TO_USE = 5;

    public RecommendationHandler(StorageInterface storage) {
        this.storage = storage;
    }

    /**
     * Handles incoming recommendation requests. Processes the user ID, fetches relevant data, 
     * builds a preference palette, and returns recommendations as JSON.
     */
    @Override
    public Object handle(Request request, Response response) {
        response.type("application/json");

        try {
            String uid = request.queryParams("uid");
            if (uid == null || uid.trim().isEmpty()) {
                response.status(400);
                return GSON.toJson(Map.of("error", "Missing or invalid uid"));
            }

            // Get user's draft pieces to analyze their tags
            List<String> searchTerms = generateSearchTerms(uid);
            System.out.println("Generated search terms: " + String.join(", ", searchTerms));

            List<Piece> allAvailablePieces = new ArrayList<>();

            // Fetch pieces for each search term
            for (String searchTerm : searchTerms) {
                try {
                    String[] styleKeywords = { "vintage", "aesthetic", "rare", "designer"};
                    String enrichedTerm = searchTerm + " " + String.join(" ", styleKeywords);
                    String encodedQuery = URLEncoder.encode(enrichedTerm, StandardCharsets.UTF_8);                    
                    List<Piece> ebayPieces = APIUtilities.fetchFromEbay(encodedQuery);
                    if (ebayPieces != null && !ebayPieces.isEmpty()) {
                        allAvailablePieces.addAll(ebayPieces);
                        System.out.println(
                            "Fetched " + ebayPieces.size() + " pieces for term: " + searchTerm);
                    }
                } catch (Exception e) {
                    System.err.println(
                        "eBay fetch failed for term '" + searchTerm + "': " + e.getMessage());
                }
            }

            // add global pieces for contingency if ebay call doesn't work
            allAvailablePieces.addAll(storage.getGlobalPieces());

            // Remove duplicates
            allAvailablePieces = new ArrayList<>(new LinkedHashSet<>(allAvailablePieces));

            System.out.println(
                "Total available pieces before filtering: " + allAvailablePieces.size());

            // Get saved pieces and create excluded IDs set
            List<Piece> savedPieces = storage.getSavedPieces(uid);
            List<Piece> clickedPieces = storage.getClickedPieces(uid);

            Set<String> excludedIds = new HashSet<>();
            excludedIds.addAll(savedPieces.stream()
                    .map(Piece::getId)
                    .collect(Collectors.toSet()));
            excludedIds.addAll(clickedPieces.stream()
                    .map(Piece::getId)
                    .collect(Collectors.toSet()));

            System.out.println("Clicked pieces count: " + clickedPieces.size());
            for (Piece p : clickedPieces) {
                System.out.println("Clicked piece ID: " + p.getId());
            }

            // Filter out saved pieces and pieces used in drafts
            List<Piece> availablePieces = allAvailablePieces.stream()
                .filter(piece -> !excludedIds.contains(piece.getId()) &&
                    (piece.getUsedInDrafts() == null || piece.getUsedInDrafts().isEmpty()))
                .collect(Collectors.toList());

            Collections.shuffle(availablePieces);

            System.out.println("Available pieces after filtering: " + availablePieces.size());

            List<Piece> onboardingPieces = storage.getOnboardingResponses(uid);

            List<String> onboardingKeywords = new ArrayList<>();

            for (Piece p : onboardingPieces) {
                onboardingKeywords.addAll(p.getTags());
            }

            System.out.println("Onboarding keywords: " + onboardingKeywords);

            // build user's palette
            Map<String, Double> palette = PaletteCreator.createPalette(savedPieces,
            onboardingKeywords, clickedPieces);
            System.out.println("Generated palette: " + palette);

            List<Piece> recommendations = RecommendationCreator.recommendPieces(
                availablePieces,
                palette,
                excludedIds,
                DEFAULT_RECOMMENDATION_LIMIT
            );

            return GSON.toJson(Map.of(
                "status", "success",
                "recommendations", recommendations,
                "debugInfo", Map.of(
                    "totalPieces", allAvailablePieces.size(),
                    "availableAfterFiltering", availablePieces.size(),
                    "recommendationCount", recommendations.size(),
                    "searchTermsUsed", searchTerms
                )
            ));

        } catch (Exception e) {
            e.printStackTrace();
            response.status(500);
            return GSON.toJson(Map.of("error", "Recommendation failed: " + e.getMessage()));
        }
    }

    /**
     * Generates a list of search terms derived from the user's saved drafts for
     * fetching relevant fashion pieces.
     * 
     * 
     * @param uid is the user's ID as a string
     * @return a list of keyword strings to use in search queries.
     * 
     * 
     * @throws RecommenderException
     */
    private List<String> generateSearchTerms(String uid) throws RecommenderException {
        Set<String> uniqueTags = new HashSet<>();
        List<String> searchTerms = new ArrayList<>();

        // Get user's drafts and their pieces
        List<Piece> draftPieces = new ArrayList<>();
        try{
        draftPieces = storage.getSavedPieces(uid);
        } catch (Exception e){
            throw new RecommenderException("[Recommendation Handler]" + e.getMessage());
        }

        // Collect tags from draft pieces
        for (Piece piece : draftPieces) {
            if (piece.getTags() != null) {
                uniqueTags.addAll(piece.getTags());
            }
        }

        // Remove common words and clean up tags
        uniqueTags.removeIf(tag -> tag == null || tag.trim().isEmpty()
            || tag.equalsIgnoreCase("clothing")
            || tag.equalsIgnoreCase("fashion")
            || tag.equalsIgnoreCase("men")
            || tag.equalsIgnoreCase("women")
            || tag.equalsIgnoreCase("men's")
            || tag.equalsIgnoreCase("women's")
            || tag.equalsIgnoreCase("shoes & accessories")
            || tag.equalsIgnoreCase("accessories"));

        // Convert unique tags to search terms
        List<String> tagList = new ArrayList<>(uniqueTags);
        Collections.shuffle(tagList); // Randomize tag selection

        // Use up to MAX_TAGS_TO_USE tags
        for (int i = 0; i < Math.min(MAX_TAGS_TO_USE, tagList.size()); i++) {
            searchTerms.add(tagList.get(i));
        }

        // If no tags were found or too few, add fallback terms
        if (searchTerms.isEmpty()) {
            searchTerms.add(FALLBACK_SEARCH_TERM);
            searchTerms.add("fashion");
            searchTerms.add("vintage");
        }

        return searchTerms;
    }




}