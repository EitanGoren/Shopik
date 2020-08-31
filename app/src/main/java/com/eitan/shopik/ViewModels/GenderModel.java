package com.eitan.shopik.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.facebook.ads.InterstitialAd;

public class GenderModel extends AndroidViewModel {

    private MutableLiveData<String> gender, type, imageUrl,name, sub_category;
    private com.facebook.ads.InterstitialAd interstitialAd;

    public GenderModel(Application application){
        super(application);
        this.gender = new MutableLiveData<>();
        this.type = new MutableLiveData<>();
        this.name = new MutableLiveData<>();
        this.imageUrl = new MutableLiveData<>();
        this.sub_category = new MutableLiveData<>();
    }

    public LiveData<String> getGender(){
        return gender;
    }

    public LiveData<String> getType(){
        return type;
    }

    public LiveData<String> getName(){
        return name;
    }

    public LiveData<String> getImageUrl(){
        return imageUrl;
    }

    public LiveData<String> getSub_category() {
        return sub_category;
    }

    public void setGender(String gender){
        this.gender.setValue(gender);
    }

    public void setType(String type){
        this.type.setValue(type);
    }

    public void setName(String name){
        this.name.setValue(name);
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl.setValue(imageUrl);
    }

    public void setSub_category(String sub_category) {
        this.sub_category.setValue(sub_category);
    }

    public InterstitialAd getInterstitialAd() {
        return interstitialAd;
    }

    public void setInterstitialAd(InterstitialAd interstitialAd) {
        this.interstitialAd = interstitialAd;
    }

    public void destroyInterstitialAd(){
        this.interstitialAd.destroy();
        this.interstitialAd = null;
    }
}
