package com.eitan.shopik.database;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eitan.shopik.Macros;
import com.eitan.shopik.PublicUser;
import com.eitan.shopik.customer.ShopikUser;
import com.eitan.shopik.database.models.Company;
import com.eitan.shopik.database.models.ShoppingItem;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database {

    private static volatile Database database = null;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static final String CUSTOMERS = "Customers";
    public static final String USERS_PUBLIC = "UsersPublic";
    public static final String MESSAGES = "Messages";
    public static final String COMPANIES = "Companies";
    public static final String ITEMS = "Items";
    public static final String MEN = "Men";
    public static final String WOMEN = "Women";
    public static final String LIKED = "Liked";
    public static final String ID = "id";
    public static final String UNLIKED = "Unliked";
    public static final String FAVORITE = "favorite";
    public static final String PREFERRED_ITEMS = "Preferred items";
    public static final String TOP_WORDS = "TOP_WORDS";
    public static final String DATE_CREATED = "date_created";
    public static final int DOCUMENTS_FETCHED_LIMIT = 42;
    private final String TAG = "com.eitan.shopik.database";
    private final int NUMBER_OF_ITEM_FIELDS = 16;

    private final FirebaseFirestore mFirestore;
    private final FirebaseDatabase mRtDatabase;
    private final ShopikUser mUser;

    public static final class NewItemEvent {
        public final HashSet<ShoppingItem> shoppingItems;

        public NewItemEvent(HashSet<ShoppingItem> si){
            this.shoppingItems = si;
        }
    }
    public static final class ItemModifiedEvent {
        public final HashSet<ShoppingItem> shoppingItems;

        public ItemModifiedEvent(HashSet<ShoppingItem> si){
            this.shoppingItems = si;
        }
    }
    public static final class UsersPastInteractedItemsEvent {
        public Map<String,String> interactedItems;
        public int liked, unliked;
        public UsersPastInteractedItemsEvent(Map<String,String> interactedItems, int liked, int unliked){
            this.interactedItems = interactedItems;
            this.liked = liked;
            this.unliked = unliked;
        }
    }
    public static final class TopWordsEvent{
        public HashSet<String> topWords;
        public TopWordsEvent(HashSet<String> topWords){
            this.topWords = topWords;
        }
    }
    public static final class PreferredAttributesEvent{
        public Map<String, Long> preferredMap;

        public PreferredAttributesEvent(Map<String, Long> map){
            this.preferredMap = map;
        }
    }
    public static final class LastItemEvent{
        public String itemId;
        public LastItemEvent(String id){
            this.itemId = id;
        }
    }
    public static final class UserDataChangedEvent{
        public Map<String, Object> userData;
        public UserDataChangedEvent(Map<String, Object> userData){
            this.userData = userData;
        }
    }
    public static final class NewCompanyInfo{
        public List<Company> info;
        public NewCompanyInfo(List<Company> info){
            this.info = info;
        }
    }
    public static final class ItemsLikesUnlikesInfo {
        public Set<ShoppingItem> allItems;
        public ItemsLikesUnlikesInfo(Set<ShoppingItem> items){
            this.allItems = items;
        }
    }
    public static final class InteractedUserInfo{
        public ShoppingItem item;
        public InteractedUserInfo(ShoppingItem item){
            this.item = item;
        }
    }
    public static final class OnCompanyInfoReceivedEvent{
        public ShoppingItem item;
        public OnCompanyInfoReceivedEvent(ShoppingItem item){
            this.item = item;
        }
    };
    public static final class UserDataUpdated{}

    private Database(){
        mFirestore = FirebaseFirestore.getInstance();
        mRtDatabase = FirebaseDatabase.getInstance();
        mUser = ShopikUser.getInstance();
    }
    public static Database getInstance() {
        if (database == null)
            database = new Database();
        return database;
    }

    public void registerNewUser(){
        Map<String,String> new_user_public = new HashMap<>();
        String userName = mUser.getFirstName() + " " + mUser.getLastName();
        new_user_public.put("name", userName);
        new_user_public.put("imageUrl", mUser.getImageUrl());
        new_user_public.put("gender", mUser.getGender());
        new_user_public.put("id", mUser.getId());

        mFirestore.collection(USERS_PUBLIC).
                document(mUser.getId()).
                set(new_user_public);

        mFirestore.collection(CUSTOMERS)
                .document(mUser.getId())
                .set(mUser);

        EventBus.getDefault().post(new UserDataUpdated());
    }
    public void fetchExistingUser(){
        mFirestore.collection(CUSTOMERS).
                document(mUser.getId()).get().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            mUser.setFirstName(Objects.requireNonNull(doc.get("firstName")).toString());
                            mUser.setLastName(Objects.requireNonNull(doc.get("lastName")).toString());
                            mUser.setImageUrl(Objects.requireNonNull(doc.get("imageUrl")).toString());
                            mUser.setCoverPhoto(Objects.requireNonNull(doc.get("coverPhoto")).toString());
                            EventBus.getDefault().post(new UserDataUpdated());
                        } else {
                            Log.i(TAG, "Value Doesn't Exist");
                            registerNewUser();
                        }
                    } else {
                        Log.i(TAG, "No Such UserId");
                    }
                });
    }
    public void getTopWords(String category, String gender){
        DocumentReference topWordsDoc = mFirestore.collection(ITEMS)
                .document(gender)
                .collection(category)
                .document(TOP_WORDS);

        topWordsDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                HashSet<String> words = new HashSet<>();
                if (documentSnapshot.contains("asos_words")) {
                    words.addAll((ArrayList<String>) Objects.requireNonNull(documentSnapshot.get("asos_words")));
                }
                if (documentSnapshot.contains("renuar_words")) {
                    words.addAll((ArrayList<String>) Objects.requireNonNull(documentSnapshot.get("renuar_words")));
                }
                EventBus.getDefault().post(new TopWordsEvent(words));
            }
        });
    }
    public void updateUserData(Map<String, Object> userData){
        DocumentReference ref = mFirestore.
                collection(CUSTOMERS).
                document(mUser.getId());
        ref.update(userData);
    }
    public void getAllCompaniesInfo() {
        databaseWriteExecutor.execute(() -> mFirestore.collection(COMPANIES).get().
                addOnCompleteListener(documentSnapshot -> {
                    List<Company> companies = new ArrayList<>();
                    for(QueryDocumentSnapshot comp : documentSnapshot.getResult()){
                        Company company = new Company();
                        company.setDescription(comp.get("description") != null ? comp.get("description").toString() : "");
                        company.setName(comp.get("seller") != null ? comp.get("seller").toString() : "");
                        company.setId(comp.get("id") != null ? comp.get("id").toString() : "");
                        company.setLogoUrl(comp.get("logo_url") != null ? comp.get("logo_url").toString() : "");
                        company.setCover(comp.get("cover_image_url") != null ? comp.get("cover_image_url").toString() : "");
                        company.setFacebook(comp.get("fb") != null ? comp.get("fb").toString() : "");
                        company.setInstagram(comp.get("ig") != null ? comp.get("ig").toString() : "");
                        company.setYoutube(comp.get("yt") != null ? comp.get("yt").toString() : "");
                        company.setTwitter(comp.get("twitter") != null ? comp.get("twitter").toString() : "");
                        company.setSite(comp.get("web") != null ? comp.get("web").toString() : "");
                    }
                    EventBus.getDefault().post(new NewCompanyInfo(companies));
                })
        );
    }
    public void onItemUnliked(ShoppingItem shoppingItem){
        new updateDatabase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                shoppingItem.getType(), shoppingItem.getGender(), shoppingItem.getId(), Macros.Items.UNLIKED, shoppingItem.getSeller());
    }
    public void onItemLiked(ShoppingItem shoppingItem, String action){
        new updateDatabase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                shoppingItem.getType(), shoppingItem.getGender(), shoppingItem.getId(), action, shoppingItem.getSeller());
    }
    public void updatePreferredField(String type, String attr, String gender){
        Map<String, Object> map = new HashMap<>();
        mRtDatabase.getReference().
                child(Macros.CUSTOMERS).
                child(mUser.getId()).
                child(gender).
                child(Macros.CustomerMacros.PREFERRED_ITEMS).
                child(type).
                child(attr).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long num;
                        if(dataSnapshot.exists() && attr.equals(dataSnapshot.getKey())){
                            num = (long) dataSnapshot.getValue();
                            num++;
                            map.put(attr, num);

                            mRtDatabase.getReference().
                                    child(Macros.CUSTOMERS).
                                    child(mUser.getId()).
                                    child(gender).
                                    child(Macros.CustomerMacros.PREFERRED_ITEMS).
                                    child(type).
                                    updateChildren(map);
                        }
                        else if(attr.equals(dataSnapshot.getKey())){
                            mRtDatabase.getReference().
                                    child(Macros.CUSTOMERS).
                                    child(mUser.getId()).
                                    child(gender).
                                    child(Macros.CustomerMacros.PREFERRED_ITEMS).
                                    child(type).
                                    child(attr).
                                    setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }
    public void listenToItems(String item_type, String item_gender){
        final Query docRef = mFirestore.
                collection(ITEMS)
                .document(item_gender)
                .collection(item_type)
                .orderBy(ID)
                .limit(DOCUMENTS_FETCHED_LIMIT);

        docRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                System.out.println("************************************ Failed ************************************");
                System.out.println("Error getting documents: " + e.getMessage());
                System.out.println("********************************************************************************");
            }
            else if(queryDocumentSnapshots == null)
                System.out.println("Current data: null");
            else {
                HashSet<ShoppingItem> add_items = new HashSet<>();
                HashSet<ShoppingItem> mod_items = new HashSet<>();
                for (DocumentChange dc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges()) {
                    ShoppingItem shoppingItem = parseOneItemToShoppingItem(dc);
                    switch (dc.getType()) {
                        case ADDED:
                            if (dc.getDocument().exists() && dc.getDocument().getData().size() == NUMBER_OF_ITEM_FIELDS)
                                add_items.add(shoppingItem);
                            break;
                        case MODIFIED:
                            if (dc.getDocument().exists() && dc.getDocument().getData().size() == NUMBER_OF_ITEM_FIELDS)
                                mod_items.add(shoppingItem);
                            break;
                        case REMOVED:
                            break;
                    }
                }
                if(!mod_items.isEmpty()) EventBus.getDefault().post(new ItemModifiedEvent(mod_items));
                if(!add_items.isEmpty()) EventBus.getDefault().post(new NewItemEvent(add_items));
            }
        });
    }
    public void fetchPreferredAttributes(String item_type, String item_gender){
        ChildEventListener mPreferredItemsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Map<String, Long> map = new HashMap<>();
                map.put(snapshot.getKey(), (Long) snapshot.getValue());
                if (snapshot.exists()) {
                    EventBus.getDefault().post(new PreferredAttributesEvent(map));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        DatabaseReference ref = mRtDatabase.getReference().
                child(CUSTOMERS).
                child(mUser.getId()).
                child(item_gender).
                child(PREFERRED_ITEMS).
                child(item_type);
        ref.addChildEventListener(mPreferredItemsListener);
    }
    public void fetchLikedUnlikedItems(String item_type, String item_gender){
        ValueEventListener mInteractionsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> map = new HashMap<>();
                String lastSwiped = null;
                int liked = 0, unliked = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        if (Objects.requireNonNull(key).equals("last_swiped"))
                            lastSwiped = Objects.requireNonNull(snapshot.getValue()).toString();
                        else {
                            if (Objects.requireNonNull(snapshot.getValue()).toString().equals(LIKED))
                                liked++;
                            else
                                unliked++;

                            map.put(key, Objects.requireNonNull(snapshot.getValue()).toString());
                        }
                    }
                }
                EventBus.getDefault().post(new UsersPastInteractedItemsEvent(map, liked, unliked));
                if (lastSwiped != null)
                    EventBus.getDefault().post(new LastItemEvent(lastSwiped));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        DatabaseReference ref2 = mRtDatabase.getReference().
                child(CUSTOMERS).
                child(mUser.getId()).
                child(item_gender).
                child(item_type);

        ref2.addListenerForSingleValueEvent(mInteractionsEventListener);
    }
    public void getLastSeenItem(String item_type, String item_gender){
        ValueEventListener mLastSwipedItemEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    EventBus.getDefault().post(new LastItemEvent(Objects.requireNonNull(snapshot.getValue()).toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mRtDatabase.getReference().
                child(Macros.CUSTOMERS).
                child(mUser.getId()).
                child(item_gender).
                child(item_type).
                child("last_swiped").
                addListenerForSingleValueEvent(mLastSwipedItemEventListener);
    }
    public void getLikesOfShoppingItem(String item_type, String item_gender, String firstId, String lastId, Set<ShoppingItem> items){
        ValueEventListener mLikesOfItemInfo = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> user_interaction = (Map<String, Object>) dataSnapshot.getValue();
                    for(ShoppingItem item : items) {
                        Map<String, String> item_interactions = (Map<String, String>) user_interaction.get(item.getSeller().toLowerCase() + "-" + item.getId());
                        if(item_interactions != null) {
                            for(String user : item_interactions.keySet()){
                                if(Objects.equals(item_interactions.get(user), LIKED)) {
                                    item.addLikedUserId(user);
                                }
                                else {
                                    item.addUnlikedUserId(user);
                                }
                            }
                        }
                    }
                }
                EventBus.getDefault().post(new ItemsLikesUnlikesInfo(items));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        mRtDatabase.getReference().
                child(ITEMS).
                child(item_gender).
                child(item_type).orderByKey().startAt(firstId).endAt(lastId).
                addListenerForSingleValueEvent(mLikesOfItemInfo);
    }
    public void getInteractedUserInfo(ShoppingItem item){
        List<String> usersInteracted = new ArrayList<>(item.getConvertedLikedUsersIds());
        usersInteracted.addAll(item.getConvertedUnlikedUsersIds());
        mFirestore.collection(USERS_PUBLIC).
                whereIn("id", usersInteracted)
                .addSnapshotListener((value, error) -> {
                    if(value != null) {
                        if (error != null) {
                            System.out.println(error.getCode());
                            EventBus.getDefault().post(new OnCompanyInfoReceivedEvent(item));
                        }
                        if (!value.isEmpty()) {
                            for (DocumentChange dc : value.getDocumentChanges()) {
                                Map<String, Object> map = dc.getDocument().getData();
                                String name = map.get("name").toString();
                                String image = map.get("imageUrl").toString();
                                String gender = map.get("gender").toString();
                                String id = map.get("id").toString();
                                PublicUser publicUser = new PublicUser(image, name, gender, id);
                                item.addInteractedUser(publicUser);
                            }
                        }
                    }
                    EventBus.getDefault().post(new OnCompanyInfoReceivedEvent(item));
                });
    }
    public void updateItemsDB(String item_id, String item_type, String item_gender, String company) {
        Map<String, Object> unliked = new HashMap<>();
        unliked.put(mUser.getId(), UNLIKED);
        // remove user_id from liked list
        mRtDatabase.getReference().
                child(ITEMS).
                child(item_gender).
                child(item_type).
                child(company + "-" + item_id).
                updateChildren(unliked);

        unliked.clear();
        unliked.put(company + "-" + item_id, UNLIKED);
        mRtDatabase.getReference().
                child(CUSTOMERS).
                child(mUser.getId()).
                child(item_gender).
                child(item_type).
                child(company + "-" + item_id).
                updateChildren(unliked);

        unliked = null;
    }

    private static class updateDatabase extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... strings) {

            String type = strings[0];
            String gender = strings[1];
            String item_id = strings[2];
            String action = strings[3];
            String company = strings[4];

            String userId = database.mUser.getId();
            String temp_action = action;
             if(action.equals(FAVORITE)) temp_action = LIKED;

            Map<String,Object> userInfo = new HashMap<>();
            userInfo.put(userId, temp_action);
            database.mRtDatabase.getReference().
                    child(ITEMS).
                    child(gender).
                    child(type).
                    child(company.toLowerCase() + "-" + item_id).
                    setValue(userInfo);
//            Map<String,Object> info = new HashMap<>();
//            info.put("link", link);
//            info.put("image", image);

//            database.mRtDatabase.getReference().
//                    child(ITEMS).
//                    child(ITEMS).
//                    child(gender).
//                    child(type).
//                    // child(company).
//                    child(company.toLowerCase() + "-" + item_id).
//                    child("Info").
//                    setValue(info);
            userInfo.clear();
            userInfo.put(company.toLowerCase() + "-" + item_id, temp_action);
            database.mRtDatabase.getReference().
                    child(CUSTOMERS).
                    child(userId).
                    child(gender).
                    child(type).
                    updateChildren(userInfo);

            database.mRtDatabase.getReference().
                    child(CUSTOMERS).
                    child(userId).
                    child(gender).
                    child(type).
                    child("last_swiped").
                    setValue(item_id);

            return null;
        }
    }
    private ShoppingItem parseOneItemToShoppingItem(DocumentChange documentChange){
        Map<String, Object> map = documentChange.getDocument().getData();
        ArrayList<String> images = (ArrayList<String>) map.get("images");
        Map<String, String> prices = (HashMap<String, String>) map.get("prices");
        ArrayList<String> name = new ArrayList<>(Arrays.asList(((String) Objects.requireNonNull(map.get("description"))).split(" ")));

        ShoppingItem shoppingItem = new ShoppingItem();
        shoppingItem.setConvertedImages(images);
        shoppingItem.setBrand((String) Objects.requireNonNull(map.get("brand")));
        shoppingItem.setType((String) map.get("type"));
        shoppingItem.setGender((String) map.get("gender"));
        shoppingItem.setSeller((String) map.get("seller"));
        shoppingItem.setSellerId((String) map.get("seller_id"));
        shoppingItem.setId((String) map.get("id"));
        shoppingItem.setConvertedName(name);
        shoppingItem.setSite_link((String) map.get("link"));
        shoppingItem.setOn_sale((Boolean) map.get("on_sale"));
        if (shoppingItem.isOn_sale()) {
            shoppingItem.setPrice(Objects.requireNonNull(prices).get("old_price"));
            shoppingItem.setReducedPrice(prices.get("new_price"));
        } else {
            shoppingItem.setPrice(Objects.requireNonNull(prices).get("old_price"));
            shoppingItem.setReducedPrice(null);
        }
        return shoppingItem;
    }
}

