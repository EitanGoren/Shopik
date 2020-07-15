package com.eitan.shopik.CompanyFragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eitan.shopik.Database;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link postOrStopSaleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class postOrStopSaleFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ShoppingItem item;
    private EditText days,amount,hours;
    private TextView sale_exists;
    private Button publish;
    ImageView close;
    private Database connection;
    private RelativeLayout discount_layout;
    BottomNavigationView bottomNav;

    public postOrStopSaleFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static postOrStopSaleFragment newInstance(ShoppingItem item) {
        postOrStopSaleFragment fragment = new postOrStopSaleFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_or_stop_sale, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        amount = getView().findViewById(R.id.sale_amount);
        days = getView().findViewById(R.id.sale_ending_days);
        hours = getView().findViewById(R.id.sale_hours_till_finish);
        sale_exists = getView().findViewById(R.id.sale_exists_text);
        publish = getView().findViewById(R.id.publish_sale);
        close = getView().findViewById(R.id.sale_close_x);
        discount_layout = getView().findViewById(R.id.discount_layout);
        bottomNav = getActivity().findViewById(R.id.company_bottom_nav);

        connection = new Database();

        if(item.isOn_sale()){
            sale_exists.setVisibility(View.VISIBLE);
            discount_layout.setVisibility(View.GONE);
            publish.setText("STOP SALE");
            sale_exists.setText(
                    "This " + item.getType() +
                            " is already on SALE" + System.lineSeparator() +
                            "You can stop it at any time");
            sale_exists.setTextColor(getResources().getColor(R.color.navigationBar));
            publish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setDiscount(null);
                    item.setOn_sale(false);
                   // connection.stopSale(item);
                    bottomNav.setSelectedItemId(R.id.nav_all_items);
                }
            });
        }
        else{
            sale_exists.setVisibility(View.GONE);
            discount_layout.setVisibility(View.VISIBLE);
            publish.setText("PUBLISH SALE");

            publish.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    String sale_amount = amount.getText().toString();
                    String sale_days = days.getText().toString();
                    String sale_hours = hours.getText().toString();

                    int final_price = Math.round(((float)(100 - Integer.parseInt(sale_amount))/100)*(float)Integer.parseInt(item.getPrice()));
                    String sale = final_price + "," + sale_days + "," + sale_hours;

                    item.setDiscount(sale);
                    item.setOn_sale(true);
                  //  connection.publishSale(item);
                    bottomNav.setSelectedItemId(R.id.nav_all_items);
                }
            });
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNav.setSelectedItemId(R.id.nav_all_items);
            }
        });

    }
}
