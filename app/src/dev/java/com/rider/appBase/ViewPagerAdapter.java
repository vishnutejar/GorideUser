package com.rider.appBase;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by br on 26/2/18.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<BaseFragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public List<BaseFragment> getmFragmentList() {
        return mFragmentList;
    }

    public void clearData() {
        mFragmentList.clear();
        mFragmentTitleList.clear();
    }

    @Override
    public BaseFragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }


    public void addFrag(BaseFragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public void updatePageTitle(int position, String newTitle) {
        mFragmentTitleList.set(position, newTitle);
    }
}