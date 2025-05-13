package edu.brown.cs.termproject.draft.Handlers.PieceHandlers;

import com.google.gson.Gson;
import edu.brown.cs.termproject.draft.Utilities.Storage.FirebaseUtilities;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class RemovePieceHandler implements Route {

  /**
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Gson gson = new Gson();
    Map<String, Object> responseMap = new HashMap<>();

    try {
      String userId = request.queryParams("userId");
      String pieceId = request.queryParams("pieceId");
      String draftId = request.queryParams("draftId");

      FirebaseUtilities.removePieceFromDraft(userId, draftId, pieceId);
      FirebaseUtilities.removePieceForUser(userId, pieceId);

      responseMap.put("status", "success");
      responseMap.put("message", "piece with id " + pieceId + " was removed!");
      return gson.toJson(responseMap);
    } catch (Exception e) {
      responseMap.put("status", "error");
      responseMap.put("message", "[RemovePieceHandler] Server error: " + e.getMessage());
      return gson.toJson(responseMap);
    }
  }
}
