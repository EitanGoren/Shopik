package com.eitan.shopik;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.eitan.shopik.customer.GenderFilteringActivity;
import com.eitan.shopik.customer.ShopikUser;
import com.eitan.shopik.system.SystemUpdates;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class NoInternetActivity extends AppCompatActivity {

    MaterialButton mTryAgainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        EventBus.getDefault().register(this);

        mTryAgainButton = findViewById(R.id.try_again_btn);
        mTryAgainButton.setOnClickListener(view -> {
            Macros.Functions.showSnackbar(
                    view,
                    "No Internet connection",
                    this,
                    R.drawable.ic_round_signal_wifi_connected_no_internet_4_24,
                    Macros.SnackbarGravity.BOTTOM
            );
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        RelativeLayout relativeLayout = findViewById(R.id.no_internet_layout);
        Snackbar snackbar = Snackbar.make(relativeLayout, "No Internet connection", Snackbar.LENGTH_SHORT);
        View snackbarLayout = snackbar.getView();

        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)snackbarLayout.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbarLayout.setLayoutParams(params);

        TextView textView = snackbarLayout.findViewById(R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_round_signal_wifi_connected_no_internet_4_24, 0);
        textView.setAllCaps(true);
        textView.setBackgroundColor(Color.TRANSPARENT);
        snackbarLayout.setBackgroundColor(this.getColor(R.color.SnackbarBackground));
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(16);
        snackbar.show();
    }

    @Subscribe
    public void onInternetConnectionChanged(SystemUpdates.InternetConnectionChanged internetConnectionChanged){
        if(internetConnectionChanged.isConnected)
            HandleReconnectedEvent();
    }
    private void HandleReconnectedEvent() {
        Intent intent;
        if(ShopikUser.getInstance().isAuthenticated())
            intent = new Intent(NoInternetActivity.this, GenderFilteringActivity.class);
        else {
            intent = new Intent(NoInternetActivity.this, LandingPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        startActivity(intent);
        finish();
    }
}