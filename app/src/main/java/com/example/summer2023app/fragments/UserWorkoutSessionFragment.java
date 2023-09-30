package com.example.summer2023app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.airbnb.lottie.LottieAnimationView;
import com.example.summer2023app.activities.MainActivity;
import com.example.summer2023app.R;
import com.example.summer2023app.databinding.FragmentUserWorkoutSessionBinding;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.summer2023app.models.User;

public class UserWorkoutSessionFragment extends Fragment {

    public static final String TAG=UserWorkoutSessionFragment.class.getSimpleName();
    FragmentUserWorkoutSessionBinding binding;
    CardView cardView2,cardView3;
    Handler handler = new Handler(Looper.getMainLooper());
    ExecutorService executorService;
    //int streakCounter =0;
    DatabaseReference dbRef;
    DatabaseReference userRef;

    FragmentManager manager;
    OnButtonContinueProfilePaymentClick mListener;

    public UserWorkoutSessionFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnButtonContinueProfilePaymentClick) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " OnButtonContinueProfilePaymentClick");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentUserWorkoutSessionBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        manager =getParentFragmentManager();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cardView2 = getView().findViewById(R.id.card_view2);
        cardView3 = getView().findViewById(R.id.card_view3);

        executorService = Executors.newSingleThreadExecutor();

        binding.btnYesReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView2.setVisibility(View.VISIBLE);
            }
        });

        dbRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        String userId = userF.getUid();
        Log.e(TAG, "onViewCreated: Current User ID: --> "+userId );
        binding.btnGoWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                runProgressBar();
                FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
                if (userF != null) {
                    //IMPORTANT 2
                    String userId = userF.getUid();
                    userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                User currentUser = dataSnapshot.getValue(User.class);
                                assert currentUser != null;

                                //condition to reset BMI
                                currentUser.setStreakCounter(currentUser.getStreakCounter()+1);

                                Log.e(TAG, "onDataChange: currentUserStreakCounter-->"+currentUser.getStreakCounter() );

                                if ((currentUser.getStreakCounter()!=0 && currentUser.getNumberOfDays()!=0 && currentUser.getStreakCounter() > currentUser.getNumberOfDays())){

                                    Log.d(TAG, "Inside condition. streakCounter: " + currentUser.getStreakCounter() + ", numberOfDays: " + currentUser.getNumberOfDays());

                                    //Toast.makeText(getContext(),"This goal is already achieved. Please reset your BMI and follow further instructions.",Toast.LENGTH_LONG).show();

                                    Log.d(TAG, "btnContinue visibility before setting: " + binding.btnContinue.getVisibility());
                                    binding.progressBar2.setVisibility(View.INVISIBLE);
                                    binding.btnContinue.setVisibility(View.INVISIBLE);

                                    Log.d(TAG, "btnContinue visibility after setting: " + binding.btnContinue.getVisibility());

                                    cardView3.setVisibility(View.VISIBLE);
                                }

                                userRef.setValue(currentUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    Log.e(TAG, "onComplete: streakCounter-->"+(currentUser.getStreakCounter()) );
                                                } else {
                                                    Exception exception = task.getException();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (error != null) {
                            Log.e(TAG, "streakCounter update failed");
                            Log.e(TAG, "Save operation failed: " + error.getMessage(), error.toException());
                        } else {
                            Log.e(TAG, "streakCounter update succeeded.");
                        }
                    }
                });
            }
        }
    });



        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onContinueProfilePaymentClick();
            }
        });

        binding.btnResetBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        binding.imgBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        // Retrieve user data
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists() && dataSnapshot.hasChild("username") ) {
                        String username = dataSnapshot.child("username").getValue(String.class);
                        binding.txtViewHelloUsername.setText("Hello " +username+",");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    private void runProgressBar() {

        binding.progressBar2.setVisibility(View.VISIBLE);
        String durationE = binding.editTxtSetDuration.getText().toString();
        try {
            //accept a double value for EditText user input, e.g.:0.1 (minute)
            double durationInMinutes = Double.parseDouble(durationE);
            int durationInMilliseconds =(int)(durationInMinutes*60*1000);

            int sleepTime = (int)(durationInMilliseconds / 100);

            executorService.execute(() -> {

                int progress = 0;

                while (progress <= 100) {
                    try {
                        Thread.sleep(sleepTime);
                        progress++;

                        publishProgress(progress);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.post(() -> {
                    LottieAnimationView congratsAnimation = binding.congratsAnimation;
                    congratsAnimation.setVisibility(View.VISIBLE);
                    congratsAnimation.playAnimation();

                });
                handler.postDelayed(() -> {
                        LottieAnimationView congratsAnimation = binding.congratsAnimation;
                        congratsAnimation.cancelAnimation();
                        congratsAnimation.setVisibility(View.INVISIBLE);

                },3000);
                handler.post(() -> {
                    binding.btnGoWorkout.setVisibility(View.INVISIBLE);

                    //condition to show btnContinue if goal achieved already
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                User currentUser = dataSnapshot.getValue(User.class);
                                assert currentUser != null;

                                if (currentUser.getStreakCounter() <= currentUser.getNumberOfDays()){
                                    binding.btnContinue.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });

                });

            });
        } catch(Exception ex){}

    }
    private void publishProgress(int progress) {
        handler.post(()->{
            binding.progressBar2.setProgress(progress);
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
    public interface OnButtonContinueProfilePaymentClick{
        public void onContinueProfilePaymentClick();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}