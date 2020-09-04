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

    private Button mClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_fullscreen_image);

        init();

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

        mClose = findViewById(R.id.fullscreen_close_x);
        ViewPager2 viewPager = findViewById(R.id.fullscreen_image_viewPager);
        viewPager.setAdapter(new FullscreenAdapter(imagesUrl,description,id,category));
    }

    /*
    private class fullscreenPicsAdapter extends RecyclerView.Adapter<fullscreenPicsAdapter.PicsViewHolder> {

        @NonNull
        @Override
        public PicsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PicsViewHolder(LayoutInflater.from(parent.getContext()).
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

        private void changeTabs(int position) {
            switch (position){
                case 0:
                    mDot1.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_lens_black_24dp));
                    mDot2.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_baseline_panorama_fish_eye));
                    break;
                case 1:
                    mDot1.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_baseline_panorama_fish_eye));
                    mDot2.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_lens_black_24dp));
                    mDot3.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_baseline_panorama_fish_eye));
                    break;
                case 2:
                    mDot2.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_baseline_panorama_fish_eye));
                    mDot3.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_lens_black_24dp));
                    mDot4.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_baseline_panorama_fish_eye));
                    break;
                case 3:
                    mDot3.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_baseline_panorama_fish_eye));
                    mDot4.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_lens_black_24dp));
                    mDot5.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_baseline_panorama_fish_eye));
                    break;
                case 4:
                    mDot4.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_baseline_panorama_fish_eye));
                    mDot5.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_lens_black_24dp));
                    break;
            }
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
                dots = findViewById(R.id.fullscreen_dotsLayout);
                button = findViewById(R.id.close_card);
                buttons_layout = findViewById(R.id.buttons_layout);
            }

            private void setData(int position){
                textView.setText(description);
                String no_video_text = "No video for this item";
                no_video.setText(no_video_text);

                if (position != imagesUrl.size()) {
                    videoLayout.setVisibility(View.INVISIBLE);
                    videoMediaController.hide();
                    photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    photoView.setPadding(0,0,0,0);
                    Macros.Functions.GlidePicture(photoView.getContext(),imagesUrl.get(position),photoView);
                    mVideoView.pause();
                    videoMediaController.hide();
                }

                viewPager.registerOnPageChangeCallback (new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                    @Override
                    public void onPageSelected(int pos) {
                        changeTabs(pos);
                        if(category.equals("ASOS") && (pos == imagesUrl.size())) {
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
                               getVideoLink getVideoLink = new getVideoLink(id);
                               getVideoLink.execute();
                            }

                            videoMediaController.setAnchorView(anchor);
                            mVideoView.setOnPreparedListener(mp -> {
                                mVideoView.requestFocus();
                                mVideoView.start();
                            });

                            mVideoView.setOnCompletionListener(MediaPlayer::start);
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

            private class getVideoLink extends AsyncTask<Void,Integer,String> {

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
                        Log.d(Macros.TAG, "FullscreenActivity:getVideoLink: " + Objects.requireNonNull(e.getMessage()));
                    }
                    return video_path;
                }

                private void getVideo(String id) throws IOException {

                    URL url = new URL("https://video.asos-media.com/products/0/" + id + "-catwalk-AVS.m3u8");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    try {

                        StringBuilder data = new StringBuilder();

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
                        inputStream.close();
                        bufferedReader.close();
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
                        photoView.setImageResource(R.drawable.tooki);
                        photoView.setPadding(25,40,25,40);
                        no_video.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.supportFinishAfterTransition();
    }
}
