package edu.brown.cs.termproject.draft;

import java.util.*;

import edu.brown.cs.termproject.draft.Exceptions.DraftException;

/**
 * Represents a curated Draft, which is a collection of user-selected Pieces
 * with a name and unique identifier. A Draft allows for adding pieces and
 * retrieving the complete set of saved pieces in a read-only fashion.
 */
public class Draft {
  private String id;
  private String name;
  private Set<Piece> pieces = new HashSet<>();

  /**
   * Constructs a Draft object with a specified ID and name.
   *
   * @param id   the unique identifier for the draft
   * @param name the name of the draft
   * @throws DraftException if the ID or name is null or the name is blank
   */
  public Draft(String id, String name) throws DraftException {
    if (id == null || name == null || name.isBlank()) {
      throw new DraftException("Draft must have a valid ID and non-empty name.");
    }
    this.id = id;
    this.name = name;
  }

  /**
   * Adds a piece to the draft.
   *
   * @param piece the Piece object to add
   * @throws DraftException if the piece is null
   */
  public void addPiece(Piece piece) throws DraftException {
    if (piece == null) {
      throw new DraftException("Cannot add null piece to draft.");
    }
    pieces.add(piece);
  }

  /**
   * Retrieves an unmodifiable view of the pieces in the draft.
   *
   * @return an unmodifiable set of pieces
   */
  public Set<Piece> getPieces() {
    return Collections.unmodifiableSet(pieces); // ensures only a draft can modify the set of pieces (encapsulation)
  }
}

