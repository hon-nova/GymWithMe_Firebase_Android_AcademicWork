package com.example.summer2023app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.summer2023app.activities.LoginActivity;
import com.example.summer2023app.activities.MainActivity;
import com.example.summer2023app.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.summer2023app.models.User;

public class ProfileFragment extends Fragment {
    public static final String TAG = ProfileFragment.class.getSimpleName();

    FragmentProfileBinding binding;

    DatabaseReference userReference;
    int numberOfDays;
    int totalCaloriesBurned;
    int streakCounter = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid(); //wrQNpnpqZpYzKnrbIzA9Cs7JWlj2
            userReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists() && dataSnapshot.hasChild("numberOfDays") &&
                            dataSnapshot.hasChild("streakCounter")
                            && dataSnapshot.hasChild("totalCaloriesBurned")
                            && dataSnapshot.hasChild("email")
                            && dataSnapshot.hasChild("bmi")
                            && dataSnapshot.hasChild("workoutName")
                            && dataSnapshot.hasChild("streakCounter")
                            && dataSnapshot.hasChild("maxCaloriesDump")) {
                        numberOfDays = dataSnapshot.child("numberOfDays").getValue(Integer.class);
                        totalCaloriesBurned = dataSnapshot.child("totalCaloriesBurned").getValue((Integer.class));
                        String email = dataSnapshot.child("email").getValue(String.class);
                        double bmi = dataSnapshot.child("bmi").getValue(Double.class);
                        String name = dataSnapshot.child("workoutName").getValue(String.class);
                        streakCounter = dataSnapshot.child("streakCounter").getValue(Integer.class);
                        double maxCaloriesDump = dataSnapshot.child("maxCaloriesDump").getValue(Double.class);


                        binding.maxWorkoutSessions.setText("" + numberOfDays);
                        binding.caloriesBurnF.setText("" + totalCaloriesBurned);

                        binding.emailF.setText("" + email);
                        binding.bmiF.setText("" + bmi);
                        binding.activityNameF.setText("" + name);
                        binding.noSessionsSoFar.setText(("" + streakCounter));
                        binding.maxCaloriesDumped.setText("" + maxCaloriesDump);

                        int noSessionLeft = numberOfDays - streakCounter;
                        binding.numWorkoutSessionsLeft.setText("" + noSessionLeft);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        binding.btnViewProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = user.getUid();
                userReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            int streakCounter = user.getStreakCounter();
                            int numberOfDays = user.getNumberOfDays();
                            Log.d(TAG, "Streak Counter -->" + streakCounter);
                            Log.d(TAG, "Number of Days --> " + numberOfDays);

                            int progress = (int) Math.round(((double) streakCounter / (double) numberOfDays) * 100);

                            Log.e(TAG, "onDataChange: progress-->" + progress);

                            binding.progressBarProfile.setProgress(progress);
                            binding.progressBarProfile.setVisibility(View.VISIBLE);

                            // If the goal is achieved
                            if (streakCounter!=0 && numberOfDays!=0 && streakCounter == numberOfDays) {
                                // Load the animation
                                LottieAnimationView congratsAnimation = binding.congratsAnimation;
                                congratsAnimation.setVisibility(View.VISIBLE);
                                congratsAnimation.playAnimation();
                                new Handler().postDelayed(() -> congratsAnimation.setVisibility(View.GONE), 3000);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.w(TAG, "Failed to read value.", databaseError.toException());
                    }
                });
            }
        });
        binding.txtViewBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
        binding.txtViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
    }

}