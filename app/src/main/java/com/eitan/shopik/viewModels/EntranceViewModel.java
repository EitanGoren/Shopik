package com.eitan.shopik.viewModels;

import android.app.Application;
import android.os.Build;

import androidx.annotation.Keep;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.Macros;
import com.eitan.shopik.items.RecyclerItem;
import com.eitan.shopik.items.ShoppingItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

@Keep
public class EntranceViewModel extends AndroidViewModel implements Serializable {

    private final MutableLiveData<ArrayList<RecyclerItem>> liked_items;
    private final MutableLiveData<ArrayList<RecyclerItem>> men_liked_items;
    private final MutableLiveData<ArrayList<RecyclerItem>> women_liked_items;

    private final MutableLiveData<ArrayList<ShoppingItem>> men_shoes_items;
    private final MutableLiveData<ArrayList<ShoppingItem>> women_shoes_items;

    private final MutableLiveData<ArrayList<ShoppingItem>> men_clothing_items;
    private final MutableLiveData<ArrayList<ShoppingItem>> women_clothing_items;

    private final MutableLiveData<Integer> current_shoes_item;
    private final MutableLiveData<Integer> current_clothing_item;


    public EntranceViewModel(Application application) {
        super(application);

        this.current_clothing_item = new MutableLiveData<>();
        this.current_shoes_item = new MutableLiveData<>();
        this.liked_items = new MutableLiveData<>();
        this.men_liked_items = new MutableLiveData<>();
        this.women_liked_items = new MutableLiveData<>();
        this.men_shoes_items = new MutableLiveData<>();
        this.women_shoes_items = new MutableLiveData<>();
        this.men_clothing_items = new MutableLiveData<>();
        this.women_clothing_items = new MutableLiveData<>();

        ArrayList<RecyclerItem> likes = new ArrayList<>();
        ArrayList<RecyclerItem> men_likes = new ArrayList<>();
        ArrayList<RecyclerItem> women_likes = new ArrayList<>();
        ArrayList<ShoppingItem> new_women_shoes = new ArrayList<>();
        ArrayList<ShoppingItem> new_men_shoes = new ArrayList<>();
        ArrayList<ShoppingItem> new_women_clothing = new ArrayList<>();
        ArrayList<ShoppingItem> new_men_clothing = new ArrayList<>();

        liked_items.setValue(likes);
        women_liked_items.setValue(women_likes);
        men_liked_items.setValue(men_likes);
        women_clothing_items.setValue(new_women_clothing);
        men_clothing_items.setValue(new_men_clothing);
        women_shoes_items.setValue(new_women_shoes);
        men_shoes_items.setValue(new_men_shoes);
    }

    public LiveData<ArrayList<RecyclerItem>> getRecentLikedItems(){
        return liked_items;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setNewLikedItems(MutableLiveData<ArrayList<RecyclerItem>> likedItems){
        clearLiked();
        Objects.requireNonNull(liked_items.getValue()).addAll(Objects.requireNonNull(likedItems.getValue()));
        ArrayList<RecyclerItem> temp = liked_items.getValue();
        temp.sort((o1, o2) -> Integer.parseInt(String.valueOf(o2.getLikes() - o1.getLikes())));
        liked_items.postValue(temp);
    }
    public void clearLiked(){
        Objects.requireNonNull(liked_items.getValue()).clear();
    }
    public void addMenLikedItem(RecyclerItem recyclerItem){
        Objects.requireNonNull(men_liked_items.getValue()).add(0, recyclerItem);
    }
    public void addWomenLikedItem(RecyclerItem recyclerItem){
        Objects.requireNonNull(women_liked_items.getValue()).add(0, recyclerItem);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setLiked_items(String gender){
        if(gender.equals(Macros.CustomerMacros.WOMEN))
            setNewLikedItems(this.women_liked_items);
        else
            setNewLikedItems(this.men_liked_items);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeAllType(String type,String gender){
        if(gender.equals(Macros.CustomerMacros.MEN)) {
            Objects.requireNonNull(men_liked_items.getValue()).removeIf(item -> item.getType().equals(type));
        }
        else{
            Objects.requireNonNull(women_liked_items.getValue()).removeIf(item -> item.getType().equals(type));
        }
    }

    public void addMenShoesItem(ShoppingItem shoppingItem){
        Objects.requireNonNull(men_shoes_items.getValue()).add(shoppingItem);
        men_shoes_items.postValue(men_shoes_items.getValue());
    }
    public void addWomenShoesItem(ShoppingItem shoppingItem){
        Objects.requireNonNull(women_shoes_items.getValue()).add(shoppingItem);
        women_shoes_items.postValue(women_shoes_items.getValue());
    }
    public LiveData<ArrayList<ShoppingItem>> getMen_shoes_items() {
        return men_shoes_items;
    }
    public LiveData<ArrayList<ShoppingItem>> getWomen_shoes_items() {
        return women_shoes_items;
    }

    public void addMenClothingItem(ShoppingItem shoppingItem){
        Objects.requireNonNull(men_clothing_items.getValue()).add(shoppingItem);
        men_clothing_items.postValue(men_clothing_items.getValue());
    }
    public void addWomenClothingItem(ShoppingItem shoppingItem){
        Objects.requireNonNull(women_clothing_items.getValue()).add(shoppingItem);
        women_clothing_items.postValue(women_clothing_items.getValue());
    }
    public LiveData<ArrayList<ShoppingItem>> getMen_clothing_items() {
        return men_clothing_items;
    }
    public LiveData<ArrayList<ShoppingItem>> getWomen_clothing_items() {
        return women_clothing_items;
    }

    public LiveData<Integer> getCurrentClothingItem() {
        return current_clothing_item;
    }
    public void setCurrentClothingItem(Integer integer){
        this.current_clothing_item.postValue(integer);
    }

    public LiveData<Integer> getCurrentShoesItem() {
        return current_shoes_item;
    }
    public void setCurrentShoesItem(Integer integer){
        this.current_shoes_item.postValue(integer);
    }

    public void clearAllShoesItems(){
        Objects.requireNonNull(this.women_shoes_items.getValue()).clear();
        Objects.requireNonNull(this.men_shoes_items.getValue()).clear();
    }
    public void clearAllClothingItems(){
        Objects.requireNonNull(this.women_clothing_items.getValue()).clear();
        Objects.requireNonNull(this.men_clothing_items.getValue()).clear();
    }
}
