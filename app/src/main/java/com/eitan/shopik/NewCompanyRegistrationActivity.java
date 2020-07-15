package com.eitan.shopik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewCompanyRegistrationActivity extends AppCompatActivity {

    private EditText mEmail,mPassword,mCompanyName,mCompanyNumber;
    private TextView mTerms,mEmailError,mPasswordError,mCompanyNameError,mCompanyNumberError;
    private Switch mAgreeSwitch;
    private Button mBack;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private boolean isTermsAccepted = false;
    private DatabaseReference companiesDb;
    private int num_of_companies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_company_registration);
/*
        mBack = findViewById(R.id.back);
        mAgreeSwitch = findViewById(R.id.terms_of_Use);
        mTerms = findViewById(R.id.terms_text);
        companiesDb = FirebaseDatabase.getInstance().getReference().child("Companies");

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            Toast.makeText(this,"you already signed in  from this phone, sign out first.",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NewCompanyRegistrationActivity.this, MainWelcomeActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        Intent intent = new Intent(NewCompanyRegistrationActivity.this, CompanyLoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            };
        }

        getCompaniesNum();

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        Button btnRegister = findViewById(R.id.register);
        mCompanyName = findViewById(R.id.company_name);
        mCompanyNumber = findViewById(R.id.company_number);

        mEmailError = findViewById(R.id.email_explanation);
        mPasswordError = findViewById(R.id.password_explanation);
        mCompanyNameError = findViewById(R.id.company_name_error);
        mCompanyNumberError = findViewById(R.id.company_number_error);

        mAgreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isTermsAccepted = true;
                    mTerms.setTextColor(Color.parseColor("#000000"));
                }
                else{
                    isTermsAccepted = false;
                }
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        mCompanyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCompanyNameError.setVisibility(View.GONE);
                mCompanyNameError.setHintTextColor(Color.GRAY);
            }
        });
        mEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailError.setVisibility(View.GONE);
                mEmailError.setHintTextColor(Color.GRAY);
            }
        });
        mPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordError.setVisibility(View.GONE);
                mPassword.setHintTextColor(Color.GRAY);
            }
        });
        mCompanyNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCompanyNumberError.setVisibility(View.GONE);
                mCompanyNumber.setHintTextColor(Color.GRAY);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEmailError.setVisibility(View.GONE);
                mPasswordError.setVisibility(View.GONE);
                mCompanyNameError.setVisibility(View.GONE);
                mCompanyNumberError.setVisibility(View.GONE);

                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String company_name = mCompanyName.getText().toString();
                final String company_number = mCompanyNumber.getText().toString();

                if(company_name.isEmpty()){
                    printErrorMessage("Company name is missing");
                    return;
                }
                else if(company_number.isEmpty()){
                    printErrorMessage("Company number is missing");
                    return;
                }
                else if(email.isEmpty()){
                    printErrorMessage("Email is missing");
                    return;
                }
                else if(password.isEmpty()){
                    printErrorMessage("Password is missing");
                    return;
                }
                if(!isTermsAccepted){
                    printErrorMessage("Accept terms of use please");
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(NewCompanyRegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            String msg = Objects.requireNonNull(task.getException()).getMessage();
                            assert msg != null;
                            printErrorMessage(msg);
                        }
                        else {
                            String companyId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            DatabaseReference currentCompanyDB = FirebaseDatabase.getInstance().getReference().child("Companies");
                            Map userInfo = new HashMap<>();
                            Company company = new Company(companyId,company_name,company_number,"0",null,"0",null);
                            userInfo.put(companyId,company);
                            userInfo.put("total_companies",num_of_companies+1);
                            currentCompanyDB.updateChildren(userInfo);
                            mAuth.signOut();
                        }
                    }
                });
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    private void goBack(){
      //  Intent intent = new Intent(NewCompanyRegistrationActivity.this, MainWelcomeActivity.class);
      //  startActivity(intent);
      //  finish();
    }

    private void printErrorMessage(String msg){
        if(msg.isEmpty()){
            Toast.makeText(NewCompanyRegistrationActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
            return;
        }
        switch (msg) {
            case "Company name is missing":
                mCompanyNameError.setText(msg);
                mCompanyNameError.setVisibility(View.VISIBLE);
                break;
            case "Company number is missing":
                mCompanyNumberError.setText(msg);
                mCompanyNumberError.setVisibility(View.VISIBLE);
                break;
            case "Email is missing":
                mEmailError.setText(msg);
                mEmailError.setVisibility(View.VISIBLE);
                break;
            case "The email address is already in use by another account.":
                mEmail.setText("");
                mEmailError.setText(msg);
                mEmailError.setVisibility(View.VISIBLE);
                break;
            case "The email address is badly formatted.":
                mEmail.setText("");
                mEmailError.setText("Wromg email address");
                mEmailError.setVisibility(View.VISIBLE);
                break;
            case "Password is missing":
                mPasswordError.setText(msg);
                mPasswordError.setVisibility(View.VISIBLE);
                break;
            case "The given password is invalid. [ Password should be at least 6 characters ]":
                mPassword.setText("");
                mPasswordError.setText("Password is invalid, should be at least 6 characters");
                mPasswordError.setVisibility(View.VISIBLE);
                break;
            case "Accept terms of use please":
                mTerms.setTextColor(Color.RED);
                Toast.makeText(NewCompanyRegistrationActivity.this, msg, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void getCompaniesNum(){
        DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Companies");
        currentUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    num_of_companies = (int) dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
