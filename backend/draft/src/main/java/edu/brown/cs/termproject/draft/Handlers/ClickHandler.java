package edu.brown.cs.termproject.draft.Handlers;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;
import edu.brown.cs.termproject.draft.Utilities.Storage.StorageInterface;

public class ClickHandler implements Route {
    private final StorageInterface storage;
    private final Gson gson = new Gson();

    public ClickHandler(StorageInterface storage) {
        this.storage = storage;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Map<String, Object> body = gson.fromJson(request.body(), Map.class);

        String userId = (String) body.get("userId");
        String pieceId = (String) body.get("pieceId");
        Long timestamp = System.currentTimeMillis();

        if (userId == null || pieceId == null) {
            response.status(400);
            return gson.toJson(Map.of("status", "error", "message", "Missing userId or pieceId"));
        }

        storage.logPieceClick(userId, pieceId, timestamp);
        System.out.println(pieceId + " has been clicked at " + timestamp);

        response.status(200);
        return gson.toJson(Map.of("status", "success"));
    }
}
