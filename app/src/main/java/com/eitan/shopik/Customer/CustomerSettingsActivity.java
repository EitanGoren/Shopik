package com.eitan.shopik.Customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.eitan.shopik.Company.CompanyProfileActivity;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.GenderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerSettingsActivity extends AppCompatActivity {

    private EditText mFirstNameField,mLastNameField,mAge,mAddress,mCity,mPhone;
  //  private TextView mEmail;
    private ImageView bgImage;
    private CircleImageView mProfileImage,toolbar_pic;
    private String UserId;
    private String first_name;
    private String last_name;
    private String profileImageUrl;
    private String cover;
    private String age;
    private String address;
    private String city;
    private String phone;
    private String email;//TODO: RETURN THIS EMAIL SHIT...
    private DocumentReference customerFS;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBar;
    private Map data;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 ) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri resultUri = data.getData();
                mProfileImage.setImageURI(resultUri);
                toolbar_pic.setImageURI(resultUri);
                if (resultUri != null) {

                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profileImages").child(UserId);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(Macros.TAG, Objects.requireNonNull(e.getMessage()));
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);

                    Glide.with(getApplicationContext()).load(bitmap).into(mProfileImage);
                    Glide.with(getApplicationContext()).load(bitmap).into(toolbar_pic);

                    byte[] data2 = baos.toByteArray();

                    UploadTask uploadTask = filePath.putBytes(data2);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(Macros.TAG, "failed to load customer picture:" + e.getMessage());
                        }
                    });

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map<String, Object> userInfo = new HashMap<String, Object>();
                                    userInfo.put("imageUrl", uri.toString());
                                    profileImageUrl = uri.toString();
                                    customerFS.update(userInfo);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.d(Macros.TAG, "failed to load customer picture:" + exception.getMessage());
                                }
                            });
                        }
                    });

                }
            }
        }
        if(requestCode == 2 ) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri resultUri = data.getData();
                bgImage.setImageURI(resultUri);
                if (resultUri != null) {

                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("coverImages").child(UserId);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(Macros.TAG, Objects.requireNonNull(e.getMessage()));
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);

                    Glide.with(getApplicationContext()).load(bitmap).into(bgImage);

                    byte[] data2 = baos.toByteArray();

                    UploadTask uploadTask = filePath.putBytes(data2);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            return;
                        }
                    });

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map<String, Object> userInfo = new HashMap<String, Object>();
                                    userInfo.put("cover_photo", uri.toString());
                                    cover = uri.toString();
                                    customerFS.update(userInfo);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.d(Macros.TAG, "failed to load customer picture:" + exception.getMessage());

                                }
                            });
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        init();

        customerFS.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                data = documentSnapshot.getData();
                assert data != null;
                age = documentSnapshot.get("age") != null ? documentSnapshot.get("age").toString() : null;
                first_name = documentSnapshot.get("first_name") != null ? documentSnapshot.get("first_name").toString() : null;
                last_name = documentSnapshot.get("last_name") != null ? documentSnapshot.get("last_name").toString() : null;
                address = documentSnapshot.get("address") != null ? documentSnapshot.get("address").toString() : null;
                city =  documentSnapshot.get("city") != null ? documentSnapshot.get("city").toString() : null;
                phone = documentSnapshot.get("phone") != null ? documentSnapshot.get("phone").toString() : null;
                profileImageUrl = documentSnapshot.get("imageUrl") != null ? documentSnapshot.get("imageUrl").toString() : Macros.DEFAULT_PROFILE_IMAGE;
                email = documentSnapshot.get("email") != null ? documentSnapshot.get("email").toString() : null;
                cover = documentSnapshot.get("cover_photo") != null ? documentSnapshot.get("cover_photo").toString() : Macros.DEFAULT_COVER_PHOTO;
            }
        }).addOnCompleteListener(task -> {

            mFirstNameField.setText(first_name);
            mLastNameField.setText(last_name);
            Glide.with(getApplicationContext()).load(profileImageUrl).into(mProfileImage);
            Glide.with(getApplicationContext()).load(profileImageUrl).into(toolbar_pic);
            Glide.with(getApplicationContext()).load(cover).into(bgImage);
            mAge.setText(age);
            mCity.setText(city);
            mAddress.setText(address);
            mPhone.setText(phone);
           // mEmail.setText(email);
            setTitle();
            setCollapsingBar();

        }).addOnFailureListener(e -> Log.d(Macros.TAG, Objects.requireNonNull(e.getMessage())));

        appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if(verticalOffset <= -550)
                toolbar_pic.setVisibility(View.VISIBLE);
            else
                toolbar_pic.setVisibility(View.INVISIBLE);
        });

        mProfileImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
                return true;
            }
        });

        bgImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,2);
                return true;
            }
        });
    }

    private void setNavigationBarButtonsColor(int navigationBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (isColorLight(navigationBarColor)) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            decorView.setSystemUiVisibility(flags);
        }
    }

    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }

    private void setCollapsingBar() {

        toolbar_pic.setBorderColor(Color.WHITE);

        final Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto_medium);
        collapsingToolbar.setCollapsedTitleTypeface(typeface);
        collapsingToolbar.setExpandedTitleTypeface(typeface);
        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbar.setContentScrimColor(getColor(R.color.CompanyProfileScrim));

        final ImageView imageView = bgImage;
        Glide.with(getApplicationContext()).asBitmap().load(cover).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                imageView.setImageBitmap(resource);
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                int height = bitmap.getHeight();

                checkBottomLayoutColor(bitmap, height);
            }
            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {}
        });
    }

    private void setTitle() {
        String name = first_name +" "+ last_name;
        collapsingToolbar.setTitle(name);
    }

    private void init() {

        setNavigationBarButtonsColor(getWindow().getNavigationBarColor());

        bgImage = findViewById(R.id.bgImage);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        appBar = findViewById(R.id.appbar);
        toolbar_pic = findViewById(R.id.toolbar_pic);
        mFirstNameField = findViewById(R.id.first_name);
        mLastNameField = findViewById(R.id.last_name);
        mProfileImage = findViewById(R.id.logo);
        mAge = findViewById(R.id.age);
        mAddress = findViewById(R.id.address);
        mCity = findViewById(R.id.city);
        mPhone = findViewById(R.id.phone);
       // mEmail = findViewById(R.id.email_text);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        UserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        customerFS = FirebaseFirestore.getInstance().collection(Macros.CUSTOMERS).document(UserId);
    }

    private void saveUserInformation() {

        try {
            first_name = mFirstNameField.getText().toString();
            last_name = mLastNameField.getText().toString();
            city = mCity.getText().toString();
            phone = mPhone.getText().toString();
            age = mAge.getText().toString();
            address = mAddress.getText().toString();

            Map<String, Object> userInfo = new HashMap<String, Object>();
            userInfo.put("first_name", first_name);
            userInfo.put("last_name", last_name);
            userInfo.put("age", age);
            userInfo.put("city", city);
            userInfo.put("address", address);
            userInfo.put("phone", phone);

            customerFS.update(userInfo);
            setTitle();
        }
        catch (Exception e){
            Log.d(Macros.TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveUserInformation();
    }

    private void checkBottomLayoutColor(Bitmap bitmap, int height){

        int pixel_bottom_left = bitmap.getPixel(50,height - 50 );
        int red_bottom = Color.red(pixel_bottom_left);
        int blue_bottom  = Color.blue(pixel_bottom_left);
        int green_bottom  = Color.green(pixel_bottom_left);

        if( (red_bottom < 115 && blue_bottom < 115) ||
                (green_bottom < 115 && red_bottom < 115) ||
                (green_bottom < 115 && blue_bottom < 115) ) {

            collapsingToolbar.setExpandedTitleColor(Color.WHITE);
            mProfileImage.setBorderColor(Color.WHITE);
        }
        else {
            collapsingToolbar.setExpandedTitleColor(Color.BLACK);
            mProfileImage.setBorderColor(Color.BLACK);
        }
    }

}
