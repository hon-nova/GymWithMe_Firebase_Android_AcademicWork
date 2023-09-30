package com.example.summer2023app.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.summer2023app.R;

public class TabInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_info, container, false);
    }
    public interface OnFragmentInteractionListener{
        //Todo: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}