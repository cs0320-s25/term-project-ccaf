package edu.brown.cs.termproject.draft.Utilities;


import com.google.gson.*;
import edu.brown.cs.termproject.draft.Piece;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;
import org.apache.commons.text.StringEscapeUtils;


public class APIUtilities {

  private static final String EBAY_API_URL = "https://api.ebay.com/buy/browse/v1/item_summary/search";

  /**
   * Fetches items from eBay based on the search query.
   *
   * @param query Search term to query eBay API.
   * @return A list of Piece objects from eBay.
   */
  public static List<Piece> fetchFromEbay(String query) {
    List<Piece> piecesFromEbay = new ArrayList<>();
    Set<String> namesOfItems = new HashSet<>();

    try {
      // Construct the eBay API URL with the search query
      String apiUrl = EBAY_API_URL + "?q=" + query + "&limit=20&category_ids=11450";

      // Open a connection to the eBay API
      HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
      connection.setRequestMethod("GET");
      String token = getEbayAccessToken();
      connection.setRequestProperty("Authorization", "Bearer " + token);
      connection.setRequestProperty("Accept", "application/json");

      // Check for successful response
      int status = connection.getResponseCode();
      System.out.println("Status code: " + status);
      if (status == HttpURLConnection.HTTP_OK) {
        // Parse the JSON response
        JsonObject jsonResponse = JsonParser.parseReader(
            new InputStreamReader(connection.getInputStream())).getAsJsonObject();
        JsonArray itemsArray = jsonResponse.getAsJsonArray("itemSummaries");


        // Iterate through the eBay items and create Piece objects
        for (JsonElement itemElement : itemsArray) {
          JsonObject item = itemElement.getAsJsonObject();
          List<String> tags = new ArrayList<>();
          if (item.has("categories")) {
            JsonArray categories = item.getAsJsonArray("categories");
            for (JsonElement categoryElem : categories) {
              JsonObject categoryObj = categoryElem.getAsJsonObject();
              String categoryName = categoryObj.get("categoryName").getAsString();
              tags.add(StringEscapeUtils.unescapeHtml4(categoryName.trim().toLowerCase()));
            }
          }

          String title = item.get("title").getAsString();
          if (!namesOfItems.contains(title)) {
            double price = item.getAsJsonObject("price").get("value").getAsDouble();
            String url = item.get("itemWebUrl").getAsString();

            String imageUrl = "https://via.placeholder.com/225"; // fallback image

            if (item.has("image") && item.getAsJsonObject("image").has("imageUrl")) {
              imageUrl = item.getAsJsonObject("image").get("imageUrl").getAsString();
            } else if (item.has("thumbnailImages")) {
              JsonArray thumbnails = item.getAsJsonArray("thumbnailImages");
              if (!thumbnails.isEmpty() && thumbnails.get(0).getAsJsonObject().has("imageUrl")) {
                imageUrl = thumbnails.get(0).getAsJsonObject().get("imageUrl").getAsString();
              }
            } else if (item.has("additionalImages")) {
              JsonArray additional = item.getAsJsonArray("additionalImages");
              if (!additional.isEmpty() && additional.get(0).getAsJsonObject().has("imageUrl")) {
                imageUrl = additional.get(0).getAsJsonObject().get("imageUrl").getAsString();
              }
            }

            String size = getSize(title);
            String color = getColor(title);

            String condition = item.has("condition") ? item.get("condition").getAsString() : "N/A";
            String uniqueKey = "ebay" + "|" + title;
            String pieceId = UUID.nameUUIDFromBytes(uniqueKey.getBytes()).toString();

            // Create a new Piece object and add it to the list
            Piece piece = new Piece(
                pieceId,
                title, price, "eBay", url, imageUrl,
                size, color, condition, tags
            );
            System.out.println(pieceId);
            piecesFromEbay.add(piece);
            namesOfItems.add(title);
          }

        }
      } else {
        System.err.println("Error fetching eBay data: " + status);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return piecesFromEbay;
  }

  @NotNull
  private static String getColor(String title) {
    String color = "N/A";
    String loweredTitle = title.toLowerCase();

    if (loweredTitle.contains("red")) color = "Red";
    else if (loweredTitle.contains("orange")) color = "Orange";
    else if (loweredTitle.contains("yellow")) color = "Yellow";
    else if (loweredTitle.contains("green")) color = "Green";
    else if (loweredTitle.contains("blue")) color = "Blue";
    else if (loweredTitle.contains("indigo")) color = "Indigo";
    else if (loweredTitle.contains("violet") || loweredTitle.contains("purple")) color = "Purple";
    else if (loweredTitle.contains("pink")) color = "Pink";
    else if (loweredTitle.contains("black")) color = "Black";
    else if (loweredTitle.contains("white")) color = "White";
    else if (loweredTitle.contains("grey") || loweredTitle.contains("gray")) color = "Gray";
    else if (loweredTitle.contains("brown")) color = "Brown";
    else if (loweredTitle.contains("beige")) color = "Beige";
    else if (loweredTitle.contains("gold")) color = "Gold";
    else if (loweredTitle.contains("silver")) color = "Silver";
    return color;
  }

  @NotNull
  private static String getSize(String title) {
    String size = "N/A";
    String loweredTitle = title.toLowerCase();

    // Check for clear full-size words first
    if (loweredTitle.contains("extra small") || loweredTitle.contains("xs")) size = "XS";
    else if (loweredTitle.contains("small") || loweredTitle.matches(".*\\bs\\b.*")) size = "S";
    else if (loweredTitle.contains("medium") || loweredTitle.matches(".*\\bm\\b.*")) size = "M";
    else if (loweredTitle.contains("large") || loweredTitle.matches(".*\\bl\\b.*")) size = "L";
    else if (loweredTitle.contains("extra large") || loweredTitle.contains("xl")) size = "XL";
    else if (loweredTitle.contains("xxl") || loweredTitle.contains("2xl")) size = "XXL";
    else if (loweredTitle.contains("xxxl") || loweredTitle.contains("3xl")) size = "XXXL";
    else if (loweredTitle.matches(".*\\b[0-9]{2}\\b.*")) {
      // For numbered sizes like "32", "34", etc.
      size = loweredTitle.replaceAll(".*\\b([0-9]{2})\\b.*", "$1");
    }

    return size;
  }

  private static String cachedToken = null;
  private static long tokenExpiryTime = 0;

  public static String getEbayAccessToken() throws IOException {
    if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTime) {
      return cachedToken;
    }

    Dotenv dotenv = Dotenv.load();
    String clientId = dotenv.get("EBAY_CLIENT_ID");
    String clientSecret = dotenv.get("EBAY_CLIENT_SECRET");

    if (clientId == null || clientSecret == null) {
      throw new IllegalStateException("Missing EBAY_CLIENT_ID or EBAY_CLIENT_SECRET in .env.");
    }

    String credentials = Base64.getEncoder()
        .encodeToString((clientId + ":" + clientSecret).getBytes());

    URL url = new URL("https://api.ebay.com/identity/v1/oauth2/token");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    conn.setRequestProperty("Authorization", "Basic " + credentials);
    conn.setDoOutput(true);

    String requestBody = "grant_type=client_credentials&scope=https://api.ebay.com/oauth/api_scope";

    try (OutputStream os = conn.getOutputStream()) {
      os.write(requestBody.getBytes());
    }

    int responseCode = conn.getResponseCode();
    if (responseCode != 200) {
      throw new IOException("Failed to fetch token. HTTP response code: " + responseCode);
    }

    try (InputStream is = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      String response = reader.lines().collect(Collectors.joining());
      JsonObject json = JsonParser.parseString(response).getAsJsonObject();

      cachedToken = json.get("access_token").getAsString();
      int expiresIn = json.get("expires_in").getAsInt(); // in seconds
      tokenExpiryTime = System.currentTimeMillis() + expiresIn * 1000L;

      return cachedToken;
    }
  }
}

/**
 * Fetches an OAuth access token from eBay's Sandbox using client credentials.
 *
 * @return OAuth access token string
 * @throws IOException if the request fails
 */
//  public static String getEbayAccessToken() throws IOException, IOException {
//    Dotenv dotenv = Dotenv.load();
//    String clientId = dotenv.get("SANDBOX_APP_ID");
//    String clientSecret = dotenv.get("SANDBOX_CERT_ID");
//
//    if (clientId == null || clientSecret == null) {
//      throw new IllegalStateException("Missing SANDBOX_APP_ID or SANDBOX_CERT_ID in environment variables.");
//    }
//
//    String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
//
//    URL url = new URL("https://api.sandbox.ebay.com/identity/v1/oauth2/token");
//    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//    conn.setRequestMethod("POST");
//    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//    conn.setRequestProperty("Authorization", "Basic " + credentials);
//    conn.setDoOutput(true);
//
//    String requestBody = "grant_type=client_credentials&scope=https://api.ebay.com/oauth/api_scope";
//
//    try (OutputStream os = conn.getOutputStream()) {
//      os.write(requestBody.getBytes());
//    }
//
//    int responseCode = conn.getResponseCode();
//    if (responseCode != 200) {
//      throw new IOException("Failed to fetch token. HTTP response code: " + responseCode);
//    }
//
//    try (InputStream is = conn.getInputStream();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
//
//      String response = reader.lines().collect(Collectors.joining());
//      JsonObject json = JsonParser.parseString(response).getAsJsonObject();
//      return json.get("access_token").getAsString();
//    }
//  }
//}
