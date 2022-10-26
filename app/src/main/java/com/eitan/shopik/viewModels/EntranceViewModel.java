package com.eitan.shopik.viewModels;

import android.app.Application;

import androidx.annotation.Keep;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.Macros;
import com.eitan.shopik.items.RecyclerItem;
import com.eitan.shopik.database.models.ShoppingItem;

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

    private final MutableLiveData<ArrayList<ShoppingItem>> men_asos_items;
    private final MutableLiveData<ArrayList<ShoppingItem>> women_asos_items;

    private final MutableLiveData<ArrayList<ShoppingItem>> men_shein_items;
    private final MutableLiveData<ArrayList<ShoppingItem>> women_shein_items;

    private final MutableLiveData<ArrayList<ShoppingItem>> men_renuar_items;
    private final MutableLiveData<ArrayList<ShoppingItem>> women_renuar_items;

    private final MutableLiveData<ArrayList<ShoppingItem>> men_tx_items;
    private final MutableLiveData<ArrayList<ShoppingItem>> women_tx_items;

    private final MutableLiveData<ArrayList<ShoppingItem>> men_hoodies_items;
    private final MutableLiveData<ArrayList<ShoppingItem>> women_hoodies_items;

    private final MutableLiveData<ArrayList<ShoppingItem>> men_tfs_items;
    private final MutableLiveData<ArrayList<ShoppingItem>> women_tfs_items;

    private final MutableLiveData<Integer> current_shoes_item;
    private final MutableLiveData<Integer> current_clothing_item;
    private final MutableLiveData<Integer> current_asos_item;
    private final MutableLiveData<Integer> current_shein_item;
    private final MutableLiveData<Integer> current_tx_item;
    private final MutableLiveData<Integer> current_renuar_item;
    private final MutableLiveData<Integer> current_tfs_item;
    private final MutableLiveData<Integer> current_hoodies_item;

    public EntranceViewModel(Application application) {
        super(application);

        this.current_clothing_item = new MutableLiveData<>();
        this.current_shoes_item = new MutableLiveData<>();
        this.current_renuar_item = new MutableLiveData<>();
        this.current_tx_item = new MutableLiveData<>();
        this.current_shein_item = new MutableLiveData<>();
        this.current_asos_item = new MutableLiveData<>();
        this.current_tfs_item = new MutableLiveData<>();
        this.current_hoodies_item = new MutableLiveData<>();
        this.liked_items = new MutableLiveData<>();
        this.men_liked_items = new MutableLiveData<>();
        this.women_liked_items = new MutableLiveData<>();
        this.men_shoes_items = new MutableLiveData<>();
        this.women_shoes_items = new MutableLiveData<>();
        this.men_clothing_items = new MutableLiveData<>();
        this.women_clothing_items = new MutableLiveData<>();
        this.men_asos_items = new MutableLiveData<>();
        this.women_asos_items = new MutableLiveData<>();
        this.men_tx_items = new MutableLiveData<>();
        this.women_tx_items = new MutableLiveData<>();
        this.men_renuar_items = new MutableLiveData<>();
        this.women_renuar_items = new MutableLiveData<>();
        this.men_shein_items = new MutableLiveData<>();
        this.women_shein_items = new MutableLiveData<>();
        this.men_tfs_items = new MutableLiveData<>();
        this.women_tfs_items = new MutableLiveData<>();
        this.men_hoodies_items = new MutableLiveData<>();
        this.women_hoodies_items = new MutableLiveData<>();

        ArrayList<RecyclerItem> likes = new ArrayList<>();
        ArrayList<RecyclerItem> men_likes = new ArrayList<>();
        ArrayList<RecyclerItem> women_likes = new ArrayList<>();
        ArrayList<ShoppingItem> new_women_shoes = new ArrayList<>();
        ArrayList<ShoppingItem> new_men_shoes = new ArrayList<>();
        ArrayList<ShoppingItem> new_women_clothing = new ArrayList<>();
        ArrayList<ShoppingItem> new_men_clothing = new ArrayList<>();
        ArrayList<ShoppingItem> new_women_asos = new ArrayList<>();
        ArrayList<ShoppingItem> new_men_asos = new ArrayList<>();
        ArrayList<ShoppingItem> new_women_shein = new ArrayList<>();
        ArrayList<ShoppingItem> new_men_shein = new ArrayList<>();
        ArrayList<ShoppingItem> new_women_renuar = new ArrayList<>();
        ArrayList<ShoppingItem> new_men_renuar = new ArrayList<>();
        ArrayList<ShoppingItem> new_women_tx = new ArrayList<>();
        ArrayList<ShoppingItem> new_men_tx = new ArrayList<>();
        ArrayList<ShoppingItem> new_women_tfs = new ArrayList<>();
        ArrayList<ShoppingItem> new_men_tfs = new ArrayList<>();
        ArrayList<ShoppingItem> new_women_hoodies = new ArrayList<>();
        ArrayList<ShoppingItem> new_men_hoodies = new ArrayList<>();

        liked_items.setValue(likes);
        women_liked_items.setValue(women_likes);
        men_liked_items.setValue(men_likes);
        women_clothing_items.setValue(new_women_clothing);
        men_clothing_items.setValue(new_men_clothing);
        women_shoes_items.setValue(new_women_shoes);
        men_shoes_items.setValue(new_men_shoes);
        women_asos_items.setValue(new_women_asos);
        men_asos_items.setValue(new_men_asos);
        women_renuar_items.setValue(new_women_renuar);
        men_renuar_items.setValue(new_men_renuar);
        women_shein_items.setValue(new_women_shein);
        men_shein_items.setValue(new_men_shein);
        women_tx_items.setValue(new_women_tx);
        men_tx_items.setValue(new_men_tx);
        women_tfs_items.setValue(new_women_tfs);
        men_tfs_items.setValue(new_men_tfs);
        women_hoodies_items.setValue(new_women_hoodies);
        men_hoodies_items.setValue(new_men_hoodies);
    }

    public LiveData<ArrayList<RecyclerItem>> getRecentLikedItems(){
        return liked_items;
    }
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
    public void setLiked_items(String gender){
        if(gender.equals(Macros.CustomerMacros.WOMEN))
            setNewLikedItems(this.women_liked_items);
        else
            setNewLikedItems(this.men_liked_items);
    }

    public void removeAllType(String type,String gender){
        if(gender.equals(Macros.CustomerMacros.MEN)) {
            Objects.requireNonNull(men_liked_items.getValue()).removeIf(item -> item.getType().equals(type));
        }
        else{
            Objects.requireNonNull(women_liked_items.getValue()).removeIf(item -> item.getType().equals(type));
        }
    }

    public void addItem(ShoppingItem shoppingItem){
        switch (shoppingItem.getSeller()){
            case "Aldo":
                if(shoppingItem.getGender().equals(Macros.CustomerMacros.WOMEN)){
                    Objects.requireNonNull(women_shoes_items.getValue()).add(shoppingItem);
                    women_shoes_items.postValue(women_shoes_items.getValue());
                }else {
                    Objects.requireNonNull(men_shoes_items.getValue()).add(shoppingItem);
                    men_shoes_items.postValue(men_shoes_items.getValue());
                }
                break;
            case "Asos":
                if(shoppingItem.getGender().equals(Macros.CustomerMacros.WOMEN)){
                    Objects.requireNonNull(women_asos_items.getValue()).add(shoppingItem);
                    women_asos_items.postValue(women_asos_items.getValue());
                }else {
                    Objects.requireNonNull(men_asos_items.getValue()).add(shoppingItem);
                    men_asos_items.postValue(men_asos_items.getValue());
                }
                break;
            case "Renuar":
                if(shoppingItem.getGender().equals(Macros.CustomerMacros.WOMEN)){
                    Objects.requireNonNull(women_renuar_items.getValue()).add(shoppingItem);
                    women_renuar_items.postValue(women_renuar_items.getValue());
                }else {
                    Objects.requireNonNull(men_renuar_items.getValue()).add(shoppingItem);
                    men_renuar_items.postValue(men_renuar_items.getValue());
                }
                break;
            case "Terminal X":
                if(shoppingItem.getGender().equals(Macros.CustomerMacros.WOMEN)){
                    Objects.requireNonNull(women_tx_items.getValue()).add(shoppingItem);
                    women_tx_items.postValue(women_tx_items.getValue());
                }else {
                    Objects.requireNonNull(men_tx_items.getValue()).add(shoppingItem);
                    men_tx_items.postValue(men_tx_items.getValue());
                }
                break;
            case "Castro":
                if(shoppingItem.getGender().equals(Macros.CustomerMacros.WOMEN)){
                    Objects.requireNonNull(women_clothing_items.getValue()).add(shoppingItem);
                    women_clothing_items.postValue(women_clothing_items.getValue());
                }else {
                    Objects.requireNonNull(men_clothing_items.getValue()).add(shoppingItem);
                    men_clothing_items.postValue(men_clothing_items.getValue());
                }
                break;
            case "Shein":
                if(shoppingItem.getGender().equals(Macros.CustomerMacros.WOMEN)){
                    Objects.requireNonNull(women_shein_items.getValue()).add(shoppingItem);
                    women_shein_items.postValue(women_shein_items.getValue());
                }else {
                    Objects.requireNonNull(men_shein_items.getValue()).add(shoppingItem);
                    men_shein_items.postValue(men_shein_items.getValue());
                }
                break;
            case "Hoodies":
                if(shoppingItem.getGender().equals(Macros.CustomerMacros.WOMEN)){
                    Objects.requireNonNull(women_hoodies_items.getValue()).add(shoppingItem);
                    women_hoodies_items.postValue(women_hoodies_items.getValue());
                }else {
                    Objects.requireNonNull(men_hoodies_items.getValue()).add(shoppingItem);
                    men_hoodies_items.postValue(men_hoodies_items.getValue());
                }
                break;
            case "TwentyFourSeven":
                if(shoppingItem.getGender().equals(Macros.CustomerMacros.WOMEN)){
                    Objects.requireNonNull(women_tfs_items.getValue()).add(shoppingItem);
                    women_tfs_items.postValue(women_tfs_items.getValue());
                }else {
                    Objects.requireNonNull(men_tfs_items.getValue()).add(shoppingItem);
                    men_tfs_items.postValue(men_tfs_items.getValue());
                }
                break;
        }
    }
    public LiveData<ArrayList<ShoppingItem>> getMen_shoes_items() {
        return men_shoes_items;
    }
    public LiveData<ArrayList<ShoppingItem>> getWomen_shoes_items() {
        return women_shoes_items;
    }

    public LiveData<ArrayList<ShoppingItem>> getMen_renuar_items() {
        return men_renuar_items;
    }
    public LiveData<ArrayList<ShoppingItem>> getWomen_renuar_items() {
        return women_renuar_items;
    }

    public LiveData<ArrayList<ShoppingItem>> getMen_shein_items() {
        return men_shein_items;
    }
    public LiveData<ArrayList<ShoppingItem>> getWomen_shein_items() {
        return women_shein_items;
    }

    public LiveData<ArrayList<ShoppingItem>> getMen_tx_items() {
        return men_tx_items;
    }
    public LiveData<ArrayList<ShoppingItem>> getWomen_tx_items() {
        return women_tx_items;
    }

    public LiveData<ArrayList<ShoppingItem>> getMen_asos_items() {
        return men_asos_items;
    }
    public LiveData<ArrayList<ShoppingItem>> getWomen_asos_items() {
        return women_asos_items;
    }

    public LiveData<ArrayList<ShoppingItem>> getMen_clothing_items() {
        return men_clothing_items;
    }
    public LiveData<ArrayList<ShoppingItem>> getWomen_clothing_items() {
        return women_clothing_items;
    }

    public LiveData<ArrayList<ShoppingItem>> getMen_tfs_items() {
        return men_tfs_items;
    }
    public LiveData<ArrayList<ShoppingItem>> getWomen_tfs_items() {
        return women_tfs_items;
    }

    public LiveData<ArrayList<ShoppingItem>> getMen_hoodies_items() {
        return men_hoodies_items;
    }
    public LiveData<ArrayList<ShoppingItem>> getWomen_hoodies_items() {
        return women_hoodies_items;
    }

    public LiveData<Integer> getCurrentClothingItem() {
        return current_clothing_item;
    }
    public void setCurrentClothingItem(Integer integer){
        this.current_clothing_item.postValue(integer);
    }

    public LiveData<Integer> getCurrentRenuarItem() {
        return current_renuar_item;
    }
    public void setCurrentRenuarItem(Integer integer){
        this.current_renuar_item.postValue(integer);
    }

    public LiveData<Integer> getCurrentAsosItem() {
        return current_asos_item;
    }
    public void setCurrentAsosItem(Integer integer){
        this.current_asos_item.postValue(integer);
    }

    public LiveData<Integer> getCurrentSheinItem() {
        return current_shein_item;
    }
    public void setCurrentSheinItem(Integer integer){
        this.current_shein_item.postValue(integer);
    }

    public LiveData<Integer> getCurrentTerminalXItem() {
        return current_tx_item;
    }
    public void setCurrentTerminalXItem(Integer integer){
        this.current_tx_item.postValue(integer);
    }

    public LiveData<Integer> getCurrentHoodiesItem() {
        return current_hoodies_item;
    }
    public void setCurrentHoodiesItem(Integer integer){
        this.current_hoodies_item.postValue(integer);
    }

    public LiveData<Integer> getCurrentTfsItem() {
        return current_tfs_item;
    }
    public void setCurrentTfsItem(Integer integer){
        this.current_tfs_item.postValue(integer);
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
    public void clearAllAsosItems(){
        Objects.requireNonNull(this.women_asos_items.getValue()).clear();
        Objects.requireNonNull(this.men_asos_items.getValue()).clear();
    }
    public void clearAllSheinItems(){
        Objects.requireNonNull(this.women_shein_items.getValue()).clear();
        Objects.requireNonNull(this.men_shein_items.getValue()).clear();
    }
    public void clearAllTxItems(){
        Objects.requireNonNull(this.women_tx_items.getValue()).clear();
        Objects.requireNonNull(this.men_tx_items.getValue()).clear();
    }
    public void clearAllHoodiesItems(){
        Objects.requireNonNull(this.women_hoodies_items.getValue()).clear();
        Objects.requireNonNull(this.men_hoodies_items.getValue()).clear();
    }
    public void clearAllTfsItems(){
        Objects.requireNonNull(this.women_tfs_items.getValue()).clear();
        Objects.requireNonNull(this.men_tfs_items.getValue()).clear();
    }
    public void clearAllRenuarItems(){
        Objects.requireNonNull(this.women_renuar_items.getValue()).clear();
        Objects.requireNonNull(this.men_renuar_items.getValue()).clear();
    }
}
