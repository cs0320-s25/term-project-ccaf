package edu.brown.cs.termproject.draft.Server;
import edu.brown.cs.termproject.draft.Handlers.Drafts.CreateDraftHandler;
import edu.brown.cs.termproject.draft.Handlers.Drafts.RemoveDraftHandler;
import edu.brown.cs.termproject.draft.Handlers.SearchHandler;
import edu.brown.cs.termproject.draft.Piece;
import edu.brown.cs.termproject.draft.Utilities.Storage.FirebaseUtilities;
import edu.brown.cs.termproject.draft.Utilities.Storage.StorageInterface;
import java.io.IOException;
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
        ),
        new Piece(
            "3", "Blue Jeans", 25.99, "example.com",
            "https://example.com/jeans", "32", "Black", "Used",
            "/img/jeans.jpg", new HashSet<>(Set.of("blue", "jeans", "pants"))
        )
    );

    int port = 3232;
    Spark.port(port);; // set the server port

    // set up CORS
    Spark.options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });

    Spark.before((request, response) -> {
      response.header("Access-Control-Allow-Origin", "*");
      response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
      response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
    });

    StorageInterface firebaseUtils;
    try {
      firebaseUtils = new FirebaseUtilities();

      Spark.get("/search", new SearchHandler());
      Spark.get("/create", new CreateDraftHandler(firebaseUtils));
      Spark.get("/delete", new RemoveDraftHandler(firebaseUtils));

      Spark.init();
      Spark.awaitInitialization();

      System.out.println("Server started at http://localhost:" + port);

    } catch (IOException e) {
      e.printStackTrace();
      System.err.println(
          "Error: Could not initialize Firebase. Likely due to firebase_config.json not being found. Exiting.");
      System.exit(1);
    }


  }
}
