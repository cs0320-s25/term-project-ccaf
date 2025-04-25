package edu.brown.cs.termproject.draft;

import static spark.Spark.*;

import edu.brown.cs.termproject.draft.Handlers.SearchHandler;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    // Example global store of pieces (replace with your real data source)
    List<Piece> allPieces = List.of(
        new Piece("1", "Blue Denim Jacket", List.of("denim", "jacket", "blue"), 39.99, "/img/denim.jpg", "https://example.com/denim"),
        new Piece("2", "Red Flannel Shirt", List.of("flannel", "shirt", "red"), 29.99, "/img/flannel.jpg", "https://example.com/flannel"),
        new Piece("3", "Black Jeans", List.of("black", "jeans", "pants"), 24.99, "/img/jeans.jpg", "https://example.com/jeans")
    );

    SearchHandler searchHandler = new SearchHandler(allPieces);

    port(4567); // Set the server port

    get("/search", (req, res) -> {
      res.type("application/json");
      String query = req.queryParams("query");
      return searchHandler.handleSearch(query);
    });
  }
}
