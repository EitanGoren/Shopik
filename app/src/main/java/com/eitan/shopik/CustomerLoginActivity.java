package com.eitan.shopik;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.eitan.shopik.Customer.CustomerMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CustomerLoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private TextView mEmailError, mPasswordError;
    private FirebaseAuth mAuth;
    private DatabaseReference customersDb;
    private String uid;
    private boolean is_customer = false;
    private FirebaseAuth.AuthStateListener firebaseauthstatelistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        mAuth = FirebaseAuth.getInstance();
        customersDb = FirebaseDatabase.getInstance().getReference().child("Customers");

        firebaseauthstatelistener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null ) {
                isCustomer(user.getUid());
            }
        };

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        Button btnLogin = findViewById(R.id.btn_login);

        mEmailError = findViewById(R.id.email_explanation);
        mPasswordError = findViewById(R.id.password_explanation);

        btnLogin.setOnClickListener(v -> {
            final String email = mEmail.getText().toString();
            final String password = mPassword.getText().toString();

            if (email.isEmpty()) {
                mEmail.setHintTextColor(Color.RED);
                Toast.makeText(CustomerLoginActivity.this, "Fields are missing", Toast.LENGTH_SHORT).show();
                return;
            } else if (password.isEmpty()) {
                mPassword.setHintTextColor(Color.RED);
                Toast.makeText(CustomerLoginActivity.this, "Fields are missing", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(CustomerLoginActivity.this, task -> {

                boolean new_user_bool = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getAdditionalUserInfo()).isNewUser();
                if (!new_user_bool) {
                    uid = Objects.requireNonNull(task.getResult().getUser()).getUid();
                }

                if (!task.isSuccessful()) {
                    mEmail.setHintTextColor(Color.RED);
                    mEmailError.setVisibility(View.VISIBLE);
                    mPassword.setHintTextColor(Color.RED);
                    mPasswordError.setVisibility(View.VISIBLE);
                    Toast.makeText(CustomerLoginActivity.this, "Wrong Email or Password, Try again", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void isCustomer(final String uid) {
        customersDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    is_customer = dataSnapshot.hasChild(uid);
                    if(is_customer){
                        Intent intent = new Intent(CustomerLoginActivity.this, CustomerMainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("customer_id", uid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(getApplication(),"This Email is owned by a Company",Toast.LENGTH_LONG).show();
                        mEmail.setText("");
                        mPassword.setText("");
                        mEmailError.setText("This Email is owned by a Company");
                        mEmailError.setVisibility(View.VISIBLE);
                        mEmailError.setTextColor(Color.RED);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseauthstatelistener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseauthstatelistener);
    }
}
