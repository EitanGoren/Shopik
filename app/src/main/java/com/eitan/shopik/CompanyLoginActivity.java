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

import com.eitan.shopik.Company.CompanyMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompanyLoginActivity extends AppCompatActivity {

    private EditText mEmail,mPassword;
    private TextView mEmailError,mPasswordError;
    private FirebaseAuth mAuth;
    private Button mBack;
    private boolean isCompany = false;
    private DatabaseReference companiesDb;
    private String name,type;
    private FirebaseAuth.AuthStateListener firebaseauthstatelistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        mAuth = FirebaseAuth.getInstance();
        companiesDb = FirebaseDatabase.getInstance().getReference().child("Companies");

        firebaseauthstatelistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    isCompany(user.getUid());
                }
            }
        };

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        Button btnLogin = findViewById(R.id.btn_login);

        mEmailError = findViewById(R.id.email_explanation);
        mPasswordError = findViewById(R.id.password_explanation);

        mBack = findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //     Intent intent = new Intent(CompanyLoginActivity.this, MainWelcomeActivity.class );
           //     startActivity(intent);
           //     finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                if(email.isEmpty()){
                    mEmail.setHintTextColor(Color.RED);
                    Toast.makeText(CompanyLoginActivity.this, "Fields are missing", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (password.isEmpty()){
                    mPassword.setHintTextColor(Color.RED);
                    Toast.makeText(CompanyLoginActivity.this, "Fields are missing", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(CompanyLoginActivity.this, task -> {

                    if(!task.isSuccessful()){
                        mEmail.setHintTextColor(Color.RED);
                        mEmailError.setVisibility(View.VISIBLE);
                        mPassword.setHintTextColor(Color.RED);
                        mPasswordError.setVisibility(View.VISIBLE);
                        Toast.makeText(CompanyLoginActivity.this, "Wrong Email or Password, Try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void isCompany(final String uid) {
        companiesDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    isCompany = dataSnapshot.hasChild(uid);
                    if(isCompany){
                        Intent intent = new Intent(CompanyLoginActivity.this, CompanyMainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("company_id", uid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(getApplication(),"This Email is owned by a Customer",Toast.LENGTH_LONG).show();
                        mEmail.setText("");
                        mPassword.setText("");
                        mEmailError.setText("This Email is owned by a Customer");
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
