package com.eitan.shopik.CustomerFragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

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
    ArrayList<ShoppingItem> castro = new ArrayList<>();
    ArrayList<ShoppingItem> tx = new ArrayList<>();
    ArrayList<ShoppingItem> asos = new ArrayList<>();
    private String userId;
    private Map<String,String> last_items;
    private BottomNavigationView bottomNavigationView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
        swipesModel = new ViewModelProvider(requireActivity()).get(SwipesModel.class);
        GenderModel genderModel = new ViewModelProvider(requireActivity()).get(GenderModel.class);
        item_gender = genderModel.getGender().getValue();
        item_type = genderModel.getType().getValue();
        item_sub_category = genderModel.getSub_category().getValue();
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

        swipesModel.getLast_item_id().observe(requireActivity(), stringStringMap -> last_items = stringStringMap);

        mainModel.getAll_items_ids().observe(requireActivity(), pairs -> {

            swipesModel.clearAllItems();
            arrayAdapter.notifyDataSetChanged();

            castro.clear();
            tx.clear();
            asos.clear();

            // Separate items
            for (Pair<String, ShoppingItem> p : pairs) {
                assert p.first != null;
                switch (p.first) {
                    case "Castro":
                        castro.add(p.second);
                    case "ASOS":
                        asos.add(p.second);
                    case "Terminal X":
                        tx.add(p.second);
                }
            }

            castro.sort((o1, o2) -> (int) Long.parseLong(String.valueOf(Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()))));
            tx.sort((o1, o2) -> (int) Long.parseLong(String.valueOf(Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()))));
            asos.sort((o1, o2) -> (int) Long.parseLong(String.valueOf(Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()))));
            flingContainer.setAdapter(arrayAdapter);

            int count = 0;
            for (ShoppingItem cas_item : castro) {
                assert last_items != null;
                if (last_items.get(cas_item.getSeller()) == null ||
                        Long.parseLong(Objects.requireNonNull(last_items.get(cas_item.getSeller()))) < Long.parseLong(cas_item.getId())) {
                    swipesModel.addToItems(cas_item);
                    arrayAdapter.notifyDataSetChanged();

                    count++;
                    if ((count % Macros.SWIPES_TO_AD == 0) && count > 0) {
                        ShoppingItem shoppingItemAd = (ShoppingItem) mainModel.getNextAd();
                        if (shoppingItemAd != null) {
                            swipesModel.addToItems(shoppingItemAd);
                            count++;
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            for (ShoppingItem asos_item : asos) {
                assert last_items != null;
                if (last_items.get(asos_item.getSeller()) == null ||
                        Long.parseLong(Objects.requireNonNull(last_items.get(asos_item.getSeller()))) < Long.parseLong(asos_item.getId())) {
                    swipesModel.addToItems(asos_item);
                    arrayAdapter.notifyDataSetChanged();

                    count++;
                    if ((count % Macros.SWIPES_TO_AD == 0) && count > 0) {
                        ShoppingItem shoppingItemAd = (ShoppingItem) mainModel.getNextAd();
                        if (shoppingItemAd != null) {
                            swipesModel.addToItems(shoppingItemAd);
                            count++;
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            for (ShoppingItem tx_item : tx) {
                assert last_items != null;
                if (last_items.get(tx_item.getSeller()) == null ||
                        Long.parseLong(Objects.requireNonNull(last_items.get(tx_item.getSeller())))
                                < Long.parseLong(tx_item.getId())) {

                    swipesModel.addToItems(tx_item);
                    arrayAdapter.notifyDataSetChanged();

                    count++;
                    if ((count % Macros.SWIPES_TO_AD == 0) && count > 0) {
                        ShoppingItem shoppingItemAd = (ShoppingItem) mainModel.getNextAd();
                        if (shoppingItemAd != null) {
                            swipesModel.addToItems(shoppingItemAd);
                            count++;
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

        });

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
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
            public void onAdapterAboutToEmpty(int itemsInAdapter) {}

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
        flingContainer.setFlingListener(null);
        mainModel.getAll_items_ids().removeObservers(getViewLifecycleOwner());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
      //  tabLayout = requireActivity().findViewById(R.id.top_nav);
        bottomNavigationView = requireActivity().findViewById(R.id.main_bottom_navigation_view);
        flingContainer = requireActivity().findViewById(R.id.frame);
        arrayAdapter = new CardsAdapter(requireActivity(), R.layout.swipe_item, swipesModel.getItems().getValue());
    }

    private void updateBadge() {

        BadgeDrawable fav_badge = bottomNavigationView.getOrCreateBadge(R.id.bottom_favorites);

        if (Objects.requireNonNull(fav_badge).hasNumber()) {
            int num = Objects.requireNonNull(fav_badge).getNumber();
            Objects.requireNonNull(fav_badge).setNumber(num + 1);
        }
        else
            Objects.requireNonNull(fav_badge).setNumber(1);

        Objects.requireNonNull(fav_badge).setVisible(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void onItemLiked(Object dataObject) {
        final MediaPlayer mp = MediaPlayer.create(requireContext(), R.raw.swipe);
        mp.start();
        final ShoppingItem shoppingItem = (ShoppingItem) dataObject;
        Database connection = new Database();
        if (!shoppingItem.isAd()) {
            String item_id = shoppingItem.getId();
            if (item_id != null) {

                String seller = shoppingItem.getSeller();
                boolean isFavorite = arrayAdapter.isFavorite();

                shoppingItem.setFavorite(isFavorite);
                if (isFavorite) {
                    dialog = new Dialog(requireContext());
                    String imageUrl = shoppingItem.getImages().get(0);
                    showFavoritesDialog(imageUrl);
                }
                swipesModel.setLast_item_id(item_id,seller);

                updateBadge();

                String action;
                if (isFavorite)
                    action = Macros.CustomerMacros.FAVOURITE;
                else
                    action = Macros.CustomerMacros.LIKED;

                connection.onCustomerInteractWithItem(item_id, item_type, item_gender,item_sub_category, action,seller);
                // add user_id to items Liked field
                connection.onItemAction(item_type, item_gender, item_id,action,seller);

                connection.updatePreferredItem(shoppingItem, item_sub_category);
            }
        }
    }

    private void onItemUnliked(Object dataObject) {
        final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.swipe);
        mp.start();
        final ShoppingItem shoppingItem = (ShoppingItem) dataObject;
        if (!shoppingItem.isAd()) {
            String item_id = shoppingItem.getId();
            String seller = shoppingItem.getSeller();
            if (item_id != null) {

                swipesModel.setLast_item_id(item_id,seller);

                Database connection = new Database();
                connection.onItemAction(item_type, item_gender, item_id, Macros.Items.UNLIKED, seller);
                connection.onCustomerInteractWithItem(item_id, item_type, item_gender, item_sub_category,
                        Macros.Items.UNLIKED, seller);
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
}
