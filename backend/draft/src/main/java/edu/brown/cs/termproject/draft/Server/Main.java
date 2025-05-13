package edu.brown.cs.termproject.draft.Server;
import edu.brown.cs.termproject.draft.Handlers.PieceHandlers.RemovePieceHandler;
import edu.brown.cs.termproject.draft.Handlers.PieceHandlers.ViewPieceGivenDraftHandler;
import edu.brown.cs.termproject.draft.Handlers.CheckUserHandler;
import edu.brown.cs.termproject.draft.Handlers.RecommendationHandler;
import edu.brown.cs.termproject.draft.Handlers.PieceHandlers.SavePieceHandler;
import edu.brown.cs.termproject.draft.Handlers.SearchHandler;
import edu.brown.cs.termproject.draft.Handlers.DraftHandlers.CreateDraftHandler;
import edu.brown.cs.termproject.draft.Handlers.DraftHandlers.RemoveDraftHandler;
import edu.brown.cs.termproject.draft.Handlers.DraftHandlers.ViewDraftHandler;
import edu.brown.cs.termproject.draft.Piece;
import edu.brown.cs.termproject.draft.Utilities.Storage.FirebaseUtilities;
import edu.brown.cs.termproject.draft.Utilities.Storage.StorageInterface;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Spark;


public class Main {
  private static final JsonObject poshmarkMock; // Declare the type
  private static final JsonObject depopMock;    // Declare the type

  static {
    try {
      InputStream poshStream = Main.class.getResourceAsStream("/mock_data/poshmark_mock.json");
      InputStream depopStream = Main.class.getResourceAsStream("/mock_data/depop_mock.json");


      if (poshStream == null || depopStream == null) {
        throw new RuntimeException("Mock data files not found in resources." +  " Checking file path for Poshmark mock data: " +
          Main.class.getResource("/mock_data/poshmark_mock.json"));
      }

      poshmarkMock = JsonParser.parseReader(new InputStreamReader(poshStream)).getAsJsonObject();
      depopMock = JsonParser.parseReader(new InputStreamReader(depopStream)).getAsJsonObject();

    } catch (Exception e) {
      throw new RuntimeException("Failed to load mock data files", e);
    }
  }


  //  static {
//    try {
//      // Load mock data from files
//      depopMock = JsonParser.parseReader(
//          new FileReader("./PoshMock.json")).getAsJsonObject();
//      poshmarkMock = JsonParser.parseReader(
//          new FileReader("./DepopMock.json")).getAsJsonObject();
//
//    } catch (IOException e) {
//      throw new RuntimeException("Failed to load mock data files", e);
//    }
//  }
  public static void main(String[] args) {
    // Example global store of pieces (replace with your real data source)
    List<Piece> allPieces = List.of(
        new Piece(
            "1", "Blue Denim Jacket", 39.99, "example.com",
            "https://example.com/denim", "https://", "Small", "Blue",
            "Used", List.of("denim", "jacket", "blue"), false
        ),
        new Piece(
            "2", "Red Flannel Shirt", 29.99, "example.com",
            "https://example.com/flannel", "https://", "Large", "Red",
            "New", List.of("flannel", "shirt"), false
        ),
        new Piece(
            "3", "Black Jeans", 24.99, "example.com",
            "https://example.com/jeans", "https://", "Medium", "Black",
            "Used", List.of("black", "jeans", "pants"), false
        ),
        new Piece(
            "3", "Blue Jeans", 25.99, "example.com",
            "https://example.com/jeans", "https://", "Extra Large", "Dark Blue",
            "Like New", List.of("blue", "jeans", "pants"), false
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
      Spark.get("/search", new SearchHandler(poshmarkMock, depopMock, firebaseUtils));

      Spark.post("/check-user", new CheckUserHandler());
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
