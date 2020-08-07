package com.eitan.shopik.CustomerFragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.eitan.shopik.Adapters.FavouritesListAdapter;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.LikedUser;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.FavoritesModel;
import com.eitan.shopik.ViewModels.GenderModel;
import com.eitan.shopik.ViewModels.MainModel;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FavoritesFragment extends Fragment {

    private CollectionReference customerFS;
    private DatabaseReference itemsDB,AsosDB,CastroDB,TxDB;
    private FavouritesListAdapter arrayAdapter;
    private String item_type;
    private String item_sub_category;
    private ListView listContainer;
    private FavoritesModel model;
    private ImageView down_arrow;
    private GenderModel genderModel;
    private String currentUId;
    private MainModel mainModel;
    private Map<String,String> favs;
    private String item_gender;
    private FloatingActionButton scroll;
    private boolean scrollUp;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOnCreate();
        MobileAds.initialize(getContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        arrayAdapter = new FavouritesListAdapter(requireActivity(), R.layout.list_item, model.getItems().getValue());
        listContainer.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        ChildEventListener childEventListener = new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && !favs.containsKey(dataSnapshot.getKey())) {
                    favs.put(dataSnapshot.getKey(), Objects.requireNonNull(dataSnapshot.getValue()).toString());
                    for (ShoppingItem shoppingItem : Objects.requireNonNull(mainModel.getAll_items().getValue())) {

                        String item_id = shoppingItem.getId();
                        if (Objects.equals(dataSnapshot.getKey(), item_id)) {
                            shoppingItem.setFavorite(Objects.equals(favs.get(item_id), Macros.CustomerMacros.FAVOURITE));
                            getLikes(shoppingItem);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        TxDB.addChildEventListener( childEventListener);
        CastroDB.addChildEventListener( childEventListener);
        AsosDB.addChildEventListener(childEventListener);

        mainModel.getAll_items().observe(requireActivity(), shoppingItems -> {
            model.clearItems();
            arrayAdapter.notifyDataSetChanged();
            for (ShoppingItem shoppingItem : shoppingItems) {

                if (favs.containsKey(shoppingItem.getId())) {
                    shoppingItem.setFavorite(Objects.equals(favs.get(shoppingItem.getId()), Macros.CustomerMacros.FAVOURITE));
                    model.addToItems(shoppingItem);
                    arrayAdapter.notifyDataSetChanged();
                    if( (model.getItems().getValue().size() % Macros.FAV_TO_AD == 0)
                            && model.getItems().getValue().size() > 0 ) {

                        ShoppingItem shoppingItemAd = (ShoppingItem) mainModel.getNextAd();
                        if(shoppingItemAd != null) {
                            model.addToItems(shoppingItemAd);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            arrayAdapter.notifyDataSetChanged();
        });
        listContainer.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // NOT MOVING
                if (view.canScrollList(View.SCROLL_AXIS_VERTICAL) && (scrollState == SCROLL_STATE_IDLE)) {
                    showArrow();
                }
                // SCROLLING
                else if ((scrollState != SCROLL_STATE_IDLE) && view.canScrollList(View.SCROLL_AXIS_VERTICAL)) {
                    hideArrow();
                }
                if( !view.canScrollList(View.SCROLL_AXIS_VERTICAL) && scrollState == View.SCROLL_INDICATOR_BOTTOM ){
                    System.out.println("bottom");
                    Toast.makeText(getContext(),"bottom", Toast.LENGTH_SHORT).show();
                }
                else if (!view.canScrollList(View.SCROLL_AXIS_VERTICAL) && scrollState == View.SCROLL_INDICATOR_TOP) {
                    System.out.println("top");
                    Toast.makeText(getContext(),"top", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
               // Toast.makeText(getContext(),totalItemCount + " " + firstVisibleItem, Toast.LENGTH_SHORT).show();
               // System.out.println(totalItemCount + " " + firstVisibleItem);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        genderModel.getType().removeObservers(getViewLifecycleOwner());
        genderModel.getSub_category().removeObservers(getViewLifecycleOwner());
        listContainer = null;
        down_arrow = null;
        scroll = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init(){
        genderModel.getType().observe(getViewLifecycleOwner(), s -> item_type = s );
        genderModel.getSub_category().observe(getViewLifecycleOwner(), s -> item_sub_category = s);
        mainModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
        down_arrow = requireView().findViewById(R.id.see_items_below);
        listContainer = requireView().findViewById(R.id.favorites_list);
        scroll = requireView().findViewById(R.id.scroll_up_down);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initOnCreate() {

        genderModel = new ViewModelProvider(requireActivity()).get(GenderModel.class);
        model = new ViewModelProvider(requireActivity()).get(FavoritesModel.class);

        item_gender = genderModel.getGender().getValue();
        item_type = genderModel.getType().getValue();
        item_sub_category = genderModel.getSub_category().getValue();

        favs = new HashMap<>();

        currentUId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        assert item_gender != null;

        itemsDB = FirebaseDatabase.getInstance().getReference().child(Macros.ITEMS);

        AsosDB = FirebaseDatabase.getInstance().getReference().
                child(Macros.CUSTOMERS).
                child(currentUId).
                child(item_gender).
                child(Macros.Items.LIKED).
                child("ASOS").
                child(item_type).
                child(item_sub_category);

        CastroDB = FirebaseDatabase.getInstance().getReference().
                child(Macros.CUSTOMERS).
                child(currentUId).
                child(item_gender).
                child(Macros.Items.LIKED).
                child("Castro").
                child(item_type).
                child(item_sub_category);

        TxDB = FirebaseDatabase.getInstance().getReference().
                child(Macros.CUSTOMERS).
                child(currentUId).
                child(item_gender).
                child(Macros.Items.LIKED).
                child("Terminal X").
                child(item_type).
                child(item_sub_category);

        customerFS = FirebaseFirestore.getInstance().collection(Macros.CUSTOMERS);
    }

    private void hideArrow(){
        down_arrow.clearAnimation();
        down_arrow.setVisibility(View.GONE);
    }

    private void showArrow(){
        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.blink_anim);
        down_arrow.startAnimation(animation);
        down_arrow.setVisibility(View.VISIBLE);
    }

    private void getLikes(final ShoppingItem shoppingItem) {

        itemsDB.child(item_gender).
                child(item_type).
                child(shoppingItem.getSeller() + "-" + shoppingItem.getId()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Map unliked_users = new HashMap<>();
                            Map<String,String> liked_users = new HashMap<>();

                            //set likes num
                            if(dataSnapshot.hasChild(Macros.Items.LIKED)){
                                shoppingItem.setLikes(dataSnapshot.child(Macros.Items.LIKED).getChildrenCount());
                                liked_users = (Map) dataSnapshot.child(Macros.Items.LIKED).getValue();
                            }
                            else {
                                shoppingItem.setLikes(0);
                                shoppingItem.setLikedUsers(null);
                            }
                            //set unlikes num
                            if(dataSnapshot.hasChild(Macros.Items.UNLIKED)) {
                                shoppingItem.setUnlikes(dataSnapshot.child(Macros.Items.UNLIKED).getChildrenCount());
                                unliked_users = (Map) dataSnapshot.child(Macros.Items.UNLIKED).getValue();
                            }
                            else {
                                shoppingItem.setUnlikes(0);
                                shoppingItem.setUnlikedUsers(null);
                            }

                            if(liked_users == null || liked_users.isEmpty()) {
                                assert unliked_users != null;
                                if(!unliked_users.isEmpty()) {
                                    getUnlikedUserInfo(shoppingItem, unliked_users);
                                }
                            }
                            else
                                getInteractedUsersInfo(shoppingItem,liked_users,unliked_users);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(Macros.TAG,"FavoritesFragment::onCancelled: " + databaseError.getMessage());
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getInteractedUsersInfo(ShoppingItem shoppingItem, Map<String,String> liked_users, Map<String,String> unliked_users) {

        ArrayList<String> list = new ArrayList<>(liked_users.keySet());
        for(String customer_id : list) {
                customerFS.whereEqualTo("id", customer_id).
                        get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot doc : documentSnapshots) {
                            LikedUser likedUser = new LikedUser(
                                    Objects.requireNonNull(doc.get("imageUrl")).toString(),
                                    Objects.requireNonNull(doc.get("first_name")).toString(),
                                    Objects.requireNonNull(doc.get("last_name")).toString()
                            );

                            if (doc.getId().equals(currentUId))
                                likedUser.setLast_name(likedUser.getLast_name() + " (You)");

                            mainModel.setCustomers_info(customer_id,likedUser);
                            likedUser.setFavorite(Objects.equals(liked_users.get(doc.getId()), Macros.CustomerMacros.FAVOURITE));
                            shoppingItem.addLikedUser(likedUser);
                        }
                    }
                    else {
                        Log.d(Macros.TAG, "FavoritesFragment::users : " + customer_id + " do not exist");
                    }
                });
        }

        if(unliked_users == null || unliked_users.isEmpty()) {
            model.addToItems(shoppingItem);
            arrayAdapter.notifyDataSetChanged();
        }
        else
            getUnlikedUserInfo(shoppingItem, unliked_users);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getUnlikedUserInfo(final ShoppingItem shoppingItem, Map<String,String> unlikes_list) {
        assert unlikes_list != null;
        ArrayList<String> list = new ArrayList<>(unlikes_list.keySet());
        for(String customer_id : list) {
                customerFS.whereEqualTo("id", customer_id).
                        get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot doc : documentSnapshots) {
                            LikedUser likedUser = new LikedUser (
                                    Objects.requireNonNull(doc.get("imageUrl")).toString(),
                                    Objects.requireNonNull(doc.get("first_name")).toString(),
                                    Objects.requireNonNull(doc.get("last_name")).toString()
                            );
                            if (doc.getId().equals(currentUId)) {
                                likedUser.setLast_name(likedUser.getLast_name() + " (You)");
                            }
                            mainModel.setCustomers_info(customer_id,likedUser);
                            shoppingItem.addUnlikedUser(likedUser);
                        }
                    }
                    else {
                        Log.d(Macros.TAG, "CustomerHomeFragment::users : " + unlikes_list + " do not exist");
                    }
                });
        }

        model.addToItems(shoppingItem);
        arrayAdapter.notifyDataSetChanged();
    }
}