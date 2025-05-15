package edu.brown.cs.termproject.draft.Exceptions;

/**
 * Thrown when a database-related operation fails (e.g., Firestore read/write
 * issues).
 */
public class DatabaseOperationException extends Exception {
    public DatabaseOperationException(String message) {
        super(message);
    }
}
