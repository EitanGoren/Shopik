package com.eitan.shopik.customerFragments;

import static com.eitan.shopik.database.Database.LIKED;
import static com.eitan.shopik.database.Database.UNLIKED;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.eitan.shopik.PublicUser;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.adapters.SwipeCardsAdapter;
import com.eitan.shopik.ads.AdMobInterstitialAd;
import com.eitan.shopik.customer.ShopikUser;
import com.eitan.shopik.database.Database;
import com.eitan.shopik.database.ShopikRepository;
import com.eitan.shopik.database.models.ShoppingItem;
import com.eitan.shopik.system.SystemUpdates;
import com.eitan.shopik.viewModels.MainModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CustomerHomeFragment extends Fragment {

    private SwipeCardsAdapter arrayAdapter;

    private MainModel mainModel;
    private boolean isSwiped;
    private static int swipesCount = 0;
    private Observer<Integer> total_items_observer;
    private Observer<Set<ShoppingItem>> unseenItemsObserver;
    private int total_items_num = 0;
    private Observer<Pair<Integer, Integer>> current_items_observer;
    private ShopikUser mShopikUser;
    private Database mDatabase;
    private SwipeFlingAdapterView.onFlingListener mOnFlingListener;
    private SwipesViewHolder viewHolder;
    private final Set<ShoppingItem> items = new HashSet<>();

    private static class SwipesViewHolder extends RecyclerView.ViewHolder {

        private final View itemView;
        private final TextView percentage, total;
        private final SwipeFlingAdapterView flingContainer;

        public SwipesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            flingContainer = itemView.findViewById(R.id.frame);
            percentage = itemView.findViewById(R.id.percentage);
            total = itemView.findViewById(R.id.total);
            percentage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
        mShopikUser = ShopikUser.getInstance();
        mDatabase = Database.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(viewHolder == null) {
            View view = inflater.inflate(R.layout.fragment_customer_home, container,false);
            viewHolder = new SwipesViewHolder(view);
        }

        mOnFlingListener = new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                isSwiped = true;
                swipesCount++;
                arrayAdapter.remove(arrayAdapter.getItem(0));
                if (swipesCount % 60 == 0)
                    SystemUpdates.getInstance().launchReview(getActivity());
            }

            @Override

            public void onLeftCardExit(Object dataObject) {
                onItemUnliked(dataObject);
            }

            @Override
            public void onRightCardExit(final Object dataObject) {
                onItemLiked(dataObject);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                if (itemsInAdapter == 0 && isSwiped) {
                    isSwiped = false;
                    // Show interstitial video ad
                    AdMobInterstitialAd.getInstance().showAd(getContext());
                    // Fetch more items from db
                }
                if (itemsInAdapter == 5) {
                    AdMobInterstitialAd.getInstance().loadNewAd(getActivity());
                }
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = viewHolder.flingContainer.getSelectedView();
                if (view != null) {
                    view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                    view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
                }
            }
        };

        // Show interstitial video ad
        // Fetch more items from db

        current_items_observer = pair -> {
            viewHolder.percentage.setVisibility(View.VISIBLE);
            String text = pair.first + " /";
            viewHolder.percentage.setText(text);
            if (pair.first > 1 && pair.first.equals(total_items_num) || pair.first > total_items_num) {
                viewHolder.percentage.setVisibility(View.GONE);
                viewHolder.total.setVisibility(View.GONE);
            }
        };
        total_items_observer = integer -> {
            viewHolder.total.setVisibility(View.VISIBLE);
            viewHolder.total.setText(String.valueOf(integer));
            total_items_num = integer;
        };
        unseenItemsObserver = shoppingItems -> {
            if(!shoppingItems.isEmpty()) {
                items.clear();
                items.addAll(shoppingItems);
                if(arrayAdapter == null) {
                    arrayAdapter = new SwipeCardsAdapter(requireActivity(), R.layout.swipe_item, items);
                    viewHolder.flingContainer.setAdapter(arrayAdapter);
                    viewHolder.flingContainer.setFlingListener(mOnFlingListener);
                    arrayAdapter.setFlingContainer(viewHolder.flingContainer);
                }
                arrayAdapter.notifyDataSetChanged();
            }
        };

        return viewHolder.itemView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isSwiped = false;
        mainModel.getTotalItems().observe(getViewLifecycleOwner(), total_items_observer);
        mainModel.getCurrentItem().observe(getViewLifecycleOwner(), current_items_observer);
        mainModel.getUnseenItems().observe(getViewLifecycleOwner(), unseenItemsObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainModel.getCurrentItem().removeObserver(current_items_observer);
        mainModel.getTotalItems().removeObserver(total_items_observer);
        total_items_num = 0;
    }

    private void updateBadge() {
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav);
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.bottom_favorites);

        if (badgeDrawable.hasNumber()) {
            int num = badgeDrawable.getNumber();
            badgeDrawable.setNumber(num + 1);
        }
        else
            badgeDrawable.setNumber(1);

        badgeDrawable.setVisible(true);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    private void onItemLiked(Object dataObject) {
        final ShoppingItem shoppingItem = (ShoppingItem)dataObject;
        String item_id = shoppingItem.getId();

        if (item_id != null) {
            boolean isFavorite = arrayAdapter.isFavorite();
            shoppingItem.setFavorite(isFavorite);
            String action;
            if (isFavorite)
                action = Macros.CustomerMacros.FAVORITE;
            else
                action = Macros.CustomerMacros.LIKED;

            updateLikes(shoppingItem);
            updateBadge();
            mainModel.addLikedItemId(shoppingItem.getId(),1);
            mDatabase.onItemLiked(shoppingItem, action);
            ShopikRepository repository = new ShopikRepository(requireActivity().getApplication());
            shoppingItem.setLiked(true);
            repository.insertNewInteractedItem(shoppingItem);
            mainModel.removeUnseenItem(shoppingItem);

            for (String attr : shoppingItem.getConvertedName()) {
                increasePreferredFieldByOneRTDB(attr, shoppingItem.getType(), shoppingItem.getGender());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void increasePreferredFieldByOneRTDB(String attribute, final String type, final String gender) {
        if (attribute.equals("")
                || Arrays.asList(Macros.Items.shit_words).contains(attribute)) {
            return;
        }
        final String attr = attribute.
                replace(".","").
                replace("$","").
                replace("#","").
                replace("[","").
                replace("]","").
                replace(":","");

        mDatabase.updatePreferredField(type, attr, gender);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    private void onItemUnliked(Object dataObject) {
        final ShoppingItem shoppingItem = (ShoppingItem) dataObject;
        if (shoppingItem.getId() != null) {
            mainModel.addUnlikedItemId(shoppingItem.getId(),1);
            mDatabase.onItemUnliked(shoppingItem);
            ShopikRepository repository = new ShopikRepository(requireActivity().getApplication());
            shoppingItem.setLiked(false);
            repository.insertNewInteractedItem(shoppingItem);
            mainModel.removeUnseenItem(shoppingItem);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    private void updateLikes(ShoppingItem shoppingItem) {
        PublicUser likedUser = new PublicUser(mShopikUser.getImageUrl(),
                mShopikUser.getFirstName(),
                mShopikUser.getLastName(),
                mShopikUser.getGender()
        );
        likedUser.setFavorite(shoppingItem.isFavorite());
        shoppingItem.addInteractedUser(likedUser);
    }
}
