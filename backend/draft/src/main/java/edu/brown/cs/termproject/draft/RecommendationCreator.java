package edu.brown.cs.termproject.draft;

import java.util.*;
import java.util.stream.Collectors;

import edu.brown.cs.termproject.draft.Exceptions.DraftException;
import edu.brown.cs.termproject.draft.Exceptions.PaletteException;

public class RecommendationCreator {

    // TODO: need to track the clicking of pieces on the frontend
    // TODO: maybe add weight if a piece matches recently searched key terms
    // TODO: need to write test class for recommendation interaction w/ all functionality: palette, clicks, saved pieces, etc

//    /**
//     * Scores and ranks all pieces based on match with palette
//     *
//     * @param allPieces is all available pieces
//     * @param palette is the user's weighted preference map
//     * @param alreadySavedIds is a set of item IDs the user already saved (exclude from
//     *                        results)
//     * @param limit is the number of top recommendations to return
//     * @return a ranked list of recommended pieces
//     * @throws DraftException if any input is invalid
//     */

    private static double computeScore(Piece piece, Map<String, Double> palette) {
        if (piece == null || piece.getTags() == null || palette == null) {
            return 0.0;
        }

        double score = 0.0;
        List<String> pieceTags = piece.getTags();
        Set<String> matchedCategories = new HashSet<>();

        // Score based on tag matches with palette
        for (String tag : pieceTags) {
            if (palette.containsKey(tag.toLowerCase())) {
                score += palette.get(tag.toLowerCase());

                // Track matched category
                String category = getCategoryFromTags(List.of(tag));
                if (!category.equals("other")) {
                    matchedCategories.add(category);
                }
            }
        }

        // Boost score for items matching multiple categories
        if (matchedCategories.size() > 1) {
            score *= 1.2; // 20% boost for versatile items
        }

        // Consider title keywords as additional signals
        String[] titleWords = piece.getTitle().toLowerCase().split("\\s+");
        for (String word : titleWords) {
            if (palette.containsKey(word)) {
                score += palette.get(word) * 0.5; // Half weight for title matches
            }
        }

        return score;
    }

    public static List<Piece> recommendPieces(
        List<Piece> allPieces,
        Map<String, Double> palette,
        Set<String> alreadySavedIds,
        int limit
    ) throws DraftException {
        if (allPieces == null || palette == null || alreadySavedIds == null) {
            throw new DraftException("Inputs to recommendation engine cannot be null.");
        }

        // Double-check filtering of saved pieces
        List<Piece> availablePieces = allPieces.stream()
            .filter(p -> p != null && !alreadySavedIds.contains(p.getId()))
            .collect(Collectors.toList());

        // Group filtered pieces by category
        Map<String, List<Piece>> categorizedPieces = availablePieces.stream()
            .collect(Collectors.groupingBy(p -> getCategoryFromTags(p.getTags())));

        // Score pieces within each category
        Map<Piece, Double> scores = new HashMap<>();
        categorizedPieces.forEach((category, pieces) -> {
            pieces.forEach(piece -> {
                double score = computeScore(piece, palette);
                scores.put(piece, score);
            });
        });

        // Get top pieces from each category to ensure variety
        List<Piece> recommendations = new ArrayList<>();
        int piecesPerCategory = limit / categorizedPieces.size();

        categorizedPieces.forEach((category, pieces) -> {
            pieces.stream()
                .sorted((p1, p2) -> Double.compare(scores.get(p2), scores.get(p1)))
                .limit(piecesPerCategory)
                .forEach(recommendations::add);
        });

        // Fill remaining slots with highest scoring pieces overall
        while (recommendations.size() < limit) {
            Optional<Piece> nextBest = scores.entrySet().stream()
                .filter(e -> !recommendations.contains(e.getKey()))
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);

            if (nextBest.isPresent()) {
                recommendations.add(nextBest.get());
            } else {
                break;
            }
        }

        return recommendations;
    }

    private static String getCategoryFromTags(List<String> tags) {
        // Define specific category mappings
        Map<String, List<String>> categoryMappings = Map.of(
            "dress", List.of("dress", "gown", "midi-dress", "maxi-dress", "mini-dress", "sundress"),
            "top", List.of("blouse", "shirt", "t-shirt", "tank-top", "sweater", "cardigan", "crop-top", "turtleneck", "polo"),
            "bottom", List.of("pants", "jeans", "shorts", "skirt", "trousers", "leggings", "joggers", "culottes"),
            "outerwear", List.of("jacket", "coat", "blazer", "hoodie", "vest", "windbreaker", "parka", "raincoat"),
            "shoes", List.of("sneakers", "boots", "sandals", "heels", "flats", "loafers", "pumps", "oxfords"),
            "accessories", List.of("bag", "purse", "scarf", "hat", "belt", "jewelry", "necklace", "earrings", "bracelet", "watch")
        );

        // Check tags against specific subcategories and return the main category
        return tags.stream()
            .flatMap(tag -> categoryMappings.entrySet().stream()
                .filter(entry -> entry.getValue().contains(tag.toLowerCase()))
                .map(Map.Entry::getKey))
            .findFirst()
            .orElse("other");
    }
