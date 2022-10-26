package com.eitan.shopik.customer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.database.Database;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerSettingsActivity extends AppCompatActivity {

    private EditText mFirstNameField,mLastNameField,mAge,mAddress,mCity,mPhone;
    private TextView mGender;
    private ImageView bgImage;
    private CircleImageView toolbar_pic;
    private ExtendedFloatingActionButton mSubmitButton;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBar;

    private String tempCoverPic;

    private Database mDatabase;
    private ShopikUser mShopikUser;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 ) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri resultUri = data.getData();
                bgImage.setImageURI(resultUri);
                if (resultUri != null) {

                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().
                            child("coverImages").child(mShopikUser.getId());
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().
                                getContentResolver(), resultUri);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);

                    Glide.with(getApplicationContext()).load(bitmap).into(bgImage);

                    byte[] data2 = baos.toByteArray();

                    UploadTask uploadTask = filePath.putBytes(data2);
                    uploadTask.addOnSuccessListener(taskSnapshot ->
                            filePath.getDownloadUrl().addOnSuccessListener(uri -> tempCoverPic = uri.toString()));
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();

        setView();

        appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if(verticalOffset <= -300) {
                toolbar_pic.setVisibility(View.VISIBLE);
            }
            else {
                toolbar_pic.setVisibility(View.INVISIBLE);
            }
        });

        bgImage.setOnLongClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,2);
            return true;
        });

        mSubmitButton.setOnClickListener(v -> {
            saveUserInformation();
            setView();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.supportFinishAfterTransition();
    }

    private void initialize() {
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setNavigationBarButtonsColor(getWindow().getNavigationBarColor());
        setContentView(R.layout.fragment_settings);
        mShopikUser = ShopikUser.getInstance();
        mDatabase = Database.getInstance();

        bgImage = findViewById(R.id.bgImage);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        appBar = findViewById(R.id.appbar);
        toolbar_pic = findViewById(R.id.toolbar_pic);
        mFirstNameField = findViewById(R.id.first_name);
        mLastNameField = findViewById(R.id.last_name);
        mAge = findViewById(R.id.age);
        mAddress = findViewById(R.id.address);
        mCity = findViewById(R.id.city);
        mPhone = findViewById(R.id.phone);
        mSubmitButton = findViewById(R.id.submit_btn);
        mGender = findViewById(R.id.gender_view);
        tempCoverPic = null;
    }
    private void setNavigationBarButtonsColor(int navigationBarColor) {
        View decorView = getWindow().getDecorView();
        int flags = decorView.getSystemUiVisibility();
        if (isColorLight(navigationBarColor)) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        else {
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        decorView.setSystemUiVisibility(flags);
    }
    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color)
                + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }
    private void setCollapsingBar() {
        toolbar_pic.setBorderColor(Color.WHITE);
        final Typeface typeface = ResourcesCompat.getFont(this, R.font.cutive);
        collapsingToolbar.setCollapsedTitleTypeface(typeface);
        collapsingToolbar.setExpandedTitleTypeface(typeface);
        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbar.setExpandedTitleColor(Color.WHITE);
        collapsingToolbar.setContentScrimColor(getColor(R.color.CompanyProfileScrim));
    }
    private void setView() {
        mFirstNameField.setText(mShopikUser.getFirstName());
        mLastNameField.setText(mShopikUser.getLastName());
        Macros.Functions.GlidePicture(this, mShopikUser.getImageUrl(), toolbar_pic, 0);
        Macros.Functions.GlidePicture(this, mShopikUser.getCoverPhoto(), bgImage, 0);
        mAge.setText(mShopikUser.getAge());
        mCity.setText(mShopikUser.getCity());
        mAddress.setText(mShopikUser.getAddress());
        mPhone.setText(mShopikUser.getPhone());
        mGender.setText(mShopikUser.getGender());
        setCollapsingBar();
    }
    private void saveUserInformation() {
        try {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("first_name", mFirstNameField.getText().toString());
            userInfo.put("last_name", mLastNameField.getText().toString());
            userInfo.put("age", mAge.getText().toString());
            userInfo.put("city", mCity.getText().toString());
            userInfo.put("address", mAddress.getText().toString());
            userInfo.put("phone", mPhone.getText().toString());
            userInfo.put("gender", mGender.getText().toString());
            if(tempCoverPic != null) {
                userInfo.put("cover_photo", tempCoverPic);
                mShopikUser.setCoverPhoto(tempCoverPic);
            }

            mShopikUser.setPhone(userInfo.get("phone").toString());
            mShopikUser.setAddress(userInfo.get("address").toString());
            mShopikUser.setFirstName(userInfo.get("first_name").toString());
            mShopikUser.setLastName(userInfo.get("last_name").toString());
            mShopikUser.setGender(userInfo.get("gender").toString());
            mShopikUser.setCity(userInfo.get("city").toString());
            mShopikUser.setAge(userInfo.get("age").toString());

            collapsingToolbar.setTitle(mShopikUser.getFirstName() +" "+ mShopikUser.getLastName());
            mDatabase.updateUserData(userInfo);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
