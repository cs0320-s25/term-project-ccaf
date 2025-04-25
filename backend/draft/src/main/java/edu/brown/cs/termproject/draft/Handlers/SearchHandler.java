package edu.brown.cs.termproject.draft.Handlers;
import com.google.gson.Gson;
import edu.brown.cs.termproject.draft.Piece;
import java.util.*;
import java.util.stream.Collectors;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchHandler implements Route {
  private List<Piece> allPieces;

  public SearchHandler(List<Piece> allPieces) {
    this.allPieces = allPieces;
  }


  private String toJson(List<Piece> pieces) {
    // Use a library like Gson to turn Java objects into JSON
    Gson gson = new Gson();
    return gson.toJson(pieces);
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();

    String query = request.queryParams("query");

    if (query == null || query.trim().isEmpty()) {
      return "[]"; // return empty JSON array if no query
    }

    String normalizedQuery = query.trim().toLowerCase();

    Set<Piece> matchingPieces = new HashSet<>();
    for (Piece piece : this.allPieces) {
      System.out.println(piece.getTitle().toLowerCase());
      System.out.println(piece.getTags());
      System.out.println(normalizedQuery);
      if (piece.getTitle().toLowerCase().contains(normalizedQuery) || piece.getTags().contains(normalizedQuery)) {
        matchingPieces.add(piece);
      }
    }

    matchingPieces.forEach(piece -> {
      System.out.println("Title: " + piece.getTitle());
      System.out.println("Tags: " + piece.getTags());
    });

    String matchesJson = toJson(matchingPieces.stream().toList());
    responseMap.put("matches", matchesJson);
    return responseMap;
  }
}
