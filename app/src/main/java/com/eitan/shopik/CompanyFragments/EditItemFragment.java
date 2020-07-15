package com.eitan.shopik.CompanyFragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Database;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditItemFragment extends Fragment {

    private String price, name, company_id, company_name, company_logo, site_link;
    private EditText mName,mPrice,mLink;
    private ShoppingItem item;
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNav;
    Spinner types, colors, brands;
    ArrayAdapter<CharSequence> types_adapter, colors_adapter, brands_adapter;


    public EditItemFragment() {
        // Required empty public constructor
    }

    public static EditItemFragment newInstance(ShoppingItem item) {
        EditItemFragment fragment = new EditItemFragment();
        Bundle args = new Bundle();
        args.putSerializable("item", (Serializable) item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = (ShoppingItem) getArguments().getSerializable("item");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_item, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        company_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        TextView mCompanyName = Objects.requireNonNull(getActivity()).findViewById(R.id.company_name);
        company_name = mCompanyName.getText().toString();
        DatabaseReference companyDB = FirebaseDatabase.getInstance().getReference().child("Companies").child(company_id);
        Button mConfirm = Objects.requireNonNull(getView()).findViewById(R.id.edit_confirm);
        bottomNav = Objects.requireNonNull(getActivity()).findViewById(R.id.company_bottom_nav);
        bottomNav.setVisibility(View.INVISIBLE);
        bottomNav.setSelected(false);

        companyDB.child("logo_url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                company_logo = Objects.requireNonNull(dataSnapshot.getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        types = Objects.requireNonNull(getView()).findViewById(R.id.edit_type_spinner);
        types_adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.types, R.layout.support_simple_spinner_dropdown_item);
        int type_pos = types_adapter.getPosition(item.getType());
        types_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        types.setAdapter(types_adapter);
        types.setSelection(type_pos);

        brands = Objects.requireNonNull(getView()).findViewById(R.id.edit_brand_spinner);
        brands_adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.brands, R.layout.support_simple_spinner_dropdown_item);
        int brands_pos = brands_adapter.getPosition(item.getBrand());
        brands_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        brands.setAdapter(brands_adapter);
        brands.setSelection(brands_pos);

        colors = Objects.requireNonNull(getView()).findViewById(R.id.edit_color_spinner);
        colors_adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.colors, R.layout.support_simple_spinner_dropdown_item);
        int colors_pos = colors_adapter.getPosition(item.getColor());
        colors_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        colors.setAdapter(colors_adapter);
        colors.setSelection(colors_pos);

        mName = getView().findViewById(R.id.edit_item_name);
        mLink = getView().findViewById(R.id.edit_item_site_link);
        mLink.setText(item.getSite_link());
        mPrice = Objects.requireNonNull(getView()).findViewById(R.id.edit_edit_price);
        mPrice.setText(item.getPrice());

        Database connection = new Database();

        mConfirm.setOnClickListener(v -> {

            name = mName.getText().toString();
            price = mPrice.getText().toString();
            site_link = mLink.getText().toString();

            final Map newImage = new HashMap();

            newImage.put("name", name);
            newImage.put("price", price);
            newImage.put("seller", company_name);
            newImage.put("seller_id",company_id);
            newImage.put("seller_logo",company_logo);
            newImage.put("site_link",site_link);

            mAuth = FirebaseAuth.getInstance();
            bottomNav.setVisibility(View.VISIBLE);
           // connection.updateItem(item.getId(),item.getType(),newImage);
        });
    }
}