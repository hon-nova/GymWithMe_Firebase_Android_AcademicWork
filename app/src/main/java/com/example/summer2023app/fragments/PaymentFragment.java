package com.example.summer2023app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.summer2023app.databinding.FragmentPaymentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class PaymentFragment extends Fragment {

    public static final String TAG=PaymentFragment.class.getSimpleName();
    String publishableKey="pk_test_51NJYdrA8hitTZ8gp9nhQgSPKIGpt2SekyIlLm3R5q5tFYzQYhj1tSfe10vbiiu1AhTMKFUWYTNgvBrPtX6KIA1eD00o9KBmRqC";
    String secretKey = "sk_test_51NJYdrA8hitTZ8gphViHkY15YjFfvEwhQXXlnRt36AxDUjiXDC2me9tHYZumwR34Bj0j5ho6rjn39MF7XOxXrGJS009pe2hhWs";
    String customerId;
    String ephemeralKey;
    String clientSecret;
    PaymentSheet paymentSheet;
    FragmentPaymentBinding binding;
    int amount;
    OnPaymentSuccessListener listener;

    public PaymentFragment(){
        //constructor: default required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPaymentBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPaymentSuccessListener) {
            listener = (OnPaymentSuccessListener) context;
        } else {
            throw new RuntimeException(context.toString() + " OnPaymentSuccessListener");
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PaymentConfiguration.init(getContext(),publishableKey);
        paymentSheet = new PaymentSheet(this,paymentSheetResult -> {

            onPaymentResult(paymentSheetResult);
        });

        StringRequest request;
        request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);

                    customerId = object.getString("id");//"id": "cus_OK8ZfSOuztdK6N",
                    Toast.makeText(getContext(), "Starts Payment With Stripe", Toast.LENGTH_SHORT).show();

                    getEmphemeralKeys();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {  }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String,String> header = new HashMap<>();
                header.put("Authorization","Bearer "+secretKey);
                return header;
            }
        };//for Header part--> -u sk_test_51NJYdr...

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }
    private void paymentFlow() {
        paymentSheet.presentWithPaymentIntent(clientSecret,new PaymentSheet.Configuration("GymWithMe", new PaymentSheet.CustomerConfiguration(
                customerId,
                ephemeralKey
        )));
    }
    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {

        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(getContext(), "Payment received. A notification was sent to you!!", Toast.LENGTH_LONG).show();
            savePaymentDataToHost(String.valueOf(amount),new Date());
            listener.onPaymentSuccess(String.valueOf(amount), new Date());            // Updating subscribed status in Firebase
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null) {
                String userId = user.getUid();
                FirebaseDatabase.getInstance().getReference().child("users").child(userId)
                        .child("subscribed").setValue(true)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Subscription status successfully updated!");

                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to update subscription status", e);
                        });
            }

        }
    }

    private void getEmphemeralKeys() {
        StringRequest request;
        request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);

                    ephemeralKey = object.getString("id");// "id": "ephkey_1NXUsRA8hitTZ8gp1Wtk3u0m",
                    Log.e(TAG, "onResponse: "+ephemeralKey );
                    getClientSecret(customerId,ephemeralKey); // "id": "ephkey_1NXUUHA8hitTZ8gpoUW5jjDo"
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: "+error.getLocalizedMessage() );
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /**
                 *   -u sk_test_51NJYdrA8hitTZ8gphViHkY15Yj...
                 *   -H "Stripe-Version: 2022-11-15" \
                 *   -X "POST" \
                 *   -d "customer"="{{CUSTOMER_ID}}" \
                 */

                Map<String,String> header = new HashMap<>();
                header.put("Authorization","Bearer "+secretKey);
                header.put("Stripe-Version","2022-11-15");
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer",customerId);
                return params;
            }
        };//for Header part--> -u sk_test_51NJYdr...

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }

    //https://api.stripe.com/v1/payment_intents
    private void getClientSecret(String customerId, String ephemeralKey) {
        StringRequest request;
        request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    clientSecret = object.getString("client_secret");//"secret": "ek_test_YWNjdF8xTkpZZHJBOGhpdFRaOGdwLHF5cE1iMWtRbHBjWnduaFdRYlFxVEVqUGUwSVZpREk_00HaWPhtHT"

                    Log.e(TAG, "onResponse: "+clientSecret );
                    paymentFlow(); // "id": "ephkey_1NXUUHA8hitTZ8gpoUW5jjDo"


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: "+error.getLocalizedMessage() );
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String,String> header = new HashMap<>();
                header.put("Authorization","Bearer "+secretKey);
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                /**
                 *   -u sk_test_51NJYdrA8hitTZ8gphViHk...
                 *   -X "POST" \
                 *   -d "customer"="{{CUSTOMER_ID}}" \
                 *   -d "amount"=1099 \
                 *   -d "currency"="eur" \
                 *   -d "automatic_payment_methods[enabled]"=true \
                 */
                Map<String, String> params = new HashMap<>();
                params.put("customer",customerId);
                amount = 10;
                params.put("amount",String.valueOf(""+amount)+"00");
                params.put("currency","cad");
                params.put("automatic_payment_methods[enabled]","true");
                return params;
            }
        };//for Header part--> -u sk_test_51NJYdr...

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }

    public void savePaymentDataToHost(String amount, Date date) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("PaymentData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("amount", amount);
        editor.putLong("date", date.getTime()); // convert Date to long
        editor.apply();
    }

    public interface OnPaymentSuccessListener {
        void onPaymentSuccess(String amount, Date date);
    }

}