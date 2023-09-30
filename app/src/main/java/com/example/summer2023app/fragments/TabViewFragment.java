package com.example.summer2023app.fragments;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.summer2023app.utilities.RecyclerViewTouchHelper;
import com.example.summer2023app.adapters.ViewuserAdapter;
import com.example.summer2023app.databinding.FragmentTabViewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import com.example.summer2023app.interfaces.OnDialogCloseListener;
import com.example.summer2023app.models.User;


public class TabViewFragment extends Fragment implements OnDialogCloseListener {
    DatabaseReference userReference;
    ArrayList<User> userList;
    ArrayList<String> userIdList;
    ViewuserAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    FragmentTabViewBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTabViewBinding.inflate(inflater,container,false);

        layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerViewTab.setLayoutManager(layoutManager);

        adapter = new ViewuserAdapter(getContext(),userList,userIdList);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       usersRetrieval();
    }
    private void usersRetrieval() {
        userList = new ArrayList<>();
        userIdList = new ArrayList<>();
        // Retrieve user data
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child("users");
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userList.clear(); // clear old data
                    userIdList.clear(); // clear old data
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                        if (userSnapshot.hasChild("email") && userSnapshot.hasChild("username")) {
                            String uId = userSnapshot.getKey();
                            String email = userSnapshot.child("email").getValue(String.class);
                            String username = userSnapshot.child("username").getValue(String.class);
                            userList.add(new User(username, email));
                            userIdList.add(uId); // add user id to this list
                        }
                    }

                    adapter = new ViewuserAdapter(getContext(),userList,userIdList);
                    binding.recyclerViewTab.setAdapter(adapter);

                    //touchDialog
                    ItemTouchHelper itemTouchHelper= new ItemTouchHelper(new RecyclerViewTouchHelper(adapter,getContext(),TabViewFragment.this));
                    itemTouchHelper.attachToRecyclerView(binding.recyclerViewTab);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        usersRetrieval();
    }

    public interface OnFragmentInteractionListener{
        //Todo: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}