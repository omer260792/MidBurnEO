package com.example.omer.midburneo.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.omer.midburneo.R;
import com.example.omer.midburneo.Tabs.MainPageAc;

import de.hdodenhof.circleimageview.CircleImageView;

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

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CircleImageView imageView;
        if (convertView == null) {
            imageView = new CircleImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams
                    (470, 470));
            // imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBorderColor(R.color.colorAccent);
            imageView.setBorderWidth(10);
            imageView.setPadding(8, 16, 8, 8);
        } else {
            imageView = (CircleImageView) convertView;
        }
        imageView.setImageResource(myImages[position]);
        return imageView;
    }

    private Integer[] myImages = {
            R.drawable.admin_btn_logo,
            R.drawable.chat_btn_logo,
            R.drawable.equipment_list_btn_logo,
            R.drawable.meeting_btn_logo,
            R.drawable.mission_comlete_btn_logo,
            R.drawable.profile_btn_logo

    };
}
