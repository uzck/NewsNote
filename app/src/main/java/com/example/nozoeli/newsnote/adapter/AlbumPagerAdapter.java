package com.example.nozoeli.newsnote.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.nozoeli.newsnote.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by nozoeli on 16-4-20.
 */
public class AlbumPagerAdapter extends PagerAdapter {

    private ArrayList<String> urlSets;
    private ArrayList<ImageView> imgViewSets;
    private Context context;

    public AlbumPagerAdapter(Context context, ArrayList<String> urlSets, ArrayList<ImageView> imgViewSets) {
        this.context = context;
        this.urlSets = urlSets;
        this.imgViewSets = imgViewSets;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return imgViewSets.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(imgViewSets.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(imgViewSets.get(position));
        Picasso.with(context)
                .load(urlSets.get(position))
                .placeholder(R.drawable.common_fail_placeholder)
                .into(imgViewSets.get(position));

        return imgViewSets.get(position);

    }
}
