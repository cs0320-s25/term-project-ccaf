package edu.brown.cs.termproject.draft.Server;


import static edu.brown.cs.termproject.draft.Handlers.SearchHandler.createMockDataD;
import static edu.brown.cs.termproject.draft.Handlers.SearchHandler.createMockDataP;

import edu.brown.cs.termproject.draft.Handlers.PieceHandlers.*;
import edu.brown.cs.termproject.draft.Handlers.*;
import edu.brown.cs.termproject.draft.Handlers.DraftHandlers.*;
import edu.brown.cs.termproject.draft.Utilities.Storage.FirebaseUtilities;
import edu.brown.cs.termproject.draft.Utilities.Storage.StorageInterface;

import java.io.IOException;

import com.google.gson.JsonObject;

import spark.Spark;

public class Main {
  // Create empty mock objects instead of loading from files
  private static final JsonObject poshmarkMock = createMockDataP("poshmark");
  private static final JsonObject depopMock = createMockDataD("depop");


  public static void main(String[] args) {
//    List<Piece> allPieces = List.of(
//        new Piece(
//            "1", "Blue Denim Jacket", 39.99, "example.com",
//            "https://example.com/denim", "M", "Blue", "Used",
//            "/img/denim.jpg", List.of("denim", "jacket", "blue")
//        ),
//        new Piece(
//            "2", "Red Flannel Shirt", 29.99, "example.com",
//            "https://example.com/flannel", "L", "Red", "New",
//            "/img/flannel.jpg", List.of("flannel", "shirt", "red")
//        ),
//        new Piece(
//            "3", "Black Jeans", 24.99, "example.com",
//            "https://example.com/jeans", "32", "Black", "Used",
//            "/img/jeans.jpg", List.of("black", "jeans", "pants")
//        ),
//        new Piece(
//            "3", "Blue Jeans", 25.99, "example.com",
//            "https://example.com/jeans", "32", "Black", "Used",
//            "/img/jeans.jpg", List.of("blue", "jeans", "pants")
//        )
//    );

    int port = 3232;
    Spark.port(port);

    // CORS setup
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
      Spark.get("/search", new SearchHandler(poshmarkMock, depopMock));
      Spark.post("/check-user", new CheckUserHandler());
      Spark.post("/log-click", new ClickHandler(firebaseUtils));
      Spark.get("/create-draft", new CreateDraftHandler(firebaseUtils));
      Spark.get("/remove-draft", new RemoveDraftHandler(firebaseUtils));
      Spark.get("/view-drafts", new ViewDraftHandler(firebaseUtils));
      Spark.get("/save-piece", new SavePieceHandler());
      Spark.get("/remove-piece", new RemovePieceHandler());
      Spark.get("/view-piece", new ViewPieceGivenDraftHandler());
      Spark.get("/recommend", new RecommendationHandler(firebaseUtils));

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