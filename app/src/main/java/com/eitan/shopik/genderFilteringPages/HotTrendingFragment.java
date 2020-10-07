package com.eitan.shopik.genderFilteringPages;

import android.animation.LayoutTransition;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.eitan.shopik.CustomItemAnimator;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.adapters.RecyclerAdapter;
import com.eitan.shopik.adapters.RecyclerGridAdapter;
import com.eitan.shopik.items.RecyclerItem;
import com.eitan.shopik.items.ShoppingItem;
import com.eitan.shopik.viewModels.EntranceViewModel;
import com.eitan.shopik.viewModels.GenderModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class HotTrendingFragment extends Fragment {

    private static String gender;
    private static EntranceViewModel entranceViewModel;
    private RecyclerAdapter recyclerAdapter;
    private TextView txt;
    private GenderModel model;
    private Dialog dialog;
    private CopyOnWriteArrayList<ShoppingItem> new_shoes_items;
    private CopyOnWriteArrayList<ShoppingItem> new_clothing_items;
    private CopyOnWriteArrayList<ShoppingItem> new_asos_items;
    private CopyOnWriteArrayList<ShoppingItem> new_tx_items;
    private CopyOnWriteArrayList<ShoppingItem> new_renuar_items;
    private CopyOnWriteArrayList<ShoppingItem> new_shein_items;
    private CopyOnWriteArrayList<ShoppingItem> new_tfs_items;
    private CopyOnWriteArrayList<ShoppingItem> new_hoodies_items;
    private RelativeLayout layout1, layout2, layout3, layout4, layout6, layout5, layout7, layout8;
    private TextView liked_counter;
    private RelativeLayout layout;
    private TextView header;
    private ImageView textView1, textView2, textView3, textView4, textView5,
            textView6, textView7, textView8;
    private RecyclerView recyclerView;
    private Observer<String> observer;
    private Observer<ArrayList<RecyclerItem>> recentObserver;
    private Observer<ArrayList<ShoppingItem>> shoesObserver, clothingObserver,
            asosObserver, txObserver, renuarObserver, sheinObserver, tfsObserver, hoodiesObserver;
    private Observer<Integer> sheinCountObserver, asosCountObserver, castroCountObserver,
            txCountObserver, renuarCountObserver, aldoCountObserver, tfsCountObserver, hoodiesCountObserver;
    private RecyclerGridAdapter shoesGridAdapter, clothingGridAdapter,
            TXGridAdapter, AsosGridAdapter, renuarGridAdapter, SheinGridAdapter,
            TfsGridAdapter, HoodiesGridAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        entranceViewModel = new ViewModelProvider(requireActivity()).get(EntranceViewModel.class);
        recyclerAdapter = new RecyclerAdapter(entranceViewModel.getRecentLikedItems().getValue(),"Item");
        new_clothing_items = new CopyOnWriteArrayList<>();
        new_shoes_items = new CopyOnWriteArrayList<>();
        new_asos_items = new CopyOnWriteArrayList<>();
        new_tx_items = new CopyOnWriteArrayList<>();
        new_renuar_items = new CopyOnWriteArrayList<>();
        new_shein_items = new CopyOnWriteArrayList<>();
        new_tfs_items = new CopyOnWriteArrayList<>();
        new_hoodies_items = new CopyOnWriteArrayList<>();
        model = new ViewModelProvider(requireActivity()).get(GenderModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_e1, container, false);

        liked_counter = view.findViewById(R.id.best_sellers_count);
        layout1 = view.findViewById(R.id.layout2);
        layout2 = view.findViewById(R.id.layout3);
        layout3 = view.findViewById(R.id.layout4);
        layout4 = view.findViewById(R.id.layout5);
        layout5 = view.findViewById(R.id.layout6);
        layout6 = view.findViewById(R.id.layout7);
        layout7 = view.findViewById(R.id.layout8);
        layout8 = view.findViewById(R.id.layout9);
        layout = view.findViewById(R.id.layout1);
        header = view.findViewById(R.id.header);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(),
                LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setScrollbarFadingEnabled(true);
        recyclerView.setItemAnimator(new CustomItemAnimator());

        observer = s -> {
            if(!gender.equals(s)) {
                gender = s;

                recyclerAdapter.notifyDataSetChanged();
                liked_counter.setText(R.string.loading);
            }
        };

        layout1.setOnClickListener(v -> AsosDialog());
        layout2.setOnClickListener(v -> ClothingDialog());
        layout3.setOnClickListener(v -> SheinDialog());
        layout4.setOnClickListener(v -> ShoesDialog());
        layout5.setOnClickListener(v -> RenuarDialog());
        layout6.setOnClickListener(v -> TXDialog());
        layout7.setOnClickListener(v -> TFSDialog());
        layout8.setOnClickListener(v -> HoodiesDialog());

        recentObserver = recyclerItems -> {
            if(recyclerItems.isEmpty())
                layout.setVisibility(View.GONE);
            else {
                layout.setVisibility(View.VISIBLE);
                String text = "(" + recyclerItems.size() + " items)";
                liked_counter.setText(text);
                recyclerAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(recyclerAdapter);
            }
        };
        clothingObserver = recyclerItems -> {
            new_clothing_items.clear();
            new_clothing_items.addAll(recyclerItems);
            clothingGridAdapter.notifyDataSetChanged();
        };
        shoesObserver = recyclerItems -> {
            new_shoes_items.clear();
            new_shoes_items.addAll(recyclerItems);
            shoesGridAdapter.notifyDataSetChanged();
        };
        txObserver = recyclerItems -> {
            new_tx_items.clear();
            new_tx_items.addAll(recyclerItems);
            TXGridAdapter.notifyDataSetChanged();
        };
        asosObserver = recyclerItems -> {
            new_asos_items.clear();
            new_asos_items.addAll(recyclerItems);
            AsosGridAdapter.notifyDataSetChanged();
        };
        renuarObserver = recyclerItems -> {
            new_renuar_items.clear();
            new_renuar_items.addAll(recyclerItems);
            renuarGridAdapter.notifyDataSetChanged();
        };
        sheinObserver = recyclerItems -> {
            new_shein_items.clear();
            new_shein_items.addAll(recyclerItems);
            SheinGridAdapter.notifyDataSetChanged();
        };
        hoodiesObserver = recyclerItems -> {
            new_hoodies_items.clear();
            new_hoodies_items.addAll(recyclerItems);
            HoodiesGridAdapter.notifyDataSetChanged();
        };
        tfsObserver = recyclerItems -> {
            new_tfs_items.clear();
            new_tfs_items.addAll(recyclerItems);
            TfsGridAdapter.notifyDataSetChanged();
        };

        sheinCountObserver = this::setNumOfItems;
        asosCountObserver = this::setNumOfItems;
        aldoCountObserver = this::setNumOfItems;
        castroCountObserver = this::setNumOfItems;
        txCountObserver = this::setNumOfItems;
        renuarCountObserver = this::setNumOfItems;
        tfsCountObserver = this::setNumOfItems;
        hoodiesCountObserver = this::setNumOfItems;

        return view;
    }

    private void setNumOfItems(Integer integer){
        String text = "(" + integer + " items)";
        txt.setText(text);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        model.getGender().observe(getViewLifecycleOwner(), observer);
        entranceViewModel.getRecentLikedItems().observe(requireActivity(), recentObserver);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {

        gender = model.getGender().getValue();
        dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.new_items_grid_dialog);
        CardView cardSheet = dialog.findViewById(R.id.card_sheet);
        BottomSheetBehavior<CardView> bottomSheetBehavior = BottomSheetBehavior.from(cardSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setDraggable(true);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dialog.dismiss();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        txt = dialog.findViewById(R.id.items_count);

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setItemAnimator(new CustomItemAnimator());

        ((ViewGroup) requireView().findViewById(R.id.root)).getLayoutTransition().
                enableTransitionType(LayoutTransition.APPEARING);

        setEntrance();

        String name = Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().
                getCurrentUser()).getDisplayName()).split(" ")[0];
        String text = "Hi " + name + ", Welcome to Shopik ! ";
        header.setText(text);
    }

    private void ClothingDialog() {
       stopAllObservers();
       RecyclerView recyclerView = dialog.findViewById(R.id.new_items_grid);
       clothingGridAdapter = new RecyclerGridAdapter(new_clothing_items,"outlets");
       recyclerView.setItemAnimator(new CustomItemAnimator());
       recyclerView.setAdapter(clothingGridAdapter);
       recyclerView.setLayoutManager(mLayoutManager);

       entranceViewModel.getCurrentClothingItem().observe(getViewLifecycleOwner(), castroCountObserver);

       if(gender.equals(Macros.CustomerMacros.WOMEN))
           entranceViewModel.getWomen_clothing_items().observe(getViewLifecycleOwner(), clothingObserver);
       else
           entranceViewModel.getMen_clothing_items().observe(getViewLifecycleOwner(), clothingObserver);

       String text_header;
       TextView header = dialog.findViewById(R.id.new_items_header);
        header.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        text_header = "New In Castro";
        header.setText(text_header);

       Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
       dialog.getWindow().setGravity(Gravity.BOTTOM);
       dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
       dialog.show();
    }
    private void ShoesDialog() {
        stopAllObservers();
        RecyclerView recyclerView = dialog.findViewById(R.id.new_items_grid);
        shoesGridAdapter = new RecyclerGridAdapter(new_shoes_items,"outlets");
        recyclerView.setItemAnimator(new CustomItemAnimator());
        recyclerView.setAdapter(shoesGridAdapter);
        recyclerView.setLayoutManager(mLayoutManager);

       entranceViewModel.getCurrentShoesItem().observe(getViewLifecycleOwner(), aldoCountObserver);

       if(gender.equals(Macros.CustomerMacros.WOMEN))
           entranceViewModel.getWomen_shoes_items().observe(getViewLifecycleOwner(), shoesObserver);
       else
           entranceViewModel.getMen_shoes_items().observe(getViewLifecycleOwner(), shoesObserver);

       String text_header;

       TextView header = dialog.findViewById(R.id.new_items_header);
           header.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
           text_header = "New In Aldo";
           header.setText(text_header);

       Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
       dialog.getWindow().setGravity(Gravity.BOTTOM);
       dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
       dialog.show();
    }
    private void TXDialog() {
        stopAllObservers();
        RecyclerView recyclerView = dialog.findViewById(R.id.new_items_grid);
        TXGridAdapter = new RecyclerGridAdapter(new_tx_items,"outlets");
        recyclerView.setItemAnimator(new CustomItemAnimator());
        recyclerView.setAdapter(TXGridAdapter);
        recyclerView.setLayoutManager(mLayoutManager);

        entranceViewModel.getCurrentTerminalXItem().observe(getViewLifecycleOwner(),txCountObserver);

        if(gender.equals(Macros.CustomerMacros.WOMEN))
            entranceViewModel.getWomen_tx_items().observe(getViewLifecycleOwner(), txObserver);
        else
            entranceViewModel.getMen_tx_items().observe(getViewLifecycleOwner(), txObserver);

        String text_header;
        TextView header = dialog.findViewById(R.id.new_items_header);
        header.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        text_header = "New In Terminal X";
        header.setText(text_header);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }
    private void AsosDialog() {
        stopAllObservers();
        RecyclerView recyclerView = dialog.findViewById(R.id.new_items_grid);
        AsosGridAdapter = new RecyclerGridAdapter(new_asos_items,"outlets");
        recyclerView.setItemAnimator(new CustomItemAnimator());
        recyclerView.setAdapter(AsosGridAdapter);
        recyclerView.setLayoutManager(mLayoutManager);

        entranceViewModel.getCurrentAsosItem().observe(getViewLifecycleOwner(),asosCountObserver);

        if(gender.equals(Macros.CustomerMacros.WOMEN))
            entranceViewModel.getWomen_asos_items().observe(getViewLifecycleOwner(), asosObserver);
        else
            entranceViewModel.getMen_asos_items().observe(getViewLifecycleOwner(), asosObserver);

        String text_header;

        TextView header = dialog.findViewById(R.id.new_items_header);
        header.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        text_header = "New In Asos";
        header.setText(text_header);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }
    private void RenuarDialog() {
        stopAllObservers();
        RecyclerView recyclerView = dialog.findViewById(R.id.new_items_grid);
        renuarGridAdapter = new RecyclerGridAdapter(new_renuar_items,"outlets");
        recyclerView.setItemAnimator(new CustomItemAnimator());
        recyclerView.setAdapter(renuarGridAdapter);
        recyclerView.setLayoutManager(mLayoutManager);

        entranceViewModel.getCurrentRenuarItem().observe(getViewLifecycleOwner(),renuarCountObserver);

        if(gender.equals(Macros.CustomerMacros.WOMEN))
            entranceViewModel.getWomen_renuar_items().observe(getViewLifecycleOwner(), renuarObserver);
        else
            entranceViewModel.getMen_renuar_items().observe(getViewLifecycleOwner(), renuarObserver);

        String text_header;

        TextView header = dialog.findViewById(R.id.new_items_header);
        header.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        text_header = "New In Renuar";
        header.setText(text_header);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }
    private void SheinDialog() {
        stopAllObservers();
        RecyclerView recyclerView = dialog.findViewById(R.id.new_items_grid);
        SheinGridAdapter = new RecyclerGridAdapter(new_shein_items,"outlets");
        recyclerView.setItemAnimator(new CustomItemAnimator());
        recyclerView.setAdapter(SheinGridAdapter);
        recyclerView.setLayoutManager(mLayoutManager);

        entranceViewModel.getCurrentSheinItem().observe(getViewLifecycleOwner(),sheinCountObserver);

        if(gender.equals(Macros.CustomerMacros.WOMEN))
            entranceViewModel.getWomen_shein_items().observe(getViewLifecycleOwner(), sheinObserver);
        else
            entranceViewModel.getMen_shein_items().observe(getViewLifecycleOwner(), sheinObserver);

        String text_header;

        TextView header = dialog.findViewById(R.id.new_items_header);
        header.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        text_header = "New In Shein";
        header.setText(text_header);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }
    private void TFSDialog() {
        stopAllObservers();
        RecyclerView recyclerView = dialog.findViewById(R.id.new_items_grid);
        TfsGridAdapter = new RecyclerGridAdapter(new_tfs_items,"outlets");
        recyclerView.setItemAnimator(new CustomItemAnimator());
        recyclerView.setAdapter(TfsGridAdapter);
        recyclerView.setLayoutManager(mLayoutManager);

        entranceViewModel.getCurrentTfsItem().observe(getViewLifecycleOwner(),tfsCountObserver);

        if(gender.equals(Macros.CustomerMacros.WOMEN))
            entranceViewModel.getWomen_tfs_items().observe(getViewLifecycleOwner(), tfsObserver);
        else
            entranceViewModel.getMen_tfs_items().observe(getViewLifecycleOwner(), tfsObserver);

        String text_header;

        TextView header = dialog.findViewById(R.id.new_items_header);
        header.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        text_header = "New In TFS";
        header.setText(text_header);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }
    private void HoodiesDialog() {
        stopAllObservers();
        RecyclerView recyclerView = dialog.findViewById(R.id.new_items_grid);
        HoodiesGridAdapter = new RecyclerGridAdapter(new_hoodies_items,"outlets");
        recyclerView.setItemAnimator(new CustomItemAnimator());
        recyclerView.setAdapter(HoodiesGridAdapter);
        recyclerView.setLayoutManager(mLayoutManager);

        entranceViewModel.getCurrentHoodiesItem().observe(getViewLifecycleOwner(),hoodiesCountObserver);

        if(gender.equals(Macros.CustomerMacros.WOMEN))
            entranceViewModel.getWomen_hoodies_items().observe(getViewLifecycleOwner(), hoodiesObserver);
        else
            entranceViewModel.getMen_hoodies_items().observe(getViewLifecycleOwner(), hoodiesObserver);

        String text_header;

        TextView header = dialog.findViewById(R.id.new_items_header);
        header.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        text_header = "New In Hoodies";
        header.setText(text_header);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    private void setAnimation() {
        Animation fading = AnimationUtils.loadAnimation(requireActivity(),R.anim.fade_in);

        layout1.startAnimation(fading);
        layout2.startAnimation(fading);
        layout3.startAnimation(fading);
        layout4.startAnimation(fading);
        layout5.startAnimation(fading);
        layout6.startAnimation(fading);
        layout7.startAnimation(fading);
        layout8.startAnimation(fading);
    }

    private void initViews(){
        String castro = "CASTRO";
        String aldo = "ALDO";
        String asos = "ASOS";
        String shein = "SHEIN";
        String renuar = "RENUAR";
        String tx = "TERMINAL X";
        String tfs = "TFS";
        String hoodies = "Hoodies";

        setAnimation();

        textView1 = requireView().findViewById(R.id.text_btn1);
        textView2 = requireView().findViewById(R.id.text_btn2);
        textView3 = requireView().findViewById(R.id.text_btn4);
        textView4 = requireView().findViewById(R.id.text_btn5);
        textView5 = requireView().findViewById(R.id.text_btn6);
        textView6 = requireView().findViewById(R.id.text_btn7);
        textView7 = requireView().findViewById(R.id.text_btn8);
        textView8 = requireView().findViewById(R.id.text_btn9);

        TextView text_header1 = requireView().findViewById(R.id.text_header1);
        TextView text_header2 = requireView().findViewById(R.id.text_header2);
        TextView text_header3 = requireView().findViewById(R.id.text_header4);
        TextView text_header4 = requireView().findViewById(R.id.text_header5);
        TextView text_header5 = requireView().findViewById(R.id.text_header6);
        TextView text_header6 = requireView().findViewById(R.id.text_header7);
        TextView text_header7 = requireView().findViewById(R.id.text_header8);
        TextView text_header8 = requireView().findViewById(R.id.text_header9);

        text_header1.setText(asos);
        text_header2.setText(castro);
        text_header3.setText(shein);
        text_header4.setText(aldo);
        text_header5.setText(renuar);
        text_header6.setText(tx);
        text_header7.setText(tfs);
        text_header8.setText(hoodies);
    }
    private void setEntrance() {

        initViews();

        Macros.Functions.GlidePicture(getContext(),Macros.FIRST_PIC,textView1);
        Macros.Functions.GlidePicture(getContext(),Macros.SECOND_PIC,textView2);
        Macros.Functions.GlidePicture(getContext(),Macros.THIRD_PIC,textView3);
        Macros.Functions.GlidePicture(getContext(),Macros.FOURTH_PIC,textView4);
        Macros.Functions.GlidePicture(getContext(),Macros.FIFTH_PIC,textView5);
        Macros.Functions.GlidePicture(getContext(),Macros.SIXTH_PIC,textView6);
        Macros.Functions.GlidePicture(getContext(),Macros.SEVENTH_PIC,textView7);
        Macros.Functions.GlidePicture(getContext(),Macros.EIGHTH_PIC,textView8);
    }
    private void stopAllObservers(){
        entranceViewModel.getCurrentSheinItem().removeObserver(sheinCountObserver);
        entranceViewModel.getCurrentAsosItem().removeObserver(asosCountObserver);
        entranceViewModel.getCurrentTerminalXItem().removeObserver(txCountObserver);
        entranceViewModel.getCurrentRenuarItem().removeObserver(renuarCountObserver);
        entranceViewModel.getCurrentShoesItem().removeObserver(aldoCountObserver);
        entranceViewModel.getCurrentClothingItem().removeObserver(castroCountObserver);
        entranceViewModel.getCurrentHoodiesItem().removeObserver(hoodiesCountObserver);
        entranceViewModel.getCurrentTfsItem().removeObserver(tfsCountObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        model.getGender().removeObserver(observer);
        entranceViewModel.getRecentLikedItems().removeObserver(recentObserver);
        layout = null;
        layout1 = null;
        layout2 = null;
        layout3 = null;
        layout4 = null;
        layout5 = null;
        layout6 = null;
    }
}
