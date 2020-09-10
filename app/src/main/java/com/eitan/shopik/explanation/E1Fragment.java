package com.eitan.shopik.explanation;

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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eitan.shopik.Adapters.DialogGridAdapter;
import com.eitan.shopik.Adapters.RecyclerAdapter;
import com.eitan.shopik.CustomItemAnimator;
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.EntranceViewModel;
import com.eitan.shopik.ViewModels.GenderModel;
import com.google.android.material.progressindicator.ProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class E1Fragment extends Fragment {

    private String gender;
    private RecyclerAdapter recyclerAdapter;
    private EntranceViewModel entranceViewModel;
    private GenderModel model;
    private Dialog dialog;
    private ArrayList<RecyclerItem> new_items;
    private RelativeLayout layout1,layout2,layout3;
    private TextView liked_counter;
    private RelativeLayout layout;
    private TextView header;
    private RecyclerView recyclerView;
    private Observer<String> observer;
    private Observer<ArrayList<RecyclerItem>> recentObserver;
    private ProgressIndicator progressIndicator;
    private DialogGridAdapter gridAdapter;
    private GridView gridContainer;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entranceViewModel = new ViewModelProvider(requireActivity()).get(EntranceViewModel.class);
        recyclerAdapter = new RecyclerAdapter(entranceViewModel.getRecentLikedItems().getValue(),"Item");
        model = new ViewModelProvider(requireActivity()).get(GenderModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_e1, container, false);

        liked_counter = view.findViewById(R.id.best_sellers_count);
        layout1 = view.findViewById(R.id.layout2);
        layout2 = view.findViewById(R.id.layout3);
        layout3 = view.findViewById(R.id.layout4);
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

                if(gender.equals(Macros.CustomerMacros.WOMEN))
                    setWomenEntrance();
                else
                    setMenEntrance();
            }
        };

        layout1.setOnClickListener(v -> showNewItemsDialog(Macros.NEW_CLOTHING));
        layout2.setOnClickListener(v -> showNewItemsDialog(Macros.NEW_SHOES));
        layout3.setOnClickListener(v -> showNewItemsDialog(Macros.NEW_TRENDING));

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

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        model.getGender().observe(getViewLifecycleOwner(),observer);
        entranceViewModel.getRecentLikedItems().observe(requireActivity(), recentObserver );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {

        new_items = new ArrayList<>();
        gender = model.getGender().getValue();
        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.new_items_grid_dialog);
        gridAdapter = new DialogGridAdapter(requireActivity(), R.layout.e3_grid_item, new_items);
        gridContainer = dialog.findViewById(R.id.new_items_grid);
        gridContainer.setAdapter(gridAdapter);
        progressIndicator = dialog.findViewById(R.id.new_items_progress_bar);
        progressIndicator.setVisibility(View.VISIBLE);

        ((ViewGroup) requireView().findViewById(R.id.root)).
                getLayoutTransition().enableTransitionType(LayoutTransition.APPEARING);

        if(gender.equals(Macros.CustomerMacros.WOMEN))
            setWomenEntrance();
        else
            setMenEntrance();

        String name = Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().
                getCurrentUser()).getDisplayName()).split(" ")[0];
        String text = "Hi " + name + ", Welcome to Shopik ! ";
        header.setText(text);
    }

    private void showNewItemsDialog(String type) {

        TextView txt = dialog.findViewById(R.id.items_count);

        new_items.clear();
        int i=0;
        for( RecyclerItem recyclerItem : Objects.requireNonNull(entranceViewModel.getItems().getValue())) {
            if(recyclerItem.getType().equals(type)) {
                new_items.add(recyclerItem);
                gridAdapter.notifyDataSetChanged();
                progressIndicator.setVisibility(View.GONE);
                ++i;
                String text = "(" + i + " items)";
                txt.setText(text);
            }
        }

        String text_header;
        if(type.equals(Macros.NEW_TRENDING)){
            text_header = "Trending Now";
            TextView header = dialog.findViewById(R.id.new_items_header);
            header.setText(text_header);
            header.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.
                    getDrawable(dialog.getContext(),R.drawable.ic_baseline_trending_up),null);
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
        Animation fading = AnimationUtils.loadAnimation(requireActivity(),R.anim.fade_in);

        layout1.startAnimation(fading);
        layout2.startAnimation(fading);
        layout3.startAnimation(fading);
    }

    private void setWomenEntrance() {

        String first_header = "ALL NEW CLOTHING ITEMS";
        String second_header = "OUR NEWEST SHOES COLLECTION";
        String third_header = "MOST TRENDING NOW";

        setAnimation();

        ImageView textView1 = requireView().findViewById(R.id.text_btn1);
        ImageView textView2 = requireView().findViewById(R.id.text_btn2);
        ImageView textView3 = requireView().findViewById(R.id.text_btn3);

        TextView text_header1 = requireView().findViewById(R.id.text_header1);
        TextView text_header2 = requireView().findViewById(R.id.text_header2);
        TextView text_header3 = requireView().findViewById(R.id.text_header3);

        text_header1.setText(first_header);
        text_header2.setText(second_header);
        text_header3.setText(third_header);

        Macros.Functions.GlidePicture(getContext(),Macros.WOMEN_FIRST_PIC,textView1);
        Macros.Functions.GlidePicture(getContext(),Macros.WOMEN_SECOND_PIC,textView2);
        Macros.Functions.GlidePicture(getContext(),Macros.WOMEN_THIRD_PIC,textView3);
    }

    private void setMenEntrance() {

        String first_header = "ALL NEW CLOTHING ITEMS";
        String second_header = "OUR NEWEST SHOES COLLECTION";
        String third_header = "TRENDING NOW";

        setAnimation();

        ImageView textView1 = requireView().findViewById(R.id.text_btn1);
        ImageView textView2 = requireView().findViewById(R.id.text_btn2);
        ImageView textView3 = requireView().findViewById(R.id.text_btn3);

        TextView text_header1 = requireView().findViewById(R.id.text_header1);
        TextView text_header2 = requireView().findViewById(R.id.text_header2);
        TextView text_header3 = requireView().findViewById(R.id.text_header3);

        text_header1.setText(first_header);
        text_header2.setText(second_header);
        text_header3.setText(third_header);

        Macros.Functions.GlidePicture(getContext(),Macros.MEN_FIRST_PIC,textView1);
        Macros.Functions.GlidePicture(getContext(),Macros.MEN_SECOND_PIC,textView2);
        Macros.Functions.GlidePicture(getContext(),Macros.MEN_THIRD_PIC,textView3);
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
    }
}
