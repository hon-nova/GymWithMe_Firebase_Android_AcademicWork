package com.example.summer2023app.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.summer2023app.models.CaloriesBurned;
import com.example.summer2023app.adapters.CaloriesRecyclerViewAdapter;
import com.example.summer2023app.R;
import com.example.summer2023app.utilities.VolleySingleton;
import com.example.summer2023app.databinding.FragmentFitnessFilterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FitnessFilterFragment extends Fragment {

    public static final String TAG=FitnessFilterFragment.class.getSimpleName();

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CaloriesRecyclerViewAdapter adapter;
    RequestQueue requestQueue;
    ArrayList<CaloriesBurned> caloriesList;
    FragmentFitnessFilterBinding binding;
    FragmentManager manager;
    CardFitnessFragment cardFitnessFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* get all REFERENCES of variables*/
        binding = FragmentFitnessFilterBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        recyclerView = rootView.findViewById(R.id.recycler_view_calories_burned);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        requestQueue = VolleySingleton.getInstance(getContext()).getRequestQueue();
        caloriesList = new ArrayList<>();
        manager = getActivity().getSupportFragmentManager();
        cardFitnessFragment = new CardFitnessFragment();

        //take inputs from FilterFragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            String name = bundle.getString("nameFilter", "");
            int duration = bundle.getInt("durationFilter", 0);
            int weight = bundle.getInt("weightFilter", 0);
            Log.e(TAG, "onCreateView():FitnessFilterFragment name->"+name+ ",duration --> "+duration+", weight -->"+weight );

            fetchFilterCaloriesAPI(weight, name, duration);
        }
        return rootView;
    }


    private void fetchFilterCaloriesAPI(int weight, String name, final int duration) {
        try {
            String encodedName = URLEncoder.encode(name, "UTF-8");

            String caloriesUrl = "https://calories-burned-by-api-ninjas.p.rapidapi.com/v1/caloriesburned?activity=" + encodedName + "&weight=" + weight + "&duration=" + duration;

            StringRequest caloriesRequest = new StringRequest(Request.Method.GET, caloriesUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String name = jsonObject.getString("name");
                                    int calories_per_hour = jsonObject.getInt("calories_per_hour");
                                    int duration = jsonObject.getInt("duration_minutes");
                                    int total_calories = jsonObject.getInt("total_calories");
                                    Log.e(TAG, "onResponse: (fetchDAta)"+calories_per_hour+"  duration-->"+duration+",total_calories-->"+total_calories );

                                    CaloriesBurned caloriesBurned = new CaloriesBurned(name, calories_per_hour, duration, total_calories);
                                    caloriesList.add(caloriesBurned);
                                }

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            adapter = new CaloriesRecyclerViewAdapter(getContext(), caloriesList, new CaloriesRecyclerViewAdapter.OnClickMe() {
                                @Override
                                public void clickMe(int position) {

                                    //Let's store the data into firebase
                                   // CaloriesBurned selectedActivity = caloriesList.get(position);

                                    // Create a Bundle to pass data to the CardFitnessFragment
                                    CaloriesBurned caloriesBurned = caloriesList.get(position);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("caloriesName", caloriesBurned.getName());
                                    bundle.putInt("caloriesDuration",caloriesBurned.getDuration_minutes());
                                    bundle.putInt("caloriesTotal",caloriesBurned.getTotal_calories());
                                    cardFitnessFragment.setArguments(bundle);
                                    Log.e(TAG, "clickMe: (clickMe)"+"name->"+ caloriesBurned.getName()+" getDuration-> "+caloriesBurned.getDuration_minutes()+", getTtCalo->"+caloriesBurned.getTotal_calories() );

                                    // Add CardFitnessFragment
                                    Log.i(TAG, "FragmentManager: " + manager);
                                    if (manager != null) {
                                        FragmentTransaction transaction = manager.beginTransaction();

                                        transaction.replace(R.id.frame_in_playground2, cardFitnessFragment,"cardFTag");
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    } else {
                                        Log.e(TAG, "FragmentManager is null");
                                    }


                                    FirebaseUser myUser = FirebaseAuth.getInstance().getCurrentUser();
                                }
                            });
                            recyclerView.setAdapter(adapter);
                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("CALL-API-ERROR", error.toString());
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("X-RapidAPI-Key", "58b02c42c2mshcdcdb63451ddf85p1cb089jsn6fc8ed02b302");
                    headers.put("X-RapidAPI-Host", "calories-burned-by-api-ninjas.p.rapidapi.com");
                    return headers;
                }
            };
            requestQueue.add(caloriesRequest);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        manager = getActivity().getSupportFragmentManager();
    }

}
