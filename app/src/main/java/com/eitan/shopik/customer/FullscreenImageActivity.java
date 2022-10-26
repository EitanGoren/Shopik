package com.eitan.shopik.customer;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.eitan.shopik.R;
import com.eitan.shopik.adapters.FullscreenAdapter;
import com.eitan.shopik.ads.AdMobBannerAd;
import com.google.android.gms.ads.AdView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class FullscreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrepareScreen();
        init();
    }

    private void PrepareScreen() {
        postponeEnterTransition();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.borderLine));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setStatusBarContrastEnforced(true);
        }
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        View decorView = window.getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_fullscreen_image);

        startPostponedEnterTransition();
    }

    private void init() {
        TextView title = findViewById(R.id.title);
        TextView description = findViewById(R.id.description);
        TextView price = findViewById(R.id.price);
        TextView old_price = findViewById(R.id.old_price);

        FloatingActionButton mClose = findViewById(R.id.close_fs);
        mClose.setOnClickListener(v -> finish());

        Bundle bundle = getIntent().getBundleExtra("package");
        ArrayList<String> images = Objects.requireNonNull(bundle).getStringArrayList("images");

        ArrayList<String> _description = Objects.requireNonNull(bundle).getStringArrayList("description");
        String _price = bundle.getString("price");
        String _old_price = bundle.getString("old_price");
        String _seller = bundle.getString("seller");
        String _brand = bundle.getString("brand");

        title.setText(_brand + " by " + _seller);
        price.setText(_price);
        old_price.setText(_old_price);

        description.setText(_description.toString());

        ViewPager2 viewPager = findViewById(R.id.fullscreen_image_viewPager);
        viewPager.setAdapter(new FullscreenAdapter(this, images));

        //Google AdMob Banner Ad
        AdMobBannerAd adMobBannerAd = AdMobBannerAd.getInstance();
        AdView adView = findViewById(R.id.adView);
        adMobBannerAd.loadNewAd(adView);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
