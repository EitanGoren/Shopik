package com.eitan.shopik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Company.Company;
import com.eitan.shopik.Company.CompanyMainActivity;
import com.eitan.shopik.Customer.Customer;
import com.eitan.shopik.Customer.GenderFilteringActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainRegistrationActivity extends AppCompatActivity {

    private boolean isCompany = false;
    private Switch is_company;
    String id, first_name, last_name, imageUrl,email,provider,token;
    CircleImageView imageView;
    TextView welcome;
    FirebaseUser user;
    Button finish_signup_button;
    Database connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_registration);

        is_company = findViewById(R.id.is_company);
        imageView = findViewById(R.id.image_profile);
        welcome = findViewById(R.id.welcome_signed_in);
        user = FirebaseAuth.getInstance().getCurrentUser();
        finish_signup_button = findViewById(R.id.finish_signup_button);
        connection = new Database();
        final String[] name = user.getDisplayName().split(" ", 2);

        id = getIntent().getStringExtra("id_in_provider");
        imageUrl = getIntent().getStringExtra("imageUrl");
        email = getIntent().getStringExtra("email");
        provider = getIntent().getStringExtra("provider");
        token = getIntent().getStringExtra("token");

        is_company.setText("Hi " + name[0] + ", are you a Company ?");
        welcome.setText("Good to see you " + name[0]);
        Glide.with(this).load(imageUrl).into(imageView);

        is_company.setOnCheckedChangeListener((buttonView, isChecked) -> isCompany = isChecked);

        finish_signup_button.setOnClickListener(v -> {
            if (isCompany) {
                Company new_company = new Company(user.getUid(),
                        user.getDisplayName(),
                        "0",
                        0,
                        imageUrl,
                        0,
                        provider,id,email,token,"Write description here...",null,null,null);

                connection.pushNewCompany(new_company);

                Intent intent = new Intent(MainRegistrationActivity.this, CompanyMainActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                first_name = name[0];
                last_name = name[1];
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
                        null, imageUrl, provider, id, email, token);

                connection.pushNewCustomer(new_customer);

                Intent intent = new Intent(MainRegistrationActivity.this, GenderFilteringActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("imageUrl",imageUrl);
                bundle.putString("name",user.getDisplayName());
                intent.putExtra("bundle",bundle);
                startActivity(intent);
                finish();
            }
        });
    }
}
