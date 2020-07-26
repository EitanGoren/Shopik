package com.eitan.shopik.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Items.Catagory;
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Items.SubCategory;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemsCatagoriesListAdapter extends BaseExpandableListAdapter {

    private List<Catagory> items;
    private String imageUrl;
    private TextView main_header;
    private RecyclerView recyclerView;
    private TextView mLayout;

    public ItemsCatagoriesListAdapter(List<Catagory> items, String imageUrl) {
        this.items = items;
        this.imageUrl = imageUrl;
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getGroup(groupPosition).getSubCategoriesSize() > 0 ? 1 : 0;
    }

    @Override
    public Catagory getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public SubCategory getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).getSubCategory(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {

        final Catagory catagory = getGroup(groupPosition);

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.category_group,null);
        }

        CircleImageView mImage = convertView.findViewById(R.id.cat_icon);
        main_header = convertView.findViewById(R.id.main_header);
        TextView sub_header = convertView.findViewById(R.id.sub_header);

        setHeaderColor(parent.getContext(), catagory.getGender(), isExpanded);

        StringBuilder sub_header_text = new StringBuilder();
        main_header.setText(setMainHeader(catagory.getName()).toUpperCase());

        for (int i = 0; i < catagory.getSubCategories().size(); ++i) {
            if(i > 2) break;
            String sub_name = catagory.getSubCategories().get(i).getName();
            String first_letter = String.valueOf(sub_name.charAt(0)).toUpperCase();
            sub_header_text.append(first_letter).append(sub_name.substring(1)).append(" | ");

        }
        sub_header_text.append("...");

        sub_header.setText(sub_header_text.toString());

        String icon = setmButtonIcon(catagory.getGender(),catagory.getName());
        Glide.with(parent.getContext()).load(icon).into(mImage);

        return convertView;
    }

    private void setHeaderColor(Context context, String gender, boolean isExpanded) {
        int color;
        if(isExpanded)
            color = gender.equals(Macros.CustomerMacros.MEN) ? context.getColor(R.color.menColor) : context.getColor(R.color.womenColor);
        else
            color = Color.BLACK;

        main_header.setTextColor(color);
        main_header.setTextColor(color);
    }

    private String setMainHeader(String catagory) {
        String name = catagory;
        switch (name) {
            case Macros.BAG:
                name = "BAGS";
                break;
            case Macros.SHIRT:
                name = "SHIRTS";
                break;
            case Macros.DRESS:
                name = "DRESSES";
                break;
            case Macros.WATCH:
                name = "WATCHES";
                break;
        }
        return name;
    }

    private String setmButtonIcon(String gender, String category){
        String icon = "";
        switch (category) {
            case Macros.BAG:
                icon = Macros.Items.BAGS_IC;
                break;
            case Macros.SHIRT:
                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MENS_SHIRTS_IC : Macros.Items.WOMENS_SHIRTS_IC;
                break;
            case Macros.DRESS:
                icon = Macros.Items.DRESS_IC;
                break;
            case Macros.WATCH:
                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MEN_WATCH_IC : Macros.Items.WOMEN_WATCH_IC;
                break;
            case Macros.JACKETS:
                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MEN_JACKET_IC : Macros.Items.WOMEN_JACKET_IC ;
                break;
            case Macros.JEANS:
                icon = Macros.Items.JEANS_IC;
                break;
            case Macros.SUNGLASSES:
                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MEN_GLASSES_IC : Macros.Items.WOMEN_GLASSES_IC ;
                break;
            case Macros.SWIMWEAR:
                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MENS_SWIM_IC : Macros.Items.WOMENS_SWIM_IC;
                break;
            case Macros.JEWELLERY:
                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MEN_JEWELLERY_IC : Macros.Items.WOMEN_JEWELLERY_IC;
                break;
            case Macros.SHOES:
                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MENS_SHOES_IC : Macros.Items.WOMENS_SHOES_IC;
                break;
        }
        return icon;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {

        final ArrayList<SubCategory> subCategories = (getGroup(groupPosition)).getSubCategories();
        String gender = subCategories.get(0).getGender();

        ArrayList<RecyclerItem> recyclerItems = new ArrayList<>();
        for ( SubCategory sub : subCategories ) {
            RecyclerItem new_item = new RecyclerItem(sub.getName(),null);
            new_item.setImage_resource(sub.getResource());
            new_item.setType(items.get(groupPosition).getName());
            new_item.setItem_sub_category(sub.getName());
            new_item.setGender(sub.getGender());

            String name = String.valueOf(sub.getName().charAt(0)).toUpperCase();
            name += sub.getName().substring(1);
            new_item.setText(name);

            new_item.setUserImageUrl(imageUrl);
            recyclerItems.add(new_item);
        }

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.category_item,null);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(parent.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = convertView.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(recyclerItems, "SubCategory");
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setScrollbarFadingEnabled(true);

        mLayout = convertView.findViewById(R.id.text_layout);

        setLayoutColors(parent.getContext(), gender);

        return convertView;
    }

    private void setLayoutColors(Context context, String gender) {
        if(gender.equals(Macros.CustomerMacros.WOMEN)){
            recyclerView.setBackground(context.getDrawable(R.drawable.women_category_background));
            mLayout.setBackground(context.getDrawable(R.drawable.women_category_background));
        }
        else{
            recyclerView.setBackground(context.getDrawable(R.drawable.men_category_background));
            mLayout.setBackground(context.getDrawable(R.drawable.men_category_background));
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void collapseAll(ExpandableListView listContainer){
        for( int i=0; i < getGroupCount(); ++i ){
            listContainer.collapseGroup(i);
        }
    }
}
