package edu.brown.cs.termproject.draft.Utilities;

import edu.brown.cs.termproject.draft.Piece;
import java.util.ArrayList;
import java.util.List;

public class APIUtilitiesMock extends APIUtilities {
    private static boolean installed = false;

    public static void install() {
        if (installed)
            return;
        installed = true;
    }

    public static List<Piece> fetchFromEbay(String query) {
        List<Piece> pieces = new ArrayList<>();
        // add a fake piece that matches test queries
        if (query.contains("nike")) {
            pieces.add(new Piece("ebay_1", "Nike Running Shoes", 60.0, "ebay",
                    "http://ebay.com/1", "http://image.url/1", "10", "Black", "Used",
                    List.of("nike", "shoes", "running"), new ArrayList<>()));
        }
        return pieces;
    }
}
