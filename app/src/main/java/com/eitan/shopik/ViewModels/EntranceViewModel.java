package com.eitan.shopik.ViewModels;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Macros;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EntranceViewModel extends ViewModel {

    private MutableLiveData<ArrayList<RecyclerItem>> liked_items;
    private MutableLiveData<ArrayList<RecyclerItem>> men_liked_items;
    private MutableLiveData<ArrayList<RecyclerItem>> women_liked_items;
    private MutableLiveData<ArrayList<RecyclerItem>> new_items;
    private MutableLiveData<ArrayList<RecyclerItem>> men_new_items;
    private MutableLiveData<ArrayList<RecyclerItem>> women_new_items;
    private MutableLiveData<Map<String, Object>> men_new_num;
    private MutableLiveData<Map<String, Object>> women_new_num;

    public EntranceViewModel() {
        this.men_new_num = new MutableLiveData<>();
        this.women_new_num = new MutableLiveData<>();
        this.liked_items = new MutableLiveData<>();
        this.men_liked_items = new MutableLiveData<>();
        this.women_liked_items = new MutableLiveData<>();
        this.men_new_items = new MutableLiveData<>();
        this.women_new_items = new MutableLiveData<>();
        this.new_items = new MutableLiveData<>();

        Map<String,Object> men_num_map = new HashMap<>();
        Map<String,Object> women_num_map = new HashMap<>();
        ArrayList<RecyclerItem> likes = new ArrayList<>();
        ArrayList<RecyclerItem> men_likes = new ArrayList<>();
        ArrayList<RecyclerItem> women_likes = new ArrayList<>();
        ArrayList<RecyclerItem> new_items_list = new ArrayList<>();
        ArrayList<RecyclerItem> new_women_list = new ArrayList<>();
        ArrayList<RecyclerItem> new_men_list = new ArrayList<>();

        liked_items.setValue(likes);
        women_liked_items.setValue(women_likes);
        men_liked_items.setValue(men_likes);
        men_new_items.setValue(new_men_list);
        women_new_items.setValue(new_women_list);
        new_items.setValue(new_items_list);
        men_new_num.setValue(men_num_map);
        women_new_num.setValue(women_num_map);
    }

    public LiveData<ArrayList<RecyclerItem>> getRecentLikedItems(){
        return liked_items;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setNewLikedItems(MutableLiveData<ArrayList<RecyclerItem>> likedItems){
        clearLiked();
        Objects.requireNonNull(liked_items.getValue()).addAll(Objects.requireNonNull(likedItems.getValue()));
        ArrayList<RecyclerItem> temp = liked_items.getValue();
        temp.sort((o1, o2) -> Integer.parseInt(String.valueOf(o2.getLikes() - o1.getLikes())));
        liked_items.postValue(temp);
    }
    public void clearLiked(){
        Objects.requireNonNull(liked_items.getValue()).clear();
    }
    public void addMenLikedItem(RecyclerItem recyclerItem){
        Objects.requireNonNull(men_liked_items.getValue()).add(0, recyclerItem);
    }
    public void addWomenLikedItem(RecyclerItem recyclerItem){
        Objects.requireNonNull(women_liked_items.getValue()).add(0, recyclerItem);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setLiked_items(String gender){
        if(gender.equals(Macros.CustomerMacros.WOMEN))
            setNewLikedItems(this.women_liked_items);
        else
            setNewLikedItems(this.men_liked_items);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeAllType(String type,String gender){
        if(gender.equals(Macros.CustomerMacros.MEN)) {
            Objects.requireNonNull(men_liked_items.getValue()).removeIf(item -> item.getType().equals(type));
        }
        else{
            Objects.requireNonNull(women_liked_items.getValue()).removeIf(item -> item.getType().equals(type));
        }
    }
    public LiveData<ArrayList<RecyclerItem>> getItems(){
        return new_items;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setNew_items(MutableLiveData<ArrayList<RecyclerItem>> items) {
        Objects.requireNonNull(this.new_items.getValue()).clear();
        this.new_items.getValue().addAll(Objects.requireNonNull(items.getValue()));
        ArrayList<RecyclerItem> temp = new_items.getValue();
        new_items.postValue(temp);
    }
    public void addMenItem(RecyclerItem recyclerItem){
        Objects.requireNonNull(men_new_items.getValue()).add(recyclerItem);
    }
    public void addWomenItem(RecyclerItem recyclerItem){
        Objects.requireNonNull(women_new_items.getValue()).add(recyclerItem);
    }
    public void addMen_new_num(String key, Integer value) {
        Objects.requireNonNull(this.men_new_num.getValue()).put(key,value);
    }
    public void addWomen_new_num(String key,Integer value) {
        Objects.requireNonNull(this.women_new_num.getValue()).put(key,value);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setList(String gender){
        if(gender.equals(Macros.CustomerMacros.WOMEN))
            setNew_items(this.women_new_items);
        else
            setNew_items(this.men_new_items);
    }
}
