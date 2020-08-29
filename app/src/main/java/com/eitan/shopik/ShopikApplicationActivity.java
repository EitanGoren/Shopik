package com.eitan.shopik;

import android.app.Application;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Collections;
import java.util.List;

import static com.facebook.ads.AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE;

public class ShopikApplicationActivity extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Google Ads
        List<String> testDeviceIds = Collections.singletonList(Macros.TEST_DEVICE_ID);
        RequestConfiguration configuration = new RequestConfiguration.Builder().
                setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        //Facebook Ads
        AdSettings.setIntegrationErrorMode(INTEGRATION_ERROR_CRASH_DEBUG_MODE);
        AudienceNetworkAds.initialize(this);
    }
}