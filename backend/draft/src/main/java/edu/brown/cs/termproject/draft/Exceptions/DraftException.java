package edu.brown.cs.termproject.draft.Exceptions;

/**
 * Thrown when there is an error specific to draft-related operations.
 */
public class DraftException extends Exception{
    public DraftException(String message) {
        super(message);
    }
}
