package com.eitan.shopik.CustomerFragments;

import android.view.View;

import androidx.fragment.app.Fragment;

public class SuggestedFragment extends Fragment implements View.OnClickListener{
    @Override
    public void onClick(View v) {

    }
/*
    private SuggestedModel suggestedModel;
    private MainModel mainModel;
    private Chip price;
    private Chip sale;
    private Chip match;
    private Chip company;
    private Chip brand;
    private FloatingActionButton scroll;
    private boolean scrollUp;
    private RecyclerGridAdapter recyclerGridAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private AppBarLayout appBarLayout;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOnCreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_items, container,false);
    }

    private void initOnCreate() {
        suggestedModel = new ViewModelProvider(requireActivity()).get(SuggestedModel.class);
        mainModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        price = null;
        sale = null;
        match = null;
        company = null;
        brand = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollUp = false;
        //initialize views
        init();

        mainModel.getAll_items().observe(requireActivity(), shoppingItems -> {
            suggestedModel.clearItems();
            int count = 0;
            int items_num = 0;
            for (ShoppingItem shoppingItem : shoppingItems) {

                if(shoppingItem.getPercentage() >= Macros.CustomerMacros.SUGGESTION_PERCENTAGE){
                    mRecyclerView.setAdapter(recyclerGridAdapter);
                    suggestedModel.addToAllItems(shoppingItem);
                    count++;
                    items_num++;
                    if( (count % Macros.SUGGESTED_TO_AD == 0) && count > 0 ){
                        ShoppingItem shoppingItemAd = (ShoppingItem) mainModel.getNextAd();
                        shoppingItemAd.setAd(true);
                        suggestedModel.addToAllItems(shoppingItemAd);
                        count++;
                    }
                    recyclerGridAdapter.notifyDataSetChanged();
                }
            }
            recyclerGridAdapter.setAllItems(Objects.requireNonNull(suggestedModel.getAllItems().getValue()));
        });

        scroll.setOnClickListener(v -> {
            //scroll down
            if(!scrollUp) {
                scroll.hide();
                mLayoutManager.smoothScrollToPosition(mRecyclerView,
                        null, Objects.requireNonNull(suggestedModel.getAllItems().getValue()).size() - 1);
            } //scroll down
            else {
                scroll.hide();
                mLayoutManager.smoothScrollToPosition(mRecyclerView,null,0);
            }
            scrollUp = !scrollUp;
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // ON BOTTOM
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scroll.show();
                    scroll.setRotation(180);
                }
                // ON TOP
                else if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE){
                    scroll.show();
                    scroll.setRotation(0);
                }
            }
        });

        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            RelativeLayout relativeLayout = requireView().findViewById(R.id.info_layout);
            Toolbar toolbar = requireView().findViewById(R.id.toolbar);

            // Collapsed
            if(verticalOffset <= -50) {
                relativeLayout.setVisibility(View.INVISIBLE);
                toolbar.setVisibility(View.VISIBLE);
            }
            // Expanded
            else {
                toolbar.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void init() {

        appBarLayout = requireView().findViewById(R.id.appbar);
        mRecyclerView = requireView().findViewById(R.id.grid_recycler_view);
        scroll = requireView().findViewById(R.id.scroll_up_down);
        recyclerGridAdapter = new RecyclerGridAdapter(suggestedModel.getAllItems().getValue(),null);
        mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(recyclerGridAdapter);

        initChips();
    }

    private void initChips(){

        price = requireView().findViewById(R.id.price_chip);
        sale = requireView().findViewById(R.id.sale_chip);
        match = requireView().findViewById(R.id.match_chip);
        brand = requireView().findViewById(R.id.brand_chip);
        company = requireView().findViewById(R.id.company_chip);
        Chip favorite = requireView().findViewById(R.id.favorites_chip);

        Chip toolbar_price = requireView().findViewById(R.id.price_chip2);
        Chip toolbar_sale = requireView().findViewById(R.id.sale_chip2);
        Chip toolbar_match = requireView().findViewById(R.id.match_chip2);
        Chip toolbar_favorite = requireView().findViewById(R.id.favorites_chip2);
        Chip toolbar_company = requireView().findViewById(R.id.company_chip2);
        Chip toolbar_brand = requireView().findViewById(R.id.brand_chip2);

        price.setOnClickListener(this);
        sale.setOnClickListener(this);
        company.setOnClickListener(this);
        brand.setOnClickListener(this);
        match.setOnClickListener(this);
        favorite.setOnClickListener(this);

        toolbar_favorite.setOnClickListener(this);
        toolbar_match.setOnClickListener(this);
        toolbar_brand.setOnClickListener(this);
        toolbar_company.setOnClickListener(this);
        toolbar_sale.setOnClickListener(this);
        toolbar_price.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.match_chip2:
            case R.id.match_chip:
                sortItems("match");
                break;
            case R.id.price_chip:
            case R.id.price_chip2:
                sortItems("price");
                break;
            case R.id.sale_chip2:
            case R.id.sale_chip:
                sortItems("sale");
                break;
            case R.id.favorites_chip:
            case R.id.favorites_chip2:
                sortItems("favorites");
                break;
            case R.id.company_chip:
            case R.id.company_chip2:
                sortItems("company");
                break;
            case R.id.brand_chip:
            case R.id.brand_chip2:
                sortItems("brand");
                break;
            default:
                sortItems("clear");
                break;
        }
    }

    private void sortItems(String sort_by){
        recyclerGridAdapter.getSortingFilter().filter(sort_by);
    }

    @Override
    public void onPause() {
        super.onPause();
        scroll.hide();
    }

    @Override
    public void onResume() {
        super.onResume();
        scroll.show();
    }

 */
}