package com.eitan.shopik.viewModels;

import android.app.Application;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.database.models.ShoppingItem;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Keep
public class AllItemsModel extends AndroidViewModel implements Serializable {

    private final MutableLiveData<Set<ShoppingItem>> all_items_list;

    public AllItemsModel(@NonNull Application application) {
        super(application);

        this.all_items_list = new MutableLiveData<>();
        Set<ShoppingItem> list = new HashSet<>();
        this.all_items_list.setValue(list);
    }

    public LiveData<Set<ShoppingItem>> getItems() {
        return all_items_list;
    }

    public void addItem(ShoppingItem ShoppingItem){
        Objects.requireNonNull(this.all_items_list.getValue()).add(ShoppingItem);
        Set<ShoppingItem> koko = this.all_items_list.getValue();
        this.all_items_list.postValue(koko);
    }

    public void clearItems(){
        Objects.requireNonNull(this.all_items_list.getValue()).clear();
    }
}