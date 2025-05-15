package edu.brown.cs.termproject.draft.Handlers.PieceHandlers;

import com.google.gson.Gson;
import edu.brown.cs.termproject.draft.Piece;
import edu.brown.cs.termproject.draft.Utilities.Storage.FirebaseUtilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handles accessing of full piece information saved to a given draft.
 */
public class ViewPieceGivenDraftHandler implements Route {

  /** Retrieves the piece ids that are saved under a user's drafts in Firebase, and
   * cross-references them with the global store of pieces to access the information that
   * users would like to see (cost, condition, etc).
   * @param request a map with the userId and draftId
   * @return a map of Strings indicating success with the list of pieces and the draftData or failure + error
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Gson gson = new Gson();
    Map<String, Object> responseMap = new HashMap<>();

    String draftId = request.queryParams("draftId");
    String userId = request.queryParams("userId");
    if (draftId == null || draftId.isEmpty() || userId == null || userId.isEmpty()) {
      responseMap.put("status", "failure");
      responseMap.put("error", "missing required params");
      return gson.toJson(responseMap);
    }

    try {
      List<String> pieceIds = FirebaseUtilities.getPiecesFromDraft(userId, draftId);
      List<Piece> pieces = new ArrayList<>();
      if (!pieceIds.isEmpty()) {
        for (String pieceId : pieceIds) {
          if (!pieceId.isEmpty() || pieceId != null) {
            Piece retrievedPiece = FirebaseUtilities.getPieceById(pieceId);
            if (retrievedPiece != null) {
              pieces.add(retrievedPiece);
            } else {
              System.out.println("Could not find piece with id " + pieceId);
            }
          }
        }
      }


      Map<String, Object> draftData = FirebaseUtilities.getDraftById(userId, draftId);
      if (draftData == null || draftData.isEmpty()) {
        responseMap.put("status", "failure");
        responseMap.put("error", "no draft found");
        return gson.toJson(responseMap);
      }

      if (pieces.isEmpty()) {
        responseMap.put("status", "success");
        responseMap.put("message", "no pieces found!");
        responseMap.put("pieces", new ArrayList<>());
        responseMap.put("draftData", draftData);
        return gson.toJson(responseMap);
      }


      responseMap.put("status", "success");
      responseMap.put("message", "pieces found!");
      responseMap.put("pieces", pieces);
      responseMap.put("draftData", draftData);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
      responseMap.put("status", "failure");
      responseMap.put("error", "Server error: " + e.getMessage());
    }

    return gson.toJson(responseMap);
  }
}
