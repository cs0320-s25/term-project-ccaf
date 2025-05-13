package edu.brown.cs.termproject.draft.Handlers.DraftHandlers;

import edu.brown.cs.termproject.draft.Utilities.JSONUtils;
import edu.brown.cs.termproject.draft.Utilities.Storage.StorageInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewDraftHandler implements Route {

  private StorageInterface storageHandler;

  public ViewDraftHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();

    String userId = request.queryParams("userId");

    if (userId == null) {
      responseMap.put("response_type", "failure");
      responseMap.put("error", "Missing userId");
      return JSONUtils.toMoshiJson(responseMap);
    }

    try {
      // Get all drafts from the user's draft collection
      List<Map<String, Object>> drafts = this.storageHandler.getCollection(userId, "drafts");
      // System.out.println("drafts: " + drafts.size());
      responseMap.put("response_type", "success");
      responseMap.put("drafts", drafts);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return JSONUtils.toMoshiJson(responseMap);
  }
}

