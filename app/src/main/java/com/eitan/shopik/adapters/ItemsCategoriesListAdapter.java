package com.eitan.shopik.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.customer.CustomerMainActivity;
import com.eitan.shopik.items.Category;

import java.util.List;

public class ItemsCategoriesListAdapter extends RecyclerView.Adapter<ItemsCategoriesListAdapter.CategoriesHolder> {

    private final List<Category> items;

    public ItemsCategoriesListAdapter(@NonNull List<Category> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CategoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoriesHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.category_group, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CategoriesHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout cat_layout;
        private final TextView main_header;

        public CategoriesHolder(@NonNull View itemView) {
            super(itemView);
            main_header = itemView.findViewById(R.id.main_header);
            cat_layout = itemView.findViewById(R.id.cat_layout);
        }

        public void setItem(final Category item) {
            main_header.setText(item.getMainHeader());

            String cur_color = "#" + Integer.toHexString(20 * (1 + getAbsoluteAdapterPosition())) + Macros.WOMEN_CATEGORY_BACKGROUND_COLOR;
            if(item.getGender().equals(Macros.CustomerMacros.MEN))
                cur_color = "#" + Integer.toHexString(20 * (1 + getAbsoluteAdapterPosition())) + Macros.MEN_CATEGORY_BACKGROUND_COLOR;

            cat_layout.setBackgroundColor(Color.parseColor(cur_color));
            itemView.setOnClickListener(view -> GoToPage(item));
        }

        private void GoToPage(Category item) {
            Intent intent = new Intent(getContext(), CustomerMainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("gender", item.getGender());
            bundle.putString("type", item.getMainHeader());
            bundle.putString("imageUrl", null);
            intent.putExtra("bundle", bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            getContext().startActivity(intent);
            ((Activity) getContext()).overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        }

        public Context getContext() {
            return itemView.getContext();
        }
    }
}
//
//    @Override
//    public int getGroupCount() {
//        return items.size();
//    }
//
//    @Override
//    public int getChildrenCount(int groupPosition) {
//        return getGroup(groupPosition).getRecyclerItemsSize() > 0 ? 1 : 0;
//    }
//
//    @Override
//    public Category getGroup(int groupPosition) {
//        return items.get(groupPosition);
//    }
//
//    @Override
//    public RecyclerItem getChild(int groupPosition, int childPosition) {
//        return items.get(groupPosition).getRecyclerItem(childPosition);
//    }
//
//    @Override
//    public long getGroupId(int groupPosition) {
//        return groupPosition;
//    }
//
//    @Override
//    public long getChildId(int groupPosition, int childPosition) {
//        return 0;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return true;
//    }
//
//    @Override
//    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
//
//        final Category category = getGroup(groupPosition);
//        if(convertView == null){
//            LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            assert layoutInflater != null;
//            convertView = layoutInflater.inflate(R.layout.category_group, parent,false);
//        }
//
//        CircleImageView mImage = convertView.findViewById(R.id.cat_icon);
//        TextView main_header = convertView.findViewById(R.id.main_header);
//        TextView sub_header = convertView.findViewById(R.id.sub_header);
//
//        int color;
//        if(isExpanded)
//            color = category.getGender().equals(Macros.CustomerMacros.MEN) ?
//                    parent.getContext().getColor(R.color.menColor) :
//                    parent.getContext().getColor(R.color.womenColor);
//        else
//            color = Color.BLACK;
//
//        main_header.setTextColor(color);
//        StringBuilder sub_header_text = new StringBuilder();
//        String name = category.getName().toUpperCase();
//        switch (category.getName()) {
//            case "Bag":
//                name = "BAGS";
//                break;
//            case "Shirt":
//                name = "SHIRTS";
//                break;
//            case "Dress":
//                name = "DRESSES";
//                break;
//            case "Watch":
//                name = "WATCHES";
//                break;
//        }
//        main_header.setText(name);
//
//        for (int i = 0; i < category.getRecyclerItemsSize(); ++i) {
//            if( i < 2 ) {
//                String sub_name = category.getRecyclerItem(i).getItem_sub_category();
//                String first_letter = String.valueOf(sub_name.charAt(0)).toUpperCase();
//                sub_header_text.append(first_letter).append(sub_name.substring(1)).append(" | ");
//            }
//            else if( i==2 ) {
//                String sub_name = category.getRecyclerItem(i).getItem_sub_category();
//                String first_letter = String.valueOf(sub_name.charAt(0)).toUpperCase();
//                sub_header_text.append(first_letter).append(sub_name.substring(1)).append("...");
//            }
//            else
//                break;
//        }
//        sub_header.setText(sub_header_text.toString());
//
//        String icon = setmButtonIcon(category.getGender(),category.getName());
//        Macros.Functions.GlidePicture(parent.getContext(), icon, mImage, 0);
//
//        return convertView;
//    }
//
//    private String setmButtonIcon(String gender, String category){
//        String icon = "";
//        switch (category) {
//            case Macros.BAG:
//                icon = Macros.Items.BAGS_IC;
//                break;
//            case Macros.SHIRT:
//                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MENS_SHIRTS_IC : Macros.Items.WOMENS_SHIRTS_IC;
//                break;
//            case Macros.DRESS:
//                icon = Macros.Items.DRESS_IC;
//                break;
//            case Macros.WATCH:
//                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MEN_WATCH_IC : Macros.Items.WOMEN_WATCH_IC;
//                break;
//            case Macros.JACKETS:
//                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MEN_JACKET_IC : Macros.Items.WOMEN_JACKET_IC ;
//                break;
//            case Macros.JEANS:
//                icon = Macros.Items.JEANS_IC;
//                break;
//            case Macros.SUNGLASSES:
//                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MEN_GLASSES_IC : Macros.Items.WOMEN_GLASSES_IC ;
//                break;
//            case Macros.SWIMWEAR:
//                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MENS_SWIM_IC : Macros.Items.WOMENS_SWIM_IC;
//                break;
//            case Macros.JEWELLERY:
//                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MEN_JEWELLERY_IC : Macros.Items.WOMEN_JEWELLERY_IC;
//                break;
//            case Macros.SHOES:
//                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MENS_SHOES_IC : Macros.Items.WOMENS_SHOES_IC;
//                break;
//            case Macros.ACCESSORIES:
//                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MEN_ACCESSORIES_IC : Macros.Items.WOMEN_ACCESSORIES_IC;
//                break;
//            case Macros.UNDERWEAR:
//                icon = gender.equals(Macros.CustomerMacros.MEN) ? Macros.Items.MEN_UNDERWEAR_IC : Macros.Items.WOMEN_LINGERIE_IC;
//                break;
//        }
//        return icon;
//    }
//
//    @Override
//    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
//
//        if(convertView == null){
//            LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().
//                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            assert layoutInflater != null;
//            convertView = layoutInflater.inflate(R.layout.category_item, parent,false);
//        }
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager( parent.getContext(),
//                LinearLayoutManager.HORIZONTAL,false );
//        RecyclerView recyclerView = convertView.findViewById(R.id.recycler);
//        recyclerView.setLayoutManager(layoutManager);
//        RecyclerAdapter recyclerAdapter = new RecyclerAdapter( (getGroup(groupPosition)).getRecyclerItems(),"SubCategory");
//        recyclerView.setAdapter(recyclerAdapter);
//
//        return convertView;
//    }
//
//    @Override
//    public boolean isChildSelectable(int groupPosition, int childPosition) {
//        return true;
//    }
//
//    public void collapseAll(ExpandableListView listContainer){
//        for( int i=0; i < getGroupCount(); ++i ){
//            listContainer.collapseGroup(i);
//        }
//    }
