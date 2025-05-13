package edu.brown.cs.termproject.draft.Utilities.Storage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import edu.brown.cs.termproject.draft.Piece;

public interface StorageInterface {

  void addDocument(String uid, String collection_id, String doc_id, Map<String, Object> data);

  List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException;

  void deleteDocument(String uid, String collection_id, String doc_id) throws InterruptedException, ExecutionException;

  List<Piece> getSavedPieces(String uid) throws Exception;

  List<Piece> getClickedPieces(String uid) throws Exception;

  List<Piece> getOnboardingResponses(String uid) throws Exception;

  List<Piece> getGlobalPieces() throws Exception;

  boolean userExists(String uid);

  void createUser(String uid) throws Exception;

  void logPieceClick(String userId, String pieceId, long timestamp) throws Exception;

}