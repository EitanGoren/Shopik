package com.eitan.shopik.ViewModels;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.LikedUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainModel extends ViewModel {

    private MutableLiveData<Map<String,Map<String,Object>>> companies_info;
    private MutableLiveData<Map<String, LikedUser>> customers_info;
    private MutableLiveData<CopyOnWriteArrayList<ShoppingItem>> all_items;
    private MutableLiveData<CopyOnWriteArrayList<Pair<String,ShoppingItem>>> all_items_ids;
    private MutableLiveData<CopyOnWriteArrayList<ShoppingItem>> castro_items;
    private ArrayList<ShoppingItem> shoppingAdsArray;

    public MainModel(){

        shoppingAdsArray = new ArrayList<>();

        Map<String,Map<String,Object>> companies_info_map = new HashMap<>();
        this.companies_info = new MutableLiveData<>();
        this.companies_info.setValue(companies_info_map);

        Map<String,LikedUser> customers_info_map = new HashMap<>();
        this.customers_info = new MutableLiveData<>();
        this.customers_info.setValue(customers_info_map);

        CopyOnWriteArrayList<ShoppingItem> list = new CopyOnWriteArrayList<>();
        this.all_items = new MutableLiveData<>();
        all_items.setValue(list);

        CopyOnWriteArrayList<Pair<String,ShoppingItem>> ids = new CopyOnWriteArrayList<>();
        this.all_items_ids = new MutableLiveData<>();
        all_items_ids.setValue(ids);

        CopyOnWriteArrayList<ShoppingItem> castro = new CopyOnWriteArrayList<>();
        this.castro_items = new MutableLiveData<>();
        castro_items.setValue(castro);
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

    public LiveData<CopyOnWriteArrayList<ShoppingItem>> getAll_items() {
        return all_items;
    }
    public LiveData<CopyOnWriteArrayList<Pair<String,ShoppingItem>>> getAll_items_ids() {
        return all_items_ids;
    }

    //castro
    public void add_castro_item(ShoppingItem item) {
        Objects.requireNonNull(this.castro_items.getValue()).add(item);
    }
    public LiveData<CopyOnWriteArrayList<ShoppingItem>> getAllCastroItems() {
        return castro_items;
    }
    public void postAllCastroItemsIds(){
        CopyOnWriteArrayList<ShoppingItem> koko = this.castro_items.getValue();
        castro_items.postValue(koko);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addItemId(Pair<String,ShoppingItem> pair) {
        Objects.requireNonNull(this.all_items_ids.getValue()).add(pair);
    }
    public void postAllItemsIds(){
        CopyOnWriteArrayList<Pair<String,ShoppingItem>> koko = this.all_items_ids.getValue();
        all_items_ids.postValue(koko);
    }

    public void addAd(ShoppingItem shoppingItem){
        this.shoppingAdsArray.add(shoppingItem);
    }
    public Object getNextAd(){
        Random random = new Random();
        if( shoppingAdsArray.size() > 0) {
            int idx = (random.nextInt(shoppingAdsArray.size()));
            return shoppingAdsArray.get(idx);
        }
        else
            return null;
    }
    public int getAdsContainerSize(){
        return shoppingAdsArray.size();
    }

    public void clearAds(){
        for(ShoppingItem item : shoppingAdsArray) {
            item.destroyAd();
        }
        shoppingAdsArray.clear();
    }
}
