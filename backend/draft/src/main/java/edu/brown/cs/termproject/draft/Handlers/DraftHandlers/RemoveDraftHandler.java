package edu.brown.cs.termproject.draft.Handlers.DraftHandlers;


import com.google.gson.Gson;
import edu.brown.cs.termproject.draft.Utilities.Storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

//TODO: check if this properly removes all the pieces being deleted (mainly just check that it updates
// usedInDrafts + removes pieces from the global if necessary)
/** Handler class to remove a Draft from the Firestore database. */
public class RemoveDraftHandler implements Route {

  private StorageInterface storageHandler;

  public RemoveDraftHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Invoked when a request is made to this route's corresponding path, "remove-draft" or by the
   * trash can on an individual draft page. Removes a draft from the user's drafts collection!
   *
   * @param request object with a map with all userId and the draftId
   * @return a serialized map of Strings indicating success + confirmation or failure + error
   */
  @Override
  public Object handle(Request request, Response response) {
    Gson gson = new Gson();
    Map<String, Object> responseMap = new HashMap<>();

    try {
      String userId = request.queryParams("userId");
      String draftId = request.queryParams("draftId");

      if (userId == null || draftId == null) {
        responseMap.put("response_type", "failure");
        responseMap.put("error", "Missing required parameters.");
        return gson.toJson(responseMap);
      }

      storageHandler.deleteDraft(userId, "drafts", draftId);

      responseMap.put("response_type", "success");
      responseMap.put("message", "Draft removed successfully.");
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return gson.toJson(responseMap);
  }
}
