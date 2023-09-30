package com.example.summer2023app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.summer2023app.R;
import com.example.summer2023app.databinding.ActivityMainBinding;
import com.example.summer2023app.fragments.FitnessFragment;
import com.example.summer2023app.fragments.HomeFragment;
import com.example.summer2023app.fragments.InfoFragment;
import com.example.summer2023app.fragments.NotificationsFragment;
import com.example.summer2023app.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
{

    ActivityMainBinding binding;
    public static final String TAG=MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* toolbar */
        // step 1: Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /* set HomeFragment as the default */
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new HomeFragment()).commit();

        /* Bottom Navigation Bar */

        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Log.d("OPTION SELECTED",item.getItemId()+"");

                if(item.getItemId() ==R.id.home){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
                } else if (item.getItemId() == R.id.notifications){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new NotificationsFragment()).commit();
                } else if (item.getItemId() == R.id.profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProfileFragment()).commit();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu,menu);

        // Update the admin icon visibility based on the admin status
        updateAdminIconVisibility(menu);
        return true;
    }
    private void updateAdminIconVisibility(Menu menu) {

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isAdmin = preferences.getBoolean("isAdmin", false);
        MenuItem adminItem = menu.findItem(R.id.admin);
        adminItem.setVisible(isAdmin);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.d("OPTION SELECTED",item.getItemId()+"");
        Log.d("MENU", "Option selected: " + item.getItemId());

        if(item.getItemId() == R.id.logout){
            logOut();
            Toast.makeText(this,"You've successfully logged out .", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() ==R.id.frag_unsubscribedInfo){
            Log.d("MENU", "Option selected: frag_goal " + item.getItemId());
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new InfoFragment()).commit();
        }
        else if (item.getItemId() == R.id.frag_payment){
            Toast.makeText(this,"Stripe Payment",Toast.LENGTH_LONG).show();

        } else if (item.getItemId() ==R.id.admin){
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);

        } else if(item.getItemId() == R.id.frag_fitness){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new FitnessFragment()).commit();

        } else if(item.getItemId() == R.id.profile){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProfileFragment()).commit();

        }
        else if(item.getItemId() == R.id.notifications){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new NotificationsFragment()).commit();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        // Redirect the user to the login or registration screen
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }



}