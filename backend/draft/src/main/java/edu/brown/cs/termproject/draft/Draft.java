package edu.brown.cs.termproject.draft;

import java.util.*;

import edu.brown.cs.termproject.draft.Exceptions.DraftException;

public class Draft {
  private String id;
  private String name;
  private Set<Piece> pieces = new HashSet<>();

  public Draft(String id, String name) throws DraftException {
    if (id == null || name == null || name.isBlank()) {
      throw new DraftException("Draft must have a valid ID and non-empty name.");
    }
    this.id = id;
    this.name = name;
  }

  public void addPiece(Piece piece) throws DraftException {
    if (piece == null) {
      throw new DraftException("Cannot add null piece to draft.");
    }
    pieces.add(piece);
  }

  public Set<Piece> getPieces() {
    return Collections.unmodifiableSet(pieces); // ensures only a draft can modify the set of pieces (encapsulation)
  }
}

