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
import com.google.android.material.tabs.TabLayout;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.Objects;
import java.util.Random;

public class CustomerHomeFragment extends Fragment {

    private CardsAdapter arrayAdapter;
    private String item_type;
    private String item_gender;
    private String item_sub_category;
    private SwipesModel swipesModel;
    private Dialog dialog;
    private static final int DELAY_MILLIS = 2500;
    private SwipeFlingAdapterView flingContainer;
    private MainModel mainModel;

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

        mainModel.getAll_items().observe(requireActivity(), shoppingItems -> {

            swipesModel.clearAllItems();
            arrayAdapter.notifyDataSetChanged();

            for( ShoppingItem shoppingItem : shoppingItems ) {

                if(!shoppingItem.isSeen()) {
                    swipesModel.addToItems(shoppingItem);
                }

                if ((swipesModel.getItems().getValue().size() % Macros.SWIPES_TO_AD == 0)
                        && swipesModel.getItems().getValue().size() > 0) {

                    ShoppingItem shoppingItemAd = (ShoppingItem) mainModel.getNextAd();
                    if (shoppingItemAd != null) {
                        shoppingItemAd.setAd(true);
                        swipesModel.addToItems(shoppingItemAd);
                    }
                }
            }

            arrayAdapter.notifyDataSetChanged();
            flingContainer.setAdapter(arrayAdapter);

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

        flingContainer.setFlingListener(null);
        mainModel.getAll_items().removeObservers(getViewLifecycleOwner());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
        flingContainer = requireActivity().findViewById(R.id.frame);
        arrayAdapter = new CardsAdapter(requireActivity(), R.layout.swipe_item, swipesModel.getItems().getValue());
    }

    private void updateBadge() {

        TabLayout tabLayout = requireActivity().findViewById(R.id.top_nav);
        TabLayout.Tab tab = tabLayout.getTabAt(1);

        assert tab != null;
        if (tab.getOrCreateBadge().hasNumber()) {
            int num = tab.getOrCreateBadge().getNumber();
            tab.getOrCreateBadge().setNumber(num + 1);
        }
        else
            tab.getOrCreateBadge().setNumber(1);

        tab.getOrCreateBadge().setVisible(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void onItemLiked(Object dataObject) {

        final MediaPlayer mp = MediaPlayer.create(requireContext(), R.raw.swipe);
        mp.start();
        final ShoppingItem shoppingItem = (ShoppingItem) dataObject;
        Database connection = new Database();
        if (!shoppingItem.isAd()) {
            String item_id = shoppingItem.getId();
            String link = shoppingItem.getSite_link();
            String imageUrl = shoppingItem.getImages().get(0);

            if (item_id != null) {

                String seller = shoppingItem.getSeller();
                boolean isFavorite = arrayAdapter.isFavorite();

                shoppingItem.setFavorite(isFavorite);
                if (isFavorite) {
                    dialog = new Dialog(requireContext());
                    showFavoritesDialog(imageUrl);
                }
                mainModel.addSwipedItemId(item_id);

                updateBadge();

                String action;
                if (isFavorite)
                    action = Macros.CustomerMacros.FAVOURITE;
                else
                    action = Macros.CustomerMacros.LIKED;

                connection.onCustomerInteractWithItem(item_id, item_type, item_gender,
                        item_sub_category, action, seller);

                // add user_id to items Liked field
                connection.onItemAction(item_type, item_gender, item_id, action, seller, link, imageUrl);

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
            String link = shoppingItem.getSite_link();
            String imageUrl = shoppingItem.getImages().get(0);

        if (item_id != null) {

            mainModel.addSwipedItemId(item_id);
            Database connection = new Database();

            connection.onItemAction(item_type, item_gender, item_id, Macros.Items.UNLIKED, seller, link,imageUrl);
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
