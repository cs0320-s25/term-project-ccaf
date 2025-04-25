package edu.brown.cs.termproject.draft;

import java.util.HashSet;
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

}