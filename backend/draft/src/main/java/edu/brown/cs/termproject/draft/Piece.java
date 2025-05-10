package edu.brown.cs.termproject.draft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Piece {

private String id;
private String title;
private double price;
private String sourceWebsite;
private String url;
private String imageUrl;
private String size;
private String color;
private String condition;
private Set<String> tags;


    public Piece(String id, String title, double price, String sourceWebsite, String url,
        String imageUrl, String size, String color, String condition, Set<String> tags) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.sourceWebsite = sourceWebsite;
        this.url = url;
        this.imageUrl = imageUrl;
        this.size = size;
        this.color = color;
        this.condition = condition;
        this.tags = tags;
    }

    public static Piece fromMap(Map<String, Object> data) {
    try {
        String id = (String) data.get("id");
        String title = (String) data.get("title");
        double price = ((Number) data.get("price")).doubleValue();
        String sourceWebsite = (String) data.get("sourceWebsite");
        String url = (String) data.get("url");
        String imageUrl = (String) data.get("imageUrl");
        String size = (String) data.get("size");
        String color = (String) data.get("color");
        String condition = (String) data.get("condition");

        // optional field: tags
        Set<String> tags = new HashSet<>();
        Object rawTags = data.get("tags");
        if (rawTags instanceof List<?>) {
        for (Object tag : (List<?>) rawTags) {
            if (tag instanceof String) {
            tags.add((String) tag);
            }
        }
        }

        return new Piece(id, title, price, sourceWebsite, url, imageUrl, size, color, condition, tags);

    } catch (Exception e) {
        System.err.println("Error parsing Piece from map: " + e.getMessage());
        return null;
    }
    }

    // Converts a Piece object into a Firestore-compatible map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("price", price);
        map.put("sourceWebsite", sourceWebsite);
        map.put("url", url);
        map.put("imageUrl", imageUrl);
        map.put("size", size);
        map.put("color", color);
        map.put("condition", condition);
        map.put("tags", new ArrayList<>(tags)); // Firestore expects a list
        return map;
    }


    public Set<String> getTags() {
        return this.tags;
    }
    public String getId() { return id; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getSourceWebsite() { return sourceWebsite; }
    public String getUrl() { return url; }
    public String getSize() { return size; }
    public String getColor() { return color; }
    public String getCondition() { return condition; }
    
}