package com.eitan.shopik.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eitan.shopik.database.Database;


public class DatabaseViewModel extends ViewModel {

    private static DatabaseViewModel instance;

    public MutableLiveData<Boolean> mNewUserRegistered;

    public static DatabaseViewModel getInstance(){
        if (instance == null)
            instance = new DatabaseViewModel();
        return instance;
    }

    private DatabaseViewModel(){
        mNewUserRegistered = new MutableLiveData<>();
    }

    public void postNewUserRegistered(){
        this.mNewUserRegistered.postValue(true);
    }

    public void updateTopWords(String category, String gender){
        Database.getInstance().getTopWords(category, gender);
    }
}
