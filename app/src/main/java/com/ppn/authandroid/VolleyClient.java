package com.ppn.authandroid;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyClient {
    private static VolleyClient mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private VolleyClient(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();


    }

    public static synchronized VolleyClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyClient(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void request(Request<T> req) {
        getRequestQueue().add(req);
    }
}