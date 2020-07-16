package com.eitan.shopik.CustomerFragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Adapters.CardsAdapter;
import com.eitan.shopik.Database;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.GenderModel;
import com.eitan.shopik.ViewModels.MainModel;
import com.eitan.shopik.ViewModels.SwipesModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.google.android.gms.ads.formats.NativeAdOptions.ADCHOICES_TOP_LEFT;

public class CustomerHomeFragment extends Fragment {

    private CardsAdapter arrayAdapter;
    private String item_type;
    private String item_gender;
    private String item_sub_category;
    private TabLayout tabLayout;
    private SwipesModel swipesModel;
    private Dialog dialog;
    private static final int DELAY_MILLIS = 2500;
    private SwipeFlingAdapterView flingContainer;
    private MainModel mainModel;
    private static final int NUM_OF_ADS = 15;
    private UnifiedNativeAd tempAd;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: REMOVE IN PRODUCTION
        List<String> testDeviceIds = Collections.singletonList(Macros.TEST_DEVICE_ID);
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(requireActivity(), Macros.NATIVE_VIDEO_TEST_AD);

        for( int i=0; i < NUM_OF_ADS; ++i ){
            loadAds();
        }

        swipesModel = new ViewModelProvider(requireActivity()).get(SwipesModel.class);
        GenderModel genderModel = new ViewModelProvider(requireActivity()).get(GenderModel.class);
        item_gender = genderModel.getGender().getValue();
        item_type = genderModel.getType().getValue();
        item_sub_category = genderModel.getSub_category().getValue();

