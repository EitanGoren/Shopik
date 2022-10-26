package com.eitan.shopik.viewModels;

import android.util.Pair;

import androidx.annotation.Keep;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eitan.shopik.PublicUser;
import com.eitan.shopik.items.PreferredItem;
import com.eitan.shopik.database.models.ShoppingItem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Keep
public class MainModel extends ViewModel implements Serializable {

    private final MutableLiveData<Map<String,Map<String,Object>>> companies_info;
    private final MutableLiveData<Map<String, PublicUser>> customers_info;
    private final MutableLiveData<Set<ShoppingItem>> all_items;
    private final Set<String> likedIds;
    private final MutableLiveData<Set<ShoppingItem>> likedItems;
    private final MutableLiveData<Set<ShoppingItem>> unlikedItems;
    private final MutableLiveData<Set<ShoppingItem>> unseenItems;
    private final MutableLiveData<PreferredItem> preferred;
    private final MutableLiveData<Pair<Integer,Integer>> currentItem;
    private final MutableLiveData<Integer> totalItems;

    public MainModel(){
        currentItem = new MutableLiveData<>();
        totalItems = new MutableLiveData<>();

        this.preferred = new MutableLiveData<>();

        Map<String,Map<String,Object>> companies_info_map = new HashMap<>();
        this.companies_info = new MutableLiveData<>(companies_info_map);

        Map<String, PublicUser> customers_info_map = new HashMap<>();
        this.customers_info = new MutableLiveData<>(customers_info_map);

        Set<ShoppingItem> items = new HashSet<>();
        this.all_items = new MutableLiveData<>(items);

        likedIds = new HashSet<>();

        Set<ShoppingItem> _likedItems = new HashSet<>();
        likedItems = new MutableLiveData<>(_likedItems);

        Set<ShoppingItem> _unlikedItems = new HashSet<>();
        unlikedItems = new MutableLiveData<>(_unlikedItems);

        Set<ShoppingItem> _unseenItems = new HashSet<>();
        unseenItems = new MutableLiveData<>(_unseenItems);
    }

    public LiveData<Map<String, PublicUser>> getCustomers_info() {
        return customers_info;
    }
    public LiveData<Map<String,Map<String,Object>>> getCompanies_info() {
        return companies_info;
    }
    public void setCustomers_info(String id, PublicUser likedUser) {
        Objects.requireNonNull(this.customers_info.getValue()).put(id,likedUser);
    }
    public LiveData<Set<ShoppingItem>> getAllItems() {
        return all_items;
    }
    public void addItem(ShoppingItem shoppingItem, int fromTotal) {
        Objects.requireNonNull(this.all_items.getValue()).add(shoppingItem);
        if (this.all_items.getValue().size() == fromTotal) {
            postAllItems();
        }
    }
    private ShoppingItem getItemById(String id){
        for(ShoppingItem shoppingItem : Objects.requireNonNull(all_items.getValue())){
            String itemId = shoppingItem.getSeller().toLowerCase() + "-" + shoppingItem.getId();
            if(itemId.equals(id)) {
                return shoppingItem;
            }
        }
        return null;
    }
    public void postAllItems() {
        all_items.postValue(this.all_items.getValue());
    }

    //LIKED
    public void addLikedItemId(String id, int totalLiked) {
        likedIds.add(id);
        Objects.requireNonNull(likedItems.getValue()).add(getItemById(id));
        if(likedItems.getValue().size() == totalLiked)
            likedItems.postValue(likedItems.getValue());
    }
    public MutableLiveData<Set<ShoppingItem>> getLikedItems(){
        return likedItems;
    }

    //UNLIKED
    public void addUnlikedItemId(String id, int totalUnliked) {
        Objects.requireNonNull(unlikedItems.getValue()).add(getItemById(id));
        if(unlikedItems.getValue().size() == totalUnliked)
            unlikedItems.postValue(unlikedItems.getValue());
    }

    public MutableLiveData<Set<ShoppingItem>> getUnLikedItems(){
        return unlikedItems;
    }

    public void updateUnseenItems(){
        for(ShoppingItem item : Objects.requireNonNull(all_items.getValue())){
            if(!Objects.requireNonNull(likedItems.getValue()).contains(item) &&
                    !Objects.requireNonNull(unlikedItems.getValue()).contains(item))
            {
                Objects.requireNonNull(unseenItems.getValue()).add(item);
            }
        }
        unseenItems.postValue(unseenItems.getValue());
    }

    public MutableLiveData<Set<ShoppingItem>> getUnseenItems(){
        return unseenItems;
    }

    public void removeUnseenItem(ShoppingItem shoppingItem){
        unseenItems.getValue().remove(shoppingItem);
        unseenItems.postValue(unseenItems.getValue());
    }

    //PREFERRED
    public MutableLiveData<PreferredItem> getPreferred() {
        return preferred;
    }
    public void setPreferred(PreferredItem preferredItem) {
        this.preferred.postValue(preferredItem);
    }

    //CURRENT ITEM FETCH
    public MutableLiveData<Pair<Integer,Integer>> getCurrentItem() {
        return currentItem;
    }

    //TOTAL ITEMS NUM
    public MutableLiveData<Integer> getTotalItems() {
        return totalItems;
    }
}
