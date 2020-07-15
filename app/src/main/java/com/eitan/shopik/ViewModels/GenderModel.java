package com.eitan.shopik.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GenderModel extends ViewModel {

    private MutableLiveData<String> gender, type, imageUrl,name, sub_category;

    public GenderModel(){
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
}
