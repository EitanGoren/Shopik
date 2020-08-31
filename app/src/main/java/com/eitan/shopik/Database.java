package com.eitan.shopik;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.eitan.shopik.Company.Company;
import com.eitan.shopik.Customer.Customer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Database {

    private static CollectionReference companiesFS;
    private static CollectionReference customersFS;
    private static String userId;

    public Database() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            companiesFS = FirebaseFirestore.getInstance().collection(Macros.COMPANIES);
            customersFS = FirebaseFirestore.getInstance().collection(Macros.CUSTOMERS);
        }
    }

    public void pushNewCompany(Company company) {
        companiesFS.document(userId).set(company);
    }

    public void pushNewCustomer(Customer customer) {
        customersFS.document(userId).set(customer);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void increasePreferredFieldByOneRTDB(String attribute, final String type, final String gender, final String sub_category) {

        if (attribute.equals("")
                || sub_category.contains(attribute)
                || Arrays.asList(Macros.Items.shit_words).contains(attribute)) {

            return;
        }

        final String attr = attribute.
                replace(".","").
                replace("$","").
                replace("#","").
                replace("[","").
                replace("]","").
                replace(":","");

        Map<String,Object> map = new HashMap<>();

        FirebaseDatabase.getInstance().getReference().
                child(Macros.CUSTOMERS).
                child(userId).
                child(gender).
                child(Macros.CustomerMacros.PREFERRED_ITEMS).
                child(type).
                child(sub_category).
                child(attr).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long num;
                if(dataSnapshot.exists() && attr.equals(dataSnapshot.getKey())){
                    num = (long) dataSnapshot.getValue();
                    num++;
                    map.put(attr,num);

                    FirebaseDatabase.getInstance().getReference().
                            child(Macros.CUSTOMERS).
                            child(userId).
                            child(gender).
                            child(Macros.CustomerMacros.PREFERRED_ITEMS).
                            child(type).
                            child(sub_category).
                            updateChildren(map);
                }
                else if(attr.equals(dataSnapshot.getKey())){
                    FirebaseDatabase.getInstance().getReference().
                            child(Macros.CUSTOMERS).
                            child(userId).
                            child(gender).
                            child(Macros.CustomerMacros.PREFERRED_ITEMS).
                            child(type).
                            child(sub_category).
                            child(attr).
                            setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(Macros.TAG,"Database::increasePreferredFieldByOneRTDB " + databaseError.getMessage());
            }
        });
    }

    public void onItemAction(String type, String gender, String item_id, String action,String company,String link, String image){

        String temp_action = action;
        if(action.equals(Macros.CustomerMacros.FAVOURITE))
            temp_action = Macros.CustomerMacros.LIKED;

        Map<String,Object> map = new HashMap<>();
        map.put(userId, action);

        FirebaseDatabase.getInstance().getReference().
                child(Macros.ITEMS).
                child(gender).
                child(type).
                child(company + "-" + item_id).
                child(temp_action).
                updateChildren(map);

        Map<String,Object> info = new HashMap<>();
        info.put("link", link);
        info.put("image", image);

        FirebaseDatabase.getInstance().getReference().
                child(Macros.ITEMS).
                child(gender).
                child(type).
                child(company + "-" + item_id).
                child("Info").
                setValue(info);
    }

    public ArrayList<String> getASOSRecyclerImage(String type, String color, String item_id_in_seller) {
        ArrayList<String> images = new ArrayList<>();
        if (type.equals("product")) {
            for (int i = 1; i < 5; ++i) {
                if (i == 1)
                    images.add("https://images.asos-media.com/products/0/" + item_id_in_seller + "-1-" + color + "?$S$&wid=595&hei=760");
                else
                    images.add("https://images.asos-media.com/products/0/" + item_id_in_seller + "-" + i + "?$S$&wid=595&hei=760");
            }
        } else if (type.equals("group")) {
            images.add("https://images.asos-media.com/groups/0/" + item_id_in_seller + "-group-1" + "?$S$&wid=595&hei=760");
            images.add("https://images.asos-media.com/groups/0/" + item_id_in_seller + "-group-1" + "?$S$&wid=595&hei=760");
            images.add("https://images.asos-media.com/groups/0/" + item_id_in_seller + "-group-1" + "?$S$&wid=595&hei=760");
            images.add("https://images.asos-media.com/groups/0/" + item_id_in_seller + "-group-1" + "?$S$&wid=595&hei=760");
        }
        return images;
    }
}
