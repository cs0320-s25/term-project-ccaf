package edu.brown.cs.termproject.draft;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationCreator {

    /**
     * Scores and ranks all pieces based on match with palette
     * 
     * @param allPieces
     * @param palette
     * @param alreadySavedIds
     * @param topN
     * @return
     */
    public static List<Piece> recommendPieces(List<Piece> allPieces, Map<String, Double> palette,
            Set<String> alreadySavedIds, int topN) {
        if (allPieces == null || palette == null || alreadySavedIds == null)
            return new ArrayList<>();

        Map<Piece, Double> scores = new HashMap<>();

        for (Piece piece : allPieces) {
            if (piece == null || alreadySavedIds.contains(piece.getId()))
                continue;
            double score = computeScore(piece, palette);
            scores.put(piece, score);
        }

        return scores.entrySet().stream()
                .sorted(Map.Entry.<Piece, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Computes match score between a piece and the user palette.
     *  
     * @param piece
     * @param palette
     * @return
     */
    private static double computeScore(Piece piece, Map<String, Double> palette) {
        if (piece == null || palette == null || piece.getTags() == null)
            return 0.0;

        double score = 0.0;
        for (String tag : piece.getTags()) {
            if (tag != null && palette.containsKey(tag)) {
                score += palette.getOrDefault(tag, 0.0);
            }
        }
        return score;
    }

    /**
     * Recommends based only on the current draft’s theme. 
     * 
     * @param draftPieces
     * @param allPieces
     * @param alreadySavedIds
     * @param topN
     * @return list of draft-specific Piece recommendations
     */
    public static List<Piece> recommendForDraft(List<Piece> draftPieces, List<Piece> allPieces,
            Set<String> alreadySavedIds, int topN) {
        if (draftPieces == null || draftPieces.isEmpty())
            return new ArrayList<>();
        Map<String, Double> draftPalette = PaletteCreator.createPalette(draftPieces, List.of());
        return recommendPieces(allPieces, draftPalette, alreadySavedIds, topN);
    }

}
