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

/**
 * A Spark route handler for responding to search queries using both
 * external APIs (eBay) and mock datasets (Depop and Poshmark).
 * This class performs keyword matching and relevance scoring on listings
 * and returns a ranked list of matching pieces.
 */
public class SearchHandler implements Route {
  private final JsonObject poshmarkMock;
  private final JsonObject depopMock;

  /**
   * Constructs a SearchHandler with specific mock data for Depop and Poshmark.
   *
   * @param poshmarkMock the mock JSON object representing Poshmark listings
   * @param depopMock    the mock JSON object representing Depop listings
   */
  public SearchHandler(JsonObject poshmarkMock, JsonObject depopMock) {
    this.poshmarkMock = poshmarkMock;
    this.depopMock = depopMock;
    System.out.println("SearchHandler initialized with mock data:");
    System.out.println("Poshmark: " + poshmarkMock);
    System.out.println("Depop: " + depopMock);

  }

  /**
   * Default constructor that initializes the handler with built-in mock data.
   */
  public SearchHandler() {
    this.poshmarkMock = createPoshmarkMockData("poshmark");
    this.depopMock = createDepopMockData("depop");
    System.out.println("SearchHandler initialized with default mock data");
  }

  /**
   * Creates mock listing data for Poshmark.
   *
   * @param source the source name (e.g., "poshmark")
   * @return a JsonObject representing listings from the source
   */
  public static JsonObject createPoshmarkMockData(String source) {
    JsonObject mockData = new JsonObject();
    JsonArray listings = new JsonArray();

    // First listing - Designer jacket with rich details
    JsonObject listing1 = new JsonObject();
    listing1.addProperty("id", source + "_1");
    listing1.addProperty("title", "Nike Vintage Windbreaker Jacket");
    listing1.addProperty("description",
        "Rare vintage Nike windbreaker in excellent condition. Perfect for spring weather.");
    listing1.addProperty("price", 45.99);
    listing1.addProperty("source", source);
    listing1.addProperty("url", "https://" + source + ".com/listing/1");
    listing1.addProperty("size", "Medium");
    listing1.addProperty("color", "Blue and White");
    listing1.addProperty("condition", "Excellent");
    listing1.addProperty("imageUrl",
        "https://media-photos.depop.com/b1/43410517/2643968328_9295e1fb86ba4762b56fd40f896b2e32/P0.jpg");
    listing1.addProperty("seller", "VintageFinds" + source);

    JsonArray tags1 = new JsonArray();
    tags1.add("vintage");
    tags1.add("nike");
    tags1.add("windbreaker");
    tags1.add("athletic");
    tags1.add("retro");
    listing1.add("tags", tags1);
    listings.add(listing1);

    // Second listing - Popular brand dress
    JsonObject listing2 = new JsonObject();
    listing2.addProperty("id", source + "_2");
    listing2.addProperty("title", "Zara Floral Summer Dress");
    listing2.addProperty("description", "Beautiful Zara floral dress, perfect for summer. Never worn with tags.");
    listing2.addProperty("price", 35.99);
    listing2.addProperty("source", source);
    listing2.addProperty("url", "https://" + source + ".com/listing/2");
    listing2.addProperty("size", "Small");
    listing2.addProperty("color", "Floral Print");
    listing2.addProperty("condition", "New with tags");
    listing2.addProperty("imageUrl",
        "https://media-photos.depop.com/b1/23291182/2646014590_4bf1b7c7b7a045678d1aa4ee3ff86d4d/P0.jpg");
    listing2.addProperty("seller", "FashionForward" + source);

    JsonArray tags2 = new JsonArray();
    tags2.add("zara");
    tags2.add("dress");
    tags2.add("summer");
    tags2.add("floral");
    tags2.add("new");
    listing2.add("tags", tags2);
    listings.add(listing2);

    // Third listing - Popular sneakers
    JsonObject listing3 = new JsonObject();
    listing3.addProperty("id", source + "_3");
    listing3.addProperty("title", "Air Jordan 1 Mid Sneakers");
    listing3.addProperty("description", "Air Jordan 1 Mid in white and red colorway. Barely worn, great condition.");
    listing3.addProperty("price", 89.99);
    listing3.addProperty("source", source);
    listing3.addProperty("url",
        "https://media-photos.depop.com/b1/29282984/2647600876_a3ae58d8c82d4d96a4a422190084d8ba/P0.jpg");
    listing3.addProperty("size", "US 9");
    listing3.addProperty("color", "White/Red");
    listing3.addProperty("condition", "Like new");
    listing3.addProperty("imageUrl",
        "https://media-photos.depop.com/b1/27314354/2619379562_2c88245092534ab8aa9fceecf2d8a757/P0.jpg");
    listing3.addProperty("seller", "SneakerHead" + source);

    JsonArray tags3 = new JsonArray();
    tags3.add("nike");
    tags3.add("jordan");
    tags3.add("sneakers");
    tags3.add("basketball");
    tags3.add("streetwear");
    listing3.add("tags", tags3);
    listings.add(listing3);

    mockData.add("listings", listings);
    return mockData;
  }