        getLastSwipedItem();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_home, container, false);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        arrayAdapter = new CardsAdapter(getActivity(), R.layout.swipe_item, swipesModel.getItems().getValue());
        mainModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
        mainModel.getAll_items_ids().observe(getViewLifecycleOwner(), this::fillSwipeItemsModel);
        flingContainer.setFlingListener( new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                if (Objects.requireNonNull(swipesModel.getItems().getValue()).get(0).isAd()) {
                    swipesModel.getItems().getValue().get(0).getNativeAd().destroy();
                }
                swipesModel.removeFromItems();
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                onItemUnliked(dataObject);
            }

            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onRightCardExit(final Object dataObject) {
                onItemLiked(dataObject);
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                if (view != null) {
                    view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                    view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
                }
            }

        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tabLayout = null;
        flingContainer = null;
        mainModel.getAll_items_ids().removeObservers(getViewLifecycleOwner());
        arrayAdapter.clear();
        arrayAdapter = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fillSwipeItemsModel(CopyOnWriteArrayList<Pair<String, ShoppingItem>> pairs) {
        swipesModel.clearAllItems();
        pairs.sort((o1, o2) -> {
            assert o1.first != null;
            assert o2.first != null;
            return o1.first.compareTo(o2.first);
        });
        int count = 0;
        for (Pair<String, ShoppingItem> pair : pairs) {
            assert pair.first != null;
            if (swipesModel.getLast_item_id().getValue() == null || Objects.requireNonNull(swipesModel.getLast_item_id().getValue()).compareTo(pair.first) < 0) {
                swipesModel.addToItems(pair.second);
                flingContainer.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
                count++;
                if( (count%Macros.SWIPES_TO_AD == 0) && count > 0 ) {
                    ShoppingItem shoppingItemAd = (ShoppingItem) mainModel.getNextAd();
                    if(shoppingItemAd != null) {
                        swipesModel.addToItems(shoppingItemAd);
                    }
                }
            }
        }
        swipesModel.sortItems();
    }

    private void init() {
        tabLayout = requireActivity().findViewById(R.id.top_nav);
        flingContainer = requireActivity().findViewById(R.id.frame);
    }

    private void loadAds() {

        VideoOptions videoOptions = new VideoOptions.Builder().
                setStartMuted(false).
                setClickToExpandRequested(true).
                build();

        NativeAdOptions nativeAdOptions = new NativeAdOptions.Builder().
                setAdChoicesPlacement(ADCHOICES_TOP_LEFT).
                setVideoOptions(videoOptions).
                build();

        AdLoader adLoader = new AdLoader
                .Builder(Objects.requireNonNull(requireActivity()),Macros.NATIVE_VIDEO_TEST_AD)
                .forUnifiedNativeAd(unifiedNativeAd -> {
                    // Show the ad.
                    tempAd = unifiedNativeAd;
                })
                .withAdListener(new AdListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        ShoppingItem dummy = new ShoppingItem();
                        dummy.setAd(true);
                        dummy.setNativeAd(tempAd);
                        mainModel.addAd(dummy);
                    }

                    @Override
                    public void onAdFailedToLoad ( int errorCode ) {
                        Log.d(Macros.TAG,"Failed to load native ad: " + errorCode);
                    }
                })
                .withNativeAdOptions(nativeAdOptions)
                .build();

        adLoader.loadAd(new PublisherAdRequest.Builder().build());
    }

    private void updateBadge() {
        if (Objects.requireNonNull(tabLayout.getTabAt(1)).getOrCreateBadge().hasNumber()) {
            int num = Objects.requireNonNull(tabLayout.getTabAt(1)).getOrCreateBadge().getNumber();
            Objects.requireNonNull(tabLayout.getTabAt(1)).getOrCreateBadge().setNumber(num + 1);
        } else
            Objects.requireNonNull(tabLayout.getTabAt(1)).getOrCreateBadge().setNumber(1);

        Objects.requireNonNull(tabLayout.getTabAt(1)).getOrCreateBadge().setVisible(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void onItemLiked(Object dataObject) {
        final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.swipe);
        mp.start();
        final ShoppingItem shoppingItem = (ShoppingItem) dataObject;
        Database connection = new Database();
        if (!shoppingItem.isAd()) {
            String item_id = shoppingItem.getId();
            if (item_id != null) {

                boolean isFavorite = arrayAdapter.isFavorite();
                shoppingItem.setFavorite(isFavorite);
                if (isFavorite) {
                    dialog = new Dialog(requireContext());
                    String imageUrl = connection.getASOSimageUrl(1, shoppingItem.getColor(), shoppingItem.getId_in_seller());
                    showFavoritesDialog(imageUrl);
                }
                swipesModel.setLast_item_id(item_id);

                updateBadge();

                if (shoppingItem.isFavorite()) {
                    connection.onCustomerInteractWithItem(item_id, item_type, item_gender, item_sub_category, Macros.CustomerMacros.FAVOURITE);
                    // add user_id to items Liked field
                    connection.onItemAction(item_type, item_gender, item_id, Macros.CustomerMacros.FAVOURITE);
                } else {
                    connection.onCustomerInteractWithItem(item_id, item_type, item_gender, item_sub_category, Macros.CustomerMacros.LIKED);
                    // add user_id to items Liked field
                    connection.onItemAction(item_type, item_gender, item_id, Macros.Items.LIKED);
                }

                connection.updatePreferredItem(shoppingItem, item_sub_category);
               // connection.updateActionInItem(item_gender, item_type, item_id, Macros.Items.LIKES_NUM);
            }
        }
    }

    private void onItemUnliked(Object dataObject) {
        final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.swipe);
        mp.start();
        final ShoppingItem shoppingItem = (ShoppingItem) dataObject;
        if (!shoppingItem.isAd()) {
            String item_id = shoppingItem.getId();
            if (item_id != null) {

                swipesModel.setLast_item_id(item_id);

                Database connection = new Database();
                connection.onItemAction(item_type, item_gender, item_id, Macros.Items.UNLIKED);
                connection.onCustomerInteractWithItem(item_id, item_type, item_gender, item_sub_category, Macros.Items.UNLIKED);
                connection.updateActionInItem(item_gender, item_type, item_id, Macros.Items.UNLIKES_NUM);
            }
        }
    }

    private void showFavoritesDialog(String imageUrl) {

        dialog.setContentView(R.layout.favorite_dialog_layout);
        TextView header = dialog.findViewById(R.id.header);
        TextView footer = dialog.findViewById(R.id.footer);
        Random random = new Random();
        String rand_line = Macros.Arrays.FAV_LINES[(random.nextInt(Macros.Arrays.FAV_LINES.length))];
        ImageView fav_item = dialog.findViewById(R.id.fav_item);

        ImageView fav_ic = dialog.findViewById(R.id.fav_ic);
        fav_ic.setImageDrawable(dialog.getContext().getDrawable(R.drawable.ic_baseline_favorite));
        fav_ic.setColorFilter(Color.WHITE);

        footer.setText("Added Successfully");
        header.setText(rand_line);
        Glide.with(dialog.getContext()).load(imageUrl).into(fav_item);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.FavoriteSwipeAnimation;
        dialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(() -> dialog.dismiss(), DELAY_MILLIS);
    }

    private void getLastSwipedItem() {
        FirebaseDatabase.getInstance().getReference().
                child(Macros.CUSTOMERS).
                child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).
                child(item_gender).
                child(Macros.Items.LIKED).
                child(item_type).
                child(item_sub_category).
                orderByKey().
                limitToLast(1).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (Object item_id : ((Map) Objects.requireNonNull(dataSnapshot.getValue())).keySet()) {
                                swipesModel.setLast_item_id(item_id.toString());
                            }
                        }
                        FirebaseDatabase.getInstance().getReference().
                                child(Macros.CUSTOMERS).
                                child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).
                                child(item_gender).
                                child(Macros.Items.UNLIKED).
                                child(item_type).
                                child(item_sub_category).
                                orderByKey().
                                limitToLast(1).
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (Object item_id : ((Map) Objects.requireNonNull(dataSnapshot.getValue())).keySet()) {
                                                if (swipesModel.getLast_item_id().getValue() != null) {
                                                    swipesModel.setLast_item_id(item_id.toString().
                                                            compareTo(Objects.requireNonNull(swipesModel.getLast_item_id().getValue())) > 0 ?
                                                            item_id.toString() :
                                                            swipesModel.getLast_item_id().getValue());
                                                }
                                                else
                                                    swipesModel.setLast_item_id(item_id.toString());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d(Macros.TAG,"CustomerHomeFragment::onCancelled()" + databaseError.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(Macros.TAG,"CustomerHomeFragment::onCancelled()" + databaseError.getMessage());
                    }

                });
    }
}
