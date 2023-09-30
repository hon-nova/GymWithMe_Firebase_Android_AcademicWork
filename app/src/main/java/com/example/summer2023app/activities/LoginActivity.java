package com.example.summer2023app.activities;
import static android.service.controls.ControlsProviderService.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.summer2023app.R;
import com.example.summer2023app.adapters.LoginAdapter;
import com.example.summer2023app.databinding.ActivityLoginBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.DataSnapshot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.summer2023app.models.User;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    FirebaseAuth mAuth;
    /* Google SignIn starts */
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String myDeviceToken;
    static final int RC_SIGN_IN = 9001;

    @Override
    protected void onStart() {
        super.onStart();
        /* if the user already signed in, redirect to HomeFragment */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null){
            Log.d("LOGIN","Current user is not null");
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        setupTab();
        signInWithGoogle();
    }
    private void signInWithGoogle() {

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        binding.btnGoogle.setOnClickListener((View v) -> {
            Intent signInIntent = gsc.getSignInIntent();
            googleStartForResult.launch(signInIntent);
        });
    }
    ActivityResultLauncher<Intent> googleStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Log.d(TAG, "signInWithGoogle: == Code#:    "+result.getResultCode());
                    Intent data = result.getData();

                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                    handleSignInResult(task);
                } else {
                    Log.d(TAG, "NULL--- signInWithGoogle: ==Code#:    "+result.getResultCode());
                    return;
                }
            }
    );
    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        task.addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
            @Override
            public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                saveGoogleAccountToRealtimeDatabase(googleSignInAccount);
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to sign in" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    Log.d(TAG, "onActivityResult: Save Google Sign in in Firebase");
                    saveGoogleAccountToRealtimeDatabase(account);
                }
            } catch (ApiException e) {
                // Google Sign-In failed
                Log.w(TAG, "Google sign-in failed", e);
            }
        }
    }

    private void saveGoogleAccountToRealtimeDatabase(GoogleSignInAccount googleSignInAccount) {
        String email= googleSignInAccount.getEmail();
        String name = googleSignInAccount.getDisplayName();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    AuthCredential myGoogleCredentials = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(),null);
                    mAuth.signInWithCredential(myGoogleCredentials).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String userId = mAuth.getCurrentUser().getUid();
                                Log.e(TAG, "onComplete: userId-->"+userId);

                                usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            User currentUser = snapshot.getValue(User.class);
                                            // update the required fields
                                            currentUser.setUsername(name);
                                            currentUser.setEmail(email);
                                            //currentUser.setStreakCounter(currentUser.getStreakCounter());
                                            // save it back to the database
                                            usersRef.child(userId).setValue(currentUser);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                //Toast.makeText(getApplicationContext(),"Authentication failed."+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                } else {
                    Log.d(TAG, "onDataChange: Google account doesn't exist yet");
                    AuthCredential myGoogleCredentials = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(),null);

                    mAuth.signInWithCredential(myGoogleCredentials).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                String userId = mAuth.getCurrentUser().getUid();

                                User googleUser = new User(name,email,false,false,0,0,0,0,0,"",0);

                                usersRef.child(userId).setValue(googleUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //Toast.makeText(getApplicationContext(), "SAVED user to Firebase", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Toast.makeText(getApplicationContext(),"Failed to SAVE user to Firebase"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                //Toast.makeText(getApplicationContext(),"Authentication failed."+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(getApplicationContext(),"Database opeartion cancelled",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTab() {
        TabLayout tabLayout = binding.tabLayout;
        ViewPager2 viewPager2 =binding.viewPager2;

        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Register"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager2.setAdapter(new LoginAdapter(getSupportFragmentManager(),getLifecycle(),this));

        TabLayoutMediator tabLayoutMediator =new TabLayoutMediator(tabLayout,viewPager2,(tab, position) -> {
            switch (position){
                case 0: tab.setText("Login");break;
                case 1: tab.setText("Register");break;
            }
        });
        tabLayoutMediator.attach();
    }

}