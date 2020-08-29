package com.eitan.shopik.Items;

import java.io.Serializable;
import java.util.ArrayList;

public class Catagory implements Serializable {

    private String name,gender;
  //  private ArrayList<SubCategory> subCategories;
    private ArrayList<RecyclerItem> recyclerItems;

    public Catagory(String name ,String gender,ArrayList<RecyclerItem> recyclerItems ){//ArrayList<SubCategory> subCategories){
        this.name = name;
        this.gender = gender;
     //   this.subCategories = subCategories;
        this.recyclerItems = recyclerItems;

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

  /*  public ArrayList<SubCategory> getSubCategories() {
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

   */

    public ArrayList<RecyclerItem> getRecyclerItems() {
        return recyclerItems;
    }
    public void setRecyclerItems(ArrayList<RecyclerItem> recyclerItems) {
        this.recyclerItems = recyclerItems;
    }
    public RecyclerItem getRecyclerItem(int position){
        if(recyclerItems != null){
            if( position >= 0 && position <= getRexyclerItemsSize())
                return recyclerItems.get(position);
        }
        return null;
    }
    public void addRecyclerItem (RecyclerItem recyclerItem){
        recyclerItems.add(recyclerItem);
    }
    public int getRexyclerItemsSize(){
        return recyclerItems != null ? recyclerItems.size() : 0;
    }
}
