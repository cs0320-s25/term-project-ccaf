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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirebaseUtilities implements StorageInterface {

  public FirebaseUtilities() throws IOException {
    // TODO: FIRESTORE PART 0:
    // Create /resources/ folder with firebase_config.json and
    // add your admin SDK from Firebase. see:
    // https://docs.google.com/document/d/10HuDtBWjkUoCaVj_A53IFm5torB_ws06fW3KYFZqKjc/edit?usp=sharing
    String workingDirectory = System.getProperty("user.dir");
    Path firebaseConfigPath =
        Paths.get(workingDirectory, "src", "main", "resources", "firebase_config.json");

    // ^-- if your /resources/firebase_config.json exists but is not found,
    // try printing workingDirectory and messing around with this path.
    System.out.println(firebaseConfigPath);
    FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toString());

    FirebaseOptions options =
        new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

    FirebaseApp.initializeApp(options);
  }

  @Override
  public List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException, IllegalArgumentException {
    if (uid == null || collection_id == null) {
      throw new IllegalArgumentException("getCollection: uid and/or collection_id cannot be null");
    }

    // gets all documents in the collection 'collection_id' for user 'uid'

    Firestore db = FirestoreClient.getFirestore();
    // 1: Make the data payload to add to your collection
    CollectionReference dataRef = db.collection("users").document(uid).collection(collection_id);

    // 2: Get pin documents
    QuerySnapshot dataQuery = dataRef.get().get();

    // 3: Get data from document queries
    List<Map<String, Object>> data = new ArrayList<>();
    for (QueryDocumentSnapshot doc : dataQuery.getDocuments()) {
      data.add(doc.getData());
    }

    return data;
  }

  @Override
  /**
   * Adds a new document "doc_id" to collection "collection_id" for user "uid" with data payload
   * "data". Note, works best when the server is run through IntelliJ.
   */
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

    // retrieves a reference to the "words" collection created in AddWordHandler
    // path: users/userID/words
    CollectionReference dataRef = db.collection("users").document(uid).collection(collection_id);

    // creates a reference to the document to be made
    // path: users/userID/words/wordID
    DocumentReference docRef = dataRef.document(doc_id);

    try {
      System.out.println("Attempting to write data: " + data.toString());
      // adds `data` to the document. at this point, the doc is created.
      // if the doc already exists, its content is overwritten with `data`
      docRef.set(data).get();
      System.out.println("Successfully added document with ID: " + doc_id);
    } catch (Exception e) {
      System.err.println("Error writing document: " + e.getMessage());
    }
  }

  public void deleteDocument(String uid, String collection_id, String doc_id) throws InterruptedException, ExecutionException {
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
    deleteFuture.get();  // Wait for the deletion to complete

    System.out.println("Successfully deleted document with ID: " + doc_id);
  }


  private void deleteDocumentHelper(DocumentReference doc) {
    // for each subcollection, run deleteCollection()
    Iterable<CollectionReference> collections = doc.listCollections();
    for (CollectionReference collection : collections) {
      System.out.println("Successfully deleted collection: " + collection.getId());
      deleteCollection(collection);
    }
    // then delete the document
    doc.delete();
  }

  // recursively removes all the documents and collections inside a collection
  // https://firebase.google.com/docs/firestore/manage-data/delete-data#collections
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

      // NOTE: the query to documents may be arbitrarily large. A more robust
      // solution would involve batching the collection.get() call.
    } catch (Exception e) {
      System.err.println("Error deleting collection : " + e.getMessage());
    }
  }


  public static Firestore getDb() {
    return FirestoreClient.getFirestore();
  }

  public static void savePieceToDraft(String userId, String draftId, Piece piece) throws Exception {
    Firestore db = getDb();
    DocumentReference draftDoc = db
        .collection("users")
        .document(userId)
        .collection("drafts")
        .document(draftId);

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

    Map<String, Object> update = new HashMap<>();
    update.put("pieces", FieldValue.arrayUnion(pieceData));

    draftDoc.update(update).get(); // wait for the write to complete
  }

  public static Piece getPieceById(String pieceId) throws Exception {
    Firestore db = getDb();
    DocumentSnapshot doc = db.collection("pieces").document(pieceId).get().get();

    if (!doc.exists()) return null;

    // Build your Piece from the Firestore document
    Map<String, Object> data = doc.getData();

    if (data == null) return null;

    return new Piece(
        pieceId,
        (String) data.get("title"),
        ((Number) data.get("price")).doubleValue(),
        (String) data.get("sourceWebsite"),
        (String) data.get("url"),
        (String) data.get("size"),
        (String) data.get("color"),
        (String) data.get("condition"),
        (String) data.get("imageUrl"),
        new HashSet<>((List<String>) data.get("tags"))
    );
  }

  // for testing
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

    db.collection("pieces").document(piece.getId()).set(pieceData).get();
  }


}
