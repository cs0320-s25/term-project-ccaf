package edu.brown.cs.termproject.draft.Exceptions;

/**
 * Thrown when an error occurs while fetching data from an external API (e.g.,
 * eBay).
 */
public class APIFetchException extends Exception {
    public APIFetchException(String message) {
        super(message);
    }
} 