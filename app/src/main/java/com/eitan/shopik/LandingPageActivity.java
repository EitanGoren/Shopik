package com.eitan.shopik;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eitan.shopik.Customer.Customer;
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

public class LandingPageActivity extends AppCompatActivity {

    private static String provider,token,email,imageUrl,id_in_provider;
    private static FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;

        decorView.setSystemUiVisibility(uiOptions);
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_landing_page);

        init();

        //FirebaseAuth.getInstance().signOut();
        if(!isConnectedToInternet()){
            RelativeLayout LandingLayout = findViewById(R.id.LandingLayout);
            Macros.Functions.showSnackbar (
                     LandingLayout,
                    "No Internet connection",
                    this,
                     R.drawable.ic_baseline_signal_cellular
            );
        }

        if(user == null){
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.FacebookBuilder().build(),
                    new AuthUI.IdpConfig.EmailBuilder().build());
            // You must provide a custom layout XML resource and configure at least one
            // provider button ID. It's important that that you set the button ID for every provider
            // that you have enabled.
            AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                    .Builder(R.layout.activity_auth)
                    .setGoogleButtonId(R.id.google_sign_in)
                    .setFacebookButtonId(R.id.facebook_sign_in)
                    .setEmailButtonId(R.id.email_sign_in)
                    .build();
            // Create and launch sign-in intent
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(false)
                    .setAuthMethodPickerLayout(customLayout)
                    .setTheme(R.style.SignInStyle)
                    .setLogo(R.mipmap.ic_launcher_shopik)
                    .build(),1);
        }
        else {
            DocumentReference user_document = db.collection(Macros.CUSTOMERS).document(user.getUid());

            user_document.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        if(document.get("imageUrl") != null) {
                            imageUrl = Objects.requireNonNull(document.get("imageUrl")).toString();
                        }
                        goToApp();
                    }
                    else {
                        registerNewUser();
                    }
                }
                else {
                    Log.d(Macros.TAG,"Failed with " + task.getException());
                }
            });
        }
    }

    private void init() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    private void registerNewUser() {

        Database connection = new Database();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String first_name = Objects.requireNonNull(user.getDisplayName()).split(" ")[0];
        String last_name = Objects.requireNonNull(user.getDisplayName()).split(" ")[1];
        Customer new_customer = new Customer(
                user.getUid(),
                first_name,
                last_name,
                null,
                user.getPhoneNumber(),
                0,
                0,
                "Unknown",
                null,
                null, imageUrl, provider, id_in_provider, email, token);

        connection.pushNewCustomer(new_customer);
        goToCustomer();
    }

    private void checkCustomer(final String user_id) {
        db.collection(Macros.CUSTOMERS).document(user_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    goToApp();
                }
                else
                    registerNewUser();
            }
        });
    }

    private void goToApp(){
        if(user == null)
           registerNewUser();
        else
            goToCustomer();
    }

    private void goToCustomer() {
        Intent intent = new Intent(LandingPageActivity.this, GenderFilteringActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imageUrl", imageUrl);
        bundle.putString("name", user.getDisplayName());
        intent.putExtra("bundle", bundle);

        ImageView tooki = findViewById(R.id.imageView);
        YoYo.with(Techniques.Hinge).duration(3000).onEnd(animator -> {
            startActivity(intent);
            supportFinishAfterTransition();
        }).playOn(tooki);
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
                email = new_user.getEmail();
                provider = new_user.getProviderId();

                switch (provider) {
                    case Macros.Providers.FACEBOOK:
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response1) -> {
                            try {
                                id_in_provider = object.getString("id");
                                imageUrl = "http://graph.facebook.com/" + id_in_provider + "/picture?type=large&width=720&height=720";
                                checkCustomer(user.getUid());
                            }
                            catch (JSONException e) {
                                Log.d(Macros.TAG, "facebook failed: " + e.getMessage());
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name");
                        request.setParameters(parameters);
                        request.executeAsync();
                        break;
                    case Macros.Providers.GOOGLE:
                        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                        try {
                            if (acct != null) {
                                id_in_provider = acct.getId();
                                imageUrl = Objects.requireNonNull(acct.getPhotoUrl()).toString().split("=", 2)[0].concat("=s700-c");
                                checkCustomer(user.getUid());
                            }
                        } catch (Exception e) {
                            Log.d(Macros.TAG, "googleAuth failed: " + e.getMessage());
                        }
                        break;
                    case Macros.Providers.PASSWORD:
                        checkCustomer(user.getUid());
                        break;
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

    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }
}