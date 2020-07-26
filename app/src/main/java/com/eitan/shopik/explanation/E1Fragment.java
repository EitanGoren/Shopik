package com.eitan.shopik.explanation;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
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
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.EntranceViewModel;
import com.eitan.shopik.ViewModels.GenderModel;

import java.util.ArrayList;
import java.util.Objects;

public class E1Fragment extends Fragment {

    private String gender;
    private RecyclerAdapter recyclerAdapter;
    private EntranceViewModel entranceViewModel;
    private GenderModel model;
    private Dialog dialog;
    private ProgressBar dialogProgressBar;
    private ArrayList<RecyclerItem> new_items;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        closeKeyboard();
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

        TextView liked_counter = requireView().findViewById(R.id.best_sellers_count);
        model.getGender().observe(requireActivity(), s -> {
            if(!gender.equals(s)) {
                gender = s;

                entranceViewModel.setLiked_items(gender);
                recyclerAdapter.notifyDataSetChanged();
                liked_counter.setText("Loading...");

                if(gender.equals(Macros.CustomerMacros.WOMEN))
                    setWomenEntrance();
                else
                    setMenEntrance();
            }
        });

        RelativeLayout relativeLayout1 = requireView().findViewById(R.id.layout2);
        RelativeLayout relativeLayout2 = requireView().findViewById(R.id.layout3);
        RelativeLayout relativeLayout3 = requireView().findViewById(R.id.layout4);

        relativeLayout1.setOnClickListener(v -> showNewItemsDialog(Macros.NEW_CLOTHING));
        relativeLayout2.setOnClickListener(v -> showNewItemsDialog(Macros.NEW_SHOES));
        relativeLayout3.setOnClickListener(v -> showNewItemsDialog(Macros.NEW_TRENDING));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        RecyclerView recyclerView = requireView().findViewById(R.id.recycler);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setScrollbarFadingEnabled(true);

        entranceViewModel = new ViewModelProvider(requireActivity()).get(EntranceViewModel.class);
        recyclerAdapter = new RecyclerAdapter(entranceViewModel.getRecentLikedItems().getValue(),"Item");
        entranceViewModel.getRecentLikedItems().observe(requireActivity(), recyclerItems -> {
            for(int i=1; i<recyclerItems.size()+1; ++i) {
               liked_counter.setText("(" + i + " items)");
            }
            recyclerAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(recyclerAdapter);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        model.getGender().removeObservers(getViewLifecycleOwner());
        entranceViewModel.getRecentLikedItems().removeObservers(getViewLifecycleOwner());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {

        new_items = new ArrayList<>();
        model = new ViewModelProvider(requireActivity()).get(GenderModel.class);
        gender = model.getGender().getValue();
        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.new_items_grid_dialog);
        dialogProgressBar = dialog.findViewById(R.id.progressBar);

        if(gender.equals(Macros.CustomerMacros.WOMEN))
            setWomenEntrance();
        else
            setMenEntrance();
    }

    private void showNewItemsDialog(String type) {

        DialogGridAdapter gridAdapter = new DialogGridAdapter(dialog.getContext(), R.layout.e3_grid_item, new_items);
        GridView gridContainer = dialog.findViewById(R.id.new_items_grid);
        gridAdapter.notifyDataSetChanged();
        gridContainer.setAdapter(gridAdapter);

        TextView txt = dialog.findViewById(R.id.items_count);

        new_items.clear();
        dialogProgressBar.setVisibility(View.VISIBLE);
        int i=0;
        for( RecyclerItem recyclerItem : Objects.requireNonNull(entranceViewModel.getItems().getValue())) {
            dialogProgressBar.setProgress(i);
            if(recyclerItem.getType().equals(type)){
                new_items.add(recyclerItem);
                ++i;
                txt.setText("(" + i + " items)");
            }
        }
        dialogProgressBar.setVisibility(View.INVISIBLE);

        String text_header ="";
        if(type.equals(Macros.NEW_TRENDING)){
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

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    private void setAnimation() {
        Animation fading = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_in);

        RelativeLayout layout1 = requireView().findViewById(R.id.layout2);
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


        TextView textView1 = requireView().findViewById(R.id.text_btn1);
        TextView textView2 = requireView().findViewById(R.id.text_btn2);
        TextView textView3 = requireView().findViewById(R.id.text_btn3);

        TextView text_header1 = requireView().findViewById(R.id.text_header1);
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

        final TextView textView1 = requireView().findViewById(R.id.text_btn1);
        TextView text_header1 = requireView().findViewById(R.id.text_header1);
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
    private void closeKeyboard(){
        View view = requireActivity().getCurrentFocus();
        if( view != null ){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
}
