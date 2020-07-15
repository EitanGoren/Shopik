package com.eitan.shopik.explanation;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.eitan.shopik.Adapters.DialogGridAdapter;
import com.eitan.shopik.Adapters.RecyclerAdapter;
import com.eitan.shopik.Database;
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.EntranceViewModel;
import com.eitan.shopik.ViewModels.GenderModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class E1Fragment extends Fragment {

    private String gender;
    private RecyclerAdapter recyclerAdapter;
    private EntranceViewModel viewModel;
    private GenderModel model;
    private Dialog dialog;
    private DialogGridAdapter gridAdapter;
    private ProgressBar dialogProgressBar;
    private ArrayList<RecyclerItem> recyclerItems;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_e1, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        TextView liked_counter = getView().findViewById(R.id.best_sellers_count);
        model.getGender().observe(getViewLifecycleOwner(), s -> {
            if(!gender.equals(s)) {
                gender = s;

                viewModel.setLiked_items(gender);
                recyclerAdapter.notifyDataSetChanged();
                liked_counter.setText("Loading...");

                if(gender.equals(Macros.CustomerMacros.WOMEN))
                    setWomenEntrance();
                else
                    setMenEntrance();
            }
        });

        RelativeLayout relativeLayout1 = Objects.requireNonNull(getView()).findViewById(R.id.layout2);
        RelativeLayout relativeLayout2 = Objects.requireNonNull(getView()).findViewById(R.id.layout3);
        RelativeLayout relativeLayout3 = Objects.requireNonNull(getView()).findViewById(R.id.layout4);
        relativeLayout1.setOnClickListener(v -> showNewItemsDialog(Macros.Items.NEW_CLOTHING));
        relativeLayout2.setOnClickListener(v -> showNewItemsDialog(Macros.Items.NEW_SHOES));
        relativeLayout3.setOnClickListener(v -> showNewItemsDialog(Macros.Items.NEW_TRENDING));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        model.getGender().removeObservers(getViewLifecycleOwner());
      //  recyclerAdapter = null;
      //  dialog = null;
       // gridAdapter = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {

        model = new ViewModelProvider(requireActivity()).get(GenderModel.class);
        gender = model.getGender().getValue();
        viewModel = new ViewModelProvider(requireActivity()).get(EntranceViewModel.class);
        recyclerAdapter = new RecyclerAdapter(viewModel.getRecentLikedItems().getValue(),"Item");
        dialog = new Dialog(Objects.requireNonNull(getContext()));
        dialog.setContentView(R.layout.new_items_grid_dialog);
        recyclerItems = new ArrayList<>();
        gridAdapter = new DialogGridAdapter(dialog.getContext(), R.layout.e3_grid_item, recyclerItems);
        dialogProgressBar = dialog.findViewById(R.id.progressBar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        RecyclerView recyclerView = Objects.requireNonNull(getView()).findViewById(R.id.recycler);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setScrollbarFadingEnabled(true);

        if(gender.equals(Macros.CustomerMacros.WOMEN))
            setWomenEntrance();
        else
            setMenEntrance();
    }

    private class getLikedItems extends AsyncTask<Void,Integer,Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected Void doInBackground(Void... voids) {
            for( String type : Macros.Items.getAllItemTypes() ) {
                FirebaseDatabase.getInstance().getReference().
                        child(Macros.ITEMS).
                        child(gender).
                        child(type).
                        orderByChild(Macros.CustomerMacros.LIKED).
                        addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long count = 0;
                                long max = 0;
                                String item_id = "";
                                System.out.println(type + " : " + System.lineSeparator());
                                if(dataSnapshot.exists()) {
                                    for(DataSnapshot data : dataSnapshot.getChildren()){
                                        Map<String,Object> map = (Map)data.getValue();
                                        if( map != null && map.get(Macros.CustomerMacros.LIKED) != null ) {
                                            Map<String, Object> map2 = (Map<String, Object>) map.get(Macros.CustomerMacros.LIKED);
                                            assert map2 != null;
                                            count = map2.size();
                                        }
                                        if(count > max) {
                                            max = count;
                                            item_id = data.getKey();
                                        }
                                    }
                                    System.out.println(max + ", " + item_id + System.lineSeparator());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(Macros.TAG,"getLikedItems()::doInBackground() " + databaseError.getMessage());
                            }
                        });
               }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void getWomenItem(final String type, QuerySnapshot queryDocumentSnapshots){
            viewModel.removeAllType(type,gender);
            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
            int counter = 0;
            for (DocumentSnapshot item : documentSnapshots){
                RecyclerItem recyclerItem = initItem(type);
                viewModel.addWomenLikedItem(recyclerItem);
                counter++;
                //publishProgress(counter, documentSnapshots.size());
            }
            viewModel.setLiked_items(gender);
            recyclerAdapter.notifyDataSetChanged();
           // liked_counter.setText("(" + recyclerAdapter.getItemCount() + " items)");
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void getMenItem(final String type, QuerySnapshot queryDocumentSnapshots){
            viewModel.removeAllType(type,gender);
            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
            int counter = 0;
            for(DocumentSnapshot item : documentSnapshots) {
                RecyclerItem recyclerItem = initItem(type);
                viewModel.addMenLikedItem(recyclerItem);
                counter++;
               // publishProgress(counter, documentSnapshots.size());
            }
            viewModel.setLiked_items(gender);
            recyclerAdapter.notifyDataSetChanged();
           // liked_counter.setText("(" + recyclerAdapter.getItemCount() + " items)");
        }
    }

    public class fetchData extends AsyncTask<Void,Integer,Void> {

        String data = "";
        Map map;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogProgressBar.setVisibility(View.VISIBLE);
            dialogProgressBar.setProgress(0);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                map = new HashMap<>();
                if(gender.equals(Macros.CustomerMacros.WOMEN)){
                    for(int cat_num : Macros.Arrays.WOMEN_CLOTHES_TYPES) {
                        getWomenItems(cat_num);
                    }
                }
                else {
                    for (int cat_num : Macros.Arrays.MEN_CLOTHES_TYPES) {
                        getMenItems(cat_num);
                    }
                }
            }
            catch (Exception e){
                Log.d(Macros.TAG, "E1fragment::getItems() " + Objects.requireNonNull(e.getMessage()));
            }
            return null;
        }

        private void getMenItems(int cat_num) throws IOException {
            URL url = new URL("https://www.asos.com/cat/?cid=" + cat_num + "&page=" + 1);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            try {
                String cat = "";
                switch (cat_num){
                    case 17184:
                        cat = Macros.Items.NEW_SHOES;
                        break;
                    case 27441:
                        cat = Macros.Items.NEW_CLOTHING;
                        break;
                    case 13500:
                        cat = Macros.Items.NEW_TRENDING;
                }
                viewModel.addMen_new_num(cat,0);

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                String[] data_split = data.split("\"products\":", 2);
                String koko = data_split[1];
                koko = koko.replaceAll("u002F", "").
                        replaceAll("urban", ".urban").
                        replaceAll("gg-", "").
                        replaceAll("under", ".under").
                        replaceAll("ufluff", ".ufluff").
                        replaceAll("upper", ".upper").
                        replaceAll("uncommon", ".uncommon").
                        replaceAll("uoh",".uoh");

                data = koko.substring(koko.indexOf("["), koko.indexOf("]")) + "]";

                JSONArray JA = new JSONArray(data);
                int total_items =  JA.length();
                for (int i = 0; i < total_items; ++i) {
                    JSONObject JO = (JSONObject) JA.get(i);
                    String imageUrl = "https://" + JO.get("image").toString().
                            replace(".com", ".com/").
                            replace("products", "products/");
                    int opop = imageUrl.lastIndexOf("-");

                    String color = imageUrl.substring(opop + 1);
                    String id = JO.get("id").toString();
                    String link = "https://www.asos.com/" + JO.get("url").toString().
                            replace("prd","/prd/").
                            replace("asos-designasos","asos-design/asos");

                    String price = JO.get("price").toString();
                    String branda = "ASOS";

                    Currency shekel = Currency.getInstance("ILS");
                    String currency_symbol = shekel.getSymbol();
                    Double current = Double.parseDouble(price) * Macros.POUND_TO_ILS;
                    price = new DecimalFormat("##.##").format(current) + currency_symbol;

                    RecyclerItem recyclerItem = new RecyclerItem(branda, link);
                    recyclerItem.setPrice(price);
                    recyclerItem.setLink(link);
                    recyclerItem.setType(cat);

                    if((boolean) JO.get("isOutlet")){
                        recyclerItem.setOutlet(true);
                        recyclerItem.setReduced_price(JO.get("reducedPrice").toString());
                    }
                    else
                        recyclerItem.setOutlet(false);

                    if((boolean) JO.get("isSale") ){
                        recyclerItem.setSale(true);
                        recyclerItem.setReduced_price(JO.get("reducedPrice").toString());
                    }
                    else
                        recyclerItem.setSale(false);

                    Database connection = new Database();

                    if (JO.get("url").toString().contains("prd")) {
                        recyclerItem.setImages(connection.getASOSRecyclerImage("product", color, id));
                    } else {
                        recyclerItem.setImages(connection.getASOSRecyclerImage("group", color, id));
                    }

                    viewModel.addMenItem(recyclerItem);
                    publishProgress(i,total_items);
                }
            }
            catch (Exception e ) {
                Log.d(Macros.TAG, "E1::getMenItems() " + Objects.requireNonNull(e.getMessage()));
            }
            finally {
                httpURLConnection.disconnect();
            }
        }

        private void getWomenItems(int cat_num) throws IOException {

            URL url = new URL("https://www.asos.com/cat/?cid=" + cat_num + "&page=" + 1);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            try {

                String cat = "";
                switch (cat_num){
                    case 6992:
                        cat = Macros.Items.NEW_SHOES;
                        break;
                    case 2623:
                        cat = Macros.Items.NEW_CLOTHING;
                        break;
                    case 13497:
                        cat = Macros.Items.NEW_TRENDING;
                }

                viewModel.addWomen_new_num(cat,0);
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                String[] data_split = data.split("\"products\":", 2);
                String koko = data_split[1];
                koko = koko.replaceAll("u002F", "").
                        replaceAll("urban", ".urban").
                        replaceAll("gg-", "").
                        replaceAll("under", ".under").
                        replaceAll("ufluff", ".ufluff").
                        replaceAll("upper", ".upper").
                        replaceAll("uncommon", ".uncommon").
                        replaceAll("uoh",".uoh");

                data = koko.substring(koko.indexOf("["), koko.indexOf("]")) + "]";

                JSONArray JA = new JSONArray(data);
                int total_items =  JA.length();
                for (int i = 0; i < total_items; ++i) {
                    JSONObject JO = (JSONObject) JA.get(i);
                    String imageUrl = "https://" + JO.get("image").toString().
                            replace(".com", ".com/").
                            replace("products", "products/");

                    int opop = imageUrl.lastIndexOf("-");

                    String color = imageUrl.substring(opop + 1);
                    String id = JO.get("id").toString();
                    String link = "https://www.asos.com/" + JO.get("url").toString().
                            replace("prd","/prd/").
                            replace("asos-designasos", "asos-design/asos");

                    String price = JO.get("price").toString();

                    ArrayList<String> list = new ArrayList<>();
                    String[] name = JO.get("description").toString().split(" ");
                    for(String word : name){
                        if (!word.equals("")) {
                            list.add(word.toLowerCase());
                        }
                    }

                    Currency shekel = Currency.getInstance("ILS");
                    String currency_symbol = shekel.getSymbol();
                    Double current = Double.parseDouble(price) * Macros.POUND_TO_ILS;

                    price = new DecimalFormat("##.##").format(current) + currency_symbol;
                    RecyclerItem recyclerItem = new RecyclerItem(cat, link);
                    recyclerItem.setPrice(price);
                    recyclerItem.setLink(link);
                    recyclerItem.setType(cat);
                    recyclerItem.setDescription(list);

                    if((boolean) JO.get("isOutlet")){
                        recyclerItem.setOutlet(true);
                        recyclerItem.setReduced_price(JO.get("reducedPrice").toString());
                    }
                    else
                        recyclerItem.setOutlet(false);

                    if((boolean) JO.get("isSale") ){
                        recyclerItem.setSale(true);
                        recyclerItem.setReduced_price(JO.get("reducedPrice").toString());
                    }
                    else
                        recyclerItem.setSale(false);

                    Database connection = new Database();

                    if (JO.get("url").toString().contains("prd"))
                        recyclerItem.setImages(connection.getASOSRecyclerImage("product", color, id));
                    else
                        recyclerItem.setImages(connection.getASOSRecyclerImage("group", color, id));

                    viewModel.addWomenItem(recyclerItem);
                    publishProgress(i,total_items);
                }
            }
            catch (Exception e ) {
                Log.d(Macros.TAG,"E1::getWomenItems() " +  Objects.requireNonNull(e.getMessage()));
            }
            finally {
                httpURLConnection.disconnect();
            }
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialogProgressBar.setProgress((values[0]/values[1])*100);
            viewModel.setList(gender);
            gridAdapter.notifyDataSetChanged();
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            viewModel.setList(gender);
            gridAdapter.notifyDataSetChanged();
            dialogProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private RecyclerItem initItem(final String type) {
        final ShoppingItem shoppingItem = new ShoppingItem();
        shoppingItem.setAd(false);

        RecyclerItem recyclerItem = new RecyclerItem(shoppingItem.getLikes() + " Likes", shoppingItem.getSite_link());
        recyclerItem.setGender(shoppingItem.getGender());
        recyclerItem.setType(type);
        recyclerItem.setLikes(shoppingItem.getLikes());

        Database connection = new Database();
        ArrayList<String> list = new ArrayList<>();
        list.add(connection.getASOSimageUrl(1, shoppingItem.getColor(), shoppingItem.getId_in_seller()));
        list.add(connection.getASOSimageUrl(2, shoppingItem.getColor(), shoppingItem.getId_in_seller()));
        list.add(connection.getASOSimageUrl(3, shoppingItem.getColor(), shoppingItem.getId_in_seller()));
        list.add(connection.getASOSimageUrl(4, shoppingItem.getColor(), shoppingItem.getId_in_seller()));

        recyclerItem.setImages(list);

        return recyclerItem;
    }

    private void zeroAll(final String type,QuerySnapshot queryDocumentSnapshots) {
        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
        for (DocumentSnapshot item : documentSnapshots) {

            HashMap map = new HashMap();
            map.put(Macros.Items.LIKES_NUM, 0);
            map.put(Macros.Items.LIKED, null);
            map.put(Macros.Items.UNLIKES_NUM, 0);
            map.put(Macros.Items.UNLIKED, null);

        /*    itemsFS.document(gender).
                    collection(type).
                    document(item.getId()).
                    update(map); */
        }
    }

    private void showNewItemsDialog(String type) {

        recyclerItems.clear();
        for(RecyclerItem item : Objects.requireNonNull(viewModel.getItems().getValue())) {
            if (type.equals(item.getType())) {
                recyclerItems.add(item);
                gridAdapter.notifyDataSetChanged();
            }
        }

        String text_header ="";
        if(type.equals(Macros.Items.NEW_TRENDING)){
            text_header = "Trending Now";
            TextView header = dialog.findViewById(R.id.new_items_header);
            header.setText(text_header);
            header.setCompoundDrawablesWithIntrinsicBounds(null,null, dialog.getContext().getDrawable(R.drawable.ic_baseline_trending_up),null);
            header.setCompoundDrawablePadding(20);
        }
        else{
            TextView header = dialog.findViewById(R.id.new_items_header);
            header.setCompoundDrawablesWithIntrinsicBounds(null,null, null,null);
            text_header = "New " + type;
            header.setText(text_header);
        }

        GridView gridContainer = dialog.findViewById(R.id.new_items_grid);
        gridContainer.setAdapter(gridAdapter);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    private void setAnimation() {
        Animation fading = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_in);

        RelativeLayout layout1 = Objects.requireNonNull(getView()).findViewById(R.id.layout2);
        RelativeLayout layout2 = getView().findViewById(R.id.layout3);
        RelativeLayout layout3 = getView().findViewById(R.id.layout4);

        layout1.startAnimation(fading);
        layout2.startAnimation(fading);
        layout3.startAnimation(fading);
    }
    private void setWomenEntrance() {

        String first_header = "ALL NEW CLOTHING ITEMS";
        String second_header = "OUR NEWEST SHOES COLLECTION";
        String third_header = "MOST TRENDING NOW";


        TextView textView1 = Objects.requireNonNull(getView()).findViewById(R.id.text_btn1);
        TextView textView2 = Objects.requireNonNull(getView()).findViewById(R.id.text_btn2);
        TextView textView3 = Objects.requireNonNull(getView()).findViewById(R.id.text_btn3);

        TextView text_header1 = Objects.requireNonNull(getView()).findViewById(R.id.text_header1);
        text_header1.setText(first_header);
        Glide.with(this).asDrawable().load(Macros.WOMEN_FIRST_PIC).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                textView1.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {}
        });

        TextView text_header2 = getView().findViewById(R.id.text_header2);
        text_header2.setText(second_header);
        Glide.with(this).asDrawable().load(Macros.WOMEN_SECOND_PIC).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                textView2.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {}
        });

        TextView text_header3 = getView().findViewById(R.id.text_header3);
        text_header3.setText(third_header);
        Glide.with(this).asDrawable().load(Macros.WOMEN_THIRD_PIC).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                textView3.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {}
        });

        setAnimation();
    }
    private void setMenEntrance() {

        String first_header = "ALL NEW CLOTHING ITEMS";
        String second_header = "OUR NEWEST SHOES COLLECTION";
        String third_header = "TRENDING NOW";

        setAnimation();

        final TextView textView1 = Objects.requireNonNull(getView()).findViewById(R.id.text_btn1);
        TextView text_header1 = getView().findViewById(R.id.text_header1);
        text_header1.setText(first_header);
        Glide.with(this).asDrawable().load(Macros.MEN_FIRST_PIC).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                textView1.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {}
        });

        final TextView textView2 = getView().findViewById(R.id.text_btn2);
        TextView text_header2 = getView().findViewById(R.id.text_header2);
        text_header2.setText(second_header);
        Glide.with(this).asDrawable().load(Macros.MEN_SECOND_PIC).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                textView2.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {}
        });

        final TextView textView3 = getView().findViewById(R.id.text_btn3);
        TextView text_header3 = getView().findViewById(R.id.text_header3);
        text_header3.setText(third_header);
        Glide.with(this).asDrawable().load(Macros.MEN_THIRD_PIC).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                textView3.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {}
        });
    }
}
