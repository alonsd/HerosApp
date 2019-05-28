package com.example.superheros.Model;

import java.util.Arrays;

public class Hero {

    public String image;
    public String title;
    public String[] abilities;
    private boolean favorite;

    public Hero(String image, String title, String[] abilities) {
        this.image = image;
        this.title = title;
        this.abilities = abilities;
        favorite = false;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "image URL: " + image + "\n"
                + "title: " + title + "\n"
                + "abilities: " + Arrays.toString(abilities);
    }
}
