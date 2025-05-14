package edu.brown.cs.termproject.draft.Handlers.DraftHandlers;

import com.google.gson.Gson;
import edu.brown.cs.termproject.draft.Utilities.Storage.StorageInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler to manage draft creation.
 */
public class CreateDraftHandler implements Route {

  private final StorageInterface storage;

  public CreateDraftHandler(StorageInterface storage) {
    this.storage = storage;
  }

  /**
   * Called when a user selects the add draft button or if they create a new draft while
   * searching and seeing a piece. This is where a draft receives its unique id, and its
   * data fields in Firebase are initialized and added.
   *
   * @param request object with a map with all userId and the name of the new draft
   * @return a serialized map of Strings indicating success + confirmation or failure + error
   */
  @Override
  public Object handle(Request request, Response response) {
    Gson gson = new Gson();
    Map<String, Object> responseMap = new HashMap<>();

    try {
      String userId = request.queryParams("userId");
      String draftName = request.queryParams("draftName");

      if (userId == null || draftName == null) {
        responseMap.put("response_type", "failure");
        responseMap.put("error", "Missing userId or name");
        return gson.toJson(responseMap);
      }

      if (!this.storage.isDraftNameAvailable(draftName)) {
        responseMap.put("response_type", "failure");
        responseMap.put("error", "Draft name is not available");
        System.out.println("WE IN THIS HOE");
        return gson.toJson(responseMap);
      }


      String draftId = UUID.randomUUID().toString();

      Map<String, Object> draftData = new HashMap<>();
      draftData.put("id", draftId);
      draftData.put("name", draftName);
      draftData.put("pieces", new ArrayList<>());
      draftData.put("thumbnails", new ArrayList<>());


      storage.addDraft(userId, "drafts", draftId, draftData);

      responseMap.put("response_type", "success");
      responseMap.put("draft", draftData);

    } catch (Exception e) {
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return gson.toJson(responseMap);
  }
}
