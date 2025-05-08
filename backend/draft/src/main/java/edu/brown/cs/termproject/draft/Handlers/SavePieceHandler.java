package edu.brown.cs.termproject.draft.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.brown.cs.termproject.draft.Piece;
import edu.brown.cs.termproject.draft.Utilities.Storage.FirebaseUtilities;
import edu.brown.cs.termproject.draft.Utilities.Storage.StorageInterface;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
     String userId = request.queryParams("userId");
     String draftId = request.queryParams("draftId");
     String pieceId = request.queryParams("pieceId");
     String title = request.queryParams("title");
     String price = request.queryParams("price");
     String sourceWebsite = request.queryParams("sourceWebsite");
     String url = request.queryParams("url");
     String imageUrl = request.queryParams("imageUrl");
     String size = request.queryParams("size");
     String color = request.queryParams("color");
     String condition = request.queryParams("condition");

     String tagsParam = request.queryParams("tags");
     Set<String> tags = tagsParam != null && !tagsParam.isEmpty()
         ? new HashSet<>(List.of(tagsParam.split(",")))
         : new HashSet<>();


     Piece piece = FirebaseUtilities.getPieceById(pieceId);


     if (piece == null) {
       double priceDouble;
       try {
         priceDouble = Double.parseDouble(price);
       }
       catch (NumberFormatException e) {
         responseMap.put("status", "error");
         responseMap.put("error", "Invalid price");
         return gson.toJson(responseMap);
       }

       // save the piece...
       piece = new Piece(pieceId, title, priceDouble, sourceWebsite, url, imageUrl, size, color, condition, tags);
       FirebaseUtilities.savePiece(piece);
     }

     FirebaseUtilities.savePieceToDraft(userId, draftId, piece);

     responseMap.put("status", "success");
     responseMap.put("message", "Piece saved to draft in Firestore");
     return gson.toJson(responseMap);

   } catch (Exception e) {
     responseMap.put("status", "error");
     responseMap.put("message", "Server error: " + e.getMessage());
     return gson.toJson(responseMap);
   }
 }
}
