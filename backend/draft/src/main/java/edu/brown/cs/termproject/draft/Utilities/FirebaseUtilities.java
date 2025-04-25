//package edu.brown.cs.termproject.draft.Utilities;
//
//import com.google.api.core.ApiFuture;
//import com.google.cloud.firestore.*;
//import com.google.firebase.cloud.FirestoreClient;
//import edu.brown.cs.termproject.draft.Piece;
//import java.util.*;
//
//public class FirebaseUtilities {
//
//  public static Firestore getDb() {
//    return FirestoreClient.getFirestore();
//  }
//
//  public static void savePieceToDraft(String draftId, Piece piece) throws Exception {
//    Firestore db = getDb();
//    CollectionReference drafts = db.collection("drafts");
//
//    // save piece under a subcollection in the draft
//    DocumentReference draftDoc = drafts.document(draftId);
//    CollectionReference pieces = draftDoc.collection("pieces");
//
//    Map<String, Object> pieceData = new HashMap<>();
//    pieceData.put("id", piece.getId());
//    pieceData.put("title", piece.getTitle());
//    pieceData.put("price", piece.getPrice());
//    pieceData.put("sourceWebsite", piece.getSourceWebsite());
//    pieceData.put("url", piece.getUrl());
//    pieceData.put("size", piece.getSize());
//    pieceData.put("color", piece.getColor());
//    pieceData.put("condition", piece.getCondition());
//    pieceData.put("imageUrl", piece.getImageUrl());
//    pieceData.put("tags", piece.getTags());
//
//    // Save with the piece's ID as the Firestore doc ID
//    ApiFuture<WriteResult> future = pieces.document(piece.getId()).set(pieceData);
//    future.get(); // wait for the write to complete
//  }
//
//  public static Piece getPieceById(String pieceId) throws Exception {
//    Firestore db = getDb();
//    DocumentSnapshot doc = db.collection("pieces").document(pieceId).get().get();
//
//    if (!doc.exists()) return null;
//
//    // Build your Piece from the Firestore document
//    Map<String, Object> data = doc.getData();
//
//    if (data == null) return null;
//
//    return new Piece(
//        pieceId,
//        (String) data.get("title"),
//        ((Number) data.get("price")).doubleValue(),
//        (String) data.get("sourceWebsite"),
//        (String) data.get("url"),
//        (String) data.get("size"),
//        (String) data.get("color"),
//        (String) data.get("condition"),
//        (String) data.get("imageUrl"),
//        new HashSet<>((List<String>) data.get("tags"))
//    );
//  }
//
//  public static void savePiece(Piece piece) throws Exception {
//    Firestore db = getDb();
//
//    Map<String, Object> pieceData = new HashMap<>();
//    pieceData.put("id", piece.getId());
//    pieceData.put("title", piece.getTitle());
//    pieceData.put("price", piece.getPrice());
//    pieceData.put("sourceWebsite", piece.getSourceWebsite());
//    pieceData.put("url", piece.getUrl());
//    pieceData.put("size", piece.getSize());
//    pieceData.put("color", piece.getColor());
//    pieceData.put("condition", piece.getCondition());
//    pieceData.put("imageUrl", piece.getImageUrl());
//    pieceData.put("tags", new ArrayList<>(piece.getTags()));
//
//    db.collection("pieces").document(piece.getId()).set(pieceData).get();
//  }
//
//
//}
