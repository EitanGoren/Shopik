package com.eitan.shopik.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.eitan.shopik.CompanyFragments.postOrStopSaleFragment;
import com.eitan.shopik.Database;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.R;

import java.util.ArrayList;
import java.util.List;

public class allItemsListAdapter extends ArrayAdapter<ShoppingItem> {
    public allItemsListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
/*
    Context context;
    private itemPicsAdapter arrayAdapter;
    FragmentManager fragmentManager;


    public allItemsListAdapter(@NonNull Context context, int resource, List<ShoppingItem> items,FragmentManager fragmentManager) {
        super(context, resource,items);
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @SuppressLint("SetTextI18n")
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ShoppingItem item = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.all_items_list_item,parent,false);
        }

        ArrayList<String> images = new ArrayList<>();
        Database connection = new Database();
        assert item != null;

        images.add(connection.getASOSimageUrl(3,item.getColor(),item.getId_in_seller()));
        images.add(connection.getASOSimageUrl(2,item.getColor(),item.getId_in_seller()));
        images.add(connection.getASOSimageUrl(1,item.getColor(),item.getId_in_seller()));
        images.add(connection.getASOSimageUrl(4,item.getColor(),item.getId_in_seller()));

       // arrayAdapter = new itemPicsAdapter(context, images);

        TextView header = convertView.findViewById(R.id.all_items_list_item_header);
        final ImageButton delete = convertView.findViewById(R.id.delete);
        ImageButton discount = convertView.findViewById(R.id.discount);


        delete.setOnClickListener(v -> {
           // deleteItem(item);
            arrayAdapter.notifyDataSetChanged();
        });
        discount.setOnClickListener(v -> {
            Fragment fragment = new postOrStopSaleFragment().newInstance(item);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.company_cards_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            arrayAdapter.notifyDataSetChanged();
        });
      //  header.setText( item.getName());
        return convertView;
    }

    @Nullable
    @Override
    public ShoppingItem getItem(int position) {
        return super.getItem(position);
    }
*/
}

