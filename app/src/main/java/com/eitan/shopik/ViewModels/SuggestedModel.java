package com.eitan.shopik.ViewModels;

import android.app.Application;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.Items.PreferredItem;
import com.eitan.shopik.Items.ShoppingItem;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class SuggestedModel extends AndroidViewModel {

    private MutableLiveData<CopyOnWriteArrayList<ShoppingItem>> list;
    private MutableLiveData<PreferredItem> preferred;
    private ShoppingItem dummy;
    private UnifiedNativeAd tempAd;
    private int adCount = 0;

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
       // checkForAdTime();
    }
    private void addAdToItems(ShoppingItem shoppingItem){
        Objects.requireNonNull(this.list.getValue()).add(adCount * 10 - 1, shoppingItem);
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

    private void checkForAdTime(){
        int size = Objects.requireNonNull(list.getValue()).size();
        if( ( size % 10 == 0  && size > 1 )) {
            adCount++;
            addNewAd();
        }
    }
    private void addNewAd() {
        dummy = new ShoppingItem();
        dummy.setAd(true);
        loadAd();
    }
    private void loadAd(){

        AdLoader.Builder builder = new AdLoader.Builder(getApplication(), "ca-app-pub-3940256099942544/2247696110");
        builder.forUnifiedNativeAd(unifiedNativeAd -> {
            if(tempAd != null){
                //tempAd.destroy();
            }
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
