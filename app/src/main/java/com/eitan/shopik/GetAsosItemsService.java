package com.eitan.shopik;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.ViewModels.EntranceViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;

public class GetAsosItemsService extends JobIntentService {

    String data = "";
    String data2 = "";

    public static void koko(Context context, Intent work){
            enqueueWork(context,GetAsosItemsService.class,1,work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Macros.TAG,"OnCreate");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i(Macros.TAG,"starting GetAsosItemsService service");

        ArrayList<RecyclerItem> recyclerItems = new ArrayList<>();
        ArrayList<RecyclerItem> recyclerItems2 = new ArrayList<>();

        assert intent != null;
        String gender = intent.getStringExtra("gender");
        try {
            int catagory_num = 0;
            switch (gender){
                case Macros.CustomerMacros.WOMEN:
                    catagory_num = 27108;
                    break;
                case Macros.CustomerMacros.MEN:
                    catagory_num = 27112;
                    break;
            }

            URL url = new URL("https://www.asos.com/cat/?cid=" + 27108 + "&page=" + 1);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            URL url2 = new URL("https://www.asos.com/cat/?cid=" + 27112 + "&page=" + 1);
            HttpURLConnection httpURLConnection2 = (HttpURLConnection) url2.openConnection();
            InputStream inputStream2 = httpURLConnection2.getInputStream();
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream2));

            String line = "";
            while (line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }

            String line2 = "";
            while (line2 != null){
                line2 = bufferedReader2.readLine();
                data = data2 + line2;
            }

            String[] data_split = data.split("\"products\":",2);
            String koko = data_split[1];
            koko = koko.replaceAll("u002F","").
                    replaceAll("urban",".urban").
                    replaceAll("gg-","").
                    replaceAll("under",".under" ).
                    replaceAll("ufluff",".ufluff" ).
                    replaceAll("upper",".upper").
                    replaceAll("uncommon",".uncommon");

            data = koko.substring(koko.indexOf("["),koko.indexOf("]")) + "]";

            String[] data_split2 = data2.split("\"products\":",2);
            String koko2 = data_split2[1];
            koko2 = koko2.replaceAll("u002F","").
                    replaceAll("urban",".urban").
                    replaceAll("gg-","").
                    replaceAll("under",".under" ).
                    replaceAll("ufluff",".ufluff" ).
                    replaceAll("upper",".upper").
                    replaceAll("uncommon",".uncommon");

            data2 = koko2.substring(koko2.indexOf("["),koko2.indexOf("]")) + "]";

            JSONArray JA = new JSONArray(data);
            for(int i=0; i<JA.length();++i){
                JSONObject JO = (JSONObject) JA.get(i);
                String imageUrl = "https://" + JO.get("image").toString().replace(".com",".com/").replace("products","products/");
                int opop = imageUrl.lastIndexOf("-");

                String color = imageUrl.substring(opop + 1);
                String id = JO.get("id").toString();
                String[] description = JO.get("description").toString().replace("ASOS DESIGN", "").split(" in ");
                String link = "https://www.asos.com/" + JO.get("url").toString().replace("prd", "/prd/").replace("asos-designasos", "asos-design/asos");
                String price = JO.get("price").toString();
                String branda = "ASOS";

                for (String brand : Macros.Items.brands) {
                    if (description[0].contains(brand)) {
                        branda = brand;
                        break;
                    }
                }

                Currency shekel = Currency.getInstance("ILS");
                String currency_symbol = shekel.getSymbol();
                Double current = Double.parseDouble(price) * Macros.POUND_TO_ILS;
                price = new DecimalFormat("##.##").format(current) + currency_symbol;
                RecyclerItem recyclerItem = new RecyclerItem(branda + "   " + price, link);
                recyclerItem.setLink(link);
                Database connection = new Database();

                if(JO.get("url").toString().contains("prd")){
                    recyclerItem.setImages(connection.getASOSRecyclerImage("product",color,id));
                }
                else {
                    recyclerItem.setImages(connection.getASOSRecyclerImage("group",color,id));
                }
                recyclerItems.add(recyclerItem);
            }

            JSONArray JA2 = new JSONArray(data2);
            for(int i=0; i<JA2.length();++i){
                JSONObject JO = (JSONObject) JA.get(i);
                String imageUrl = "https://" + JO.get("image").toString().replace(".com",".com/").replace("products","products/");
                int opop = imageUrl.lastIndexOf("-");

                String color = imageUrl.substring(opop + 1);
                String id = JO.get("id").toString();
                String[] description = JO.get("description").toString().replace("ASOS DESIGN", "").split(" in ");
                String link = "https://www.asos.com/" + JO.get("url").toString().replace("prd", "/prd/").replace("asos-designasos", "asos-design/asos");
                String price = JO.get("price").toString();
                String branda = "ASOS";

                for (String brand : Macros.Items.brands) {
                    if (description[0].contains(brand)) {
                        branda = brand;
                        break;
                    }
                }

                Currency shekel = Currency.getInstance("ILS");
                String currency_symbol = shekel.getSymbol();
                Double current = Double.parseDouble(price) * Macros.POUND_TO_ILS;
                price = new DecimalFormat("##.##").format(current) + currency_symbol;
                RecyclerItem recyclerItem = new RecyclerItem(branda + "   " + price, link);
                recyclerItem.setLink(link);
                Database connection = new Database();

                if(JO.get("url").toString().contains("prd")){
                    recyclerItem.setImages(connection.getASOSRecyclerImage("product",color,id));
                }
                else {
                    recyclerItem.setImages(connection.getASOSRecyclerImage("group",color,id));
                }
                recyclerItems2.add(recyclerItem);
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("items_men",recyclerItems2);
            bundle.putSerializable("items_women",recyclerItems);
            // Puts the status into the Intent
            Intent localIntent = new Intent("BROADCAST_ACTION").putExtra("bundle", bundle);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            // Broadcasts the Intent to receivers in this app.

        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
            Log.i(Macros.TAG,"failed fetching items from ASOS");
        }
    }

    @Override
    public void onDestroy() {
        super.onCreate();
        Log.d(Macros.TAG,"onDestroy");
    }
}
