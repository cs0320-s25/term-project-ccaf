package edu.brown.cs.termproject.draft;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;

import edu.brown.cs.termproject.draft.Exceptions.*;

public class RecommendationTest {
    private List<Piece> allPieces;
    private Set<String> alreadySaved;

     @BeforeEach
    public void setUp() {
        allPieces = new ArrayList<>();
        alreadySaved = new HashSet<>();
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
     public void testDraftBasedRecommendations() throws DraftException, PaletteException {

         // set up the draft with pieces including the keywords "y2k", "mesh", and "butterfly"
         Piece draft1 = new Piece("7", "Y2K Mesh Top", 25.0, "eBay", "url7",
                 "img7", "S", "Purple", "Great", List.of("y2k", "mesh"), new ArrayList<>());
         Piece draft2 = new Piece("8", "Butterfly Crop Top", 30.0, "eBay", "url8",
                 "img8", "S", "Blue", "Great", List.of("butterfly", "y2k"), new ArrayList<>());

         // piece A is mesh + y2k, relevant to the draft
         Piece pieceA = new Piece("9", "Mesh Y2K Skirt", 28.0, "eBay", "url9",
                 "img9", "S", "Black", "Great", List.of("mesh", "y2k"), new ArrayList<>());
        
         // piece B is denim + vintage, unrelated to the draft
         Piece pieceB = new Piece("10", "Vintage Jeans", 40.0, "eBay", "url10",
                 "img10", "M", "Blue", "Good", List.of("vintage", "denim"), new ArrayList<>());

         // piece C is directly related to the draft
         Piece pieceC = new Piece("11", "Butterfly Hair Clip", 10.0, "eBay", "url11",
                 "img11", "One Size", "Pink", "New", List.of("butterfly"), new ArrayList<>());

         List<Piece> draft = List.of(draft1, draft2);
         allPieces.addAll(List.of(pieceA, pieceB, pieceC));

         List<Piece> recommendations = RecommendationCreator.recommendForDraft(draft, allPieces, alreadySaved, 3);
         // check that piece A was first since it had the strongest overlap (mesh + y2k)
         assertEquals("9", recommendations.get(0).getId());
        
         // check that piece B, which had one keyword overlap, makes second place
         assertEquals("11", recommendations.get(1).getId());
     }

}
