package com.example.viewpagerandfragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyFragmentPagerAdapter extends FragmentStateAdapter {

    List<Fragment> fragmentList = new ArrayList<>();  //fragment列表，用以存放被复用的fragment
    //构造函数
    public MyFragmentPagerAdapter(@NonNull FragmentManager fragmentManager,
                                  @NonNull Lifecycle lifecycle,
                                  List<Fragment> fragments) {
        super(fragmentManager, lifecycle);
        fragmentList = fragments;
    }

    //根据当前的position提供适合的fragment
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    //返回adapter中item的数量，在这里是fragment的数量
    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
