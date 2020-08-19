package com.eitan.shopik.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.Items.ShoppingItem;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class AllItemsModel extends AndroidViewModel {

    private MutableLiveData<CopyOnWriteArrayList<ShoppingItem>> all_items_list;

    public AllItemsModel(@NonNull Application application) {
        super(application);

        this.all_items_list = new MutableLiveData<>();
        CopyOnWriteArrayList<ShoppingItem> list = new CopyOnWriteArrayList<>();
        this.all_items_list.setValue(list);
    }

    public LiveData<CopyOnWriteArrayList<ShoppingItem>> getItems() {
        return all_items_list;
    }
    public void setItems(CopyOnWriteArrayList<ShoppingItem> all_items_list){
        Objects.requireNonNull(this.all_items_list.getValue()).addAll(all_items_list);
        CopyOnWriteArrayList<ShoppingItem> koko = this.all_items_list.getValue();
        this.all_items_list.postValue(koko);
    }
    public void addItem(ShoppingItem ShoppingItem){
        Objects.requireNonNull(this.all_items_list.getValue()).add(ShoppingItem);
        CopyOnWriteArrayList<ShoppingItem> koko = this.all_items_list.getValue();
        this.all_items_list.postValue(koko);
    }

    public void clearItems(){
        Objects.requireNonNull(all_items_list.getValue()).clear();
    }
}