//package edu.brown.cs.termproject.draft.Handlers;
//import com.google.gson.Gson;
//import edu.brown.cs.termproject.draft.Piece;
//import java.util.*;
//import java.util.stream.Collectors;
//import spark.Request;
//import spark.Response;
//import spark.Route;
//
//public class SearchHandler implements Route {
//  private List<Piece> allPieces;
//
//  public SearchHandler(List<Piece> allPieces) {
//    this.allPieces = allPieces;
//  }
//
//  private String toJson(List<Piece> pieces) {
//    Gson gson = new Gson();
//    return gson.toJson(pieces);
//  }
//
//  @Override
//  public Object handle(Request request, Response response) throws Exception {
//    String query = request.queryParams("query");
//    Map<String, Object> responseMap = new HashMap<>();
//
//    // if no search terms given, return an error JSON message
//    if (query == null || query.trim().isEmpty()) {
//      response.status(400); // bad request
//      response.type("application/json");
//      return new Gson().toJson(Map.of(
//          "result", "error",
//          "message", "No query inputted"
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
//    responseMap.put("matches", toJson(sortedResults));
//    return responseMap;
//  }
//}
