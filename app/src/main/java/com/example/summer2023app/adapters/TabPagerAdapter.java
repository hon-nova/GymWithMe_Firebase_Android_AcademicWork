package com.example.summer2023app.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.lifecycle.Lifecycle;

import com.example.summer2023app.fragments.TabInfoFragment;
import com.example.summer2023app.fragments.TabViewFragment;


public class TabPagerAdapter extends FragmentStateAdapter {

    int tabCount;
    //constructor
    public TabPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle, int numberOfTabs) {
        super(fragmentManager, lifecycle);
        this.tabCount = numberOfTabs;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //approach: using switch statement
        switch(position){
            case 0:{
                return new TabViewFragment();
            }//end case 0
            case 1:{
                return new TabInfoFragment();
            }//end case 1

            default: return null;
        }
    }

    @Override
    public int getItemCount() {
        return tabCount;
    }
}
