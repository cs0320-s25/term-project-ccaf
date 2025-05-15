package edu.brown.cs.termproject.draft.Exceptions;

/**
 * Thrown when the recommendation system encounters an error.
 */
public class RecommenderException extends Exception {
    public RecommenderException(String message) {
        super(message);
    }
}