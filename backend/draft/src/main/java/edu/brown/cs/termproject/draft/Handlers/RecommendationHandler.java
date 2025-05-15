package edu.brown.cs.termproject.draft.Handlers;

import com.google.gson.Gson;
import edu.brown.cs.termproject.draft.Piece;
import edu.brown.cs.termproject.draft.RecommendationCreator;
import edu.brown.cs.termproject.draft.PaletteCreator;
import edu.brown.cs.termproject.draft.Utilities.APIUtilities;
import edu.brown.cs.termproject.draft.Utilities.Storage.StorageInterface;
import spark.Request;
import spark.Response;
import spark.Route;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class RecommendationHandler implements Route {

    private final StorageInterface storage;
    private static final Gson GSON = new Gson();
    private static final int DEFAULT_RECOMMENDATION_LIMIT = 50;
    private static final String FALLBACK_SEARCH_TERM = "vintage clothing";
    private static final int MAX_TAGS_TO_USE = 5;

    public RecommendationHandler(StorageInterface storage) {
        this.storage = storage;
    }

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
                    String encodedTerm = URLEncoder.encode(searchTerm, StandardCharsets.UTF_8);
                    List<Piece> ebayPieces = APIUtilities.fetchFromEbay(encodedTerm);
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
            Set<String> excludedIds = savedPieces.stream()
                .map(Piece::getId)
                .collect(Collectors.toSet());

            // Filter out saved pieces and pieces used in drafts
            List<Piece> availablePieces = allAvailablePieces.stream()
                .filter(piece -> !excludedIds.contains(piece.getId()) &&
                    (piece.getUsedInDrafts() == null || piece.getUsedInDrafts().isEmpty()))
                .collect(Collectors.toList());

            Collections.shuffle(availablePieces);

            System.out.println("Available pieces after filtering: " + availablePieces.size());

           List<Piece> clickedPieces = storage.getClickedPieces(uid);
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

    private List<String> generateSearchTerms(String uid) throws Exception {
        Set<String> uniqueTags = new HashSet<>();
        List<String> searchTerms = new ArrayList<>();

        // Get user's drafts and their pieces
        List<Piece> draftPieces = storage.getSavedPieces(uid);

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





//    @Override
//    public Object handle(Request request, Response response) throws ProtocolException {
//        response.type("application/json");
//
//        try {
//            String uid = request.queryParams("uid");
//            System.out.println("Processing recommendations for uid: " + uid);
//
//            // Get eBay pieces first
//            List<Piece> ebayPieces = APIUtilities.fetchFromEbay("vintage fashion");
//            System.out.println("Fetched eBay pieces: " + ebayPieces.size());
//
//            // Get global pieces and combine with eBay results
//            List<Piece> allAvailablePieces = new ArrayList<>();
//            allAvailablePieces.addAll(storage.getGlobalPieces());
//            allAvailablePieces.addAll(ebayPieces);
//
//            System.out.println("Total available pieces before filtering: " + allAvailablePieces.size());
//
//            // Filter out saved pieces
//            List<Piece> availablePieces = allAvailablePieces.stream()
//                .filter(piece -> !excludedIds.contains(piece.getId()))
//                .collect(Collectors.toList());
//
//            System.out.println("Available pieces after filtering: " + availablePieces.size());
//
//            // Get recommendations with increased limit
//            List<Piece> recommendations = RecommendationCreator.recommendPieces(
//                availablePieces,
//                palette,
//                excludedIds,
//                100  // Increased limit
//            );
//
//            return GSON.toJson(Map.of(
//                "status", "success",
//                "recommendations", recommendations,
//                "debugInfo", Map.of(
//                    "totalPieces", allAvailablePieces.size(),
//                    "availableAfterFiltering", availablePieces.size(),
//                    "recommendationCount", recommendations.size()
//                )
//            ));
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.status(500);
//            return GSON.toJson(Map.of("error", "Recommendation failed: " + e.getMessage()));
//        }
//    }
//    @Override
//    public Object handle(Request request, Response response) throws ProtocolException {
//        response.type("application/json");
//
//        try {
//            // 1. Get and validate parameters
//            String uid = request.queryParams("uid");
//            if (uid == null || uid.trim().isEmpty()) {
//                response.status(400);
//                return GSON.toJson(Map.of("error", "Missing or invalid uid"));
//            }
//
//            // 2. Parse request body
//            JsonObject body = new JsonObject();
//            String requestBody = request.body();
//            if (requestBody != null && !requestBody.trim().isEmpty()) {
//                try {
//                    body = GSON.fromJson(requestBody, JsonObject.class);
//                } catch (Exception e) {
//                    response.status(400);
//                    return GSON.toJson(Map.of("error", "Invalid request body"));
//                }
//            }
//
//
//            // 3. Initialize user if needed
//            if (!storage.userExists(uid)) {
//                storage.createUser(uid);
//            }
//
//            // 4. Get user's pieces and data
//            List<Piece> savedPieces = storage.getSavedPieces(uid);
//            List<Piece> clickedPieces = storage.getClickedPieces(uid);
//            List<Piece> onboardingPieces = storage.getOnboardingResponses(uid);
//            List<Piece> globalPieces = storage.getGlobalPieces();
//
//            // 5. Create palette for recommendations
//            Map<String, Double> palette = PaletteCreator.createPalette(
//                savedPieces,
//                extractOnboardingKeywords(onboardingPieces),
//                clickedPieces
//            );
//
//            // 6. Get recommendations
//            List<Piece> recommendations;
//            if (body.has("draftId")) {
//                // Get draft-specific recommendations
//                String draftId = body.get("draftId").getAsString();
//                List<Piece> draftPieces = getDraftPieces(uid, draftId);
//                recommendations = RecommendationCreator.recommendForDraft(
//                    draftPieces,
//                    globalPieces,
//                    getSavedPieceIds(savedPieces),
//                    36 // limit to 33 recommendations
//                );
//            } else {
//                // Get general recommendations based on palette
//                recommendations = RecommendationCreator.recommendPieces(
//                    globalPieces,
//                    palette,
//                    getSavedPieceIds(savedPieces),
//                    36 // limit to top 20 recommendations
//                );
//            }
//
//            // 7. Build response
//            Map<String, Object> responseMap = new HashMap<>();
//            responseMap.put("status", "success");
//            responseMap.put("recommendations", recommendations);
//
//            if (body.has("draftId")) {
//                List<Map<String, Object>> draftData = storage.getCollection(
//                    uid,
//                    "drafts/" + body.get("draftId").getAsString()
//                );
//                responseMap.put("draftData", draftData);
//            }
//
//            return GSON.toJson(responseMap);
//
//        } catch (Exception e) {
//            response.status(500);
//            return GSON.toJson(Map.of(
//                "error", "Internal server error",
//                "message", e.getMessage()
//            ));
//        }
//    }




    // Helper method to extract onboarding keywords from pieces
    private List<String> extractOnboardingKeywords(List<Piece> onboardingPieces) {
        List<String> keywords = new ArrayList<>();
        if (onboardingPieces != null) {
            for (Piece piece : onboardingPieces) {
                if (piece.getTags() != null) {
                    keywords.addAll(piece.getTags());
                }
            }
        }
        return keywords;
    }

    // Helper method to get set of saved piece IDs
    private Set<String> getSavedPieceIds(List<Piece> savedPieces) {
        return savedPieces.stream()
            .map(Piece::getId)
            .collect(Collectors.toSet());
    }

    // Helper method to get pieces from a specific draft
    private List<Piece> getDraftPieces(String uid, String draftId) throws Exception {
        List<Map<String, Object>> draftCollection = storage.getCollection(uid, "drafts/" + draftId);
        // Convert collection data to pieces
        return draftCollection.stream()
            .map(this::convertMapToPiece)
            .collect(Collectors.toList());
    }

    // Helper method to convert map to Piece object
    private Piece convertMapToPiece(Map<String, Object> map) {
        return Piece.fromMap(map);

    }}
//    @Override
//    public Object handle(Request request, Response response) throws ProtocolException {
//        String query = request.queryParams("query");
//        if (query == null || query.trim().isEmpty()) {
//            return GSON.toJson(Map.of("error", "Query parameter is required"));
//        }
//
//        List<Piece> ebayPieces = APIUtilities.fetchFromEbay(query);
//        // Process the pieces for recommendations as needed
//
//        return GSON.toJson(Map.of("recommendations", ebayPieces));
//
//
//        String uid = request.queryParams("uid");
//        if (uid == null) {
//            response.status(400);
//            return GSON.toJson(Map.of("error", "Missing user ID"));
//        }
//
//        System.out.println("Received uid: " + uid); // Log the uid
//
//        // if it's a new user, create their database collection
//        if (!storage.userExists(uid)) {
//            try {
//                storage.createUser(uid);
//            } catch (Exception e) {
//                System.out.println("Error creating the user in Firebase: " + e.getMessage());
//            }
//        }
//
//        try {
//            // get user data
//            List<Piece> savedPieces = storage.getSavedPieces(uid);
//            List<Piece> clickedPieces = storage.getClickedPieces(uid);
//            List<Piece> onboardingPieces = storage.getOnboardingResponses(uid);
//            eBayFetcher fetcher = new eBayFetcher();
//
//            System.out.println("Saved pieces: " + savedPieces);
//            System.out.println("Clicked pieces: " + clickedPieces);
//            System.out.println("Onboarding pieces: " + onboardingPieces);
//
//            // extract onboarding keywords
//            List<String> onboardingKeywords = new ArrayList<>();
//            for (Piece p : onboardingPieces) {
//                onboardingKeywords.addAll(p.getTags());
//            }
//            System.out.println("Onboarding keywords: " + onboardingKeywords);
//
//            // build the user palette
//            Map<String, Double> palette = PaletteCreator.createPalette(savedPieces, onboardingKeywords, clickedPieces);
//            System.out.println("Generated palette: " + palette);
//
//            // compile a search query from top-weighted palette tags
//            String searchQuery = palette.entrySet().stream()
//                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
//                    .limit(5) // get top 5 tags
//                    .map(Map.Entry::getKey)
//                    .collect(Collectors.joining(" "));
//            String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
//            System.out.println("eBay search query: " + encodedQuery);
//
//            // fetch results from eBay API (wip)
//            JsonObject ebayResponse = fetcher.searchEbay(searchQuery);
////            List<Piece> ebayPieces = eBayFetcher.parseEbayResults(ebayResponse);
////            System.out.println("eBay Pieces retrieved: " + ebayPieces.size());
//
//            // avoid recommending things the user already saved
//            Set<String> alreadySavedIds = new HashSet<>();
//            for (Piece p : savedPieces) {
//                alreadySavedIds.add(p.getId());
//            }
//            System.out.println("Already saved IDs: " + alreadySavedIds);
//
//            // get top recommendations
//            List<Piece> recs = RecommendationCreator.recommendPieces(ebayPieces, palette, alreadySavedIds, 12);
//            // System.out.println("Top recommendations: " + recs);
//
//            // send out the response
//            String jsonResponse = GSON.toJson(Map.of("recommendations", recs));
//            // System.out.println("JSON Response: " + jsonResponse);
//
//            response.status(200);
//            return jsonResponse;
//
//        } catch (Exception e) {
//            // log the error and send a 500 error response
//            e.printStackTrace();
//            System.err.println("Error occurred during recommendation generation: " + e.getMessage());
//            response.status(500);
//            return GSON.toJson(Map.of("error", "[RecommendationHandler] Server error"));
//        }


