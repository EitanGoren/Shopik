package com.eitan.shopik.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.Items.PreferredItem;
import com.eitan.shopik.Items.ShoppingItem;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class SuggestedModel extends AndroidViewModel {

    private MutableLiveData<CopyOnWriteArrayList<ShoppingItem>> list;
    private MutableLiveData<PreferredItem> preferred;

    public SuggestedModel(Application application) {
        super(application);

        this.preferred = new MutableLiveData<>();

        this.list = new MutableLiveData<>();
        CopyOnWriteArrayList<ShoppingItem> not = new CopyOnWriteArrayList<>();
        this.list.setValue(not);
    }

    public LiveData<CopyOnWriteArrayList<ShoppingItem>> getAllItems() {
        return list;
    }
    public void addToAllItems(ShoppingItem shoppingItem){
        Objects.requireNonNull(this.list.getValue()).add(shoppingItem );
    }
    public void clearItems(){
        Objects.requireNonNull(this.list.getValue()).clear();
    }

    public LiveData<PreferredItem> getPreferred(){
        return preferred;
    }
    public void setPreferred(PreferredItem preferredItem){
        this.preferred.postValue(preferredItem);
    }
}
