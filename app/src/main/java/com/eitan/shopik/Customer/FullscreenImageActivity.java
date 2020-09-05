package com.eitan.shopik.Customer;

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
import androidx.viewpager2.widget.ViewPager2;

import com.eitan.shopik.Adapters.FullscreenAdapter;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FullscreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postponeEnterTransition();
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_fullscreen_image);

        init();

        Button mClose = findViewById(R.id.fullscreen_close_x);
        mClose.setOnClickListener(v -> this.supportFinishAfterTransition());
    }

    private void init() {

        ImageView mFavorite = findViewById(R.id.favorite_sign);
        CircleImageView company = findViewById(R.id.comp_logo);
        TextView name = findViewById(R.id.comp_name);

        String brand = getIntent().getStringExtra("brand");
        name.setText(brand);
        String seller_logo = getIntent().getStringExtra("seller_logo");
        Macros.Functions.GlidePicture(this, seller_logo, company);
        String category = getIntent().getStringExtra("type");
        boolean isFavorite = getIntent().getBooleanExtra("isFav", false);
        String description = getIntent().getStringExtra("description");
        String id = getIntent().getStringExtra("id");

        ArrayList<String> imagesUrl = new ArrayList<>();
        imagesUrl.add(getIntent().getStringExtra("img1"));
        imagesUrl.add(getIntent().getStringExtra("img2"));
        imagesUrl.add(getIntent().getStringExtra("img3"));
        imagesUrl.add(getIntent().getStringExtra("img4"));

        if(isFavorite) {
            mFavorite.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.blink_anim);
            mFavorite.startAnimation(animation);
        }
        else
            mFavorite.setVisibility(View.INVISIBLE);

        ViewPager2 viewPager = findViewById(R.id.fullscreen_image_viewPager);
        viewPager.setAdapter(new FullscreenAdapter(imagesUrl,description,id,category));
        startPostponedEnterTransition();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.supportFinishAfterTransition();
    }
}
