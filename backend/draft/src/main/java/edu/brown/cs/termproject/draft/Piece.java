package edu.brown.cs.termproject.draft;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Piece {

private String id;
private String title;
private double price;
private String sourceWebsite;
private String url; 
private String size;
private String color;
private String condition;
private String imageUrl;
private Set<String> tags;


    public Piece(
        String id, 
        String title, 
        double price, 
        String sourceWebsite, 
        String url, 
        String size, 
        String color, 
        String condition, 
        String imageUrl, 
        Set<String> tags)
         {
        this.id = id;
        this.title = title;
        this.price = price;
        this.sourceWebsite = sourceWebsite;
        this.url = url;
        this.size = size;
        this.color = color;
        this.condition = condition;
        this.imageUrl = imageUrl;
        this.tags = tags;
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