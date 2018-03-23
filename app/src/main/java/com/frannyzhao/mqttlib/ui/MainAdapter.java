package com.frannyzhao.mqttlib.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaofengyi on 3/1/18.
 */

public class MainAdapter extends FragmentPagerAdapter {
    public static final int HOME_PAGE = 0;
    public static final int SETTINGS_PAGE = 1;
    public static final int TEST_PAGE = 2;
    private List<Fragment> fragments = new ArrayList<>();

    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    public MainAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
