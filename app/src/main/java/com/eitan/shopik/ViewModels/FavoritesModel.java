package com.eitan.shopik.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.Items.ShoppingItem;

import java.util.ArrayList;
import java.util.Objects;

public class FavoritesModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<ShoppingItem>> list;

    public FavoritesModel(Application application){
        super(application);

        this.list = new MutableLiveData<>();
        ArrayList<ShoppingItem> list = new ArrayList<>();
        this.list.setValue(list);
    }

    public LiveData<ArrayList<ShoppingItem>> getItems() {
        return list;
    }
    public void addToItems(ShoppingItem shoppingItem){
        Objects.requireNonNull(this.list.getValue()).add(shoppingItem );
    }
    public void clearItems(){
        Objects.requireNonNull(this.list.getValue()).clear();
    }
}
