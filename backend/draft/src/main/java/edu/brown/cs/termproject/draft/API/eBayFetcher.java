package edu.brown.cs.termproject.draft.API;

import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.IOException;

public class eBayFetcher {

  private static final String EBAY_API_URL = "https://svcs.ebay.com/services/search/FindingService/v1";
  private static final String APP_ID = System.getenv("SANDBOX_APP_ID");
  private static final String GLOBAL_ID = "EBAY-US"; // Example for US region
  private static final String SERVICE_VERSION = "1.0.0";

  public JsonObject searchEbay(String query) throws Exception {
    // Encode the search query for the URL
    String encodedQuery = URLEncoder.encode(query, "UTF-8");

    // build the full eBay API URL with the encoded query
    String urlString = String.format("%s?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=%s&SECURITY-APPNAME=%s&GLOBAL-ID=%s&keywords=%s",
        EBAY_API_URL, SERVICE_VERSION, APP_ID, GLOBAL_ID, encodedQuery);

    // create a URL object
    URL url = new URL(urlString);

    // open a connection and get the response
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Content-Type", "application/json");

    try {
      // get the HTTP response code
      int responseCode = connection.getResponseCode();

      // if the response code is not code 200, it means there is an error
      if (responseCode != HttpURLConnection.HTTP_OK) {
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
      System.err.println("Error occurred while fetching eBay data: " + e.getMessage());
      throw new Exception("Error fetching data from eBay: " + e.getMessage());
    }
  }
}
