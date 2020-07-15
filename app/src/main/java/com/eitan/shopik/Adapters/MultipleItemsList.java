package com.eitan.shopik.Adapters;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.eitan.shopik.Customer.CustomerMainActivity;
import com.eitan.shopik.Items.Catagory;
import com.eitan.shopik.R;

import java.util.ArrayList;
import java.util.TreeSet;

public class MultipleItemsList extends ListActivity {
/*
    private MyCustomAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MyCustomAdapter();
        for (int i = 1; i < 50; i++) {
            mAdapter.addItem("item " + i);
            if (i % 4 == 0) {
                mAdapter.addSeparatorItem("separator " + i);
            }
        }
        setListAdapter(mAdapter);
    }

    private class MyCustomAdapter extends BaseAdapter {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
        Catagory catagory = new Catagory(null,null);

        private ArrayList mData = new ArrayList();
        private LayoutInflater mInflater;
        private ViewFlipper mFlipper;
        private Button mButton;

        private TreeSet mSeparatorsSet = new TreeSet();

        public MyCustomAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        public void addSeparatorItem(final String item) {
            mData.add(item);
            // save separator position
            mSeparatorsSet.add(mData.size() - 1);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return (String) mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            int type = getItemViewType(position);
            System.out.println("getView " + position + " " + convertView + " type = " + type);
            if (convertView == null) {
                holder = new ViewHolder();
                switch (type) {
                    case TYPE_ITEM:
                        convertView = mInflater.inflate(R.layout.image_list_flipper_item, null);
                        holder.textView = convertView.findViewById(R.id.text);
                        break;
                    case TYPE_SEPARATOR:
                        convertView = mInflater.inflate(R.layout.ads_item, null);
                        holder.textView = convertView.findViewById(R.id.textSeparator);
                        break;
                }
                assert convertView != null;
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(type == TYPE_ITEM) {
                assert catagory != null;
                ArrayList<Integer> images = catagory.getResource();
                mFlipper = convertView.findViewById(R.id.item_image);

                for (Integer image : images) {
                    flipperImages(image, mFlipper);
                }

                mButton = convertView.findViewById(R.id.item_button);
                mButton.setText(catagory.getName());
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MultipleItemsList.this, CustomerMainActivity.class);
                        intent.putExtra("type", catagory.getName());
                        startActivity(intent);
                    }
                });
            }
            return convertView;
        }
    }
    private void flipperImages(int image, ViewFlipper flipper){

        ImageView imageView = new ImageView(MultipleItemsList.this);
        imageView.setImageResource(image);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        flipper.addView(imageView);
        flipper.setFlipInterval(6000);
        flipper.setAutoStart(true);

        flipper.setInAnimation(MultipleItemsList.this,R.anim.lefttoright);
        flipper.setOutAnimation(MultipleItemsList.this,R.anim.righttoleft);
    }
    public static class ViewHolder {
        public TextView textView;
    }*/
}
