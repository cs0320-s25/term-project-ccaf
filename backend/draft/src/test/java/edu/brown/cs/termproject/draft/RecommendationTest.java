package edu.brown.cs.termproject.draft;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;

import edu.brown.cs.termproject.draft.Exceptions.*;

public class RecommendationTest {
    private List<Piece> allPieces;
    private Set<String> alreadySaved;
    private Map<String, Double> clickWeights;
    private RecommendationCreator creator;

     @BeforeEach
    public void setUp() {
        allPieces = new ArrayList<>();
        alreadySaved = new HashSet<>();
        clickWeights = new HashMap<>();
        creator = new RecommendationCreator();
    }

    @Test
    public void testBasicAccuracy() throws DraftException, PaletteException {
        Piece denimPiece = new Piece("1", "Denim Jacket", 50.0, "eBay", "url1",
                "img1", "M", "Blue", "Good", List.of("denim"), new ArrayList<>());
        Piece floralPiece = new Piece("2", "Floral Dress", 40.0, "eBay", "url2",
                "img2", "S", "Pink", "Good", List.of("floral"), new ArrayList<>());

        allPieces.add(denimPiece);
        allPieces.add(floralPiece);

        Map<String, Double> palette = PaletteCreator.createPalette(
                List.of(denimPiece), // saved
                List.of(), // onboarding
                List.of(floralPiece) // clicked
        );

        List<Piece> recommendations = RecommendationCreator.recommendPieces(allPieces, palette, alreadySaved, 2);

        // make sure there are two clothing items in the recommendations
        assertTrue(recommendations.size()==2);

        // check that the denim piece shows up before the floral piece since it was weighted higher
        assertEquals("1", recommendations.get(0).getId());
    }

    @Test
    public void testNoMatchingTags() throws DraftException {
        Map<String, Double> palette = Map.of("leather", 1.0);

        // create a piece without matching terms to the palette
        Piece satinPiece = new Piece("3", "Satin Blouse", 35.0, "eBay", "url3",
                "img3", "M", "Red", "Good", List.of("satin"), new ArrayList<>());

        allPieces.add(satinPiece);

        List<Piece> recommendations = RecommendationCreator.recommendPieces(allPieces, palette, alreadySaved, 1);

        // make sure that the recommendation does not have leather in it since there are no pieces with leather
        System.out.println(recommendations.get(0));
        assertTrue(recommendations.get(0).getTags().contains("leather") == false);
    }

    @Test
    public void testSimilarityRanking() throws DraftException {
        Map<String, Double> palette = Map.of(
                "vintage", 1.0,
                "leather", 1.0,
                "brown", 0.5);

        // piece A matches "vintage", "leather", and "brown" (highest ranking)
        Piece pieceA = new Piece("4", "Brown Vintage Leather Jacket", 70.0, "eBay", "url4",
                "img4", "L", "Brown", "Good", List.of("vintage", "leather", "brown"), new ArrayList<>());

        // piece B matches "vintage" and "leather"
        Piece pieceB = new Piece("5", "Vintage Leather Bag", 60.0, "eBay", "url5",
                "img5", "M", "Black", "Good", List.of("vintage", "leather"), new ArrayList<>());

        // piece C only matches "leather"
        Piece pieceC = new Piece("6", "Leather Belt", 20.0, "eBay", "url6",
                "img6", "S", "Brown", "Good", List.of("leather"), new ArrayList<>());

        allPieces.addAll(List.of(pieceA, pieceB, pieceC));

        List<Piece> recommendations = RecommendationCreator.recommendPieces(allPieces, palette, alreadySaved, 3);

        // make sure the recommendations contains the three specified pieces, and in the
        // correct order based on weights
        assertEquals("4", recommendations.get(0).getId());
        assertEquals("5", recommendations.get(1).getId());
        assertEquals("6", recommendations.get(2).getId());
    }

    @Test
    public void testExcludesAlreadySaved() throws DraftException {
            Piece savedPiece = new Piece("7", "Saved Skirt", 30.0, "Depop", "url7", "img7", "M", "Black", "Good",
                            List.of("skirt"), new ArrayList<>());
            Piece candidatePiece = new Piece("8", "Candidate Jeans", 45.0, "eBay", "url8", "img8", "L", "Blue", "Good",
                            List.of("jeans"), new ArrayList<>());

            allPieces.add(savedPiece);
            allPieces.add(candidatePiece);
            alreadySaved.add("7");

            Map<String, Double> palette = Map.of("skirt", 1.0, "jeans", 0.5);

            List<Piece> recommendations = RecommendationCreator.recommendPieces(allPieces, palette, alreadySaved, 2);

            // The saved piece should not be recommended
            for (Piece p : recommendations) {
                    assertNotEquals("7", p.getId(), "Saved piece was recommended");
            }
    }

    @Test
    public void testExcludeAlreadyClickedPieces() throws DraftException {
            Piece clickedPiece = new Piece("13", "Clicked Hoodie", 55.0, "eBay", "url13", "img13", "L", "Green", "Good",
                            List.of("hoodie"), new ArrayList<>());
            Piece newPiece = new Piece("14", "New Scarf", 15.0, "eBay", "url14", "img14", "S", "Red", "Good",
                            List.of("scarf"), new ArrayList<>());

            allPieces.add(clickedPiece);
            allPieces.add(newPiece);

            // Instead of clickWeights, simulate exclusion by adding clicked piece ID to
            // excluded set
            alreadySaved.add("13");

            Map<String, Double> palette = Map.of("hoodie", 1.0, "scarf", 0.5);

            List<Piece> recommendations = RecommendationCreator.recommendPieces(allPieces, palette, alreadySaved, 2);

            for (Piece p : recommendations) {
                    assertNotEquals("13", p.getId(), "Clicked piece was recommended but should be excluded");
            }
            assertTrue(recommendations.stream().anyMatch(p -> p.getId().equals("14")),
                            "New piece should be recommended");
    }

    @Test
    public void testEmptyPaletteStillReturnsPieces() throws DraftException {
            Piece simplePiece = new Piece("10", "Simple Tee", 10.0, "eBay", "url10", "img10", "M", "White", "Good",
                            List.of("tee", "white"), new ArrayList<>());

            allPieces.add(simplePiece);

            Map<String, Double> emptyPalette = new HashMap<>();

            List<Piece> recommendations = RecommendationCreator.recommendPieces(allPieces, emptyPalette, alreadySaved,
                            1);

            assertEquals(1, recommendations.size());
            assertEquals("10", recommendations.get(0).getId());
    }

}
