package com.example.gaopj.class1test1;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
/**
 * Created by gpj on 2016/11/29.
 */

public class ViewPageAdapter extends PagerAdapter {
    private List<View> views;
    private Context context;

    public ViewPageAdapter(Context context, List<View> views) {
        this.context = context;
        this.views = views;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return  views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}
