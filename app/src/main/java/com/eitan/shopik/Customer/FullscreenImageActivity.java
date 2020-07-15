package com.eitan.shopik.Customer;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class FullscreenImageActivity extends AppCompatActivity {

    private Button mClose;
    private ViewPager2 viewPager;
    private ImageView mDot1,mDot2,mDot3,mDot4,mDot5;
    private View decorView;
    private boolean isClicked = false;
    private VideoView mVideoView;
    private MediaController videoMediaController;
    private ImageView photoView;
    private RelativeLayout videoLayout,anchor;
    private TextView no_video;
    private TextView textView;
    private RelativeLayout dots;
    private CardView button;
    private RelativeLayout buttons_layout;
    private String path;

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        MobileAds.initialize(this, initializationStatus -> {});

        setDecoreView();

        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if(visibility == 0) decorView.setSystemUiVisibility(hideSystemBars());
        });

        init();

        mClose.setOnClickListener(v -> finish());
    }

    private void init() {

        boolean isFavorite = false;
        Bundle bundle = getIntent().getBundleExtra("bundle");
        ImageView mFavorite = findViewById(R.id.favorite_sign);

        assert bundle != null;
        Object item = bundle.getSerializable("item");
        if(item instanceof ShoppingItem){
              //object = (ShoppingItem)item;
              isFavorite = ((ShoppingItem) item).isFavorite();
        }
        else if(item instanceof RecyclerItem){
            //object = (RecyclerItem)item;
            isFavorite = false;
        }

        mDot1 = findViewById(R.id.fullscreen_dot_1);
        mDot2 = findViewById(R.id.fullscreen_dot_2);
        mDot3 = findViewById(R.id.fullscreen_dot_3);
        mDot4 = findViewById(R.id.fullscreen_dot_4);
        mDot5 = findViewById(R.id.fullscreen_dot_5);

        mDot1.setBackground(getDrawable(R.drawable.ic_lens_black_24dp));
        mDot2.setBackground(getDrawable(R.drawable.ic_baseline_panorama_fish_eye));
        mDot3.setBackground(getDrawable(R.drawable.ic_baseline_panorama_fish_eye));
        mDot4.setBackground(getDrawable(R.drawable.ic_baseline_panorama_fish_eye));
        mDot5.setBackground(getDrawable(R.drawable.ic_baseline_panorama_fish_eye));

        if(isFavorite) {
            mFavorite.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.blink_anim);
            mFavorite.startAnimation(animation);
        }
        else
            mFavorite.setVisibility(View.INVISIBLE);

        mClose = findViewById(R.id.fullscreen_close_x);
        viewPager = findViewById(R.id.fullscreen_image_viewPager);
        fullscreenPicsAdapter picsAdapter = new fullscreenPicsAdapter(item);
        viewPager.setAdapter(picsAdapter);
    }

    private void setDecoreView() {
        decorView = getWindow().getDecorView();
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    private int hideSystemBars(){
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY ;
    }

    private class fullscreenPicsAdapter extends RecyclerView.Adapter<fullscreenPicsAdapter.PicsViewHolder> {

        private ArrayList<String> imagesUrl;
        private String text = "";
        private String item_id;

        public fullscreenPicsAdapter(Object o){
            if(o instanceof ShoppingItem){
                imagesUrl = ((ShoppingItem) o).getImages();
                StringBuilder description = new StringBuilder();
                for(String word : ((ShoppingItem) o).getName()) {
                    description.append(word.toLowerCase().concat(" "));
                }
                text = description.toString();
                item_id = ((ShoppingItem) o).getId();
            }
            else {
                imagesUrl = ((RecyclerItem) o).getImages();
                StringBuilder description = new StringBuilder();
                for(String word : ((RecyclerItem) o).getDescription()) {
                    description.append(word.toLowerCase().concat(" "));
                }
                text = description.toString();
                item_id = ((RecyclerItem) o).getId();
            }
        }

        @NonNull
        @Override
        public PicsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PicsViewHolder(LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.fullscreen_pic,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull PicsViewHolder holder, int position) {
            holder.setData(position);
        }

        @Override
        public int getItemCount() {
            return imagesUrl.size() + 1;
        }

        class PicsViewHolder extends RecyclerView.ViewHolder{
            public PicsViewHolder(@NonNull View view) {
                super(view);
                photoView = view.findViewById(R.id.fullscreen_image_item);
                mVideoView = view.findViewById(R.id.item_video);
                videoMediaController = new MediaController(mVideoView.getContext());
                videoLayout = view.findViewById(R.id.video_layout);
                videoLayout.setVisibility(View.INVISIBLE);
                anchor = view.findViewById(R.id.video);
                textView = view.findViewById(R.id.fullscreen_item_info);
                no_video = view.findViewById(R.id.No_video_text);
                dots = findViewById(R.id.dots);
                button = findViewById(R.id.close_card);
                buttons_layout = findViewById(R.id.buttons_layout);
            }

            private void setData(int position){
                textView.setText(text);
                String no_video_text = "No video for this item" + System.lineSeparator() + text;
                no_video.setText(no_video_text);

                if (position != 4) {
                    videoLayout.setVisibility(View.INVISIBLE);
                    videoMediaController.hide();
                    photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    photoView.setPadding(0,0,0,0);
                    Glide.with(photoView.getContext()).load(imagesUrl.get(position)).into(photoView);
                    mVideoView.pause();
                    videoMediaController.hide();
                }

                viewPager.registerOnPageChangeCallback (new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                    @Override
                    public void onPageSelected(int pos) {
                        changeTabs(pos);
                        if(pos == 4) {
                            dots.setVisibility(View.VISIBLE);
                            button.setVisibility(View.VISIBLE);
                            buttons_layout.setVisibility(View.VISIBLE);

                            if(path != null) {
                                videoLayout.setVisibility(View.VISIBLE);
                                mVideoView.setVideoPath(path);
                                mVideoView.setMediaController(videoMediaController);
                                no_video.setVisibility(View.INVISIBLE);
                            }
                            else {
                               getVideoLink getVideoLink = new getVideoLink(item_id);
                               getVideoLink.execute();
                            }

                            videoMediaController.setAnchorView(anchor);
                            mVideoView.setOnPreparedListener(mp -> {
                                mVideoView.requestFocus();
                                mVideoView.start();
                            });
                        }
                        else
                            photoView.setOnLongClickListener(v -> {
                                if(isClicked){
                                    dots.setVisibility(View.VISIBLE);
                                    button.setVisibility(View.VISIBLE);
                                    buttons_layout.setVisibility(View.VISIBLE);
                                }
                                else {
                                    dots.setVisibility(View.INVISIBLE);
                                    button.setVisibility(View.INVISIBLE);
                                    buttons_layout.setVisibility(View.INVISIBLE);
                                }
                                isClicked = !isClicked;
                                return true;
                            });
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {}
                });
            }

            protected class getVideoLink extends AsyncTask<Void,Integer,String> {

                String id,video_path;

                getVideoLink(String id){
                    this.id = id;
                }

                @Override
                protected String doInBackground(Void... voids) {

                    String video_path ="";
                    try {
                        getVideo(id);
                    }
                    catch (Exception e){
                        Log.d(Macros.TAG, "e3fragment-getItems: " + Objects.requireNonNull(e.getMessage()));
                    }
                    return video_path;
                }

                private void getVideo(String id) throws IOException {
                    URL url = new URL("https://video.asos-media.com/products/0/" + id + "-catwalk-AVS.m3u8");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    try {

                        StringBuilder data = new StringBuilder();

                        InputStream inputStream = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                        String line = "";
                        int i=0;
                        while (line != null && i < 5) {
                            line = bufferedReader.readLine();
                            data.append(line);
                            ++i;
                        }
                        String[] data_split = data.toString().split("_media_",2);
                        String koko = data_split[1];
                        koko = koko.replace(".m3u8","");

                        video_path = koko;
                    }
                    catch (FileNotFoundException ex){
                        video_path = null;
                    }
                    catch(Exception e){
                        Log.d(Macros.TAG, Objects.requireNonNull(e.getMessage()));
                    }
                    finally {
                        httpURLConnection.disconnect();
                    }
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if(video_path != null) {
                            videoLayout.setVisibility(View.VISIBLE);
                            path = Macros.VIDEO_LINK + video_path;
                            mVideoView.setVideoPath(path);
                            mVideoView.setMediaController(videoMediaController);
                            no_video.setVisibility(View.INVISIBLE);
                        }
                    else {
                            videoLayout.setVisibility(View.INVISIBLE);
                            photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            photoView.setImageResource(R.drawable.animal1);
                            photoView.setPadding(25,0,25,0);
                            no_video.setVisibility(View.VISIBLE);
                        }
                }
            }
        }

        private void changeTabs(int position) {
            switch (position){
                case 0:
                    mDot1.setBackground(getDrawable(R.drawable.ic_lens_black_24dp));
                    mDot2.setBackground(getDrawable(R.drawable.ic_baseline_panorama_fish_eye));
                    break;
                case 1:
                    mDot1.setBackground(getDrawable(R.drawable.ic_baseline_panorama_fish_eye));
                    mDot2.setBackground(getDrawable(R.drawable.ic_lens_black_24dp));
                    mDot3.setBackground(getDrawable(R.drawable.ic_baseline_panorama_fish_eye));
                    break;
                case 2:
                    mDot2.setBackground(getDrawable(R.drawable.ic_baseline_panorama_fish_eye));
                    mDot3.setBackground(getDrawable(R.drawable.ic_lens_black_24dp));
                    mDot4.setBackground(getDrawable(R.drawable.ic_baseline_panorama_fish_eye));
                    break;
                case 3:
                    mDot3.setBackground(getDrawable(R.drawable.ic_baseline_panorama_fish_eye));
                    mDot4.setBackground(getDrawable(R.drawable.ic_lens_black_24dp));
                    mDot5.setBackground(getDrawable(R.drawable.ic_baseline_panorama_fish_eye));
                    break;
                case 4:
                    mDot4.setBackground(getDrawable(R.drawable.ic_baseline_panorama_fish_eye));
                    mDot5.setBackground(getDrawable(R.drawable.ic_lens_black_24dp));
                    break;
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
    }
}
