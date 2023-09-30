package com.example.summer2023app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.summer2023app.databinding.FragmentRegisterTabBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.summer2023app.models.User;

public class RegisterTabFragment extends Fragment {

    FragmentRegisterTabBinding binding;
    FirebaseAuth mAuth;

    FirebaseDatabase db;
    DatabaseReference usersRef;
    final String TAG="GYM_WITH_ME";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "onCreateView: Hello ");
        // Inflate the layout for this fragment
        binding =FragmentRegisterTabBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: REGISTER");
        mAuth = FirebaseAuth.getInstance();

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.signupUsername.getText().toString().trim();
                String email = binding.signupEmail.getText().toString();
                String password = binding.signupPassword.getText().toString();
                String confirmPassword = binding.signupConfirmPassword.getText().toString();

                /* validate inputs */
                    try {
                        if (email.equals("") || password.equals("") || username.equals("")) {
                            Toast.makeText(getContext(), "** Please fill out the required fields **.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Toast.makeText(getContext(), "Please provide a valid email.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (!password.equals(confirmPassword)) {
                            Toast.makeText(getContext(), "Passwords don't match. Try again,", Toast.LENGTH_LONG).show();
                        }
                        Log.d(TAG, "onClick: " + username + "" + email + "   " + password);

                    } catch(Exception ex){
                        ex.printStackTrace();
                        Toast.makeText(getContext(),"No mistake",Toast.LENGTH_SHORT).show();
                    }

                    // Storing the user info (User email and UUID) into Firebase AUTHENTICATION
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "SUSSESSFULLY created account", Toast.LENGTH_LONG).show();
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        String userId = firebaseUser.getUid();

                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

                                        User user = new User(username, email);

                                        userRef.setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.d(TAG, "User saved successfully");
                                                        Toast.makeText(getContext(), "SUSSESSFULLY created account", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e(TAG, "Failed to save user: " + e.getMessage());
                                                    }
                                                });
                                    }
                                }
                                else {
                                    Toast.makeText(getContext(), "FAiled SAVED Firebase: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(e -> {
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getContext(), "The email address is already in use by another account.", Toast.LENGTH_SHORT).show();
                            } else {}
                        });
            };//end onClick
        });//end binding
    }
}









