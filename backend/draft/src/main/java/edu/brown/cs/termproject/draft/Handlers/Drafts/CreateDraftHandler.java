package edu.brown.cs.termproject.draft.Handlers.Drafts;

import edu.brown.cs.termproject.draft.Utilities.JSONUtils;
import edu.brown.cs.termproject.draft.Utilities.Storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateDraftHandler implements Route {

  private final StorageInterface storage;

  public CreateDraftHandler(StorageInterface storage) {
    this.storage = storage;
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();

    try {
      String userId = request.queryParams("userId");
      String draftName = request.queryParams("draftName");

      if (userId == null || draftName == null) {
        responseMap.put("response_type", "failure");
        responseMap.put("error", "Missing userId or name");
        return JSONUtils.toMoshiJson(responseMap);
      }

      String draftId = UUID.randomUUID().toString();

      Map<String, Object> draftData = new HashMap<>();
      draftData.put("id", draftId);
      draftData.put("name", draftName);
      draftData.put("pieces", new java.util.ArrayList<>()); // start empty

      storage.addDocument(userId, "drafts", draftId, draftData);

      responseMap.put("response_type", "success");
      responseMap.put("draft", draftData);

    } catch (Exception e) {
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return JSONUtils.toMoshiJson(responseMap);
  }
}
