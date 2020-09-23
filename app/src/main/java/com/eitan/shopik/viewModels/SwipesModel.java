package com.eitan.shopik.viewModels;

import android.app.Application;

import androidx.annotation.Keep;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.items.ShoppingItem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Keep
public class SwipesModel extends AndroidViewModel implements Serializable {

    private final MutableLiveData<CopyOnWriteArrayList<ShoppingItem>> items;

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

    public void addToItems(ShoppingItem shoppingItem) {
        Objects.requireNonNull(this.items.getValue()).add(shoppingItem);
    }
}
