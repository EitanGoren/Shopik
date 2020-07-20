package com.eitan.shopik;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.eitan.shopik.Company.Company;
import com.eitan.shopik.Customer.Customer;
import com.eitan.shopik.Items.ShoppingItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Database {

    private static CollectionReference companiesFS, customersFS, itemsFS;
    private static String userId;

    public Database() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            companiesFS = FirebaseFirestore.getInstance().collection(Macros.COMPANIES);
            customersFS = FirebaseFirestore.getInstance().collection(Macros.CUSTOMERS);
            itemsFS = FirebaseFirestore.getInstance().collection(Macros.ITEMS);
        }
    }

    public void pushNewCompany(Company company) {
        companiesFS.document(userId).set(company);
    }

    public void pushNewCustomer(Customer customer) {
        customersFS.document(userId).set(customer);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void increasePreferredFieldByOneRTDB(String attribute, final String type, final String gender, final String sub_category) {

        if (attribute.equals("")
                || sub_category.contains(attribute)
                || Arrays.asList(Macros.Items.shit_words).contains(attribute)
                || Arrays.asList(Macros.Items.getList(sub_category)).contains(attribute)) {

            return;
        }

        final String attr = attribute.
                replace(".","").
                replace("$","").
                replace("#","").
                replace("[","").
                replace("]","");

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
                long num = 1;
                if(dataSnapshot.exists()){
                    num = (long) dataSnapshot.getValue();
                    num++;
                }
                dataSnapshot.getRef().setValue(num);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(Macros.TAG,"Database::increasePreferredFieldByOneRTDB " + databaseError.getMessage());
            }
        });
    }

    public void onCustomerInteractWithItem(String itemId, String type, String gender, String sub_category, String action){

        String temp_action = action;
        if(action.equals(Macros.CustomerMacros.FAVOURITE))
            temp_action = Macros.CustomerMacros.LIKED;

        Map<String,Object> map = new HashMap<>();
        map.put(itemId, action);

        FirebaseDatabase.getInstance().getReference().
                child(Macros.CUSTOMERS).
                child(userId).
                child(gender).
                child(temp_action).
                child(type).
                child(sub_category).
                updateChildren(map);
    }

    public void onItemAction(String type, String gender, String item_id, String action){

        String temp_action = action;
        if(action.equals(Macros.CustomerMacros.FAVOURITE))
            temp_action = Macros.CustomerMacros.LIKED;

        Map<String,Object> map = new HashMap<>();
        map.put(userId, action);

        FirebaseDatabase.getInstance().getReference().
                child(Macros.ITEMS).
                child(gender).
                child(type).
                child(item_id).
                child(temp_action).
                updateChildren(map);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void updatePreferredItem(ShoppingItem shoppingItem, String sub_category) {
        String item_type = shoppingItem.getType();
        String item_gender = shoppingItem.getGender();
        String price = Float.parseFloat(shoppingItem.getPrice()) < 50 ?
                "less than 50" : (Float.parseFloat(shoppingItem.getPrice()) < 100 ? "between 50 to 100" : "above 100"
        );

        //TODO add parameters like color,brand,price,etc..

        shoppingItem.getName().add(price);
        for (String attr : shoppingItem.getName()) {
            increasePreferredFieldByOneRTDB(attr, item_type, item_gender, sub_category);
        }
    }

    public String getASOSimageUrl(int imageNum, String color, String item_id_in_seller) {
        if (imageNum == 1)
            return "https://images.asos-media.com/products/0/" + item_id_in_seller + "-1-" + color + "?$S$&wid=595&hei=760";
        else
            return "https://images.asos-media.com/products/0/" + item_id_in_seller + "-" + imageNum + "?$S$&wid=595&hei=760";
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

    public void updateActionInItem(final String gender, final String type, final String item_id, final String action_num) {

        // add 1 to "action_num" field
        Map<String, Object> _action = new HashMap<>();
        _action.put(action_num, FieldValue.increment(1));
        itemsFS.document(gender).
                collection(type).
                document(item_id).
                update(_action);
    }
}
