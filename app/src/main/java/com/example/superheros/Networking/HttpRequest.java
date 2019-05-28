package com.example.superheros.Networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class HttpRequest {

    private Context context;
    private String url;
    private OnHttpCompleteListener onHttpCompleteListener;

    public HttpRequest(Context context, String url, OnHttpCompleteListener onHttpCompleteListener) {
        this.context = context;
        this.url = url;
        this.onHttpCompleteListener = onHttpCompleteListener;

    }

    public void request(){
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null){
                    onHttpCompleteListener.onComplete(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onHttpCompleteListener.onError(error.getMessage());
            }
        });
        queue.add(stringRequest);
    }


    public interface OnHttpCompleteListener{
        void onComplete(String response);
        void onError(String error);
    }
}
