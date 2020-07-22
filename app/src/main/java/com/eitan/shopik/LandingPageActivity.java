package com.eitan.shopik;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Company.CompanyMainActivity;
import com.eitan.shopik.Customer.GenderFilteringActivity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class LandingPageActivity extends AppCompatActivity {

    private static String type,provider,token,email,imageUrl,id_in_provider;
    private CircleImageView imageView;
    private TextView welcome;
    private static FirebaseUser user;
    private FirebaseFirestore db;
    private int currentApiVersion;
    private Switch is_company;
    private static final int DELAY_MILLIS = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        init();

        if(!isConnectedToInternet()){
            RelativeLayout LandingLayout = findViewById(R.id.LandingLayout);
            Macros.Functions.showSnackbar (
                     LandingLayout,
                    "No Internet connection",
                    this,
                     R.drawable.ic_baseline_signal_cellular
            );
        }

        // FirebaseAuth.getInstance().signOut();
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            getWindow().getDecorView().setSystemUiVisibility(flags);
            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            });
        }

        if(user == null){
            //TODO SIGN IN SOMEONE NEW
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.FacebookBuilder().build());

            // You must provide a custom layout XML resource and configure at least one
            // provider button ID. It's important that that you set the button ID for every provider
            // that you have enabled.
            AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                    .Builder(R.layout.activity_auth)
                    .setGoogleButtonId(R.id.google_sign_in)
                    .setFacebookButtonId(R.id.facebook_sign_in)
                    .build();

            // Create and launch sign-in intent
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(false)
                    .setAuthMethodPickerLayout(customLayout)
                    .setTheme(R.style.SignInStyle)
                    .build(),1);
        }
        else {

            String[] user_name = Objects.requireNonNull(user.getDisplayName()).split(" ",2);
            String hi_txt = "Hi " + user_name[0];
            welcome.setText(hi_txt);

            DocumentReference user_document = db.collection(Macros.CUSTOMERS).document(user.getUid());
            DocumentReference company_document = db.collection(Macros.COMPANIES).document(user.getUid());

            user_document.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        if(document.get("imageUrl") != null) {
                            imageUrl = Objects.requireNonNull(document.get("imageUrl")).toString();
                            Glide.with(getApplicationContext()).load(imageUrl).into(imageView);
                        }
                        else
                            imageView.setImageResource(R.drawable.ic_account_circle_black_24dp);

                        type = Macros.CUSTOMER;
                        goToApp();
                    }
                }
                else {
                    Log.d(Macros.TAG,"Failed with" + task.getException());
                }
            });
            company_document.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {

                        if(document.get("logo_url") != null) {
                            imageUrl = Objects.requireNonNull(document.get("logo_url")).toString();
                            Glide.with(getApplicationContext()).load(imageUrl).into(imageView);
                        }
                        else
                            imageView.setImageResource(R.drawable.ic_account_circle_black_24dp);

                        type = Macros.COMPANY;
                        goToApp();
                    }
                }
                else {
                    Log.d(Macros.TAG,"Failed with" + task.getException());
                }
            });
        }
    }

    private void init() {

        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

        imageView = findViewById(R.id.image_profile);
        welcome = findViewById(R.id.welcome_signed_in);

        animateLayouts();

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    @SuppressLint("RestrictedApi")
    private void isCustomerOrCompany(final String user_id) {

        //is_company = findViewById(R.id.is_company);
        checkCustomer(user_id);
      //  checkCompany(user_id);
    }

    private void registerNewUser(){
        if(type == null){
            Intent intent = new Intent(LandingPageActivity.this, MainRegistrationActivity.class);
            intent.putExtra("id_in_provider",id_in_provider);
            intent.putExtra("imageUrl",imageUrl);
            intent.putExtra("token",token);
            intent.putExtra("email",email);
            intent.putExtra("provider",provider);
            startActivity(intent);
            finish();
        }
    }

    private void checkCompany(String user_id) {
        db.collection(Macros.COMPANIES).document(user_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    type = Macros.COMPANY;
                    goToApp();
                }
                else
                    registerNewUser();
            }
        });
    }

    private void checkCustomer(final String user_id) {
        db.collection(Macros.CUSTOMERS).document(user_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    type = Macros.CUSTOMER;
                    goToApp();
                }
                else
                    checkCompany(user_id);
            }
        });
    }

    private void goToApp(){
        if(user == null) {
            Intent intent = new Intent(LandingPageActivity.this, MainRegistrationActivity.class);
            intent.putExtra("id_in_provider",id_in_provider);
            intent.putExtra("imageUrl",imageUrl);
            intent.putExtra("token",token);
            intent.putExtra("email",email);
            intent.putExtra("provider",provider);
            startActivity(intent);
            finish();
        }
        else {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                switch (type){
                    case Macros.CUSTOMER:
                        goToCustomer();
                        break;
                    case Macros.COMPANY:
                        goToCompany();
                        break;
                }
            },DELAY_MILLIS);
        }
    }

    private void goToCustomer() {
        Intent intent = new Intent(LandingPageActivity.this, GenderFilteringActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imageUrl",imageUrl);
        bundle.putString("name",user.getDisplayName());
        intent.putExtra("bundle",bundle);
        startActivity(intent);
        finish();
    }

    private void goToCompany() {
        Intent intent = new Intent(LandingPageActivity.this, CompanyMainActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            assert response != null;
            if (resultCode == RESULT_OK) {

                // Successfully signed in
                User new_user = response.getUser();
                token = response.getIdpToken();
                user = FirebaseAuth.getInstance().getCurrentUser();
                final String[] user_name = Objects.requireNonNull(new_user.getName()).split(" ",2);
                String hi_txt = "Hi " + user_name[0];
                welcome.setText(hi_txt);
                provider = new_user.getProviderId();
                email = new_user.getEmail();

                animateLayouts();

                if (provider.equals(Macros.Providers.FACEBOOK)) {
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response1) -> {
                        try {
                            id_in_provider = object.getString("id");
                            imageUrl = "http://graph.facebook.com/" + id_in_provider + "/picture?type=large&width=720&height=720";
                            Glide.with(getApplicationContext()).load(imageUrl).into(imageView);
                            isCustomerOrCompany(user.getUid());
                        }
                        catch (JSONException e) {
                            Log.d(Macros.TAG,"facebook failed: " + e.getMessage());
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name");
                    request.setParameters(parameters);
                    request.executeAsync();
                }
                else if (provider.equals(Macros.Providers.GOOGLE)) {
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                    try {
                        if (acct != null) {
                            id_in_provider = acct.getId();
                            imageUrl = Objects.requireNonNull(acct.getPhotoUrl()).toString().split("=", 2)[0].concat("=s700-c");
                            Glide.with(getApplicationContext()).load(imageUrl).into(imageView);
                            isCustomerOrCompany(user.getUid());
                        }
                    } catch (Exception e) {
                        Log.d(Macros.TAG,"googleAuth failed: " + e.getMessage());
                    }
                }
            }
            else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                Log.d(Macros.TAG, Objects.requireNonNull(Objects.requireNonNull(response.getError()).getMessage()));
                //and handle the error.
                Toast.makeText(this, "Something went wrong.. try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void animateLayouts() {

        Animation top_anim = AnimationUtils.loadAnimation(this, R.anim.top_lines_animation);
        Animation middle_anim = AnimationUtils.loadAnimation(this, R.anim.middle_line_animation);
        Animation bottom_anim = AnimationUtils.loadAnimation(this, R.anim.bottom_line_animation);
        Animation image_anim = AnimationUtils.loadAnimation(this, R.anim.user_image_animation);

        RelativeLayout lines = findViewById(R.id.lines_layout);
        lines.setAnimation(top_anim);

        RelativeLayout bot_lines = findViewById(R.id.bot_lines);
        bot_lines.setAnimation(bottom_anim);

        RelativeLayout user_layout = findViewById(R.id.hello_layout);
        user_layout.setAnimation(image_anim);

        TextView copyright = findViewById(R.id.copyright_text);
       // copyright.setText(Macros.COPYRIGHT_TEXT);
        copyright.setAnimation(bottom_anim);

        RelativeLayout middle_layout = findViewById(R.id.middle_layout);
        middle_layout.setAnimation(middle_anim);
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private boolean isConnectedToInternet(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        //we are connected to a network
        if(Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED){

            return internetIsConnected();
        }
        else
            return false;

    }

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        }
        catch (Exception e) {
            return false;
        }
    }
}