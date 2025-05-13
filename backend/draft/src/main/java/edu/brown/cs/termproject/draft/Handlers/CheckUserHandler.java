package edu.brown.cs.termproject.draft.Handlers;

import com.google.gson.Gson;
import edu.brown.cs.termproject.draft.Utilities.Storage.FirebaseUtilities;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class CheckUserHandler implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Gson gson = new Gson();
    Map<String, Object> responseMap = new HashMap<>();

    try {
      String uid = request.queryParams("uid");

      if (uid == null || uid.isEmpty()) {
        responseMap.put("status", "error");
        responseMap.put("message", "Missing user ID");
        return gson.toJson(responseMap);
      }

      FirebaseUtilities db = new FirebaseUtilities();
      db.createUser(uid);  // will only create if it doesn't exist
      responseMap.put("status", "success");
      responseMap.put("message", "User checked and created if not already existing.");
      return gson.toJson(responseMap);
    } catch (Exception e) {
      responseMap.put("status", "error");
      responseMap.put("message", "[CheckUserHandler] Server error: " + e.getMessage());
      return gson.toJson(responseMap);
    }
  }
}