  /**
   * Creates mock listing data for Depop.
   *
   * @param source the source name (e.g., "depop")
   * @return a JsonObject representing listings from the source
   */
  public static JsonObject createDepopMockData(String source) {
    JsonObject mockData = new JsonObject();
    JsonArray listings = new JsonArray();

    // First listing - Designer jacket with rich details
    JsonObject listing1 = new JsonObject();
    listing1.addProperty("id", source + "_1");
    listing1.addProperty("title", "Nike Vintage Windbreaker Jacket");
    listing1.addProperty("description",
        "Rare vintage Nike windbreaker in excellent condition. Perfect for spring weather.");
    listing1.addProperty("price", 45.99);
    listing1.addProperty("source", source);
    listing1.addProperty("url", "https://" + source + ".com/listing/1");
    listing1.addProperty("size", "Medium");
    listing1.addProperty("color", "Blue and White");
    listing1.addProperty("condition", "Excellent");
    listing1.addProperty("imageUrl",
        "https://media-photos.depop.com/b1/426653966/2609935701_71f30b4e13dc4b99918f08263320f8e8/P0.jpg");
    listing1.addProperty("seller", "VintageFinds" + source);

    JsonArray tags1 = new JsonArray();
    tags1.add("vintage");
    tags1.add("nike");
    tags1.add("windbreaker");
    tags1.add("athletic");
    tags1.add("retro");
    listing1.add("tags", tags1);
    listings.add(listing1);

    // Second listing - Popular brand dress
    JsonObject listing2 = new JsonObject();
    listing2.addProperty("id", source + "_2");
    listing2.addProperty("title", "Zara Summer Floral Dress");
    listing2.addProperty("description", "Beautiful Zara floral dress, perfect for summer. Never worn with tags.");
    listing2.addProperty("price", 35.99);
    listing2.addProperty("source", source);
    listing2.addProperty("url", "https://" + source + ".com/listing/2");
    listing2.addProperty("size", "Small");
    listing2.addProperty("color", "Floral Print");
    listing2.addProperty("condition", "New with tags");
    listing2.addProperty("imageUrl",
        "https://media-photos.depop.com/b1/7335144/2565484007_04b31cb627184e0a88bdd5817fd26708/P0.jpg");
    listing2.addProperty("seller", "FashionForward" + source);

    JsonArray tags2 = new JsonArray();
    tags2.add("zara");
    tags2.add("dress");
    tags2.add("summer");
    tags2.add("floral");
    tags2.add("new");
    listing2.add("tags", tags2);
    listings.add(listing2);

    // Third listing - Popular sneakers
    JsonObject listing3 = new JsonObject();
    listing3.addProperty("id", source + "_3");
    listing3.addProperty("title", "Air Jordan 1 Mid Sneakers");
    listing3.addProperty("description", "Air Jordan 1 Mid in white and red colorway. Barely worn, great condition.");
    listing3.addProperty("price", 89.99);
    listing3.addProperty("source", source);
    listing3.addProperty("url",
        "https://media-photos.depop.com/b1/247913937/2546476104_c551917875734314bc56f383a4a4b0f2/P0.jpg");
    listing3.addProperty("size", "US 9");
    listing3.addProperty("color", "White/Red");
    listing3.addProperty("condition", "Like new");
    listing3.addProperty("imageUrl",
        "https://media-photos.depop.com/b1/38665915/2648849832_307b9848f49840428c9f604119fb5153/P0.jpg");
    listing3.addProperty("seller", "SneakerHead" + source);

    JsonArray tags3 = new JsonArray();
    tags3.add("nike");
    tags3.add("jordan");
    tags3.add("sneakers");
    tags3.add("basketball");
    tags3.add("streetwear");
    listing3.add("tags", tags3);
    listings.add(listing3);

    mockData.add("listings", listings);
    return mockData;
  }

