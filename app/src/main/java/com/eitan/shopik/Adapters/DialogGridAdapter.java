package com.eitan.shopik.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.List;

public class DialogGridAdapter extends ArrayAdapter<RecyclerItem> {

    public DialogGridAdapter(@NonNull Context context, int resource, List<RecyclerItem> items){
        super(context, resource, items);
    }

    @NonNull
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final RecyclerItem item = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.e3_grid_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.image_slider);
        Button link = convertView.findViewById(R.id.store_link);
        final Button fullscreen = convertView.findViewById(R.id.fullscreen_button);
        TextView price = convertView.findViewById(R.id.slider_brand);
        TextView reduced_price = convertView.findViewById(R.id.reduced_price);
        TextView sale = convertView.findViewById(R.id.sale);

        link.setOnClickListener(v -> {
            assert item != null;
            Macros.Functions.buy(getContext(),item.getLink());
        });

        assert item != null;
        Macros.Functions.GlidePicture(getContext(),item.getImages().get(0),imageView);

        if(item.isSale())
            sale.setVisibility(View.VISIBLE);
        else
            sale.setVisibility(View.INVISIBLE);

        if(item.isOutlet() || item.isSale()){
            Currency shekel = Currency.getInstance("ILS");
            String currency_symbol = shekel.getSymbol();
            Double old = Double.parseDouble(item.getReduced_price()) * Macros.POUND_TO_ILS;
            String cur_price = new DecimalFormat("##.##").format(old) + currency_symbol;

            reduced_price.setVisibility(View.VISIBLE);
            reduced_price.setText(cur_price);
            reduced_price.setTextColor(Color.RED);
            reduced_price.setTextSize(16);

            price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            price.setText(item.getPrice());
            price.setTextSize(12);
        }
        else {
            reduced_price.setVisibility(View.INVISIBLE);
            price.setText(item.getPrice());
            price.setTextSize(16);
        }

        Pair<View, String> pair = new Pair<>(imageView,"fullscreen");
        fullscreen.setOnClickListener(v -> Macros.Functions.fullscreen(getContext(), item, pair));

        return convertView;
    }

    @Nullable
    @Override
    public RecyclerItem getItem(int position) {
        return super.getItem(position);
    }
}
