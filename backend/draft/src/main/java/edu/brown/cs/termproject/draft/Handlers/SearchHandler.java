package edu.brown.cs.termproject.draft.Handlers;

import com.google.gson.Gson;
import edu.brown.cs.termproject.draft.Piece;
import edu.brown.cs.termproject.draft.Utilities.APIUtilities;
import java.util.*;
import java.util.stream.Collectors;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchHandler implements Route {

  public SearchHandler() {
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String query = request.queryParams("q");

    if (query == null || query.trim().isEmpty()) {
      response.status(400);
      response.type("application/json");
      return new Gson().toJson(Map.of(
          "error", "No query inputted"
      ));
    }

    List<Piece> ebayResults = APIUtilities.fetchFromEbay(query.trim().toLowerCase());

    response.type("application/json");
    return new Gson().toJson(Map.of("matches", ebayResults));
  }
}





//  private final List<Piece> allPieces;
//
//  public SearchHandler(List<Piece> allPieces) {
//    this.allPieces = allPieces;
//  }
//
//  /**
//   * Search with mock data.
//   */
//  @Override
//  public Object handle(Request request, Response response) throws Exception {
//    String query = request.queryParams("q");
//
//    if (query == null || query.trim().isEmpty()) {
//      response.status(400); // bad request
//      response.type("application/json");
//      return new Gson().toJson(Map.of(
//          "error", "No query inputted"
//      ));
//    }
//
//    String normalizedQuery = query.trim().toLowerCase();
//    String[] tokens = normalizedQuery.split("\\s+");
//
//    Map<Piece, Integer> scoredMatches = new HashMap<>();
//
//    for (Piece piece : this.allPieces) {
//      String searchable = (
//          piece.getTitle() + " " +
//              piece.getColor() + " " +
//              piece.getSize() + " " +
//              piece.getCondition() + " " +
//              String.join(" ", piece.getTags())
//      ).toLowerCase();
//
//      boolean anyMatch = Arrays.stream(tokens).anyMatch(searchable::contains);
//      if (!anyMatch) continue;
//
//      int score = 0;
//      for (String token : tokens) {
//        if (piece.getTitle().toLowerCase().contains(token)) score += 3;
//        if (piece.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(token))) score += 2;
//        if (piece.getColor().toLowerCase().contains(token)) score += 1;
//        if (piece.getSize().toLowerCase().contains(token)) score += 1;
//        if (piece.getCondition().toLowerCase().contains(token)) score += 1;
//      }
//
//      scoredMatches.put(piece, score);
//    }
//
//    List<Piece> sortedResults = scoredMatches.entrySet().stream()
//        .sorted((a, b) -> b.getValue() - a.getValue())
//        .map(Map.Entry::getKey)
//        .collect(Collectors.toList());
//
//    response.type("application/json");
//    return new Gson().toJson(sortedResults);
//  }









// !!!!!!!!!!!!
//
//  /**
//   * Search with eBay API integration.
//   * @param request
//   * @param response
//   * @return
//   * @throws Exception
//   */
//  @Override
//  public Object handle(Request request, Response response) throws Exception {
//    String query = request.queryParams("q");
//    Map<String, Object> responseMap = new HashMap<>();
//
//    if (query == null || query.trim().isEmpty()) {
//      response.status(400); // bad request
//      response.type("application/json");
//      return new Gson().toJson(Map.of(
//          "result", "error",
//          "message", "No query inputted"
//      ));
//    }
//
//    // Normalize query and prepare for search
//    String normalizedQuery = query.trim().toLowerCase();
//    List<Piece> allSearchResults = new ArrayList<>();
//
//    // Search locally stored pieces first
//    List<Piece> localResults = searchLocalPieces(normalizedQuery);
//    allSearchResults.addAll(localResults);
//
//    // Fetch eBay pieces using the EbayApiUtils
//    List<Piece> ebayResults = APIUtilities.fetchFromEbay(normalizedQuery);
//    allSearchResults.addAll(ebayResults);
//
//    // Return combined and sorted results
//    response.type("application/json");
//
//    // Instead of returning the JSON-serialized string, return the actual list
//    responseMap.put("matches", allSearchResults); // Send the actual list of pieces
//    return new Gson().toJson(responseMap); // Convert responseMap to JSON
//  }
//
//  private List<Piece> searchLocalPieces(String query) {
//    String normalizedQuery = query.trim().toLowerCase();
//    String[] tokens = normalizedQuery.split("\\s+");
//
//    Map<Piece, Integer> scoredMatches = new HashMap<>();
//
//    for (Piece piece : this.allPieces) {
//      String searchable = (
//          piece.getTitle() + " " +
//              piece.getColor() + " " +
//              piece.getSize() + " " +
//              piece.getCondition() + " " +
//              String.join(" ", piece.getTags())
//      ).toLowerCase();
//
//      boolean anyMatch = Arrays.stream(tokens).anyMatch(searchable::contains);
//      if (!anyMatch) continue;
//
//      int score = 0;
//      for (String token : tokens) {
//        if (piece.getTitle().toLowerCase().contains(token)) score += 3;
//        if (piece.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(token))) score += 2;
//        if (piece.getColor().toLowerCase().contains(token)) score += 1;
//        if (piece.getSize().toLowerCase().contains(token)) score += 1;
//        if (piece.getCondition().toLowerCase().contains(token)) score += 1;
//      }
//
//      scoredMatches.put(piece, score);
//    }
//
//    List<Piece> sortedResults = scoredMatches.entrySet().stream()
//        .sorted((a, b) -> b.getValue() - a.getValue())
//        .map(Map.Entry::getKey)
//        .collect(Collectors.toList());
//
//    return sortedResults;
//  }
//}
