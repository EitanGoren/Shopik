package com.eitan.shopik;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class LikedUser implements Serializable {

    private String profile_image;
    private String first_name;
    private String last_name;
    private boolean isFavorite;

    public LikedUser(String profile_image, String first_name, String last_name){
        this.profile_image = profile_image;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getProfile_image() {
        return profile_image;
    }
    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    public boolean isFavorite() {
        return isFavorite;
    }
}
