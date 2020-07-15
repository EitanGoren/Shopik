package com.eitan.shopik.ViewModels;

import android.app.Application;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Items.ShoppingItem;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class AllItemsModel extends AndroidViewModel {

    private MutableLiveData<CopyOnWriteArrayList<RecyclerItem>> all_items_list;
    private RecyclerItem dummy;
    private UnifiedNativeAd tempAd;
    private int adCount = 0;

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
       // checkForAdTime();
        CopyOnWriteArrayList<RecyclerItem> koko = this.all_items_list.getValue();
        this.all_items_list.postValue(koko);
    }
    private void addAdToItems(RecyclerItem recyclerItem){
        Objects.requireNonNull(this.all_items_list.getValue()).add( recyclerItem);
    }
    public void clearItems(){
        Objects.requireNonNull(all_items_list.getValue()).clear();
    }

    private void checkForAdTime(){
        int size = Objects.requireNonNull(all_items_list.getValue()).size();
        if( ( size % 10 == 0  && size > 1 )) {
            adCount++;
            addNewAd();
        }
    }
    private void addNewAd() {
        dummy = new RecyclerItem(null,null);
        dummy.setAd(true);
        loadAd();
    }
    private void loadAd(){

        AdLoader.Builder builder = new AdLoader.Builder(getApplication(), "ca-app-pub-3940256099942544/2247696110");
        builder.forUnifiedNativeAd(unifiedNativeAd -> {
           // if(tempAd != null){
           //     tempAd.destroy();
           // }
            tempAd = unifiedNativeAd;
        });

        AdListener adListener = new AdListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                dummy.setAd(true);
                dummy.setNativeAd(tempAd);
                addAdToItems(dummy);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplication(), "Failed to load native ad: " + errorCode, Toast.LENGTH_SHORT).show();
            }
        };
        final AdLoader adLoader = builder.withAdListener(adListener).build();
        adLoader.loadAd(new PublisherAdRequest.Builder().build());
    }
}