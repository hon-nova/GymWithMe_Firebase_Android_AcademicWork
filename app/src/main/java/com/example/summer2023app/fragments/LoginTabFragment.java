package com.example.summer2023app.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.summer2023app.activities.MainActivity;
import com.example.summer2023app.databinding.FragmentLoginTabBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.summer2023app.models.User;

public class LoginTabFragment extends Fragment {

    FragmentLoginTabBinding binding;
    FirebaseAuth mAuth;
    final String TAG="LOGIN";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        binding = FragmentLoginTabBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.loginEmail.getText().toString();
                String password = binding.loginPassword.getText().toString();

                /* validate login inputs */
                if (email.equals("") || password.equals("")) {
                    Toast.makeText(getActivity(), "Please fill out required field(s). ", Toast.LENGTH_LONG).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getActivity(), "Enter a valid email", Toast.LENGTH_LONG).show();
                } else {
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                    Log.d(TAG, "onClick: BEFORE SIGNIN WITH AUTH");
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            try {
                                                String userId = user.getUid();
                                                DatabaseReference userRef = usersRef.child(userId);

                                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            Log.d(TAG, "onDataChange: (onDataChange snapshot)"+snapshot.getValue().toString());
                                                            User userData = snapshot.getValue(User.class);
                                                            if (userData != null) {
                                                                Log.d(TAG, "User: " + userData.getUsername());
                                                                Toast.makeText(getActivity(), "Successfully LOGGED IN", Toast.LENGTH_SHORT).show();
                                                                /* for admin route */
                                                                userRef.child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                                        String email = dataSnapshot.getValue(String.class);
                                                                        boolean isAdmin = email != null && email.equals("admin@admin.com");

                                                                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                                                        if (currentUser != null) {
                                                                            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users");

                                                                            // Get the reference to the user's isAdmin attribute in the Firebase Realtime Database
                                                                            DatabaseReference isAdminRef = userReference.child(userId).child("admin");

                                                                            // Update the isAdmin attribute to true
                                                                            isAdminRef.setValue(true)
                                                                                    .addOnCompleteListener(task -> {
                                                                                        if (task.isSuccessful()) {
                                                                                        } else {
                                                                                            // Handle the case where the update was unsuccessful
                                                                                            //Toast.makeText(getContext(), "Failed to update isAdmin.", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    });
                                                                        }
                                                                        // Store the admin status in SharedPreferences
                                                                        SharedPreferences preferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                                                        SharedPreferences.Editor editor = preferences.edit();
                                                                        editor.putBoolean("isAdmin", isAdmin);
                                                                        editor.apply();
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {}
                                                                });
                                                                /* end  admin route */
                                                                startActivity(new Intent(getActivity(), MainActivity.class));
                                                            } else {
                                                                Toast.makeText(getActivity(), "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            Toast.makeText(getActivity(), "No Such User", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(getActivity(), "Error retrieving user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {}
                                    } else {
                                        Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                   }
                            });
                }
            }
        });

        return v;
    }

}