  /**
   * Handles a GET request to perform a search based on the query parameter "q".
   * Responds with a JSON array of matching listings from eBay, Depop, and
   * Poshmark.
   *
   * @param request  the Spark HTTP request object
   * @param response the Spark HTTP response object
   * @return a JSON string containing ranked matching listings or an error message
   * @throws Exception if an unexpected error occurs during query processing
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String query = request.queryParams("q");
    System.out.println("Received search query: " + query);

    if (query == null || query.trim().isEmpty()) {
      response.status(400);
      response.type("application/json");
      return new Gson().toJson(Map.of("error", "No query inputted"));
    }

    try {
      String normalizedQuery = query.trim().toLowerCase();
      String[] searchTokens = normalizedQuery.split("\\s+");
      String ebayQuery = normalizedQuery.replace(" ", "_");

      Map<Piece, Integer> scoredResults = new HashMap<>();

      // Process eBay results first
      try {
        List<Piece> ebayResults = APIUtilities.fetchFromEbay(ebayQuery);
        for (Piece piece : ebayResults) {
          scoredResults.put(piece, calculateRelevanceScore(piece, searchTokens, normalizedQuery));
        }
      } catch (Exception e) {
        System.err.println("eBay API error: " + e.getMessage());
      }

      // Process mock data with proper null checks and error handling
      if (poshmarkMock != null) {
        try {
          JsonElement listingsElement = poshmarkMock.get("listings");
          if (listingsElement != null && listingsElement.isJsonArray()) {
            JsonArray poshListings = listingsElement.getAsJsonArray();
            for (JsonElement item : poshListings) {
              if (item != null && item.isJsonObject()) {
                JsonObject listing = item.getAsJsonObject();
                if (matchesSearch(listing, searchTokens, normalizedQuery)) {
                  try {
                    Piece piece = convertJsonToPiece(listing);
                    scoredResults.put(piece, calculateRelevanceScore(piece, searchTokens, normalizedQuery));
                  } catch (Exception e) {
                    System.err.println("Error processing Poshmark listing: " + e.getMessage());
                  }
                }
              }
            }
          }
        } catch (Exception e) {
          System.err.println("Error processing Poshmark mock data: " + e.getMessage());
        }
      }

      if (depopMock != null) {
        try {
          JsonElement listingsElement = depopMock.get("listings");
          if (listingsElement != null && listingsElement.isJsonArray()) {
            JsonArray depopListings = listingsElement.getAsJsonArray();
            for (JsonElement item : depopListings) {
              if (item != null && item.isJsonObject()) {
                JsonObject listing = item.getAsJsonObject();
                if (matchesSearch(listing, searchTokens, normalizedQuery)) {
                  try {
                    Piece piece = convertJsonToPiece(listing);
                    scoredResults.put(piece, calculateRelevanceScore(piece, searchTokens, normalizedQuery));
                  } catch (Exception e) {
                    System.err.println("Error processing Depop listing: " + e.getMessage());
                  }
                }
              }
            }
          }
        } catch (Exception e) {
          System.err.println("Error processing Depop mock data: " + e.getMessage());
        }
      }

      List<Piece> sortedResults = scoredResults.entrySet().stream()
          .sorted(Map.Entry.<Piece, Integer>comparingByValue().reversed())
          .map(Map.Entry::getKey)
          .collect(Collectors.toList());

      response.type("application/json");
      return new Gson().toJson(Map.of("matches", sortedResults));

    } catch (Exception e) {
      e.printStackTrace();
      response.status(500);
      response.type("application/json");
      return new Gson().toJson(Map.of("error", "Search failed: " + e.getMessage()));
    }
  }

  private boolean matchesSearch(JsonObject listing, String[] searchTokens, String normalizedQuery) {
    // Get the main searchable fields with null checks
    String title = "";
    if (listing.has("title") && !listing.get("title").isJsonNull()) {
      title = listing.get("title").getAsString().toLowerCase();
    }

    String color;
    if (listing.has("color") && !listing.get("color").isJsonNull()) {
      color = listing.get("color").getAsString().toLowerCase();
    } else {
      color = "";
    }

    List<String> tags = new ArrayList<>();
    if (listing.has("tags") && !listing.get("tags").isJsonNull() && listing.get("tags").isJsonArray()) {
      JsonArray tagsArray = listing.getAsJsonArray("tags");
      for (JsonElement tag : tagsArray) {
        if (!tag.isJsonNull()) {
          tags.add(tag.getAsString().toLowerCase());
        }
      }
    }

    // Check for matches in title (primary)
    boolean hasMatchInTitle = Arrays.stream(searchTokens)
        .anyMatch(title::contains);

    if (hasMatchInTitle) {
      return true;
    }

    // Check for matches in color and tags (secondary)
    boolean hasColorMatch = !color.isEmpty() && Arrays.asList(searchTokens).contains(color);

    boolean hasTagMatch = !tags.isEmpty() && Arrays.stream(searchTokens)
        .anyMatch(token -> tags.stream().anyMatch(tag -> tag.equals(token)));

    return hasColorMatch && hasTagMatch;
  }

  private int calculateRelevanceScore(Piece piece, String[] searchTokens, String normalizedQuery) {
    int score = 0;
    String title = piece.getTitle().toLowerCase();
    String color = piece.getColor().toLowerCase();
    String size = piece.getSize().toLowerCase();
    String condition = piece.getCondition().toLowerCase();
    List<String> tags = piece.getTags();

    String searchable = title + " " + String.join(" ", tags);

    // Exact phrase match gets the highest score
    if (searchable.contains(normalizedQuery)) {
      score += 20; // Higher score for exact phrase match
    }

    for (String token : searchTokens) {
      // Title matches are most important
      if (title.contains(token))
        score += 5;

      // Tag matches are next most important
      if (tags.stream().anyMatch(tag -> tag.toLowerCase().contains(token)))
        score += 3;

      // Other field matches provide additional relevance
      if (color.contains(token))
        score += 2;
      if (size.contains(token))
        score += 2;
      if (condition.contains(token))
        score += 1;
    }

    return score;
  }

  private Piece convertJsonToPiece(JsonObject listing) {
    try {
      // Get required fields with null checks
      String id = getStringOrDefault(listing, "id", "");
      String title = getStringOrDefault(listing, "title", "");
      String description = getStringOrDefault(listing, "description", "");
      double price = getDoubleOrDefault(listing, "price", 0.0);
      String source = getStringOrDefault(listing, "source", "");
      String url = getStringOrDefault(listing, "url", "");
      String size = getStringOrDefault(listing, "size", "");
      String color = getStringOrDefault(listing, "color", "");
      String condition = getStringOrDefault(listing, "condition", "");
      String imageUrl = getStringOrDefault(listing, "imageUrl", "");
      String seller = getStringOrDefault(listing, "seller", "");

      // Handle tags array
      List<String> tags = new ArrayList<>();
      if (listing.has("tags") && !listing.get("tags").isJsonNull()) {
        JsonArray tagsArray = listing.getAsJsonArray("tags");
        for (JsonElement tag : tagsArray) {
          tags.add(tag.getAsString());
        }
      }

      // Create new Piece with all fields
      return new Piece(id, title, price, source, url,
          imageUrl, size, color, condition, tags, new ArrayList<String>());
    } catch (Exception e) {
      System.err.println("Error converting listing to Piece: " + e.getMessage());
      return null;
    }
  }

  // Helper methods for safe JSON property access
  private String getStringOrDefault(JsonObject obj, String key, String defaultValue) {
    if (obj.has(key) && !obj.get(key).isJsonNull()) {
      return obj.get(key).getAsString();
    }
    return defaultValue;
  }

  private double getDoubleOrDefault(JsonObject obj, String key, double defaultValue) {
    if (obj.has(key) && !obj.get(key).isJsonNull()) {
      return obj.get(key).getAsDouble();
    }
    return defaultValue;
  }

}
