package com.example.summer2023app.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.summer2023app.fragments.LoginTabFragment;
import com.example.summer2023app.fragments.RegisterTabFragment;

public class LoginAdapter extends FragmentStateAdapter {
    Context context;
    public LoginAdapter(FragmentManager fragmentManager, Lifecycle lifecycle, Context context) {
        super(fragmentManager, lifecycle);
        this.context = context;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0){
            return new LoginTabFragment();
        }
        return new RegisterTabFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
