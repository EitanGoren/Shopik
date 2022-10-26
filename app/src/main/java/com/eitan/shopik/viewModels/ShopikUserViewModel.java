package com.eitan.shopik.viewModels;

import androidx.lifecycle.MutableLiveData;

public class ShopikUserViewModel {

    private static ShopikUserViewModel instance;
    private final MutableLiveData<Boolean> mOnUpdatedUser;

    private ShopikUserViewModel(){
        mOnUpdatedUser = new MutableLiveData<>();
    }

    public static ShopikUserViewModel getInstance(){
        if (instance == null)
            instance = new ShopikUserViewModel();
        return instance;
    }

    public void publishUserDataChanged(boolean isChanged){
        mOnUpdatedUser.postValue(isChanged);
    }

    public MutableLiveData<Boolean> subscribeUserDataChanged() {
        return mOnUpdatedUser;
    }
}
