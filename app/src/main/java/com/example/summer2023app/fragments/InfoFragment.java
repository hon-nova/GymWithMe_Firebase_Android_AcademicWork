package com.example.summer2023app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.summer2023app.databinding.FragmentInfoBinding;


public class InfoFragment extends Fragment {
    public static final String TAG = InfoFragment.class.getSimpleName();
    FragmentInfoBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentInfoBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String point1 ="1. Privacy and Data Protection:";
        String point2 ="2. Unsubscribe from Service:";
        String point3 ="3. Account Deletion:";

        String point11="At GymWithMe, we respect our customers\' privacy and comply with all relevant government regulations. Your private information will be treated as strictly confidential, and we will not share any client\'s information with unauthorized parties";
        String point22="If you no longer wish to use our services, please call 604-555-5555 to unsubscribe. Once your request is documented and confirmed, we will proceed to unsubscribe you, and you will receive a notification confirming your removal from our services.";
        String point33="If you desire to permanently delete your account, please contact our support team at administration@support.gymwithme.ca, and we will take necessary steps to delete your account and associated data.";

        binding.point1.setText(point1);
        binding.point2.setText(point2);
        binding.point3.setText(point3);
        binding.point11.setText(point11);
        binding.point22.setText(point22);
        binding.point33.setText(point33);

    }
}


