package com.eitan.shopik.ViewModels;

import android.app.Application;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.Macros;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

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
