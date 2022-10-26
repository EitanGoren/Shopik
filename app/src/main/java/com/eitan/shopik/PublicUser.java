package com.eitan.shopik;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class PublicUser implements Serializable {

    private final String profile_image;
    private String name;
    private String gender;
    private String id;
    private boolean isFavorite;

    public PublicUser(String profile_image, String name, String gender, String id){
        this.profile_image = profile_image;
        this.name = name;
        this.gender = gender;
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getGender() {
        return gender;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getProfile_image() {
        return profile_image;
    }
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    public boolean isFavorite() {
        return isFavorite;
    }
}
