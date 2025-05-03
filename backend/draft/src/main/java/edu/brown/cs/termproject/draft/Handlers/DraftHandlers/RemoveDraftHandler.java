package edu.brown.cs.termproject.draft.Handlers.DraftHandlers;


import edu.brown.cs.termproject.draft.Utilities.JSONUtils;
import edu.brown.cs.termproject.draft.Utilities.Storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler class to remove a Draft from the Firestore database. */
public class RemoveDraftHandler implements Route {

  private StorageInterface storageHandler;

  public RemoveDraftHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Invoked when a request is made to this route's corresponding path, "remove-draft."
   * Removes a draft from the user's drafts collection and optionally from the global drafts store.
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return The content to be set in the response
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // Extract userId and draftId from the request parameters
      String userId = request.queryParams("userId");
      String draftId = request.queryParams("draftId");

      if (userId == null || draftId == null) {
        responseMap.put("response_type", "failure");
        responseMap.put("error", "Missing required parameters.");
        return JSONUtils.toMoshiJson(responseMap);
      }

      // Remove draft from the user's drafts collection
      String userDraftsPath = "drafts";
      storageHandler.deleteDocument(userId, userDraftsPath, draftId);

      // Optionally, if you want to remove the draft from a global collection, you can also do:
      // storageHandler.removeDocument("global", "drafts", draftId); // if you have a global drafts collection

      responseMap.put("response_type", "success");
      responseMap.put("message", "Draft removed successfully.");
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return JSONUtils.toMoshiJson(responseMap);
  }
}
