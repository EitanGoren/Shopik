package com.eitan.shopik;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewCustomerRegistrationActivity extends AppCompatActivity {

    private EditText mEmail,mPassword,mLastName,mFirstName;
    private TextView mTerms,mEmailError,mPasswordError,mFirstNameError,mLastNameError;
    private Switch mAgreeSwitch;
    private RadioGroup mRadioGroup;
    private Button mBack;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private boolean isTermsAccepted = false;
    private int num_of_customers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_customer_registration);
        /*
        mBack = findViewById(R.id.back);
        mAgreeSwitch = findViewById(R.id.terms_of_Use);
        mTerms = findViewById(R.id.terms_text);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(NewCustomerRegistrationActivity.this, CustomerLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        Button btnRegister = findViewById(R.id.register);
        mRadioGroup = findViewById(R.id.radioGroup);
        mLastName = findViewById(R.id.last_name);
        mFirstName = findViewById(R.id.first_name);

        mEmailError = findViewById(R.id.email_explanation);
        mPasswordError = findViewById(R.id.password_explanation);
        mLastNameError = findViewById(R.id.last_name_error);
        mFirstNameError = findViewById(R.id.first_name_error);

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

        getCustomersNum();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        mLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLastNameError.setVisibility(View.GONE);
                mLastNameError.setHintTextColor(Color.GRAY);
            }
        });
        mFirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirstNameError.setVisibility(View.GONE);
                mFirstNameError.setHintTextColor(Color.GRAY);
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
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEmailError.setVisibility(View.GONE);
                mPasswordError.setVisibility(View.GONE);
                mFirstNameError.setVisibility(View.GONE);
                mLastNameError.setVisibility(View.GONE);

                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String first_name = mFirstName.getText().toString();
                final String last_name = mLastName.getText().toString();
                if(first_name.isEmpty()){
                    printErrorMessage("First name is missing");
                    return;
                }
                else if(last_name.isEmpty()){
                    printErrorMessage("Last name is missing");
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
                int selectId = mRadioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = findViewById(selectId);
                if(selectId == -1){
                    printErrorMessage("Specify your gender, idiot");
                    return;
                }
                if(!isTermsAccepted){
                    printErrorMessage("Accept terms of use please");
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(NewCustomerRegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            String msg = Objects.requireNonNull(task.getException()).getMessage();
                            assert msg != null;
                            printErrorMessage(msg);
                        }
                        else {
                            String customerId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Customers");
                            Map userInfo = new HashMap<>();
                            Customer customer = new Customer(customerId,first_name,last_name,"0","0","0","0",radioButton.getText().toString(),
                                    "0","0",null,null,null,null);
                            userInfo.put(customerId,customer);
                            userInfo.put("total_customers",num_of_customers+1);
                            currentUserDB.updateChildren(userInfo);
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
       // Intent intent = new Intent(NewCustomerRegistrationActivity.this, MainWelcomeActivity.class);
       // startActivity(intent);
       // finish();
    }

    private void printErrorMessage(String msg){
        if(msg.isEmpty()){
            Toast.makeText(NewCustomerRegistrationActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
            return;
        }
        switch (msg) {
            case "First name is missing":
                mFirstNameError.setText(msg);
                mFirstNameError.setVisibility(View.VISIBLE);
                break;
            case "Last name is missing":
                mLastNameError.setText(msg);
                mLastNameError.setVisibility(View.VISIBLE);
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
            case "Specify your gender, idiot":
                Toast.makeText(NewCustomerRegistrationActivity.this, msg, Toast.LENGTH_SHORT).show();
                break;
            case "Accept terms of use please":
                mTerms.setTextColor(Color.RED);
                Toast.makeText(NewCustomerRegistrationActivity.this, msg, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void getCustomersNum(){
        DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Customers");
        currentUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    num_of_customers = (int) dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
