package edu.brown.cs.termproject.draft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.brown.cs.termproject.draft.Handlers.SearchHandler;
import edu.brown.cs.termproject.draft.Utilities.APIUtilitiesMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SearchTest {

    private SearchHandler handler;

    // simple fake Request and response for testing
    private static class FakeRequest extends Request {
        private final Map<String, String> queryParams;

        public FakeRequest(Map<String, String> queryParams) {
            this.queryParams = queryParams;
        }

        @Override
        public String queryParams(String key) {
            return queryParams.get(key);
        }
    }

    private static class FakeResponse extends Response {
        private int status;
        private String type;

        @Override
        public void status(int statusCode) {
            this.status = statusCode;
        }

        @Override
        public void type(String contentType) {
            this.type = contentType;
        }

        public int getStatus() {
            return status;
        }

        public String getType() {
            return type;
        }
    }

    @BeforeEach
    public void setUp() {
        JsonObject poshmarkMock = SearchHandler.createPoshmarkMockData("poshmark");
        JsonObject depopMock = SearchHandler.createDepopMockData("depop");

        handler = new SearchHandler(poshmarkMock, depopMock);

        // override APIUtilities.fetchFromEbay for testing purposes
        APIUtilitiesMock.install();
    }

    @Test
    public void testEmptyQuery() throws Exception {
        FakeRequest req = new FakeRequest(Map.of("q", ""));
        FakeResponse res = new FakeResponse();

        Object result = handler.handle(req, res);

        assertEquals(400, res.getStatus());
        assertEquals("application/json", res.getType());
        String json = (String) result;
        assertTrue(json.contains("No query inputted"));
    }

    @Test
    public void testValidQuery() throws Exception {
        FakeRequest req = new FakeRequest(Map.of("q", "nike"));
        FakeResponse res = new FakeResponse();

        Object result = handler.handle(req, res);

        assertEquals("application/json", res.getType());
        String json = (String) result;
        assertTrue(json.contains("matches"));

        // basic check that some results are returned
        JsonObject parsed = JsonParser.parseString(json).getAsJsonObject();
        assertTrue(parsed.has("matches"));
        assertTrue(parsed.getAsJsonArray("matches").size() > 0);
    }

}