package edu.brown.cs.termproject.draft;

import java.util.*;

public class Draft {
  private String id;
  private String name;
  private Set<Piece> pieces = new HashSet<>();

  public void addPiece(Piece piece) {
    pieces.add(piece);
  }

  public Set<Piece> getPieces() {
    return pieces;
  }
}

