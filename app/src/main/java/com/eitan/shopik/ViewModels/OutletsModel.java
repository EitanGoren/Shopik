package com.eitan.shopik.ViewModels;

import android.app.Application;
import android.os.Build;

import androidx.annotation.Keep;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.Items.RecyclerItem;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Keep
public class OutletsModel extends AndroidViewModel implements Serializable {

    private MutableLiveData<CopyOnWriteArrayList<RecyclerItem>> outlets;
    private MutableLiveData<Integer> currentItem;
    private MutableLiveData<Integer> totalItems;

    public OutletsModel(Application application){
        super(application);

        this.outlets = new MutableLiveData<>();
        this.currentItem = new MutableLiveData<>();
        this.totalItems = new MutableLiveData<>();

        CopyOnWriteArrayList<RecyclerItem> outlets = new CopyOnWriteArrayList<>();
        this.outlets.setValue(outlets);
    }

    public void clearAllOutlets(){
        Objects.requireNonNull(this.outlets.getValue()).clear();

    }
    public LiveData<CopyOnWriteArrayList<RecyclerItem>> getOutlets() {
        return outlets;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addToOutlets(RecyclerItem recyclerItem) {
        Objects.requireNonNull(this.outlets.getValue()).add(recyclerItem);
        CopyOnWriteArrayList<RecyclerItem> temp = new CopyOnWriteArrayList<>(outlets.getValue());
        outlets.postValue(temp);
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
