package com.example.summer2023app.fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.summer2023app.databinding.FragmentCardFitnessBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.summer2023app.models.User;


public class CardFitnessFragment extends Fragment {

    FragmentCardFitnessBinding binding;
    FragmentManager manager;
    OnButtonInCardFitnessClicked mListener;
    FirebaseAuth mAuth;
    DatabaseReference rootIpAddressUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCardFitnessBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        manager = getActivity().getSupportFragmentManager();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //take inputs from BmiFragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            /* for click action */
            String name = bundle.getString("caloriesName","");
            int duration = bundle.getInt("caloriesDuration",0);
            int total_calories = bundle.getInt("caloriesTotal",0);

            String nameS= "Activity: "+name;
            String durationS ="Duration: "+duration+ " (minutes)";
            String totalS= "Total Calories Burned: "+total_calories;
            binding.activity.setText(nameS);
            binding.duration.setText(durationS);
            binding.totalCalories.setText(totalS);
            /* Firebase */
            // Retrieve user data
            FirebaseUser userF = mAuth.getInstance().getCurrentUser();
            if (userF != null) {
                //IMPORTANT 2
                String userId = userF.getUid();
                rootIpAddressUser = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                rootIpAddressUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists() && dataSnapshot.hasChild("numberOfDays")) {
                            double bmi = dataSnapshot.child("bmi").getValue(Double.class);
                            int height = dataSnapshot.child("height").getValue(Integer.class);
                            Log.e(TAG, "onDataChange: bmi= "+bmi+", height=  "+height );

                            double BMIExtra = bmi - 24.9;
                            double numKgExtra = Math.round(BMIExtra* Math.pow(height/100,2));
                            double numCalosExtra = numKgExtra * 7700;
                            int numberOfDays =(int) (numCalosExtra/total_calories);

                            /* Firebase: save numberOfDays */

                            User currentUser = dataSnapshot.getValue(User.class);
                            currentUser.setNumberOfDays(numberOfDays);
                            currentUser.setTotalCaloriesBurned(total_calories);
                            currentUser.setWorkoutName(name);
                            currentUser.setMaxCaloriesDump(numCalosExtra);
                            currentUser.setStreakCounter(0);
                            //IMPORTANT
                            rootIpAddressUser.setValue(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //Toast.makeText(getContext(), "updated numCalosExtra", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onComplete: SUCCESS");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "onFailure: FAILED" );
                                }
                            });

                            Log.e(TAG, "onDataChange: (numberOfDays)-->"+numberOfDays );
                            String outputString = String.format("%.1f (kgs)(or %.1f (calories))", BMIExtra, numCalosExtra);
                            outputString +=" .It can take approximately "+numberOfDays+ " workout sessions consecutively";

                            Log.e(TAG, "onViewCreated: outputString --->  "+ outputString );
                            binding.txtViewKgBurned.setText(outputString);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }

            /* move to UserWorkoutSessionFragment */
            binding.btnSaveAndSubscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onCardFitnessClicked();
                }
            });
            binding.btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getParentFragmentManager().popBackStack();
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnButtonInCardFitnessClicked) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnButtonInCardFitnessClicked");
        }
    }

    public interface OnButtonInCardFitnessClicked {
        void onCardFitnessClicked();
    }


}