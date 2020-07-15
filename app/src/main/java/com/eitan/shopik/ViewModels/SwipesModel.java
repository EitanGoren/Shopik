package com.eitan.shopik.ViewModels;

import android.app.Application;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.Macros;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.google.android.gms.ads.formats.NativeAdOptions.ADCHOICES_TOP_LEFT;
import static com.google.android.gms.ads.formats.NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_SQUARE;

public class SwipesModel extends AndroidViewModel {

    private MutableLiveData<CopyOnWriteArrayList<ShoppingItem>> items;
    private MutableLiveData<String> last_item_id;

    public SwipesModel(Application application){
        super(application);

        this.items = new MutableLiveData<>();
        CopyOnWriteArrayList<ShoppingItem> items = new CopyOnWriteArrayList<>();
        this.items.setValue(items);

        last_item_id = new MutableLiveData<>();
    }

    public void clearAllItems(){
        Objects.requireNonNull(this.items.getValue()).clear();
    }
    public LiveData<CopyOnWriteArrayList<ShoppingItem>> getItems() {
        return items;
    }
    public void setItems(CopyOnWriteArrayList<ShoppingItem> items) {
        this.items.postValue(items);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addToItems(ShoppingItem shoppingItem){
        Objects.requireNonNull(this.items.getValue()).add(shoppingItem);
        items.getValue().sort((o1, o2) -> {
            if(!o1.isAd() && !o2.isAd())
                return o1.getId().compareTo(o2.getId());
            else
                return o1.isAd() ? 1 : -1;
        });
    }
    public void removeFromItems(){
        Objects.requireNonNull(this.items.getValue()).remove(0);
    }

    public LiveData<String> getLast_item_id() {
        return last_item_id;
    }
    public void setLast_item_id(String last_item_id) {
        this.last_item_id.postValue(last_item_id);
    }
}
