package edu.brown.cs.termproject.draft.Handlers;
import com.google.gson.Gson;
import edu.brown.cs.termproject.draft.Piece;
import java.util.*;
import java.util.stream.Collectors;
public class SearchHandler {
  private List<Piece> allPieces;

  public SearchHandler(List<Piece> allPieces) {
    this.allPieces = allPieces;
  }

  public String handleSearch(String query) {
    if (query == null || query.trim().isEmpty()) {
      return "[]"; // return empty JSON array if no query
    }

    String normalizedQuery = query.trim().toLowerCase();

    List<Piece> matchingPieces = allPieces.stream()
        .filter(piece -> piece.getTags().stream()
            .anyMatch(tag -> tag.toLowerCase().contains(normalizedQuery)))
        .collect(Collectors.toList());

    return toJson(matchingPieces);
  }

  private String toJson(List<Piece> pieces) {
    // Use a library like Gson to turn Java objects into JSON
    Gson gson = new Gson();
    return gson.toJson(pieces);
  }

}
