package com.eitan.shopik.explanation;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.eitan.shopik.Adapters.ItemsCatagoriesListAdapter;
import com.eitan.shopik.Items.Catagory;
import com.eitan.shopik.Items.SubCategory;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.EntranceViewModel;
import com.eitan.shopik.ViewModels.GenderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class E2Fragment extends Fragment {

    private ItemsCatagoriesListAdapter adapter;
    private ArrayList<Catagory> categories;
    private String gender, imageUrl;
    private ArrayList<SubCategory> sub_jackets,sub_shoes, sub_bags,sub_dress,sub_jeans,
                                   sub_jewellery,sub_shirts,sub_glasses,sub_watches,sub_swim;
    private GenderModel model;
    private FirebaseUser user;
    private Catagory shoes;
    private Catagory jeans;
    private Catagory shirts;
    private Catagory watches;
    private Catagory sunglasses;
    private Catagory jackets;
    private Catagory jewellery;
    private Catagory swimwear;
    private Catagory lingerie;
    private Catagory accessories;

    private EntranceViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_e2, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        if(model.getImageUrl().getValue() == null){
            assert user != null;
            FirebaseFirestore.getInstance().
                    collection(Macros.CUSTOMERS).
                    document(user.getUid()).get().
                    addOnSuccessListener(documentSnapshot ->
                    imageUrl = documentSnapshot.get("imageUrl") != null ?
                    Objects.requireNonNull(documentSnapshot.get("imageUrl")).toString() : null
            );
        }
        else
            imageUrl = model.getImageUrl().getValue();

        if(gender != null){
            setCategories();
            viewModel.setList(gender);
        }

        adapter = new ItemsCatagoriesListAdapter(categories, imageUrl);
        adapter.notifyDataSetChanged();
        ExpandableListView listContainer = requireView().findViewById(R.id.e2_list);
        listContainer.setAdapter(adapter);
        listContainer.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        model.getGender().observe(getViewLifecycleOwner(), s -> {
            if(!gender.equals(s)) {
                gender = s;
                setCategories();
                viewModel.setList(gender);
                adapter.collapseAll(listContainer);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        model.getGender().removeObservers(getViewLifecycleOwner());
    }

    private void init() {

        user = FirebaseAuth.getInstance().getCurrentUser();
        categories = new ArrayList<>();

        model = new ViewModelProvider(requireActivity()).get(GenderModel.class);
        gender = model.getGender().getValue();

        viewModel = new ViewModelProvider(requireActivity()).get(EntranceViewModel.class);

        sub_bags = new ArrayList<>();
        sub_dress = new ArrayList<>();
        sub_jackets = new ArrayList<>();
        sub_shoes = new ArrayList<>();
        sub_jeans = new ArrayList<>();
        sub_jewellery = new ArrayList<>();
    }

    private void setCategories(){

        clearCategories();
        initCategories();

        if(gender.equals(Macros.CustomerMacros.MEN)){

            addJacketsSubCategory("overcoat", Macros.Items.MEN_DENIM_JACKETS_RES, Macros.CustomerMacros.MEN);
            addJacketsSubCategory("biker", Macros.Items.MEN_TRENCH_JACKETS_RES, Macros.CustomerMacros.MEN);
            addJacketsSubCategory("leather", Macros.Items.MEN_BOMBER_JACKETS_RES, Macros.CustomerMacros.MEN);
            addJacketsSubCategory("denim", Macros.Items.MEN_LEATHER_JACKETS_RES, Macros.CustomerMacros.MEN);
            addJacketsSubCategory("trench", Macros.Items.MEN_TRENCH_JACKETS_RES, Macros.CustomerMacros.MEN);
            addJacketsSubCategory("puffer", Macros.Items.MEN_BOMBER_JACKETS_RES, Macros.CustomerMacros.MEN);

            addJeansSubCategory("slim", Macros.Items.MEN_SLIM_JEANS_RES, Macros.CustomerMacros.MEN);
            addJeansSubCategory("straight", Macros.Items.MEN_STRAIGHT_JEANS_RES, Macros.CustomerMacros.MEN);
            addJeansSubCategory("skinny", Macros.Items.MEN_SKINNY_JEANS_RES, Macros.CustomerMacros.MEN);
            addJeansSubCategory("super-skinny", Macros.Items.MEN_SUPER_SKINNY_JEANS_RES, Macros.CustomerMacros.MEN);

            addJewellerySubCategory("ring", Macros.Items.MEN_RING_RES, Macros.CustomerMacros.MEN);
            addJewellerySubCategory("bracelet", Macros.Items.MEN_BRACELET_RES, Macros.CustomerMacros.MEN);
            addJewellerySubCategory("necklace", Macros.Items.MEN_NECKLACE_RES, Macros.CustomerMacros.MEN);
            addJewellerySubCategory("earrings", Macros.Items.MEN_EARRING_RES, Macros.CustomerMacros.MEN);

            addGlassesSubCategory("round", Macros.Items.MEN_ROUND_GLASSES_RES,Macros.CustomerMacros.MEN);
            addGlassesSubCategory("oversized", Macros.Items.MEN_OVERSIZED_GLASSES_RES,Macros.CustomerMacros.MEN);
            addGlassesSubCategory("square", Macros.Items.MEN_SQUARE_GLASSES_RES,Macros.CustomerMacros.MEN);

            addShoesSubCategory("loafers",Macros.Items.MEN_LOAFERS_RES,Macros.CustomerMacros.MEN);
            addShoesSubCategory("boots",Macros.Items.MEN_BOOTS_RES,Macros.CustomerMacros.MEN);
            addShoesSubCategory("trainers",Macros.Items.MEN_TRAINERS_RES,Macros.CustomerMacros.MEN);
            addShoesSubCategory("sandals",Macros.Items.MEN_SANDALS_RES,Macros.CustomerMacros.MEN);
            addShoesSubCategory("sliders",Macros.Items.MEN_SLIDERS_RES,Macros.CustomerMacros.MEN);

            addShirtsSubCategory("denim", Macros.Items.MEN_LONG_RES,Macros.CustomerMacros.MEN);
            addShirtsSubCategory("check", Macros.Items.MEN_TANK_RES,Macros.CustomerMacros.MEN);
            addShirtsSubCategory("t-shirt", Macros.Items.MEN_TSHIRT_RES,Macros.CustomerMacros.MEN);
            addShirtsSubCategory("oxford", Macros.Items.MEN_OXFORD_RES,Macros.CustomerMacros.MEN);
            addShirtsSubCategory("slim-fit", Macros.Items.MEN_MUSCLE_RES, Macros.CustomerMacros.MEN);

            addWatchesSubCategory("smart",Macros.Items.MEN_SMART_WATCH_RES,Macros.CustomerMacros.MEN);
            addWatchesSubCategory("leather",Macros.Items.MEN_LEATHER_WATCH_RES,Macros.CustomerMacros.MEN);
            addWatchesSubCategory("chronograph",Macros.Items.MEN_CHRONOGRAPH_WATCH_RES,Macros.CustomerMacros.MEN);
            addWatchesSubCategory("digital",Macros.Items.MEN_DIGITAL_RES,Macros.CustomerMacros.MEN);
            addWatchesSubCategory("mesh",Macros.Items.MEN_MESH_WATCH_RES,Macros.CustomerMacros.MEN);
            addWatchesSubCategory("bracelet",Macros.Items.MEN_BRACELET_WATCH_RES,Macros.CustomerMacros.MEN);

            addSwimwearSubCategory("shorts", Macros.Items.MEN_SHORTS_SWIM_RES,Macros.CustomerMacros.MEN);
            addSwimwearSubCategory("co-ord", Macros.Items.MEN_CO_ORD_SWIM_RES,Macros.CustomerMacros.MEN);
            addSwimwearSubCategory("oversized", Macros.Items.MEN_PLUS_SWIM_RES,Macros.CustomerMacros.MEN);
        }
        else {

            //"THONGS & KNICKERSSEXY LINGERIEBACKLESS BRASPYJAMASDRESSING GOWNSBRASSHAPEWEARBODIES"
            addBagsSubCategory("leather",Macros.Items.LEATHER_RES);
            addBagsSubCategory("bum",Macros.Items.BUM_RES);
            addBagsSubCategory("clutch",Macros.Items.CLUTCH_RES);
            addBagsSubCategory("backpack",Macros.Items.BACKPACK_RES);
            addBagsSubCategory("cross body",Macros.Items.CROSS_RES);
            addBagsSubCategory("tote",Macros.Items.TOTE_RES);

            addDressSubCategory("jumper",Macros.Items.MINI_DRESSES_RES);
            addDressSubCategory("midi",Macros.Items.MIDI_DRESS_RES);
            addDressSubCategory("wedding",Macros.Items.TIE_WAIST_DRESS_RES);
            addDressSubCategory("party",Macros.Items.PETITE_DRESS_RES);
            addDressSubCategory("maxi",Macros.Items.SUNDRESS_DRESS_RES);
            addDressSubCategory("evening",Macros.Items.SUNDRESS_DRESS_RES);
            addDressSubCategory("mini",Macros.Items.MINI_DRESSES_RES);

            addJacketsSubCategory("leather",Macros.Items.WOMEN_LEATHER_JACKETS_RES,Macros.CustomerMacros.WOMEN);
            addJacketsSubCategory("winter",Macros.Items.WOMEN_DENIM_JACKETS_RES,Macros.CustomerMacros.WOMEN);
            addJacketsSubCategory("coat",Macros.Items.WOMEN_COAT_JACKETS_RES,Macros.CustomerMacros.WOMEN);
            addJacketsSubCategory("teddy",Macros.Items.WOMEN_COAT_JACKETS_RES,Macros.CustomerMacros.WOMEN);
            addJacketsSubCategory("puffer",Macros.Items.WOMEN_BOMBER_JACKETS_RES,Macros.CustomerMacros.WOMEN);
            addJacketsSubCategory("jacket",Macros.Items.WOMEN_BOMBER_JACKETS_RES,Macros.CustomerMacros.WOMEN);

            addShoesSubCategory("sandals",Macros.Items.FLAT_SANDALS_RES,Macros.CustomerMacros.WOMEN);
            addShoesSubCategory("boots",Macros.Items.WOMEN_BOOTS_RES,Macros.CustomerMacros.WOMEN);
            addShoesSubCategory("heels",Macros.Items.WOMEN_HEELS_RES,Macros.CustomerMacros.WOMEN);
            addShoesSubCategory("leather",Macros.Items.WOMEN_LEATHER_SHOES,Macros.CustomerMacros.WOMEN);
            addShoesSubCategory("sliders",Macros.Items.WOMEN_SLIDERS_RES,Macros.CustomerMacros.WOMEN);
            addShoesSubCategory("sneakers",Macros.Items.WOMEN_SLIDERS_RES,Macros.CustomerMacros.WOMEN);

            addJeansSubCategory("slim", Macros.Items.WOMEN_SLIM_JEANS_RES, Macros.CustomerMacros.WOMEN);
            addJeansSubCategory("straight", Macros.Items.WOMEN_STRAIGHT_JEANS_RES, Macros.CustomerMacros.WOMEN);
            addJeansSubCategory("skinny", Macros.Items.WOMEN_SKINNY_JEANS_RES, Macros.CustomerMacros.WOMEN);
            addJeansSubCategory("ripped", Macros.Items.WOMEN_RIPPED_JEANS_RES, Macros.CustomerMacros.WOMEN);
            addJeansSubCategory("jeggings",Macros.Items.WOMEN_JEGGINGS, Macros.CustomerMacros.WOMEN);
            addJeansSubCategory("high-waist",Macros.Items.WOMEN_HIGH_WAIST, Macros.CustomerMacros.WOMEN);

            addJewellerySubCategory("ring", Macros.Items.WOMEN_RING_RES, Macros.CustomerMacros.WOMEN);
            addJewellerySubCategory("bracelet", Macros.Items.WOMEN_BRACELET_RES, Macros.CustomerMacros.WOMEN);
            addJewellerySubCategory("necklace", Macros.Items.WOMEN_NECKLACE_RES, Macros.CustomerMacros.WOMEN);
            addJewellerySubCategory("earrings", Macros.Items.WOMEN_EARRING_RES, Macros.CustomerMacros.WOMEN);
            addJewellerySubCategory("anklet", Macros.Items.WOMEN_ANKLET_RES, Macros.CustomerMacros.WOMEN);

            addGlassesSubCategory("round", Macros.Items.WOMEN_ROUND_GLASSES_RES,Macros.CustomerMacros.WOMEN);
            addGlassesSubCategory("square", Macros.Items.WOMEN_SQUARE_GLASSES_RES,Macros.CustomerMacros.WOMEN);
            addGlassesSubCategory("cat-eye", Macros.Items.WOMEN_CATEYE_GLASSES_RES,Macros.CustomerMacros.WOMEN);
            addGlassesSubCategory("aviator", Macros.Items.WOMEN_AVIATOR_GLASSES_RES,Macros.CustomerMacros.WOMEN);

            addShirtsSubCategory("hoodies", Macros.Items.WOMEN_LONG_RES,Macros.CustomerMacros.WOMEN);
            addShirtsSubCategory("sweatshirts", Macros.Items.WOMEN_TANK_RES,Macros.CustomerMacros.WOMEN);
            addShirtsSubCategory("t-shirts", Macros.Items.WOMEN_TSHIRT_RES,Macros.CustomerMacros.WOMEN);
            addShirtsSubCategory("blouses", Macros.Items.WOMEN_LONG_RES,Macros.CustomerMacros.WOMEN);
            addShirtsSubCategory("camis", Macros.Items.WOMEN_TANK_RES,Macros.CustomerMacros.WOMEN);
            addShirtsSubCategory("summer-top", Macros.Items.WOMEN_TSHIRT_RES,Macros.CustomerMacros.WOMEN);
            addShirtsSubCategory("evening", Macros.Items.WOMEN_TSHIRT_RES,Macros.CustomerMacros.WOMEN);

            addWatchesSubCategory("smart",Macros.Items.WOMEN_DIGITAL_RES,Macros.CustomerMacros.WOMEN);
            addWatchesSubCategory("digital",Macros.Items.WOMEN_DIGITAL_RES,Macros.CustomerMacros.WOMEN);

            addSwimwearSubCategory("bikini", Macros.Items.WOMEN_BIKINI_RES,Macros.CustomerMacros.WOMEN);
            addSwimwearSubCategory("swimsuit", Macros.Items.WOMEN_SWIMSUIT_RES,Macros.CustomerMacros.WOMEN);
            addSwimwearSubCategory("one-piece", Macros.Items.WOMEN_SWIMSUIT_RES,Macros.CustomerMacros.WOMEN);
            addSwimwearSubCategory("swimwear", Macros.Items.WOMEN_SWIMSUIT_RES,Macros.CustomerMacros.WOMEN);

            Catagory bags = new Catagory(Macros.BAG, gender, sub_bags);
            Catagory dresses = new Catagory(Macros.DRESS, gender, sub_dress);

            categories.add(bags);
            categories.add(dresses);
        }

        jackets = new Catagory(Macros.JACKETS,gender,sub_jackets);
        shoes = new Catagory(Macros.SHOES,gender,sub_shoes);
        jeans = new Catagory(Macros.JEANS,gender,sub_jeans);
        shirts = new Catagory(Macros.SHIRT,gender,sub_shirts);
        watches = new Catagory(Macros.WATCH,gender,sub_watches);
        sunglasses = new Catagory(Macros.SUNGLASSES,gender,sub_glasses);
        jewellery = new Catagory(Macros.JEWELLERY,gender,sub_jewellery);
        swimwear = new Catagory(Macros.SWIMWEAR,gender,sub_swim);
        lingerie = new Catagory(Macros.JEWELLERY,gender,sub_jewellery);
        accessories = new Catagory(Macros.SWIMWEAR,gender,sub_swim);

       fillCategories();

    }
    private void initCategories(){
        sub_shirts = new ArrayList<>();
        sub_swim = new ArrayList<>();
        sub_glasses = new ArrayList<>();
        sub_watches = new ArrayList<>();
        sub_jewellery = new ArrayList<>();
        sub_dress = new ArrayList<>();
        sub_shoes = new ArrayList<>();
        sub_bags = new ArrayList<>();
        sub_jeans = new ArrayList<>();
        sub_jackets = new ArrayList<>();
    }
    private void fillCategories() {
        categories.add(shoes);
        categories.add(shirts);
        categories.add(jeans);
        categories.add(watches);
        categories.add(sunglasses);
        categories.add(jackets);
        categories.add(jewellery);
        categories.add(swimwear);
    }
    private void clearCategories() {

        categories.clear();
        sub_jackets.clear();
        sub_bags.clear();
        sub_dress.clear();
        sub_shoes.clear();
        sub_jeans.clear();
        sub_jewellery.clear();
    }

    private void addJacketsSubCategory(String name, String res_url, String _gender) {
        SubCategory jackets = new SubCategory(name,res_url, _gender);
        sub_jackets.add(jackets);
    }
    private void addJeansSubCategory(String name,String res_url,String _gender) {
        SubCategory jeans = new SubCategory(name,res_url, _gender);
        sub_jeans.add(jeans);
    }
    private void addBagsSubCategory(String name,String res_url) {
        SubCategory bags = new SubCategory(name,res_url, Macros.CustomerMacros.WOMEN);
        sub_bags.add(bags);
    }
    private void addShoesSubCategory(String name,String res_url,String _gender) {
        SubCategory shoes = new SubCategory(name,res_url, _gender);
        sub_shoes.add(shoes);
    }
    private void addDressSubCategory(String name,String res_url) {
        SubCategory dress = new SubCategory(name,res_url, Macros.CustomerMacros.WOMEN);
        sub_dress.add(dress);
    }
    private void addJewellerySubCategory(String name,String res_url,String _gender) {
        SubCategory jewellery = new SubCategory(name,res_url, _gender);
        sub_jewellery.add(jewellery);
    }
    private void addWatchesSubCategory(String name,String res_url,String _gender) {
        SubCategory watch = new SubCategory(name,res_url, _gender);
        sub_watches.add(watch);
    }
    private void addGlassesSubCategory(String name,String res_url,String _gender) {
        SubCategory glasses = new SubCategory(name,res_url, _gender);
        sub_glasses.add(glasses);
    }
    private void addSwimwearSubCategory(String name,String res_url,String _gender) {
        SubCategory swim = new SubCategory(name,res_url, _gender);
        sub_swim.add(swim);
    }
    private void addShirtsSubCategory(String name,String res_url,String _gender) {
        SubCategory shirts = new SubCategory(name,res_url, _gender);
        sub_shirts.add(shirts);
    }
}
