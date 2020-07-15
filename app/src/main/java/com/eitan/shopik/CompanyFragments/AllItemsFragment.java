package com.eitan.shopik.CompanyFragments;

import androidx.fragment.app.Fragment;

public class AllItemsFragment extends Fragment {
/*
    private ArrayList<ShoppingItem> allBags;
    private DatabaseReference companyDB;
    private String companyId;
    FirebaseAuth mAuth;
    Database connection;
    CompanyAllItemsListAdapter listAdapter;

    public static AllItemsFragment newInstance(Company company) {
        AllItemsFragment fragment = new AllItemsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        allBags = new ArrayList<>();
        companyId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        companyDB = FirebaseDatabase.getInstance().getReference().child("Companies").child(companyId);
        connection = new Database();
        companyDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals("items")) {
                    for (DataSnapshot type : dataSnapshot.getChildren()) {
                        for (DataSnapshot item_id : type.getChildren()) {
                         //   allBags.add(connection.getItem(item_id, type.getKey()));
                            listAdapter.notifyDataSetChanged();
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
        });


        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        listAdapter = new CompanyAllItemsListAdapter(getActivity(), R.layout.all_items_list_item, allBags, fragmentManager);
        final ListView listContainer = Objects.requireNonNull(getView()).findViewById(R.id.all_items_list);
        listContainer.setAdapter(listAdapter);
        listContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
        });
    }

    private class CompanyAllItemsListAdapter extends ArrayAdapter<ShoppingItem> {

        Context context;
        private ViewPager viewPager;
        private List<ShoppingItem> items;
        private itemPicsAdapter arrayAdapter;
        private Database connection;
        private DatabaseReference itemsDB;
        FragmentManager fragmentManager;


        public CompanyAllItemsListAdapter(@NonNull Context context, int resource, List<ShoppingItem> items,FragmentManager fragmentManager) {
            super(context, resource,items);
            this.context = context;
            this.fragmentManager = fragmentManager;
        }

        @SuppressLint("SetTextI18n")
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ShoppingItem item = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.all_items_list_item,parent,false);
            }

            viewPager = convertView.findViewById(R.id.all_items_image_viewPager);
            Database connection = new Database();
            final ArrayList<String> images = new ArrayList<>();

            images.add(connection.getASOSimageUrl(3,item.getColor(),item.getId_in_seller()));
            images.add(connection.getASOSimageUrl(2,item.getColor(),item.getId_in_seller()));
            images.add(connection.getASOSimageUrl(1,item.getColor(),item.getId_in_seller()));
            images.add(connection.getASOSimageUrl(4,item.getColor(),item.getId_in_seller()));

            arrayAdapter = new itemPicsAdapter(context, images);
            viewPager.setAdapter(arrayAdapter);
            itemsDB = FirebaseDatabase.getInstance().getReference().child("Items");

            TextView header = convertView.findViewById(R.id.all_items_list_item_header);
            final ImageButton delete = convertView.findViewById(R.id.delete);
            ImageButton discount = convertView.findViewById(R.id.discount);
            ImageButton edit = convertView.findViewById(R.id.edit);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new com.eitan.shopik.CompanyFragments.EditItemFragment().newInstance(item);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.company_cards_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    arrayAdapter.notifyDataSetChanged();
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(item);
                    arrayAdapter.notifyDataSetChanged();
                }
            });
            discount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new postOrStopSaleFragment().newInstance(item);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.company_cards_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    arrayAdapter.notifyDataSetChanged();
                }
            });

           // header.setText( item.getName());
            return convertView;
        }

        @Nullable
        @Override
        public ShoppingItem getItem(int position) {
            return super.getItem(position);
        }

        private void deleteItem(final ShoppingItem item) {
            Database connection = new Database();
          //  connection.removeItem(item);
            listAdapter.remove(item);
            listAdapter.notifyDataSetChanged();
        }
    }
*/
}
