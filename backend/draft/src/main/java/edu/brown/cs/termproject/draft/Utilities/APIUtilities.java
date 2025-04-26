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

    try {
      // Construct the eBay API URL with the search query
      String apiUrl = EBAY_API_URL + "?q=" + query + "&limit=5"; // Adjust limit as needed

      // Open a connection to the eBay API
      HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
      connection.setRequestMethod("GET");
      String token = getEbayAccessToken();
      connection.setRequestProperty("Authorization", "Bearer " + token);
      connection.setRequestProperty("Accept", "application/json");

      // Check for successful response
      int status = connection.getResponseCode();
      if (status == HttpURLConnection.HTTP_OK) {
        // Parse the JSON response
        JsonObject jsonResponse = JsonParser.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
        JsonArray itemsArray = jsonResponse.getAsJsonArray("itemSummaries");

        // Iterate through the eBay items and create Piece objects
        for (JsonElement itemElement : itemsArray) {
          JsonObject item = itemElement.getAsJsonObject();

          String title = item.get("title").getAsString();
          double price = item.getAsJsonObject("price").get("value").getAsDouble();
          String url = item.get("itemWebUrl").getAsString();
          String imageUrl = item.getAsJsonObject("image").get("imageUrl").getAsString();
          String size = "N/A";  // Modify based on available data, if applicable
          String color = "N/A"; // Modify based on available data, if applicable
          String condition = "N/A";  // Modify based on available data, if applicable

          // Create a new Piece object and add it to the list
          Piece piece = new Piece(
              UUID.randomUUID().toString(),
              title, price, "eBay", url, imageUrl,
              size, color, condition, new HashSet<>()
          );

          piecesFromEbay.add(piece);
        }
      } else {
        System.err.println("Error fetching eBay data: " + status);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return piecesFromEbay;
  }

  /**
   * Fetches an OAuth access token from eBay's Sandbox using client credentials.
   *
   * @return OAuth access token string
   * @throws IOException if the request fails
   */
  public static String getEbayAccessToken() throws IOException, IOException {
    String clientId = System.getenv("SANDBOX_APP_ID");
    String clientSecret = System.getenv("SANDBOX_CERT_ID");

    if (clientId == null || clientSecret == null) {
      throw new IllegalStateException("Missing SANDBOX_APP_ID or SANDBOX_CERT_ID in environment variables.");
    }

    String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

    URL url = new URL("https://api.sandbox.ebay.com/identity/v1/oauth2/token");
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
      return json.get("access_token").getAsString();
    }
  }
}
