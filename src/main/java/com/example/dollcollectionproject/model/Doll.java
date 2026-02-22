package com.example.dollcollectionproject.model;

public class Doll {
    // VARIABLES
    private String name;
    private int year;
    private String brand;
    private String imagePath;
    // Extra variables that will be added by user
    private String description;    // user will add and delete it manually

    // CONSTRUCTOR: How we create a new Doll
    public Doll(String name, int year, String brand, String imagePath) {
        this.name = name;
        this.year = year;
        this.brand = brand;
        this.imagePath = imagePath;
        this.description = "";    // empty by default
    }

    // GETTERS: Used by JavaFX to display the data
    public String getName() { return name; }
    public int getYear() { return year; }
    public String getBrand() { return brand; }
    public String getImagePath() { return imagePath; }
    public String getDescription() { return description; }

    // SETTER: Allows the user to edit the description
    public void setDescription(String description) {
        this.description = description;
    }

    // DELETER: Clears the description
    public void deleteDescription() {
        this.description = "";
    }

}
