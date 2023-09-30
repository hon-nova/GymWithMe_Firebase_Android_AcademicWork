    package com.example.summer2023app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.summer2023app.activities.PlaygroundActivity;
import com.example.summer2023app.R;
import com.example.summer2023app.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

    public class HomeFragment extends Fragment {

    public static final String TAG=HomeFragment.class.getSimpleName();
    FragmentHomeBinding binding;
    CardView bmi_framelayout,bmi_start;
    FirebaseUser currentUser;

    private boolean isBmiFragmentVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        bmi_framelayout = view.findViewById(R.id.bmi_framelayout);
        bmi_start =view.findViewById((R.id.bmi_start));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        binding.txtViewKnowBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isBmiFragmentVisible) {
                    bmi_framelayout.setVisibility(View.VISIBLE);
                    openBMIFragment();
                    isBmiFragmentVisible = true;
                } else {
                    Fragment bmiFragment = getChildFragmentManager().findFragmentById(R.id.bmi_framelayout);
                    if (bmiFragment != null) {
                        getChildFragmentManager().beginTransaction().remove(bmiFragment).commit();
                    }
                    bmi_framelayout.setVisibility(View.INVISIBLE);
                    isBmiFragmentVisible = false;
                    bmi_start.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.txtViewStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uid = currentUser.getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userRef = database.getReference("users").child(uid);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Check if the dataSnapshot exists and contains the required attributes
                        if (dataSnapshot.exists() && dataSnapshot.hasChild("subscribed")) {
                            boolean subscribed = dataSnapshot.child("subscribed").getValue(Boolean.class);

                            if (subscribed) {
                                Log.e(TAG, "onDataChange: subscribed or not-->"+subscribed );
                                Intent intent = new Intent(getActivity(), PlaygroundActivity.class);
                                intent.putExtra("openWorkoutSession", true);
                                startActivity(intent);

                            } else {
                                Toast.makeText(getContext(), "Please check BMI first to access this feature", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("Database Error", databaseError.getMessage());
                    }
                });
            }
        });
        return view;
    }
    private void openBMIFragment() {

        BmiFragment bmiFragment = new BmiFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.bmi_framelayout, bmiFragment);
        fragmentTransaction.commit();
    }

}