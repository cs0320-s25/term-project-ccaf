package edu.brown.cs.termproject.draft.Utilities.Storage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import edu.brown.cs.termproject.draft.Piece;
import edu.brown.cs.termproject.draft.Exceptions.DatabaseOperationException;

public interface StorageInterface {

  void addDraft(String uid, String collection_id, String doc_id, Map<String, Object> data);

  List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException;

  void deleteDraft(String uid, String collection_id, String doc_id) throws InterruptedException, ExecutionException;

  boolean isDraftNameAvailable(String draftName);

  List<Piece> getSavedPieces(String uid) throws DatabaseOperationException;

  List<Piece> getClickedPieces(String uid) throws DatabaseOperationException;

  List<Piece> getOnboardingResponses(String uid) throws DatabaseOperationException;

  List<Piece> getGlobalPieces() throws DatabaseOperationException;

  boolean userExists(String uid);

  void createUser(String uid) throws DatabaseOperationException;

  void logPieceClick(String userId, String pieceId, long timestamp) throws DatabaseOperationException;

}