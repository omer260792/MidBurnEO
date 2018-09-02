package com.example.omer.midburneo.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Tabs.MainPageAc;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return myImages.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null){
            imageView = new ImageView(mContext);
            //imageView.setLayoutParams(new ViewGroup.LayoutParams
            // (1000, 1000));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8,8,8,8);
        }else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(myImages[position]);
        return imageView;
    }

    private Integer [] myImages = {
            R.drawable.admin_btn_logo,
            R.drawable.chat_btn_logo,
            R.drawable.equipment_list_btn_logo,
            R.drawable.meeting_btn_logo,
            R.drawable.mission_comlete_btn_logo,
            R.drawable.profile_btn_logo

    };
}
