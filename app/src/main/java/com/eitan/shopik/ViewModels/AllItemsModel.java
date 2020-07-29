package com.eitan.shopik.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.Items.RecyclerItem;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class AllItemsModel extends AndroidViewModel {

    private MutableLiveData<CopyOnWriteArrayList<RecyclerItem>> all_items_list;

    public AllItemsModel(@NonNull Application application) {
        super(application);

        this.all_items_list = new MutableLiveData<>();
        CopyOnWriteArrayList<RecyclerItem> list = new CopyOnWriteArrayList<>();
        this.all_items_list.setValue(list);
    }

    public LiveData<CopyOnWriteArrayList<RecyclerItem>> getItems() {
        return all_items_list;
    }
    public void setItems(CopyOnWriteArrayList<RecyclerItem> all_items_list){
        Objects.requireNonNull(this.all_items_list.getValue()).addAll(all_items_list);
        CopyOnWriteArrayList<RecyclerItem> koko = this.all_items_list.getValue();
        this.all_items_list.postValue(koko);
    }
    public void addItem(RecyclerItem recyclerItem){
        Objects.requireNonNull(this.all_items_list.getValue()).add(recyclerItem);
        CopyOnWriteArrayList<RecyclerItem> koko = this.all_items_list.getValue();
        this.all_items_list.postValue(koko);
    }
    public void shuffleItems(){
        Collections.shuffle(Objects.requireNonNull(this.all_items_list.getValue()));
    }

    public void clearItems(){
        Objects.requireNonNull(all_items_list.getValue()).clear();
    }
}