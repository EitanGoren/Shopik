package com.eitan.shopik.genderFilteringPages;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.adapters.ItemsCategoriesListAdapter;
import com.eitan.shopik.items.Category;
import com.eitan.shopik.viewModels.GenderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class CategoryPickFragment extends Fragment {

    private ItemsCategoriesListAdapter adapter;
    private ArrayList<Category> categories;
    private String gender;

    private GenderModel model;
    private androidx.lifecycle.Observer<String> observer;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(GenderModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_e2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        if(gender != null){
            setCategories();
        }

        RecyclerView listContainer = requireView().findViewById(R.id.e2_list);
        adapter = new ItemsCategoriesListAdapter(categories);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        listContainer.setLayoutManager(layoutManager);
        listContainer.setItemAnimator(new DefaultItemAnimator());
        listContainer.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        observer = s -> {
            if(!gender.equals(s)) {
                gender = s;
                setCategories();
            }
            adapter.notifyDataSetChanged();
        };
        model.getGender().observe(getViewLifecycleOwner(), observer);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        model.getGender().removeObserver(observer);
    }

    private void init() {
        categories = new ArrayList<>();
        gender = model.getGender().getValue();
    }

    private void setCategories(){
        categories.clear();
        categories.add(new Category(Macros.JACKETS, gender, Macros.Items.WOMEN_JACKET_IC));
        categories.add(new Category(Macros.SHOES, gender, Macros.Items.MENS_SHOES_IC));
        categories.add(new Category(Macros.JEANS, gender, Macros.Items.JEANS_IC));
        categories.add(new Category(Macros.SHIRT, gender, Macros.Items.WOMENS_SHIRTS_IC));
        categories.add(new Category(Macros.SWIMWEAR, gender, Macros.Items.MENS_SWIM_IC));
        categories.add(new Category(Macros.UNDERWEAR, gender, Macros.Items.WOMEN_LINGERIE_IC));
    }
}
