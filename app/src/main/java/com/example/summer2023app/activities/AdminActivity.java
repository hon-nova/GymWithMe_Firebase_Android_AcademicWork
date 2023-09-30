package com.example.summer2023app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.summer2023app.adapters.TabPagerAdapter;
import com.example.summer2023app.databinding.ActivityAdminBinding;
import com.example.summer2023app.fragments.TabInfoFragment;
import com.example.summer2023app.fragments.TabViewFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity implements
        TabViewFragment.OnFragmentInteractionListener,
        TabInfoFragment.OnFragmentInteractionListener{

    ActivityAdminBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configureTabLayout();
        binding.txtViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(AdminActivity.this, LoginActivity.class));
                }
        });

    }
    private void configureTabLayout() {
        TabLayout tabLayout = binding.tabLayout;

        final ViewPager2 viewPager2 = binding.pager;
        final TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager(), this.getLifecycle(), 2);
        viewPager2.setAdapter(adapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch(position){
                    case 0: {
                        tab.setText("View Users");
                        break;
                    }
                    case 1: {
                        tab.setText("Info");
                        break;
                    }
                }
            }
        });
        tabLayoutMediator.attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}
}