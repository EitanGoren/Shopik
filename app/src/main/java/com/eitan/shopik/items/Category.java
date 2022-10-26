package com.eitan.shopik.items;

import androidx.annotation.Keep;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
@Keep
public class Category implements Serializable {

    private final String main_header;
    private final String gender;
    private final String image;

    public Category(String main_header ,String gender, String image){
        this.main_header = main_header;
        this.image = image;
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public String getImage() {
        return image;
    }

    public String getMainHeader() {
        return main_header;
    }
}
