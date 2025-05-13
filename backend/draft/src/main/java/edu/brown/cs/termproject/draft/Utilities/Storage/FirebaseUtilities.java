package edu.brown.cs.termproject.draft.Utilities.Storage;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import edu.brown.cs.termproject.draft.Piece;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirebaseUtilities implements StorageInterface {

  public FirebaseUtilities() throws IOException {
    String workingDirectory = System.getProperty("user.dir");
    Path firebaseConfigPath = Paths.get(workingDirectory, "src", "main", "resources", "firebase_config.json");

    System.out.println(firebaseConfigPath);
    FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toString());

    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build();

    FirebaseApp.initializeApp(options);
  }

  /**
   * Gets the collection at the given id for the given user.
   * @param uid id for the user whose collection we're retrieving
   * @param collection_id the id for the collection we're retrieving
   * @return a list of maps that hold the data stored for a document in the collection we're
   * retrieving
   */
  @Override
  public List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException, IllegalArgumentException {
    if (uid == null || collection_id == null) {
      throw new IllegalArgumentException("getCollection: uid and/or collection_id cannot be null");
    }

    // gets all documents in the collection 'collection_id' for user 'uid'
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference dataRef = db.collection("users").document(uid).collection(collection_id);

    QuerySnapshot dataQuery = dataRef.get().get();

    List<Map<String, Object>> data = new ArrayList<>();
    for (QueryDocumentSnapshot doc : dataQuery.getDocuments()) {
      data.add(doc.getData());
    }

    return data;
  }


  /**
   * Adds a new document "doc_id" to collection "collection_id" for user "uid" with data payload
   * "data". Note, works best when the server is run through IntelliJ.
   * @param uid id for the user who's gaining a new document
   * @param collection_id id for the collection the new document is to be filed under
   * @param doc_id id for the new document
   * @param data payload that's to be in the new document
   * @throws IllegalArgumentException if any vital parameters are null
   */
  @Override
  public void addDocument(String uid, String collection_id, String doc_id, Map<String, Object> data)
      throws IllegalArgumentException {
    if (uid == null || collection_id == null || doc_id == null || data == null) {
      throw new IllegalArgumentException(
          "addDocument: uid, collection_id, doc_id, or data cannot be null");
    }
    if (uid.isBlank() || collection_id.isBlank() || doc_id.isBlank() || data.isEmpty()) {
      throw new IllegalArgumentException(
          "addDocument: uid, collection_id, doc_id, or data cannot be blank");
    }

    // initializes a firestore client to interact with the database
    Firestore db = FirestoreClient.getFirestore();

    CollectionReference dataRef = db.collection("users").document(uid).collection(collection_id);

    DocumentReference docRef = dataRef.document(doc_id);

    try {
      System.out.println("Attempting to write data: " + data.toString());
      docRef.set(data).get();
      System.out.println("Successfully added document with ID: " + doc_id);
    } catch (Exception e) {
      System.err.println("Error writing document: " + e.getMessage());
    }
  }

  /**
   * Removes a document "doc_id" from collection "collection_id" for user "uid". Note, works best
   * when the server is run through IntelliJ.
   * @param uid id for the user who's gaining a new document
   * @param collection_id id for the collection the new document is to be filed under
   * @param doc_id id for the document to be deleted
   * @throws IllegalArgumentException if any vital parameters are null
   */
  public void deleteDocument(String uid, String collection_id, String doc_id)
      throws InterruptedException, ExecutionException {
    if (uid == null || collection_id == null || doc_id == null) {
      throw new IllegalArgumentException("deleteDocument: uid, collection_id, or doc_id cannot be null");
    }

    // Initialize Firestore
    Firestore db = FirestoreClient.getFirestore();

    // Reference to the document to be deleted
    DocumentReference docRef = db.collection("users").document(uid).collection(collection_id).document(doc_id);

    Iterable<CollectionReference> subcollections = docRef.listCollections();
    for (CollectionReference subcollection : subcollections) {
      deleteCollection(subcollection);
    }

    // Delete the document
    ApiFuture<WriteResult> deleteFuture = docRef.delete();
    deleteFuture.get(); // Wait for the deletion to complete

    System.out.println("Successfully deleted document with ID: " + doc_id);
  }



  /**
   * Recursively removes all the documents and collections inside a collection! More info:
   * <a href="https://firebase.google.com/docs/firestore/manage-data/delete-data#collections">here.</a>
   * @param collection reference to the collection that's to be wiped out
   */
  private void deleteCollection(CollectionReference collection) {
    try {

      // get all documents in the collection
      ApiFuture<QuerySnapshot> future = collection.get();
      List<QueryDocumentSnapshot> documents = future.get().getDocuments();

      // delete each document
      for (QueryDocumentSnapshot doc : documents) {
        doc.getReference().delete();
        System.out.println("Successfully deleted collection: " + doc.getId());
      }

    } catch (Exception e) {
      System.err.println("Error deleting collection : " + e.getMessage());
    }
  }

  /**
   * Getter for the Firestore database object.
   * @return the Firestore database object
   */
  public static Firestore getDb() {
    return FirestoreClient.getFirestore();
  }

  /**
   * Given a userId, a draftId, and a Piece object, modifies the draft document corresponding to the
   * draftId. Specifically, adds an entry to the pieces array on the draft document, and adds as an
   * entry to the usedInDrafts list in a piece's document.
   * @param userId id of the user who's saving the piece
   * @param draftId id of the draft the piece is being saved to
   * @param piece piece object being saved
   */
  public static void savePieceToDraft(String userId, String draftId, Piece piece) throws Exception {
    Firestore db = getDb();
    DocumentReference draftDoc = db
        .collection("users")
        .document(userId)
        .collection("drafts")
        .document(draftId);

    Map<String, Object> update1 = new HashMap<>();
    update1.put("pieces", FieldValue.arrayUnion(piece.getId())); // store just the ID...?

    draftDoc.update(update1).get();

    DocumentReference piecesDoc = db.collection("pieces").document(piece.getId());
    Map<String, Object> update2 = new HashMap<>();
    update2.put("usedInDrafts", FieldValue.arrayUnion(draftId));
    piecesDoc.update(update2).get();
  }

  /**
   * Given a userId, a draftId, and a pieceId, modifies the draft document corresponding to the
   * draftId. Specifically, removes an entry from the pieces array on the draft document, and removes
   * as an entry from the usedInDrafts list in a piece's document.
   * @param userId id of the user who's removing the piece
   * @param draftId id of the draft the piece is being removed from
   * @param pieceId id of the piece object being removed
   */
  public static void removePieceFromDraft(String userId, String draftId, String pieceId) throws Exception {
    Firestore db = getDb();

    DocumentReference draftDoc = db
        .collection("users")
        .document(userId)
        .collection("drafts")
        .document(draftId);

    DocumentSnapshot draftSnapshot = draftDoc.get().get();

    // remember that the thumbnails should reflect the piece content, so adjust those too
    if (draftSnapshot.exists()) {
      List<String> pieces = (List<String>) draftSnapshot.get("pieces");
      List<String> thumbnails = (List<String>) draftSnapshot.get("thumbnails");

      if (pieces != null && thumbnails != null) {
        int indexToRemove = pieces.indexOf(pieceId);
        if (indexToRemove != -1 && indexToRemove < thumbnails.size()) {
          thumbnails.remove(indexToRemove);
        }
      }

      Map<String, Object> draftUpdate = new HashMap<>();
      draftUpdate.put("pieces", FieldValue.arrayRemove(pieceId));
      draftUpdate.put("thumbnails", thumbnails);
      draftDoc.update(draftUpdate).get();
    }

    DocumentReference pieceDoc = db.collection("pieces").document(pieceId);
    Map<String, Object> pieceUpdate = new HashMap<>();
    pieceUpdate.put("usedInDrafts", FieldValue.arrayRemove(draftId));
    pieceDoc.update(pieceUpdate).get();

    DocumentSnapshot pieceSnapshot = pieceDoc.get().get();
    if (pieceSnapshot.exists()) {
      List<String> usedInDrafts = (List<String>) pieceSnapshot.get("usedInDrafts");
      if (usedInDrafts == null || usedInDrafts.isEmpty()) {
        pieceDoc.delete().get();
      }
    }
  }


  /**
   * Retrieves a Piece object from the Firestore "pieces" collection by its ID.
   *
   * @param pieceId the ID of the piece to retrieve
   * @return a Piece object if found, otherwise, returns null
   * @throws Exception if an error occurs during Firestore access
   */
  public static Piece getPieceById(String pieceId) throws Exception {
    Firestore db = getDb();
    DocumentSnapshot doc = db.collection("pieces").document(pieceId).get().get();

    if (!doc.exists())
      return null;

    // Build your Piece from the Firestore document
    Map<String, Object> data = doc.getData();

    if (data == null)
      return null;

    return new Piece(
        pieceId,
        (String) data.get("title"),
        ((Number) data.get("price")).doubleValue(),
        (String) data.get("sourceWebsite"),
        (String) data.get("url"),
        (String) data.get("imageUrl"),
        (String) data.get("size"),
        (String) data.get("color"),
        (String) data.get("condition"),
        new ArrayList<>((List<String>) data.get("tags")),
        (ArrayList<String>) data.get("usedInDrafts"));
  }

  /**
   * Retrieves the draft document for a specific user and draftId.
   *
   * @param userId the ID of the user who owns the draft
   * @param draftId the ID of the draft to retrieve
   * @return a map representing the draft's data fields if found, or an empty map if not found
   * @throws Exception if an error occurs while retrieving the document from Firestore
   */
  public static Map<String, Object> getDraftById(String userId, String draftId) throws Exception {
    Firestore db = getDb();
    DocumentReference draftDoc = db
        .collection("users")
        .document(userId)
        .collection("drafts")
        .document(draftId);

    DocumentSnapshot draftSnapshot = draftDoc.get().get();
    if (draftSnapshot.exists()) {
      System.out.println("Successfully retrieved draft " + draftId);
      System.out.println(draftSnapshot.getData().toString());
      return draftSnapshot.getData();
    } else {
      return new HashMap<>(); // or throw an exception if preferred
    }
  }

  /**
   * Retrieves the list of piece IDs associated with a specific draft.
   *
   * @param userId the ID of the user who owns the draft
   * @param draftId the ID of the draft to retrieve pieces from
   * @return a list of piece IDs from the "pieces" array field in the draft document; returns an empty list if none found
   * @throws Exception if an error occurs during Firestore access
   */
  public static List<String> getPiecesFromDraft(String userId, String draftId) throws Exception {
    Firestore db = getDb();
    DocumentReference draftDoc = db
        .collection("users")
        .document(userId)
        .collection("drafts")
        .document(draftId);

    DocumentSnapshot draftSnapshot = draftDoc.get().get();
    List<String> pieces = new ArrayList<>();
    if (draftSnapshot.exists()) {
      pieces = (List<String>) draftSnapshot.get("pieces");
    }

    return pieces;
  }


  /**
   * Saves a new Piece object to the Firestore "pieces" collection.
   *
   * @param piece the Piece object to be saved
   * @throws Exception if an error occurs during Firestore write operation
   */
  public static void savePiece(Piece piece) throws Exception {
    Firestore db = getDb();

    Map<String, Object> pieceData = new HashMap<>();
    pieceData.put("id", piece.getId());
    pieceData.put("title", piece.getTitle());
    pieceData.put("price", piece.getPrice());
    pieceData.put("sourceWebsite", piece.getSourceWebsite());
    pieceData.put("url", piece.getUrl());
    pieceData.put("size", piece.getSize());
    pieceData.put("color", piece.getColor());
    pieceData.put("condition", piece.getCondition());
    pieceData.put("imageUrl", piece.getImageUrl());
    pieceData.put("tags", new ArrayList<>(piece.getTags()));
    pieceData.put("usedInDrafts", new ArrayList<>());

    db.collection("pieces").document(piece.getId()).set(pieceData).get();
  }

  /**
   * Returns a reference to a specific document inside the "drafts" subcollection of a user.
   *
   * @param uid the ID of the user
   * @param docId the ID of the document within the user's "drafts" subcollection
   * @return a DocumentReference to the specified draft document
   * @throws IllegalArgumentException if either uid or docId is null
   */
  public static DocumentReference getUserDraftDoc(String uid, String docId) {
    if (uid == null || docId == null) {
      throw new IllegalArgumentException("getUserDraftDoc: uid, collectionId, and docId cannot be null");
    }

    return getDb()
        .collection("users")
        .document(uid)
        .collection("drafts")
        .document(docId);
  }

  @Override
  public List<Piece> getSavedPieces(String uid) {
    List<Piece> savedPieces = new ArrayList<>();
    Firestore db = FirestoreClient.getFirestore();

    try {
      DocumentReference userDocRef = db.collection("users").document(uid);
      DocumentSnapshot userSnapshot = userDocRef.get().get();

      if (!userSnapshot.exists()) {
        System.out.println("No Firestore document found for user ID: " + uid);
        return savedPieces; // return empty list
      }

      CollectionReference savedRef = userDocRef.collection("saved");
      ApiFuture<QuerySnapshot> future = savedRef.get();
      List<QueryDocumentSnapshot> documents = future.get().getDocuments();

      for (QueryDocumentSnapshot doc : documents) {
        Piece piece = doc.toObject(Piece.class);
        savedPieces.add(piece);
      }

      System.out.println("Fetched " + savedPieces.size() + " saved pieces for UID: " + uid);

    } catch (Exception e) {
      System.err.println("Error fetching saved pieces for UID: " + uid);
      e.printStackTrace();
    }

    return savedPieces;
  }

  @Override
  public List<Piece> getClickedPieces(String uid) {
    List<Piece> clickedPieces = new ArrayList<>();
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference clicksRef = db.collection("users").document(uid).collection("clicks");

    try {
      ApiFuture<QuerySnapshot> future = clicksRef.get();
      List<QueryDocumentSnapshot> documents = future.get().getDocuments();

      for (QueryDocumentSnapshot doc : documents) {
        String pieceId = doc.getString("pieceId");
        if (pieceId != null) {
          Piece piece = getPieceById(pieceId); // You need to implement this helper
          if (piece != null) {
            clickedPieces.add(piece);
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Error fetching clicked pieces for UID: " + uid);
      e.printStackTrace();
    }

    return clickedPieces;
  }

  @Override
  public List<Piece> getOnboardingResponses(String uid) {
    Firestore db = FirestoreClient.getFirestore();
    List<Piece> onboardingPieces = new ArrayList<>();
    try {
      if (!userExists(uid)) {
        System.out.println("No Firestore document found for user ID: " + uid);
        return onboardingPieces;
      }

      CollectionReference onboardingRef = db.collection("users").document(uid).collection("onboarding");
      ApiFuture<QuerySnapshot> future = onboardingRef.get();
      List<QueryDocumentSnapshot> documents = future.get().getDocuments();

      for (QueryDocumentSnapshot doc : documents) {
        Piece piece = doc.toObject(Piece.class);
        onboardingPieces.add(piece);
      }

      System.out.println("Fetched " + onboardingPieces.size() + " onboarding pieces for UID: " + uid);

    } catch (Exception e) {
      System.err.println("Error fetching onboarding responses for UID: " + uid);
      e.printStackTrace();
    }
    return onboardingPieces;
  }

  @Override
  public List<Piece> getGlobalPieces() {
    List<Piece> allPieces = new ArrayList<>();
    Firestore db = FirestoreClient.getFirestore();

    try {
      CollectionReference allRef = db.collection("pieces");
      ApiFuture<QuerySnapshot> future = allRef.get();
      List<QueryDocumentSnapshot> documents = future.get().getDocuments();

      for (QueryDocumentSnapshot doc : documents) {
        Piece piece = doc.toObject(Piece.class);
        allPieces.add(piece);
      }

      System.out.println("Fetched " + allPieces.size() + " total pieces.");

    } catch (Exception e) {
      System.err.println("Error fetching all pieces.");
      e.printStackTrace();
    }
    return allPieces;
  }

  public boolean userExists(String uid) {
    Firestore db = FirestoreClient.getFirestore();

    try {
      // Assuming each user has a document with their UID as the doc ID
      DocumentSnapshot snapshot = db.collection("users").document(uid).get().get();
      return snapshot.exists();
    } catch (Exception e) {
      e.printStackTrace();
      return false; // Fail safe — user doesn't exist if there's an error
    }
  }

  public void createUser(String uid) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference userRef = db.collection("users").document(uid);

    try {
      if (!userExists(uid)) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("createdAt", FieldValue.serverTimestamp());
        userRef.set(userData).get();
        System.out.println("Created user document for UID: " + uid);
      } else {
        System.out.println("User document already exists for UID: " + uid);
      }
    } catch (Exception e) {
      System.err.println("Failed to create user document for UID: " + uid);
      e.printStackTrace();
    }
  }

  public static void savePieceForUser(String uid, Piece piece) throws Exception {
    if (uid == null || piece == null) {
      throw new IllegalArgumentException("savePieceForUser: uid or piece cannot be null");
    }

    Firestore db = getDb();

    // Save under user’s “saved” subcollection
    DocumentReference savedRef = db.collection("users").document(uid)
        .collection("saved").document(piece.getId());

    Map<String, Object> data = new HashMap<>();
    data.put("id", piece.getId());
    data.put("title", piece.getTitle());
    data.put("price", piece.getPrice());
    data.put("sourceWebsite", piece.getSourceWebsite());
    data.put("url", piece.getUrl());
    data.put("size", piece.getSize());
    data.put("color", piece.getColor());
    data.put("condition", piece.getCondition());
    data.put("imageUrl", piece.getImageUrl());
    data.put("tags", new ArrayList<>(piece.getTags()));

    savedRef.set(data).get();

    // ensure it's also in the global `pieces` collection if not already
    DocumentReference globalPieceRef = db.collection("pieces").document(piece.getId());
    if (!globalPieceRef.get().get().exists()) {
      savePiece(piece); // reuses existing helper method
    }

    System.out.println("Saved piece " + piece.getId() + " for user " + uid);
  }

  public static void removePieceForUser(String uid, String pieceId) throws Exception {
    if (uid == null || pieceId == null) {
      throw new IllegalArgumentException("unsavePieceForUser: uid or pieceId cannot be null");
    }

    Firestore db = getDb();

    DocumentReference savedRef = db.collection("users").document(uid)
        .collection("saved").document(pieceId);

    savedRef.delete().get();

    System.out.println("Un-saved piece " + pieceId + " for user " + uid);
  }

  public boolean checkIfPieceUsedByUser(String uid, String pieceId) {
    Firestore db = FirestoreClient.getFirestore();

    CollectionReference draftsRef = db.collection("users").document(uid).collection("drafts");
    // Loop through each draft and check if piece is in it
    try {
      for (DocumentSnapshot draft : draftsRef.get().get().getDocuments()) {
        List<Map<String, Object>> pieces = (List<Map<String, Object>>) draft.get("pieces");
        if (pieces != null) {
          for (Map<String, Object> piece : pieces) {
            if (pieceId.equals(piece.get("id"))) {
              return true;
            }
          }
        }
      }
    } catch (InterruptedException | ExecutionException e) {
      System.err.println(
          "Encountered a problem checking if a search result has already been saved by a user: " + e.getMessage());
    }
    return false;
  }

  @Override
  public void logPieceClick(String userId, String pieceId, long timestamp) throws Exception {
    Firestore db = FirestoreClient.getFirestore();

    DocumentReference userRef = db.collection("users").document(userId);
    CollectionReference clicksRef = userRef.collection("clicks");

    Map<String, Object> clickData = new HashMap<>();
    clickData.put("pieceId", pieceId);
    clickData.put("timestamp", timestamp);

    clicksRef.add(clickData);
  }

}
