package edu.brown.cs.termproject.draft.API;

import com.google.gson.JsonObject;

import edu.brown.cs.termproject.draft.Piece;
import edu.brown.cs.termproject.draft.Exceptions.APIFetchException;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.io.IOException;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * 
 * eBayFetcher handles querying the eBay Browse API to retrieve item data
 * related to fashion (clothing, shoes, and accessories). It provides methods
 * for sending search queries and parsing responses into Piece objects.
 *
 * API credentials are loaded from environment variables using dotenv.
 * 
 * Endpoint: https://api.ebay.com/buy/browse/v1/item_summary/search
 * Category: 11450 (Clothing, Shoes & Accessories)
 * 
 * Dependencies:
 * - com.google.gson for JSON parsing
 * - io.github.cdimascio.dotenv for environment variable loading
 */
public class eBayFetcher {
  // https://svcs.ebay.com/services/search/FindingService/v1

  private static final String EBAY_API_URL = "https://api.ebay.com/buy/browse/v1/item_summary/search";
  private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
  private static final String APP_ID = dotenv.get("EBAY_APP_ID");
  private static final String GLOBAL_ID = "EBAY-US"; 
  private static final String SERVICE_VERSION = "1.0.0";


  /**
   * Sends a search query to the eBay Browse API and returns the raw JSON
   * response.
   *
   * @param query The keyword-based search string.
   * @return JsonObject containing the eBay API response.
   * @throws Exception         if there is an error during the API request or
   *                           response parsing.
   * @throws APIFetchException wraps lower-level I/O issues with a contextual
   *                           message.
   */
  public JsonObject searchEbay(String query) throws Exception {
    System.out.println("eBay APP_ID: " + APP_ID);
    if (APP_ID == null || APP_ID.isBlank()) {
      throw new RuntimeException("Missing EBAY_APP_ID — please set your App ID as an environment variable.");
    }
    // encode the search query for the URL
    String encodedQuery = URLEncoder.encode(query, "UTF-8");

    // build the full eBay API URL with the encoded query
    String categoryId = "11450"; // clothing, shoes, and accessories
    String urlString = String.format("%s?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=%s&SECURITY-APPNAME=%s&GLOBAL-ID=%s&keywords=%s&categoryId=%s",
        EBAY_API_URL, SERVICE_VERSION, APP_ID, GLOBAL_ID, encodedQuery, categoryId);

    // create a URL object
    URL url = new URL(urlString);

    // open a connection and get the response
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("X-EBAY-SOA-REQUEST-DATA-FORMAT", "JSON");
    connection.setRequestProperty("X-EBAY-SOA-RESPONSE-DATA-FORMAT", "JSON");
    connection.setRequestProperty("X-EBAY-SOA-SECURITY-APPNAME", APP_ID);


    try {
      // get the HTTP response code
      int responseCode = connection.getResponseCode();

      // if the response code is not code 200, it means there is an error
      if (responseCode != HttpURLConnection.HTTP_OK) {
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        StringBuilder errorMsg = new StringBuilder();
        String line;
        while ((line = errorReader.readLine()) != null) {
          errorMsg.append(line);
        }
        System.err.println("eBay API Error Response: " + errorMsg);

        throw new IOException("HTTP error code: " + responseCode);
      }

      // read the response from the input stream
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder response = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        response.append(line);
      }
      reader.close();

      // close the connection
      connection.disconnect();

      // parse the response as a JSON object
      JsonObject jsonResponse = new com.google.gson.JsonParser().parse(response.toString()).getAsJsonObject();

      // check if the response contains an error from the eBay API
      if (jsonResponse.has("errorMessage")) {
        JsonObject errorMessage = jsonResponse.getAsJsonObject("errorMessage");
        String errorCode = errorMessage.get("errorCode").getAsString();
        String errorMessageText = errorMessage.get("message").getAsString();
        throw new IOException("eBay API error: " + errorCode + " - " + errorMessageText);
      }

      // return the valid JSON response
      return jsonResponse;

      // or give an error
    } catch (IOException e) {
      throw new APIFetchException("Error fetching data from eBay. " + e.getMessage());
    }
  }

  /**
   * Parses the JSON response from the eBay API into a list of Piece objects.
   * Fields not directly provided by eBay (e.g., size, color) are left blank or
   * filled with placeholders.
   *
   * @param json The JsonObject returned from searchEbay.
   * @return List of Piece objects representing the parsed eBay items.
   */
  public static List<Piece> parseEbayResults(JsonObject json) {
    List<Piece> pieces = new ArrayList<>();

    try {
      var items = json.getAsJsonObject("findItemsByKeywordsResponse")
          .getAsJsonArray().get(0).getAsJsonObject()
          .getAsJsonArray("searchResult").get(0).getAsJsonObject()
          .getAsJsonArray("item");

      for (var itemElem : items) {
        var item = itemElem.getAsJsonObject();

        String id = item.getAsJsonArray("itemId").get(0).getAsString();
        String title = item.getAsJsonArray("title").get(0).getAsString();
        String url = item.getAsJsonArray("viewItemURL").get(0).getAsString();
        String imageUrl = item.has("galleryURL") ? item.getAsJsonArray("galleryURL").get(0).getAsString() : "";

        // Handle price parsing
        double price = 0.0;
        try {
          var sellingStatus = item.getAsJsonArray("sellingStatus").get(0).getAsJsonObject();
          var currentPrice = sellingStatus.getAsJsonArray("currentPrice").get(0).getAsJsonObject();
          price = currentPrice.get("value").getAsDouble();
        } catch (Exception ignored) {
        }

        // placeholder/defaults for fields eBay doesn't provide directly
        String size = "";
        String color = "";
        String condition = item.has("condition")
            ? item.getAsJsonArray("condition").get(0).getAsJsonObject().getAsJsonArray("conditionDisplayName").get(0)
                .getAsString()
            : "Unknown";

        List<String> tags = List.of("ebay", "resale");
        List<String> usedInDrafts = new ArrayList<>();

        Piece piece = new Piece(id, title, price, "eBay", url, imageUrl, size, color, condition, tags, usedInDrafts);
        pieces.add(piece);
      }

    } catch (Exception e) {
      System.err.println("Error parsing eBay JSON: " + e.getMessage());
    }

    return pieces;
  }

}
