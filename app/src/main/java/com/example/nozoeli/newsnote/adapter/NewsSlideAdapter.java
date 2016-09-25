package com.example.nozoeli.newsnote.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.nozoeli.newsnote.fragment.NewsFragment;
import com.example.nozoeli.newsnote.fragment.NewsListFragment;

/**
 * Created by nozoeli on 16-4-13.
 */
public class NewsSlideAdapter extends FragmentPagerAdapter {

    private int PAGES = 5;
    private FragmentManager fm;
    private String tabTitles[] = {"科技", "财经", "体育", "社会", "军事"};

    public NewsSlideAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }


    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    // 也可以采用维护一个Fragment List的方法进行切换
    // 但不能通过改变一个公用对象的值来进行数据的切换
    // 否则会造成相邻的三个Fragment在createView时采用相同的数据，造成页面的重复
    @Override
    public Fragment getItem(int position) {
        return NewsListFragment.getInstance(NewsFragment.data[position], fm, position);  // 相应的新闻列表页面填充相应的数据项
    }

    @Override
    public int getCount() {
        return PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

}
