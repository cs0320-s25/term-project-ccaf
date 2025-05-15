package edu.brown.cs.termproject.draft.Handlers;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import edu.brown.cs.termproject.draft.Utilities.Storage.FirebaseUtilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class CheckUserHandler implements Route {
  @Override
  public Object handle(Request request, Response response) throws Exception {
    response.type("application/json");
    String uid = request.queryParams("uid");

    if (uid == null || uid.trim().isEmpty()) {
      response.status(400);
      return new Gson().toJson(Map.of("error", "Missing uid parameter"));
    }

    try {
      Firestore db = FirestoreClient.getFirestore();
      DocumentReference userRef = db.collection("users").document(uid);

      // Check if user exists
      DocumentSnapshot snapshot = userRef.get().get();
      boolean exists = snapshot.exists();

      if (!exists) {
        // Create new user document
        Map<String, Object> userData = new HashMap<>();
        userData.put("createdAt", FieldValue.serverTimestamp());
        userData.put("drafts", new ArrayList<>());
        userData.put("saved", new ArrayList<>());

        userRef.set(userData).get();
        System.out.println("Created new user document for: " + uid);

        return new Gson().toJson(Map.of(
            "status", "success",
            "message", "New user created",
            "isNew", true
        ));
      }

      return new Gson().toJson(Map.of(
          "status", "success",
          "message", "User already exists",
          "isNew", false
      ));

    } catch (Exception e) {
      e.printStackTrace();
      response.status(500);
      return new Gson().toJson(Map.of("error", "Failed to check/create user: " + e.getMessage()));
    }
  }
}