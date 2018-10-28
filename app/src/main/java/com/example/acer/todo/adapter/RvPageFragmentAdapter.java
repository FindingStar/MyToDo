package com.example.acer.todo.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.acer.todo.fragment.RvFragment;

import java.util.ArrayList;
import java.util.List;

public class RvPageFragmentAdapter extends FragmentStatePagerAdapter{

    private List<RvFragment>childRvFragmentList;
    private List<String>tabTitleList;

    public RvPageFragmentAdapter(FragmentManager fm,List<RvFragment>childRvFragmentList,List<String>tabList) {
        super(fm);
        this.childRvFragmentList=childRvFragmentList;
        tabTitleList=tabList;
    }

    @Override
    public Fragment getItem(int position) {
        return childRvFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return childRvFragmentList.size();
    }

    //viewpager 标题在
    // setupWithViewPager之后，tablayout会使用每个pageAdapter里的page title，而起初我并没有设置。
    //所以设置一下就可以了
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitleList.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;        //***
    }
}
