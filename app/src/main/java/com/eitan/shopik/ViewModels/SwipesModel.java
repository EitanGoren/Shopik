package com.eitan.shopik.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.Items.ShoppingItem;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class SwipesModel extends AndroidViewModel {

    private MutableLiveData<CopyOnWriteArrayList<ShoppingItem>> items;
    private MutableLiveData<String> last_item_id;

    public SwipesModel(Application application){
        super(application);

        this.items = new MutableLiveData<>();
        CopyOnWriteArrayList<ShoppingItem> items = new CopyOnWriteArrayList<>();
        this.items.setValue(items);

        last_item_id = new MutableLiveData<>();
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
    public void removeFromItems(){
        Objects.requireNonNull(this.items.getValue()).remove(0);
    }

    public LiveData<String> getLast_item_id() {
        return last_item_id;
    }
    public void setLast_item_id(String last_item_id) {
        this.last_item_id.postValue(last_item_id);
    }
}
