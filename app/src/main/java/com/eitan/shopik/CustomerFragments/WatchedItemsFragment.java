package com.eitan.shopik.CustomerFragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.eitan.shopik.Database;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WatchedItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WatchedItemsFragment extends Fragment {

    public static TextView text;
    public static ArrayList<ShoppingItem> items;
    private int page_num;
    private String item_type,item_gender;
    private FirebaseAuth mAuth;
    FragmentManager fragmentManager;

    public static WatchedItemsFragment newInstance(String item_gender,String item_type,int page_num) {
        WatchedItemsFragment fragment = new WatchedItemsFragment();
        Bundle args = new Bundle();
        args.putString("gender",item_gender);
        args.putString("type",item_type);
        args.putInt("page_num",page_num);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item_type = getArguments().getString("type");
            item_gender = getArguments().getString("gender");
            page_num = getArguments().getInt("page_num");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        mAuth = FirebaseAuth.getInstance();
        fragmentManager = getActivity().getSupportFragmentManager();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_watched_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addNewItems addNewItems = new addNewItems(page_num,item_type,item_gender);
        addNewItems.execute();
    }

    private static class addNewItems extends AsyncTask<Void,Void,Void> {
        String data = "";
        int page_num;
        String item_type,item_gender;

        addNewItems(int page_num,String item_type,String item_gender){
            this.page_num = page_num;
            this.item_gender = item_gender;
            this.item_type = item_type;
        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                int catagory_num = 0;
                switch (item_type) {
                    case Macros.Items.BAG:
                        catagory_num = 8730;
                        break;
                    case Macros.Items.SUNGLASSES:
                        catagory_num = item_gender.equals(Macros.CustomerMacros.WOMEN) ? 4545 : 6519;
                        break;
                    case Macros.Items.SHOES:
                        catagory_num = item_gender.equals(Macros.CustomerMacros.WOMEN) ? 6461 /*6455*/ /*17170*/ : 5775;
                        break;
                    case Macros.Items.SHIRT:
                        catagory_num = item_gender.equals(Macros.CustomerMacros.WOMEN) ? 4169 : 3602;
                        break;
                    case Macros.Items.JEANS:
                        catagory_num = item_gender.equals(Macros.CustomerMacros.WOMEN) ? 3630 : 16463;
                        break;
                    case Macros.Items.DRESS:
                        catagory_num = 8799;
                        break;
                    case Macros.Items.SWIMWEAR:
                        catagory_num = item_gender.equals(Macros.CustomerMacros.WOMEN) ? 10118 /*2238*/ : 13210;
                        break;
                    case Macros.Items.WATCH:
                        catagory_num = item_gender.equals(Macros.CustomerMacros.WOMEN) ? 5088 : 19855;
                        break;
                    case Macros.Items.JACKETS:
                        catagory_num = item_gender.equals(Macros.CustomerMacros.WOMEN) ? 2641 : 16245;
                        break;
                    case Macros.Items.JEWELLERY:
                        catagory_num = item_gender.equals(Macros.CustomerMacros.WOMEN) ? 4175 : 5034;
                        break;
                }

                URL url = new URL("https://www.asos.com/cat/?cid=" + catagory_num + "&page=" + page_num);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while (line!=null){
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                String[] data_split = data.split("\"products\":",2);
                String koko = data_split[1];
                koko = koko.replaceAll("u002F", "").
                        replaceAll("urban", ".urban").
                        replaceAll("gg-", "").
                        replaceAll("under", ".under").
                        replaceAll("ufluff", ".ufluff").
                        replaceAll("upper", ".upper").
                        replaceAll("uncommon", ".uncommon");
                data = koko.substring(koko.indexOf("["),koko.indexOf("]")) + "]";

                JSONArray JA = new JSONArray(data);
                for(int i=0; i<JA.length();++i){
                    JSONObject JO = (JSONObject) JA.get(i);
                    String imageUrl = "https://" + JO.get("image").toString().replace(".com",".com/").replace("products","products/");
                    int opop = imageUrl.lastIndexOf("-");
                    String id = JO.get("id").toString();

                    String color = imageUrl.substring(opop + 1);
                    String[] description = JO.get("description").toString().replace("ASOS DESIGN", "").split(" in ");
                    String link = "https://www.asos.com/" + JO.get("url").toString().replace("prd", "/prd/").replace("asos-designasos", "asos-design/asos");
                    String seller_name = "ASOS";
                    String price = JO.get("price").toString();
                    String branda = "ASOS";
                    String cut = "Regular";
                    String material = "Unknown";
                    String fit = "Regular";
                    String style = "Regular";
                    String seller_id = "gxGB5zUoNed0rizltWVC9y8FceA3";

                    ArrayList<String> list = new ArrayList<>();
                    String[] name = JO.get("description").toString().split(" ");
                    for(String word : name){
                        if (!word.equals(""))
                            list.add(word.toLowerCase());
                    }

                    for (String style1 : Macros.Items.styles) {
                        if(description[0].contains(style1)) {
                            style = style1;
                            break;
                        }
                    }
                    for (String brand : Macros.Items.brands) {
                        if (description[0].contains(brand)) {
                            branda = brand;
                            break;
                        }
                    }
                    for (String cut1 : Macros.Items.cuts ) {
                        if(description[0].contains(cut1)){
                            cut = cut1;
                            break;
                        }
                    }
                    for (String fit1 : Macros.Items.jeans_fit ) {
                        if(description[0].contains(fit1)){
                            fit = fit1;
                            break;
                        }
                    }

                    final Map newImage = new HashMap();

                    newImage.put("id_in_seller", id);
                    newImage.put("color", color);
                    newImage.put("type", item_type);
                    newImage.put("brand", branda);
                    newImage.put("name", list);
                    newImage.put("price", price);
                    newImage.put("seller", seller_name);
                    newImage.put("seller_id", seller_id);
                    newImage.put("site_link", link);
                    newImage.put("page_num",page_num);
                    newImage.put("material",material);
                    newImage.put("category_num",catagory_num);
                    newImage.put("gender", item_gender);

                    if(item_type.equals(Macros.Items.SHIRT) || item_type.equals(Macros.Items.DRESS)){
                        newImage.put("fit",fit);
                        newImage.put("cut",cut);
                        newImage.put("style",style);
                    }
                    if(item_type.equals(Macros.Items.JEANS)){
                        newImage.put("fit",fit);
                        newImage.put("cut",cut);
                    }
                    if(item_type.equals(Macros.Items.SHOES)){
                        newImage.put("fit",fit);
                        newImage.put("style",style);
                    }
                }
            }
            catch (JSONException | IOException e) {
                Log.d(Macros.TAG, "add items failed: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }
}
