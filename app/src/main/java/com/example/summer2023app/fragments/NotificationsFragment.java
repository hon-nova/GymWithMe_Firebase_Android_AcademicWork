package com.example.summer2023app.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import com.example.summer2023app.databinding.FragmentNotificationsBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import java.util.Date;

public class NotificationsFragment extends Fragment {
    DatabaseReference userReference;
    FirebaseUser user;
    public static final String TAG=NotificationsFragment.class.getSimpleName();
    FragmentNotificationsBinding binding;
    String amount;
    Date date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        SharedPreferences sharedPref = getActivity().getSharedPreferences("PaymentData", Context.MODE_PRIVATE);
        amount = sharedPref.getString("amount", "");
        long dateLong = sharedPref.getLong("date", 0);
        date = new Date(dateLong);

        String outputStr ="Total amount paid: $CAD "+amount +"\nOn Date: "+date;
        binding.notifications.setText(outputStr);

        return view;
    }

}