package edu.brown.cs.termproject.draft.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.brown.cs.termproject.draft.Piece;
import edu.brown.cs.termproject.draft.Utilities.Storage.FirebaseUtilities;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class SavePieceHandler implements Route {
 @Override
 public Object handle(Request request, Response response) throws Exception {
   Gson gson = new Gson();
   Map<String, Object> responseMap = new HashMap<>();

   try {
     JsonObject body = gson.fromJson(request.body(), JsonObject.class);
     String draftId = body.get("draftId").getAsString();
     String pieceId = body.get("pieceId").getAsString();

     Piece piece = FirebaseUtilities.getPieceById(pieceId);

     if (piece == null) {
       response.status(400);
       responseMap.put("status", "error");
       responseMap.put("message", "Invalid pieceId");
       return gson.toJson(responseMap);
     }

     FirebaseUtilities.savePieceToDraft(draftId, piece);

     responseMap.put("status", "success");
     responseMap.put("message", "Piece saved to draft in Firestore");
     return gson.toJson(responseMap);

   } catch (Exception e) {
     response.status(500);
     responseMap.put("status", "error");
     responseMap.put("message", "Server error: " + e.getMessage());
     return gson.toJson(responseMap);
   }
 }
}
