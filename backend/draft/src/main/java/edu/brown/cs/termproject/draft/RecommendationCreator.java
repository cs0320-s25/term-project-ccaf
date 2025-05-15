package edu.brown.cs.termproject.draft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import edu.brown.cs.termproject.draft.Exceptions.DraftException;

/**
 * Responsible for generating personalized fashion item recommendations 
 * based on a user's style preferences represented as a weighted palette of keywords.
 */
public class RecommendationCreator {

   /**
    * Calculates a numerical relevance score for a given Piece based on how well
    * its tags and title keywords match the user's weighted preference palette. 
    * 
    * 
    *
    * @param palette         is the user's weighted preference map
    * @param alreadySavedIds is a set of item IDs the user already saved (exclude
    *                        from
    *                        results)
    * n
    * @return a ranked list of recommended pieces
    * @throws DraftException if any input is invalid
    */
    private static double computeScore(Piece piece, Map<String, Double> palette) {
        if (piece == null || piece.getTags() == null || palette == null) {
            return 0.0;
        }

        // Skip test/mock pieces
        if (piece.getTitle().toLowerCase().contains("test") ||
            piece.getSourceWebsite().toLowerCase().contains("test")) {
            return 0.0;
        }

        double score = 0.0;
        List<String> pieceTags = piece.getTags();
        Set<String> matchedCategories = new HashSet<>();

        // Score based on tag matches
        for (String tag : pieceTags) {
            if (palette.containsKey(tag.toLowerCase())) {
                score += palette.get(tag.toLowerCase());

                String category = getCategoryFromTags(List.of(tag));
                if (!category.equals("other")) {
                    matchedCategories.add(category);
                }
            }
        }

        // Apply bonuses for better recommendations
        if (matchedCategories.size() > 1) {
            score *= 1.3; // 30% boost for versatile items
        }

        // Consider title keywords
        String[] titleWords = piece.getTitle().toLowerCase().split("\\s+");
        for (String word : titleWords) {
            if (palette.containsKey(word)) {
                score += palette.get(word) * 0.5;
            }
        }

        return score;
    }

/**
 * Generates a ranked list of recommended pieces tailored to the user's
 * preferences while excluding items the user already saved.
 * 
 * 
 * @param allPieces       is a list of all available pieces to consider.
 * @param palette         is the user's weighted keyword preferences.
 * @param alreadySavedIds is a set of piece IDs already saved by the user, which
 *                        should be excluded.
 * 
 * 
 * @param limit           is the maximum number of recommended pieces to return.
 * @return
 * @throws DraftException if any inputs are null
 */
    public static List<Piece> recommendPieces(
        List<Piece> allPieces,
        Map<String, Double> palette,
        Set<String> alreadySavedIds,
        int limit) throws DraftException {
        
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
        Set<String> recommendedIds = new HashSet<>();
        int piecesPerCategory = limit / categorizedPieces.size();

        categorizedPieces.forEach((category, pieces) -> {
            pieces.stream()
                .sorted((p1, p2) -> Double.compare(scores.get(p2), scores.get(p1)))
                .filter(p -> recommendedIds.add(p.getId()))
                .limit(piecesPerCategory)
                .forEach(recommendations::add);
        });

        // Fill remaining slots with highest scoring pieces overall
        while (recommendations.size() < limit) {
            Optional<Piece> nextBest = scores.entrySet().stream()
                .filter(e -> recommendedIds.add(e.getKey().getId()))
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

    /**
     * Determines the main category for a piece based on its tags,
     * using a hard-coded map of categories to known tags.
     * 
     * 
     * @param tags list of tags assigned to a piece.
     * @return a string representing the category or "other" if no category matches
     */
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
}