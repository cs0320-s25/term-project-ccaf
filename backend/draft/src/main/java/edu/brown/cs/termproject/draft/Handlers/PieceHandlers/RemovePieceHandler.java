package edu.brown.cs.termproject.draft.Handlers.PieceHandlers;

import com.google.gson.Gson;
import edu.brown.cs.termproject.draft.Utilities.Storage.FirebaseUtilities;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handles the removal of a piece from a specified draft.
 */
public class RemovePieceHandler implements Route {

  /** Called clicking the trash can on a draft's page. If all parameters are present and no
   * other errors occur, then the piece is removed first from the individual draft (which involves
   * a possible removal from the global store of pieces), and then from the store of
   * saved pieces for a specific (passed in) user.
   * @param request a map with the userId, pieceId, draftId parameters
   * @return a map of Strings indicating success + confirmation or failure + error
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
      responseMap.put("status", "failure");
      responseMap.put("message", "[RemovePieceHandler] Server error: " + e.getMessage());
      return gson.toJson(responseMap);
    }
  }
}
