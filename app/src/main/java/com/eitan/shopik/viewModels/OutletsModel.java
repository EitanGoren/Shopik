package com.eitan.shopik.viewModels;

import android.app.Application;

import androidx.annotation.Keep;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.database.models.ShoppingItem;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Keep
public class OutletsModel extends AndroidViewModel implements Serializable {

    private final MutableLiveData<Set<ShoppingItem>> outlets;
    private final MutableLiveData<Integer> currentItem;
    private final MutableLiveData<Integer> totalItems;

    public OutletsModel(Application application){
        super(application);

        this.outlets = new MutableLiveData<>();
        this.currentItem = new MutableLiveData<>();
        this.totalItems = new MutableLiveData<>();

        Set<ShoppingItem> outlets = new HashSet<>();
        this.outlets.setValue(outlets);
    }

    public void clearAllOutlets(){
        Objects.requireNonNull(this.outlets.getValue()).clear();
    }
    public LiveData<Set<ShoppingItem>> getOutlets() {
        return outlets;
    }

    public void addToOutlets(ShoppingItem shoppingItem) {
        Objects.requireNonNull(this.outlets.getValue()).add(shoppingItem);
        outlets.postValue(outlets.getValue());
    }

    public MutableLiveData<Integer> getTotalItems() {
        return totalItems;
    }
    public void setTotalItems(Integer totalItems) {
        this.totalItems.postValue(totalItems);
    }

    public MutableLiveData<Integer> getCurrentItem() {
        return currentItem;
    }
    public void setCurrentItem(Integer currentItem) {
        this.currentItem.postValue(currentItem);
    }
}
