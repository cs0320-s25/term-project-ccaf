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

public class ViewPieceGivenDraftHandler implements Route {

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

    String draftId = request.queryParams("draftId");
    String userId = request.queryParams("userId");
    if (draftId == null || userId == null) {
      responseMap.put("status", "failure");
      responseMap.put("error", "missing required params");
      return gson.toJson(responseMap);
    }

    try {
      List<String> pieceIds = FirebaseUtilities.getPiecesFromDraft(userId, draftId);
      System.out.println(pieceIds);
      List<Piece> pieces = new ArrayList<>();
      for (String pieceId : pieceIds) {
        Piece retrievedPiece = FirebaseUtilities.getPieceById(pieceId);
        if (retrievedPiece != null) {
          pieces.add(retrievedPiece);
        } else {
          System.out.println("Could not find piece with id " + pieceId);
        }
      }

      if (pieces.isEmpty()) {
        responseMap.put("status", "failure");
        responseMap.put("error", "no pieces found");
        return gson.toJson(responseMap);
      }

      Map<String, Object> draftData = FirebaseUtilities.getDraftById(userId, draftId);
      if (draftData == null) {
        responseMap.put("status", "failure");
        responseMap.put("error", "no draft found");
        return gson.toJson(responseMap);
      }

      responseMap.put("status", "success");
      responseMap.put("pieces", pieces);
      responseMap.put("draftData", draftData);
    } catch (Exception e) {
      responseMap.put("status", "failure");
      responseMap.put("error", "Server error: " + e.getMessage());
    }

    return gson.toJson(responseMap);
  }
}
