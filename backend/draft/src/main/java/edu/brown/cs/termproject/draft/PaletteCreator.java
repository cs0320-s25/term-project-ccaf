package edu.brown.cs.termproject.draft;

import java.util.*;

public class PaletteCreator {
    private static final Set<String> STOPWORDS = Set.of("the", "a", "and", "in", "of", "with", "to");

    /**
     * Builds weighted keyword map from saved pieces and onboarding keywords.
     * 
     * @param savedPieces
     * @param onboardingKeywords
     * @return
     */
    public static Map<String, Double> createPalette(List<Piece> savedPieces, List<String> onboardingKeywords) {
        Map<String, Double> palette = new HashMap<>();
        if (savedPieces == null)
            savedPieces = new ArrayList<>();
        if (onboardingKeywords == null)
            onboardingKeywords = new ArrayList<>();

        for (Piece piece : savedPieces) {
            if (piece == null)
                continue;
            for (String tag : extractTags(piece)) {
                if (tag == null)
                    continue;
                String key = tag.toLowerCase();
                if (!STOPWORDS.contains(key)) {
                    palette.put(key, palette.getOrDefault(key, 0.0) + 1.0);
                }
            }
        }

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
     * Extracts all tags/keywords from a piece’s title and metadata.
     * 
     * @param piece with keywords to be extracted
     * @return a list of keywords
     */
    private static List<String> extractTags(Piece piece) {
      List<String> tags = new ArrayList<>();
      if (piece == null) return tags;

      if (piece.getTags() != null) {
        tags.addAll(piece.getTags());
      }

      if (piece.getTitle() != null) {
        String[] words = piece.getTitle().toLowerCase().split("\\s+");
        for (String word : words) {
          if (!STOPWORDS.contains(word)) {
            tags.add(word);
          }
        }
      }

      return tags;
    }

}
