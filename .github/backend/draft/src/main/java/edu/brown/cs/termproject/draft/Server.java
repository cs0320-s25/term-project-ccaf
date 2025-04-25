package draft.src.main.java.edu.brown.cs.termproject.draft;

import static spark.Spark.*;
import com.google.gson.Gson;

public class Server {
    public static void main(String[] args) {
        port(8080);

        ListingAggregator aggregator = new ListingAggregator();

//        get("/search", (req, res) -> {
//            String keyword = req.queryParams("q");
//            res.type("application/json");
//            return new Gson().toJson(aggregator.search(keyword));
//        });
    }
}
