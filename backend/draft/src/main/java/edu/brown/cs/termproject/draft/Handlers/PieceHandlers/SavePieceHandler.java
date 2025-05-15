package edu.brown.cs.termproject.draft.Handlers.PieceHandlers;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import edu.brown.cs.termproject.draft.Piece;
import edu.brown.cs.termproject.draft.Utilities.Storage.FirebaseUtilities;
import java.util.ArrayList;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the saving of a piece to a specified draft and maybe the global store.
 */
public class SavePieceHandler implements Route {
 @Override
 /**
  * Called by clicking the star on a product card from the search, recommendation, or
  * even another draft that already saved the piece. If all parameters are present and no
  * other errors occur, then we check if all the piece's data is in the global store of
  * pieces, and if not, we add them. If it's already there / after we add it, we save that
  * piece to the draft specified in the request, and also mark that said draft uses that
  * piece in the "usedInDrafts" field on a Piece object. We also adjust the thumbnails on
  * the draft that we're saving this new piece to, because the new piece should show
  * in the draft previews in the gallery.
  * @param request object with a map with all the Piece info in case it's being saved to the global
  *                store, the userId, and the draftId
  * @return a serialized map of Strings indicating success + confirmation or failure + error
  */
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
     List<String> tags = tagsParam != null && !tagsParam.isEmpty()
         ? List.of(tagsParam.split(","))
         : new ArrayList<>();

       // List of required params
       String[] requiredParams = {
           "userId", "draftId", "pieceId", "title", "price",
           "sourceWebsite", "url", "imageUrl", "size", "color", "condition"
       };

       // Check if any required param is null or empty
       for (String param : requiredParams) {
         String value = request.queryParams(param);
         if (value == null || value.trim().isEmpty()) {
           responseMap.put("status", "error");
           responseMap.put("error", "Missing or empty parameter: " + param);
           return gson.toJson(responseMap);
         }
       }


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
       piece = new Piece(pieceId, title, priceDouble, sourceWebsite, url, imageUrl, size, color, condition, tags, new ArrayList<>());
       FirebaseUtilities.savePiece(piece);
       FirebaseUtilities.savePieceForUser(userId, piece);
     }

     FirebaseUtilities.savePieceToDraft(userId, draftId, piece);
     DocumentReference draftRef = FirebaseUtilities.getUserDraftDoc(userId, draftId);

     DocumentSnapshot snapshot = draftRef.get().get();
     if (snapshot.exists()) {
       Map<String, Object> draft = snapshot.getData();
       List<String> thumbnails = (List<String>) draft.getOrDefault("thumbnails", new ArrayList<>());

       if (imageUrl != null && !imageUrl.isEmpty() && !thumbnails.contains(imageUrl)) {
         if (thumbnails.size() < 4) {
           thumbnails.add(imageUrl);
         } else {
           thumbnails.set(thumbnails.size() - 1, imageUrl);
         }
         draftRef.update("thumbnails", thumbnails).get(); // wait for update
       }
     }

     responseMap.put("status", "success");
     responseMap.put("message", "Piece saved to draft in Firestore");
     return gson.toJson(responseMap);

   } catch (Exception e) {
     responseMap.put("status", "error");
     responseMap.put("message", "[SavePieceHandler] Server error: " + e.getMessage());
     return gson.toJson(responseMap);
   }
 }
}
