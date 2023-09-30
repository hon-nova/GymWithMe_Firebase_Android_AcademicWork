package com.example.summer2023app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.summer2023app.R;
import com.example.summer2023app.databinding.ActivityPlaygroundBinding;
import com.example.summer2023app.fragments.CardFitnessFragment;
import com.example.summer2023app.fragments.FitnessFilterFragment;
import com.example.summer2023app.fragments.FitnessFragment;
import com.example.summer2023app.fragments.NotificationsFragment;
import com.example.summer2023app.fragments.PaymentFragment;
import com.example.summer2023app.fragments.ProfileFragment;
import com.example.summer2023app.fragments.UserWorkoutSessionFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class PlaygroundActivity extends AppCompatActivity implements
        CardFitnessFragment.OnButtonInCardFitnessClicked,
        UserWorkoutSessionFragment.OnButtonContinueProfilePaymentClick,
        PaymentFragment.OnPaymentSuccessListener
        {

    public static final String TAG=PlaygroundActivity.class.getSimpleName();

    ActivityPlaygroundBinding binding;
    FragmentManager manager;
    int duration, weight;
    DatabaseReference userReference;
    boolean subscribed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaygroundBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        manager = getSupportFragmentManager();

        boolean fromHomeFragment = getIntent().getBooleanExtra("openWorkoutSession", false);

        if (fromHomeFragment) {
            // Hide the first FrameLayout
            LinearLayout frameLayout1 = findViewById(R.id.frame_in_playground1);
            frameLayout1.setVisibility(View.GONE);

            addUserWorkoutSessionFragment();

        } else {
            addFitnessFragment();
        }

        binding.btnSendToFitnessFilter.setOnClickListener((View v) -> {
            binding.txtViewForViewingOnly.setVisibility(View.INVISIBLE);
            replaceFitnessFilterFragment();
        });
    }
    private void addUserWorkoutSessionFragment() {
        UserWorkoutSessionFragment userWorkoutSessionFragment = new UserWorkoutSessionFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frame_in_playground2, userWorkoutSessionFragment, "userWorkoutSessionTag");
        transaction.commit();
    }

    private void addFitnessFragment() {
        FitnessFragment fitnessFragment = new FitnessFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frame_in_playground2,fitnessFragment,"fitnessTag");
        transaction.commit();
    }

    private void replaceFitnessFilterFragment() {
        FitnessFilterFragment fff = new FitnessFilterFragment();
       //start sharing
        String nameFilter = binding.editTxtSearchName.getText().toString().trim();
        String durationFilter =binding.editTxtSearchDuration.getText().toString();
        String weightFilter = binding.editTxtSearchWeight.getText().toString();
        try {
            duration = Integer.parseInt(durationFilter);
            weight =Integer.parseInt(weightFilter);
            Log.i(TAG, "sendBundleToFitnessFilter: "+duration + ",weight:   "+weight);
        }catch(Exception ed){
            Toast.makeText(this, ed.getMessage(),Toast.LENGTH_SHORT).show();
        }
        if (!nameFilter.isEmpty() && !durationFilter.isEmpty() && !weightFilter.isEmpty()){
            Bundle bundle = new Bundle();
            bundle.putString("nameFilter",nameFilter);
            bundle.putInt("durationFilter",duration);
            bundle.putInt("weightFilter",weight);

            fff.setArguments(bundle);
        } else {
            Toast.makeText(this,"No empty fields allowed",Toast.LENGTH_SHORT).show();
        }
        //end sharing

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_in_playground2,fff,"fffTag");
        transaction.commit();
    }

    @Override
    public void onCardFitnessClicked() {
        // Get the LinearLayout
        LinearLayout layout = findViewById(R.id.frame_in_playground1);
        // Set its visibility to GONE
        layout.setVisibility(View.GONE);

        UserWorkoutSessionFragment workoutSessionFragmentFragment = new UserWorkoutSessionFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_in_playground2, workoutSessionFragmentFragment,"userWorkoutFTag")
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onContinueProfilePaymentClick() {

        LinearLayout layout = findViewById(R.id.frame_in_playground1);
        layout.setVisibility(View.GONE);

        checkSubscriptionStatus();

    }
    private void checkSubscriptionStatus(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //IMPORTANT 2
            String userId = user.getUid();
            userReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists() && dataSnapshot.hasChild("subscribed")) {
                        subscribed = dataSnapshot.child("subscribed").getValue(Boolean.class);
                        //NAVIGATE TO ITS TARGET FRAGMENT
                        navigateBasedOnSubscription(subscribed);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }
    }
    private void navigateBasedOnSubscription(boolean isSubscribed){
        FragmentTransaction transaction = manager.beginTransaction();
        if(isSubscribed){
            ProfileFragment profileFragment = new ProfileFragment();
            transaction.replace(R.id.frame_in_playground2,profileFragment,"profileTAG");
        } else {
            PaymentFragment paymentFragment = new PaymentFragment();
            transaction.replace(R.id.frame_in_playground2,paymentFragment,"paymentTAG");
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onPaymentSuccess(String amount, Date date) {
        Log.e(TAG, "onPaymentSuccess:(PlaygroundActivity)--> "+date+", amount-->"+amount );
        NotificationsFragment notificationsFragment = new NotificationsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("amount", amount);
        bundle.putSerializable("date", date);

        notificationsFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_in_playground2, notificationsFragment, "notificationTAG")
                .addToBackStack(null)
                .commit();
    }


}