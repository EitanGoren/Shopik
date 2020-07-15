package com.eitan.shopik.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

public class CompanyViewModel extends AndroidViewModel {

    private MutableLiveData<Map<String,Object>> data;
    private String item_type;

    public CompanyViewModel(Application application) {
        super(application);

        this.data = new MutableLiveData<>();
        Map data = new HashMap();
        this.data.setValue(data);
    }

    public LiveData<Map<String, Object>> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data.setValue(data);
    }
}

