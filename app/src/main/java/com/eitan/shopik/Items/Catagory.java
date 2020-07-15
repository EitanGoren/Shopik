package com.eitan.shopik.Items;

import java.io.Serializable;
import java.util.ArrayList;

public class Catagory implements Serializable {

    private String name,gender;
    private ArrayList<SubCategory> subCategories;

    public Catagory(String name ,String gender,ArrayList<SubCategory> subCategories){
        this.name = name;
        this.gender = gender;
        this.subCategories = subCategories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ArrayList<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public SubCategory getSubCategory(int position){
        if(subCategories != null){
            if( position >= 0 && position <= getSubCategoriesSize())
                return subCategories.get(position);
        }
        return null;
    }

    public void addSubCategory(SubCategory subCategory){
        subCategories.add(subCategory);
    }

    public int getSubCategoriesSize(){
        return subCategories != null ? subCategories.size() : 0;
    }
}