//    public static List<Piece> recommendPieces(List<Piece> allPieces, Map<String, Double> palette,
//            Set<String> alreadySavedIds, int limit) throws DraftException {
//        if (allPieces == null || palette == null || alreadySavedIds == null) {
//            throw new DraftException("Inputs to recommendation engine cannot be null.");
//        }
//
//        Map<Piece, Double> scores = new HashMap<>();
//
//        // for every saved piece
//        for (Piece piece : allPieces) {
//            // if the piece's recommendation score hasn't already been computed
//            if (piece == null || alreadySavedIds.contains(piece.getId()))
//                continue;
//
//            // calculate its score and store it
//            double score = computeScore(piece, palette);
//            scores.put(piece, score);
//        }
//
//        // sort this by score (descending)
//        List<Piece> sortedByScore = scores.entrySet().stream()
//                .sorted(Map.Entry.<Piece, Double>comparingByValue().reversed()) // ensures descending order
//                .map(Map.Entry::getKey) // discard the scores and only keep the pieces from the map
//                .collect(Collectors.toList()); // add all of the pieces together into a list
//
//        // initialize the list of final recommendations
//        List<Piece> finalRecommendations = new ArrayList<>();
//        Set<String> includedIds = new HashSet<>();
//
//        // add top scoring pieces first (up to the limit)
//        for (Piece p : sortedByScore) {
//            if (finalRecommendations.size() >= limit)
//                break;
//            finalRecommendations.add(p);
//            includedIds.add(p.getId());
//        }
//
//        // if we still have fewer than `limit` items, fill in with random unseen pieces
//        if (finalRecommendations.size() < limit) {
//            List<Piece> shuffled = new ArrayList<>(allPieces);
//            Collections.shuffle(shuffled); // shuffle to add filler items randomly
//
//            for (Piece piece : shuffled) {
//                if (finalRecommendations.size() >= limit)
//                    break;
//
//                // only add items that haven't already been seen or saved
//                if (piece != null && !alreadySavedIds.contains(piece.getId()) && !includedIds.contains(piece.getId())) {
//                    finalRecommendations.add(piece);
//                    includedIds.add(piece.getId());
//                }
//            }
//        }
//
//        return finalRecommendations;
//    }
//
//
//    /**
//     * Computes match score between a piece and the user palette.
//     *
//     * @param piece is the secondhand item to evaluate
//     * @param palette is a keyword to weight map
//     * @return a cumulative score based on tag matches
//     */
//    private static double computeScore(Piece piece, Map<String, Double> palette) {
//        if (piece == null || palette == null || piece.getTags() == null)
//            return 0.0;
//
//        double score = 0.0;
//        for (String tag : piece.getTags()) {
//            if (tag != null && palette.containsKey(tag)) {
//                score += palette.getOrDefault(tag, 0.0);
//            }
//        }
//        return score;
//    }
//
//    /**
//     * Recommends based only on the current draft’s theme.
//     *
//     * @param draftPieces are the draft's pieces
//     * @param allPieces is the full inventory to recommend from
//     * @param alreadySavedIds user's previously saved item IDs
//     * @param limit is the number of recommendations to return
//     * @return recommendations tailored to the theme of the draft
//     * @throws DraftException if draft data is invalid
//     * @throws PaletteException if palette data is invalid
//     */
//    public static List<Piece> recommendForDraft(List<Piece> draftPieces, List<Piece> allPieces,
//            Set<String> alreadySavedIds, int limit) throws DraftException, PaletteException {
//        if (draftPieces == null || draftPieces.isEmpty()) {
//            throw new DraftException("Draft pieces must not be null or empty.");
//        }
//
//        // create a palette only using this draft's content
//        Map<String, Double> draftPalette = PaletteCreator.createPalette(draftPieces, List.of(), List.of());
//
//        // return the calculated recommended secondhand items
//        return recommendPieces(allPieces, draftPalette, alreadySavedIds, limit);
//    }
}
