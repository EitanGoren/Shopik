package com.eitan.shopik.CustomerFragments;

import androidx.fragment.app.Fragment;

public class AllItemsFragment extends Fragment{
/*
    private static CollectionReference itemsFS;
    private static String item_type;
    private static String sub_category;
    private static GridAdapter gridAdapter;
    private static SuggestedModel suggestedModel;
    private static AllItemsModel allItemsModel;
    private static ProgressBar progressBar;
    private static MainModel mainModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOnCreate();
        gridAdapter = new GridAdapter(Objects.requireNonNull(getActivity()),R.layout.grid_item,allItemsModel.getItems().getValue());
        suggestedModel.getAllItems().observe(getViewLifecycleOwner(), arrayList -> allItemsModel.addSuggestedToAllItems(arrayList));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_items, container, false);
    }

    private void initOnCreate() {

        GenderModel genderModel = new ViewModelProvider(requireActivity()).get(GenderModel.class);
        String item_gender = genderModel.getGender().getValue();
        item_type = genderModel.getType().getValue();
        sub_category = genderModel.getSub_category().getValue();

        mainModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
        suggestedModel = new ViewModelProvider(requireActivity()).get(SuggestedModel.class);
        allItemsModel = new ViewModelProvider(requireActivity()).get(AllItemsModel.class);
        allItemsModel.setItemType(item_type);

        assert item_gender != null;
        itemsFS = FirebaseFirestore.getInstance().collection(Macros.ITEMS).document(item_gender).collection(item_type);
    }

    private void init() {
        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        GridView gridContainer = Objects.requireNonNull(getView()).findViewById(R.id.grid_view);
        gridAdapter.notifyDataSetChanged();
        gridContainer.setAdapter(gridAdapter);
        progressBar = getView().findViewById(R.id.progressBar);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize views
        init();

          androidx.appcompat.widget.Toolbar toolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.customer_toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem searchItem = menu.findItem(R.id.nav_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                gridAdapter.getFilter().filter(newText);
                return false;
            }
        });

        allItemsModel.getItems().observe(getViewLifecycleOwner(), list -> {
            if(list.isEmpty()){
                try {
                    progressBar.setVisibility(View.VISIBLE);
                    fetchData fetchData = new fetchData();
                    fetchData.execute();
                }
                catch (Exception e){
                    Log.e(Macros.TAG, Objects.requireNonNull(e.getMessage()));
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private static class fetchData extends AsyncTask<Void,Boolean,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                getItems();
            }
            catch (Exception e){
                Log.d("suggested failed", Objects.requireNonNull(e.getMessage()));
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void getItems() {

            String[] list = Macros.Items.getList(sub_category);
            assert list != null;
            itemsFS.whereArrayContainsAny("name", Arrays.asList(list)).get().
                    addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty())
                            addItems(queryDocumentSnapshots);
                    }).
                    addOnFailureListener(e -> Log.d("failed to load", Objects.requireNonNull(e.getMessage()))).
                    addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        publishProgress(false);
                    });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void addItems(QuerySnapshot queryDocumentSnapshots ){

        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
        for (DocumentSnapshot item : documentSnapshots) {

            ShoppingItem shoppingItem;
            switch (item_type) {
                case Macros.Items.BAG:
                    shoppingItem = item.toObject(Bag.class);
                    break;
                case Macros.Items.SUNGLASSES:
                    shoppingItem = item.toObject(Sunglasses.class);
                    break;
                case Macros.Items.SHOES:
                    shoppingItem = item.toObject(Shoes.class);
                    break;
                case Macros.Items.SHIRT:
                    shoppingItem = item.toObject(Shirt.class);
                    break;
                case Macros.Items.JEANS:
                    shoppingItem = item.toObject(Jeans.class);
                    break;
                case Macros.Items.JACKETS:
                    shoppingItem = item.toObject(Jackets.class);
                    break;
                case Macros.Items.SWIMWEAR:
                    shoppingItem = item.toObject(Swimwear.class);
                    break;
                case Macros.Items.DRESS:
                    shoppingItem = item.toObject(Dress.class);
                    break;
                case Macros.Items.JEWELLERY:
                    shoppingItem = item.toObject(Jewellery.class);
                    break;
                default:
                    shoppingItem = item.toObject(Watch.class);
                    break;
            }

            assert shoppingItem != null;
            allItemsModel.setLast_item_id(shoppingItem.getId());
            shoppingItem.setAd(false);
            shoppingItem.setLikes(0);
            shoppingItem.setUnlikes(0);
            shoppingItem.setLikedUsers(null);
            shoppingItem.setUnlikedUsers(null);
            shoppingItem.setSellerId(Objects.requireNonNull(item.get("seller_id")).toString());
            shoppingItem.setCatagory_num(Integer.parseInt(Objects.requireNonNull(item.get("category_num")).toString()));
            shoppingItem.setPage_num(Integer.parseInt(Objects.requireNonNull(item.get("page_num")).toString()));

            Double updated_price = mainModel.getPrice(shoppingItem.getId_in_seller());
            double current_price = Double.parseDouble(shoppingItem.getPrice());

            if(updated_price != null) {
                if(current_price > updated_price){
                    shoppingItem.setOn_sale(true);
                    String discount = getNewPrice(updated_price);
                    shoppingItem.setDiscount(discount);
                }
            }

            allItemsModel.addToItems(shoppingItem);
        }
        gridAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.INVISIBLE);
        gridAdapter.setAllItems( allItemsModel.getItems().getValue());
    }

    private static String getNewPrice(Double price) {

        Currency shekel = Currency.getInstance("ILS");
        String currency_symbol = shekel.getSymbol();
        Double current = price * Macros.POUND_TO_ILS;

        return new DecimalFormat("##.##").format(current) + currency_symbol;
    }
    */
}
