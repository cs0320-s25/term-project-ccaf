package edu.brown.cs.termproject.draft.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import edu.brown.cs.termproject.draft.Piece;
import edu.brown.cs.termproject.draft.Utilities.APIUtilities;
import java.util.*;
import java.util.stream.Collectors;

import spark.Request;
import spark.Response;
import spark.Route;

public class SearchHandler implements Route {
  private final JsonObject poshmarkMock;
  private final JsonObject depopMock;

  public SearchHandler(JsonObject poshmarkMock, JsonObject depopMock) {
    this.poshmarkMock = poshmarkMock;
    this.depopMock = depopMock;
  }

  // Default constructor for backward compatibility
  public SearchHandler() {
    this.poshmarkMock = null;
    this.depopMock = null;
  }


  @Override
  public Object handle(Request request, Response response) throws Exception {
    String query = request.queryParams("q");

    if (query == null || query.trim().isEmpty()) {
      response.status(400);
      response.type("application/json");
      return new Gson().toJson(Map.of("error", "No query inputted"));
    }

    String normalizedQuery = query.trim().toLowerCase();
    String[] searchTokens = normalizedQuery.split("\\s+");
    
    // Map to store pieces with their relevance scores
    Map<Piece, Integer> scoredResults = new HashMap<>();

    // Get and score eBay results
    List<Piece> ebayResults = APIUtilities.fetchFromEbay(normalizedQuery);
    for (Piece piece : ebayResults) {
      scoredResults.put(piece, calculateRelevanceScore(piece, searchTokens));
    }

    // Add and score mock results if available
    if (poshmarkMock != null && depopMock != null) {
      // Add Poshmark mock results
      JsonArray poshListings = poshmarkMock.getAsJsonArray("listings");
      for (JsonElement item : poshListings) {
        JsonObject listing = item.getAsJsonObject();
        if (matchesSearch(listing, searchTokens)) {
          Piece piece = convertJsonToPiece(listing);
          scoredResults.put(piece, calculateRelevanceScore(piece, searchTokens));
        }
      }

      // Add Depop mock results
      JsonArray depopListings = depopMock.getAsJsonArray("listings");
      for (JsonElement item : depopListings) {
        JsonObject listing = item.getAsJsonObject();
        if (matchesSearch(listing, searchTokens)) {
          Piece piece = convertJsonToPiece(listing);
          scoredResults.put(piece, calculateRelevanceScore(piece, searchTokens));
        }
      }
    }

    // Sort results by score in descending order
    List<Piece> sortedResults = scoredResults.entrySet().stream()
        .sorted(Map.Entry.<Piece, Integer>comparingByValue().reversed())
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

    response.type("application/json");
    return new Gson().toJson(Map.of("matches", sortedResults));
  }

  private boolean matchesSearch(JsonObject listing, String[] searchTokens) {
    String title = listing.get("title").getAsString().toLowerCase();
    return Arrays.stream(searchTokens).anyMatch(title::contains);
  }

  private int calculateRelevanceScore(Piece piece, String[] searchTokens) {
    int score = 0;
    String title = piece.getTitle().toLowerCase();
    String color = piece.getColor().toLowerCase();
    String size = piece.getSize().toLowerCase();
    String condition = piece.getCondition().toLowerCase();
    Set<String> tags = piece.getTags();

    for (String token : searchTokens) {
      // Title matches are most important
      if (title.contains(token)) score += 5;
      
      // Tag matches are next most important
      if (tags.stream().anyMatch(tag -> tag.toLowerCase().contains(token))) score += 3;
      
      // Other field matches provide additional relevance
      if (color.contains(token)) score += 2;
      if (size.contains(token)) score += 2;
      if (condition.contains(token)) score += 1;
    }

    return score;
  }



  private Piece convertJsonToPiece(JsonObject listing) {
    return new Piece(
        listing.get("id").getAsString(),
        listing.get("title").getAsString(),
        listing.get("price").getAsDouble(),
        listing.get("platform").getAsString(),
        listing.get("url").getAsString(),
        listing.get("size").getAsString(),
        listing.get("color").getAsString(),
        listing.get("condition").getAsString(),
        listing.get("imageUrl").getAsString(),
        new HashSet<>(Arrays.asList(new Gson().fromJson(listing.get("tags"), String[].class)))
    );
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
//}
