package edu.brown.cs.termproject.draft;

import java.util.*;
import java.util.stream.Collectors;

import edu.brown.cs.termproject.draft.Exceptions.DraftException;
import edu.brown.cs.termproject.draft.Exceptions.PaletteException;

public class RecommendationCreator {

    // TODO: find and fix the runtime deserialization error. can't convert arraylist to boolean?
    // TODO: need to track the clicking of pieces on the frontend
    // TODO: need to test recommending on front end using mock data
    // TODO: add weight if a piece matches recently searched key terms
    // TODO: recommender has to call all APIs with search query based on a user's palette?
    // TODO: need to write tests for recommendation interaction w/: palette, clicks, saved pieces, etc
    // TODO: need to test all recommender functionality

    /**
     * Scores and ranks all pieces based on match with palette
     * 
     * @param allPieces is all available pieces
     * @param palette is the user's weighted preference map
     * @param alreadySavedIds is a set of item IDs the user already saved (exclude from
     *                        results)
     * @param limit is the number of top recommendations to return
     * @return a ranked list of recommended pieces
     * @throws DraftException if any input is invalid
     */
    public static List<Piece> recommendPieces(List<Piece> allPieces, Map<String, Double> palette,
            Set<String> alreadySavedIds, int limit) throws DraftException {
        if (allPieces == null || palette == null || alreadySavedIds == null) {
            throw new DraftException("Inputs to recommendation engine cannot be null.");
        }

        Map<Piece, Double> scores = new HashMap<>();

        // for every saved piece
        for (Piece piece : allPieces) {
            // if the piece's recommendation score hasn't already been computed
            if (piece == null || alreadySavedIds.contains(piece.getId()))
                continue;

            // calculate its score and store it
            double score = computeScore(piece, palette);
            scores.put(piece, score);
        }

        // sort this by score (descending)
        List<Piece> sortedByScore = scores.entrySet().stream()
                .sorted(Map.Entry.<Piece, Double>comparingByValue().reversed()) // ensures descending order
                .map(Map.Entry::getKey) // discard the scores and only keep the pieces from the map
                .collect(Collectors.toList()); // add all of the pieces together into a list

        // initialize the list of final recommendations
        List<Piece> finalRecommendations = new ArrayList<>();
        Set<String> includedIds = new HashSet<>();

        // add top scoring pieces first (up to the limit)
        for (Piece p : sortedByScore) {
            if (finalRecommendations.size() >= limit)
                break;
            finalRecommendations.add(p);
            includedIds.add(p.getId());
        }

        // if we still have fewer than `limit` items, fill in with random unseen pieces
        if (finalRecommendations.size() < limit) {
            List<Piece> shuffled = new ArrayList<>(allPieces);
            Collections.shuffle(shuffled); // shuffle to add filler items randomly

            for (Piece piece : shuffled) {
                if (finalRecommendations.size() >= limit)
                    break;

                // only add items that haven't already been seen or saved
                if (piece != null && !alreadySavedIds.contains(piece.getId()) && !includedIds.contains(piece.getId())) {
                    finalRecommendations.add(piece);
                    includedIds.add(piece.getId());
                }
            }
        }

        return finalRecommendations;
    }


    /**
     * Computes match score between a piece and the user palette.
     * 
     * @param piece is the secondhand item to evaluate
     * @param palette is a keyword to weight map
     * @return a cumulative score based on tag matches
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
     * @param draftPieces are the draft's pieces
     * @param allPieces is the full inventory to recommend from
     * @param alreadySavedIds user's previously saved item IDs
     * @param limit is the number of recommendations to return
     * @return recommendations tailored to the theme of the draft
     * @throws DraftException if draft data is invalid
     * @throws PaletteException if palette data is invalid
     */
    public static List<Piece> recommendForDraft(List<Piece> draftPieces, List<Piece> allPieces,
            Set<String> alreadySavedIds, int limit) throws DraftException, PaletteException {
        if (draftPieces == null || draftPieces.isEmpty()) {
            throw new DraftException("Draft pieces must not be null or empty.");
        }

        // create a palette only using this draft's content
        Map<String, Double> draftPalette = PaletteCreator.createPalette(draftPieces, List.of(), List.of());

        // return the calculated recommended secondhand items
        return recommendPieces(allPieces, draftPalette, alreadySavedIds, limit);
    }
}
