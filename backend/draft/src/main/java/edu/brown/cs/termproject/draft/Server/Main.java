package edu.brown.cs.termproject.draft.Server;

import edu.brown.cs.termproject.draft.Handlers.SearchHandler;
import edu.brown.cs.termproject.draft.Piece;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import spark.Spark;

public class Main {
  public static void main(String[] args) {
    // Example global store of pieces (replace with your real data source)
    List<Piece> allPieces = List.of(
        new Piece(
            "1", "Blue Denim Jacket", 39.99, "example.com",
            "https://example.com/denim", "M", "Blue", "Used",
            "/img/denim.jpg", new HashSet<>(Set.of("denim", "jacket", "blue"))
        ),
        new Piece(
            "2", "Red Flannel Shirt", 29.99, "example.com",
            "https://example.com/flannel", "L", "Red", "New",
            "/img/flannel.jpg", new HashSet<>(Set.of("flannel", "shirt", "red"))
        ),
        new Piece(
            "3", "Black Jeans", 24.99, "example.com",
            "https://example.com/jeans", "32", "Black", "Used",
            "/img/jeans.jpg", new HashSet<>(Set.of("black", "jeans", "pants"))
        )
    );

    int port = 3232;
    Spark.port(port);; // Set the server port

//    get("/search", (req, res) -> {
//      res.type("application/json");
//      String query = req.queryParams("query");
//      return searchHandler.handleSearch(query);
//    });

    Spark.get("/search", new SearchHandler(allPieces));

    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}
