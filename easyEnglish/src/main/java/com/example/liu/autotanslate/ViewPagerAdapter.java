package com.example.liu.autotanslate;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/2/21.
 */
public class ViewPagerAdapter extends PagerAdapter {

    private ArrayList<View> viewList;

    public ViewPagerAdapter(ArrayList<View> _viewList) {
        this.viewList=_viewList;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0==arg1;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView(viewList.get(position));
    }

    @Override
    public Object instantiateItem(View container, int position) {
        ((ViewPager) container).addView(viewList.get(position));
        return viewList.get(position);
    }

}
