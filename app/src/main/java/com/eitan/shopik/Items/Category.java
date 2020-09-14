package com.eitan.shopik.Items;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.ArrayList;
@Keep
public class Category implements Serializable {

    private String name,gender;
    private ArrayList<RecyclerItem> recyclerItems;

    public Category(String name ,String gender,ArrayList<RecyclerItem> recyclerItems ){
        this.name = name;
        this.gender = gender;
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

    public ArrayList<RecyclerItem> getRecyclerItems() {
        return recyclerItems;
    }
    public void setRecyclerItems(ArrayList<RecyclerItem> recyclerItems) {
        this.recyclerItems = recyclerItems;
    }
    public RecyclerItem getRecyclerItem(int position){
        if(recyclerItems != null){
            if( position >= 0 && position <= getRecyclerItemsSize())
                return recyclerItems.get(position);
        }
        return null;
    }
    public void addRecyclerItem (RecyclerItem recyclerItem){
        recyclerItems.add(recyclerItem);
    }
    public int getRecyclerItemsSize(){
        return recyclerItems != null ? recyclerItems.size() : 0;
    }
}
