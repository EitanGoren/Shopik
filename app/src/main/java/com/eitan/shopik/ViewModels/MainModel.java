package com.eitan.shopik.ViewModels;

import android.os.Build;
import android.util.ArraySet;
import android.util.Pair;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eitan.shopik.Items.PreferredItem;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.LikedUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainModel extends ViewModel {

    private MutableLiveData<Map<String,Map<String,Object>>> companies_info;
    private MutableLiveData<Map<String, LikedUser>> customers_info;
    private MutableLiveData<CopyOnWriteArrayList<ShoppingItem>> all_items;
    private Set<String> swipedItems;
    private ArrayList<ShoppingItem> shoppingAdsArray;
    private MutableLiveData<PreferredItem> preferred;
    private MutableLiveData<CopyOnWriteArrayList<ShoppingItem>> favorites;
    private MutableLiveData<Long> current_page;
    private MutableLiveData<Pair<Integer,Boolean>> currentItem;
    private MutableLiveData<Integer> totalItems;

    public MainModel(){

        currentItem = new MutableLiveData<>();

        totalItems = new MutableLiveData<>();

        this.preferred = new MutableLiveData<>();
        shoppingAdsArray = new ArrayList<>();
        current_page = new MutableLiveData<>();

        Map<String,Map<String,Object>> companies_info_map = new HashMap<>();
        this.companies_info = new MutableLiveData<>();
        this.companies_info.setValue(companies_info_map);

        Map<String,LikedUser> customers_info_map = new HashMap<>();
        this.customers_info = new MutableLiveData<>();
        this.customers_info.setValue(customers_info_map);

        CopyOnWriteArrayList<ShoppingItem> items = new CopyOnWriteArrayList<>();
        this.all_items = new MutableLiveData<>();
        all_items.setValue(items);

        this.favorites = new MutableLiveData<>();
        CopyOnWriteArrayList<ShoppingItem> favorites_list = new CopyOnWriteArrayList<>();
        this.favorites.setValue(favorites_list);

        swipedItems = new ArraySet<>();
    }

    public LiveData<Map<String, LikedUser>> getCustomers_info() {
        return customers_info;
    }
    public void setCompanies_info(String id,Map<String,Object> value) {
        Objects.requireNonNull(this.companies_info.getValue()).put(id,value);
    }
    public LiveData<Map<String,Map<String,Object>>> getCompanies_info() {
        return companies_info;
    }
    public void setCustomers_info(String id, LikedUser likedUser) {
        Objects.requireNonNull(this.customers_info.getValue()).put(id,likedUser);
    }

    public void markItemAsSeen(String item_id){
        for(ShoppingItem shoppingItem : Objects.requireNonNull(all_items.getValue())){
            if(shoppingItem.getId().equals(item_id)) {
                shoppingItem.setSeen(true);
                //postAllItems();
                return;
            }
        }
    }

    public LiveData<CopyOnWriteArrayList<ShoppingItem>> getAll_items() {
        return all_items;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addItem(ShoppingItem shoppingItem, int size) {
        Objects.requireNonNull(this.all_items.getValue()).add(shoppingItem);
        if(this.all_items.getValue().size() == size){
            postAllItems();
        }
    }
    public void postAllItems() {
        CopyOnWriteArrayList<ShoppingItem> koko = this.all_items.getValue();
        all_items.postValue(koko);
    }
    public void clearAllItems(){
        Objects.requireNonNull(all_items.getValue()).clear();
    }

    //PAGE NUM
    public LiveData<Long> getCurrent_page() {
        return current_page;
    }
    public void setCurrent_page(Long current_page) {
        this.current_page.postValue(current_page);
    }

    //SWIPED
    public void addSwipedItemId(String id){
        swipedItems.add(id);
    }
    public boolean isSwiped(String id){
        return swipedItems.contains(id);
    }

    //ADS
    public void clearAds(){
        for(ShoppingItem item : shoppingAdsArray) {
            item.destroyAd();
        }
        shoppingAdsArray.clear();
    }
    public void addAd(ShoppingItem shoppingItem){
        this.shoppingAdsArray.add(shoppingItem);
    }
    public Object getNextAd() {
        Random random = new Random();
        if( shoppingAdsArray.size() > 0) {
            int idx = (random.nextInt(shoppingAdsArray.size()));
            return shoppingAdsArray.get(idx);
        }
        else
            return null;
    }

    //PREFERRED
    public LiveData<PreferredItem> getPreferred(){
        return preferred;
    }
    public void setPreferred(PreferredItem preferredItem){
        this.preferred.postValue(preferredItem);
    }

    //FAVORITES
    public LiveData<CopyOnWriteArrayList<ShoppingItem>> getFavorite() {
        return favorites;
    }
    public void addFavorite(ShoppingItem shoppingItem){
        Objects.requireNonNull(this.favorites.getValue()).add(shoppingItem );
        CopyOnWriteArrayList<ShoppingItem> koko = this.favorites.getValue();
        favorites.postValue(koko);
    }
    public void clearFavorite(){
        Objects.requireNonNull(this.favorites.getValue()).clear();
    }

    //CURRENT ITEM FETCH
    public MutableLiveData<Pair<Integer,Boolean>> getCurrentItem() {
        return currentItem;
    }

    //TOTAL ITEMS NUM

    public MutableLiveData<Integer> getTotalItems() {
        return totalItems;
    }
}
