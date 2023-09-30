package com.example.summer2023app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.summer2023app.activities.PlaygroundActivity;
import com.example.summer2023app.R;
import com.example.summer2023app.databinding.FragmentBmiBinding;
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


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.example.summer2023app.models.User;

public class BmiFragment extends Fragment {
    public static final String TAG=BmiFragment.class.getSimpleName();

    int age = 33; //default value
    double weight = 0;
    int height = 0;
    double bmi=0;

    FragmentBmiBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBmiBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        return v;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnCalcBMI.setOnClickListener((View v) -> {
            final String apiKeyValue = getString(R.string.key_value);
            final String apiHostValue = getString(R.string.host_value);

            String heightStr = binding.editTxtHeight.getText().toString();
            String weightStr = binding.editTxtWeight.getText().toString();
            String ageStr = binding.editTxtAge.getText().toString();

            if (!heightStr.isEmpty() && !weightStr.isEmpty()) {
                try {
                    height = Integer.parseInt(heightStr);
                    weight = Double.parseDouble(weightStr);
                    age = Integer.parseInt(ageStr);

                } catch (NumberFormatException e) {}
            } else {
                Toast.makeText(getContext(),"Please fill out all required fields.",Toast.LENGTH_SHORT).show();
            }
            String url = "https://fitness-calculator.p.rapidapi.com/bmi?age=" + age + "&weight=" + weight + "&height=" + height;

            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject responseJson = new JSONObject(response);
                                Log.d("API-BMI", responseJson.toString());
                                int statusCode = responseJson.getInt("status_code");

                                String requestResult = responseJson.getString("request_result");

                                if (statusCode == 200 && requestResult.equals("Successful")) {
                                    JSONObject data = responseJson.getJSONObject("data");
                                    bmi = data.getDouble("bmi");
                                    String health = data.getString("health");
                                    String healthyBmiRange = data.getString("healthy_bmi_range");

                                    binding.txtViewBmiRR.setText(String.valueOf(bmi));
                                    binding.txtViewHealthRR.setText(health);
                                    binding.txtViewBmiRangeRR.setText(healthyBmiRange);

                                   // saveBMIAndHeightToFireBase();
                                    //IMPORTANT 1
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {
                                        //IMPORTANT 2
                                        String userId = user.getUid(); //wrQNpnpqZpYzKnrbIzA9Cs7JWlj2
                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    // User already exists, update the user data
                                                    User currentUser = dataSnapshot.getValue(User.class);
                                                    assert currentUser != null;
                                                    currentUser.setHeight(height);
                                                    currentUser.setBmi(bmi);
                                                    userRef.setValue(currentUser)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        //Toast.makeText(getContext(), "UPDATED SUCCESS height, bmi", Toast.LENGTH_LONG).show();
                                                                        Log.d(TAG, "onComplete:\"UPDATED SUCCESS height, bmi\" ");

                                                                    } else {
                                                                        Exception exception = task.getException();
                                                                        //Toast.makeText(getContext(), "FAILED: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    //Toast.makeText(getContext(), "FAILED updating." + e.getMessage(), Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e(TAG, "onCancelled: FAILED" );
                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle errors here
                    Toast.makeText(getContext(), "Exceeding inputs. Try again!!!!", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    Log.d("API-HEAD", getString(R.string.rapidapi_key_name));
                    Log.d("API-HEAD", getString(R.string.rapidapi_host_name));
                    headers.put(getString(R.string.rapidapi_key_name), apiKeyValue);
                    headers.put(getString(R.string.rapidapi_host_name), apiHostValue);
                    return headers;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
            requestQueue.add(jsonObjectRequest);
        });
        binding.btnShowActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), PlaygroundActivity.class);
                startActivity(intent);
            }
        });
    }


}







