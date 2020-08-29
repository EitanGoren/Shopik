package com.eitan.shopik.ViewModels;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.Items.RecyclerItem;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class OutletsModel extends AndroidViewModel {

    private MutableLiveData<CopyOnWriteArrayList<RecyclerItem>> outlets;

    public OutletsModel(Application application){
        super(application);

        this.outlets = new MutableLiveData<>();
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
    }

    public void postOutlets() {
        CopyOnWriteArrayList<RecyclerItem> temp = this.outlets.getValue();
        this.outlets.postValue(temp);
    }
}
