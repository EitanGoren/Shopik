package com.eitan.shopik.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.Items.ShoppingItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class SwipesModel extends AndroidViewModel {

    private MutableLiveData<CopyOnWriteArrayList<ShoppingItem>> items;

    public SwipesModel(Application application){
        super(application);

        this.items = new MutableLiveData<>();
        CopyOnWriteArrayList<ShoppingItem> items = new CopyOnWriteArrayList<>();
        this.items.setValue(items);

        Map<String,String> map = new HashMap<>();
        MutableLiveData<Map<String, String>> last_item_id = new MutableLiveData<>();
        last_item_id.setValue(map);
    }

    public void clearAllItems(){
        Objects.requireNonNull(this.items.getValue()).clear();
    }
    public LiveData<CopyOnWriteArrayList<ShoppingItem>> getItems() {
        return items;
    }
    public void setItems(CopyOnWriteArrayList<ShoppingItem> items) {
        this.items.postValue(items);
    }
    public void addToItems(ShoppingItem shoppingItem) {
        Objects.requireNonNull(this.items.getValue()).add(shoppingItem);
    }
}
