package edu.brown.cs.termproject.draft.Handlers.DraftHandlers;

import com.google.gson.Gson;
import edu.brown.cs.termproject.draft.Utilities.Storage.StorageInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler for viewing the list of drafts given a user.
 */
public class ViewDraftHandler implements Route {

  private StorageInterface storageHandler;

  public ViewDraftHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Handles the viewing of the drafts a user has. Called frequently, with example places being
   * to load the save piece modal with options, or to view the list of drafts on the gallery page.
   *
   * @param request object with a map with the userId of the user whose drafts we're trying to see
   * @return a serialized map of Strings indicating success with the list of drafts or failure +
   * error
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Gson gson = new Gson();
    Map<String, Object> responseMap = new HashMap<>();

    String userId = request.queryParams("userId");

    if (userId == null) {
      responseMap.put("response_type", "failure");
      responseMap.put("error", "Missing userId");
      return gson.toJson(responseMap);
    }

    try {
      List<Map<String, Object>> drafts = this.storageHandler.getCollection(userId, "drafts");
      // System.out.println("drafts: " + drafts.size());
      responseMap.put("response_type", "success");
      responseMap.put("drafts", drafts);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return gson.toJson(responseMap);
  }
}

