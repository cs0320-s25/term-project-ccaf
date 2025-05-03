package edu.brown.cs.termproject.draft;

import java.util.*;

import edu.brown.cs.termproject.draft.Exceptions.PaletteException;

public class PaletteCreator {
    // used to filter out words with little semantic value
    private static final Set<String> STOPWORDS = Set.of("the", "a", "and", "in", "of", "with", "to");

    //TODO: filter out non-clothing items? use eBay categories

    /**
     * Builds weighted keyword map from saved pieces, clicked pieces, and onboarding
     * keywords.
     * 
     * @param savedPieces        list of pieces the user has saved
     * @param onboardingKeywords list of onboarding keywords
     * @param clickedPieces      list of pieces the user has clicked on but not
     *                           saved
     * @return palette map with weighted keywords
     * @throws DraftOperationException if any list is null or contains invalid data
     */
    public static Map<String, Double> createPalette(List<Piece> savedPieces, List<String> onboardingKeywords,
            List<Piece> clickedPieces)
            throws PaletteException {
        if (savedPieces == null || onboardingKeywords == null || clickedPieces == null) {
            throw new PaletteException(
                    "Saved pieces, onboarding keywords, and clicked pieces must not be null.");
        }

        Map<String, Double> palette = new HashMap<>();

        // give every tag from a piece that a user saves a weight of 1/1
        for (Piece piece : savedPieces) {
            if (piece == null) {
                throw new PaletteException("Saved pieces list contains null element.");
            }
            updatePaletteWithPiece(palette, piece, 1.0);
        }

        // give every tag from a piece a user clicks on a weight of 0.25/1
        for (Piece piece : clickedPieces) {
            if (piece == null)
                continue;
            updatePaletteWithPiece(palette, piece, 0.25);
        }

        // for every onboarding keyword, give it a weight of 0.5/1
        for (String keyword : onboardingKeywords) {
            if (keyword == null)
                continue;
            String key = keyword.toLowerCase();
            if (!STOPWORDS.contains(key)) {
                palette.put(key, palette.getOrDefault(key, 0.0) + 0.5);
            }
        }

        return palette;
    }

    /**
     * Updates the palette incrementally by adding weights from a new piece.
     * 
     * @param palette  the current palette to update
     * @param newPiece the new piece being added
     * @param weight   the weight to apply for each tag
     */
    public static void updatePaletteWithPiece(Map<String, Double> palette, Piece newPiece, double weight) {
        for (String tag : extractTags(newPiece)) {
            if (tag != null) {
                String key = tag.toLowerCase();

                // if all remaining keywords have semantic value,
                if (!STOPWORDS.contains(key)) {
                    // add the passed in weight
                    palette.put(key, palette.getOrDefault(key, 0.0) + weight);
                }
            }
        }
    }

    /**
     * Extracts all tags/keywords from a piece’s title and metadata.
     * 
     * @param piece with keywords to be extracted
     * @return a list of keywords
     */
    private static List<String> extractTags(Piece piece) {
        List<String> tags = new ArrayList<>();
        if (piece == null) return tags;

        // add the piece's tags to the list of tags
        if (piece.getTags() != null) {
            tags.addAll(piece.getTags());
        }

        // use the piece's title to get additional keywords
        if (piece.getTitle() != null) {
            String[] words = piece.getTitle().toLowerCase().split("\\s+");

            // ensure we're not adding stop words
            for (String word : words) {
                if (!STOPWORDS.contains(word)) {
                    tags.add(word);
                }
            }
        }

        return tags;
    }


}

