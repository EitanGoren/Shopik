package com.eitan.shopik;

import static com.eitan.shopik.ShopikApplication.sellers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.eitan.shopik.customer.GenderFilteringActivity;
import com.eitan.shopik.customer.ShopikUser;
import com.eitan.shopik.database.Database;
import com.eitan.shopik.database.ShopikRepository;
import com.eitan.shopik.database.models.Company;
import com.eitan.shopik.database.models.User;
import com.eitan.shopik.viewModels.DatabaseViewModel;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;

import java.util.Map;
import java.util.Objects;

@Keep
public class LandingPageActivity extends AppCompatActivity {

    private static final String TAG = "LandingPageActivity";
    private ShopikUser mShopikUser;
    private ActivityResultLauncher<Intent> signInLauncher;

    public static class EnterAppEvent{}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        postponeEnterTransition();
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_landing_page);

        initialize();

        startPostponedEnterTransition();

        goToApp();
    }

    @SuppressLint("RestrictedApi")
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            assert response != null;
            mShopikUser.signIn();
            if (response.isNewUser()) {
                switch (mShopikUser.getProvider()) {
                    case Macros.Providers.FACEBOOK: {
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken,
                                (object, response1) -> {
                                    try {
                                        assert object != null;
                                        mShopikUser.setIdInProvider(object.getString("id"));
                                        mShopikUser.setImageUrl(object.getJSONObject("picture").getJSONObject("data").
                                                getString("url"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        assert response1 != null;
                                        Log.w(TAG, Objects.requireNonNull(Objects.requireNonNull(response1.getError()).getErrorMessage()));
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "picture.type(large),id");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }
                    break;
                    case Macros.Providers.GOOGLE: {
                        try {
                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
                            if (acct != null) {
                                mShopikUser.setIdInProvider(acct.getId());
                                mShopikUser.setImageUrl(Objects.requireNonNull(acct.getPhotoUrl()).toString().
                                        split("=", 2)[0].concat("=s700-c"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.w(TAG, Objects.requireNonNull(e.getMessage()));
                        }
                    }
                    break;
                    case Macros.Providers.FIREBASE:
                        mShopikUser.setImageUrl(Macros.DEFAULT_PROFILE_IMAGE);
                    default:
                        break;
                }
                registerNewUser();
            }
            else{
                fetchExistingUserData();
            }
        }
        else {
            // Sign in failed.
            mShopikUser.signOut();
            assert response != null;
            Log.e("Auth", Objects.requireNonNull(response.getError()).toString());
            AuthenticateUser();
        }
    }

    private void fetchExistingUserData() {
        Database.getInstance().fetchExistingUser();
    }

    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }

    private void AuthenticateUser() {
        mShopikUser.AuthenticateUser(signInLauncher);
    }

    private void initialize() {
        mShopikUser = ShopikUser.getInstance();

        ShopikRepository repository = new ShopikRepository(getApplication());
        repository.getAllCompanies().observe(this, companyList -> {
            if(!companyList.isEmpty()){
                ShopikApplication.companiesInfo = companyList;
                for (Company company : companyList) {
                    sellers.add(company.getName());
                }
            }
        });

        signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(), this::onSignInResult
        );
    }

    private void registerNewUser() {
        Database.getInstance().registerNewUser();
    }

    private void goToApp(){
        if(!mShopikUser.isAuthenticated())
            AuthenticateUser();
        else{
            goToCustomer();
        }
    }

    private void goToCustomer() {
        Intent intent = new Intent(LandingPageActivity.this, GenderFilteringActivity.class);
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            EventBus.getDefault().post(new EnterAppEvent());
        },2000);

        handler.postDelayed(() -> {
            startActivity(intent);
            supportFinishAfterTransition();
        },400);
    }

    @Subscribe
    public void onUserDataUpdated(Database.UserDataUpdated event) {
        goToCustomer();
    }

    @Override
    public void finish() {
        EventBus.getDefault().unregister(this);
        super.finish();
    }

    @Override
    public void onBackPressed() {}
}