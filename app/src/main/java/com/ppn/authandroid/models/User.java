/*
 * The MIT License
 *
 * Copyright (c) 2017-2018 Paramananda Pradhan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.ppn.authandroid.models;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.ppn.authandroid.App;
import com.ppn.authandroid.BuildConfig;
import com.ppn.authandroid.Promise;

import org.json.JSONException;
import org.json.JSONObject;


public class User {
    private static final String TAG = "User";
    public String _id;
    public String name;
    public String firstName;
    public String lastName;
    public String address;
    public String email;
    public String phone;
    public String password;
    public String about;
    public String photo;
    public String createdAt;
    public String updatedAt;
    public boolean isAdmin;


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


    public Promise signup() {
        Promise p = new Promise();
        JSONObject data = null;
        try {
            data = new JSONObject(new Gson().toJson(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (data != null) {
            JsonObjectRequest request = new JsonObjectRequest
                    (Request.Method.POST, App.getUrl(App.URL_SIGNUP),
                            data, response -> {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "Response : " + response.toString());
                        }

                        JSONObject obj = response.optJSONObject("data");
                        Log.i(TAG, "Message : " + response.optString("message"));

                        if (obj != null) {
                            User user = new Gson().fromJson(obj.toString(), User.class);
                            p.resolve(user);
                        } else {
                            p.reject("Empty response!");
                        }
                    }, p::reject);

            App.volley().request(request);
        } else {
            p.reject("Invalid request body!");
        }

        return p;
    }

    public Promise signin() {
        Promise p = new Promise();
        JSONObject data = null;
        try {
            data = new JSONObject(new Gson().toJson(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (data != null) {
            JsonObjectRequest request = new JsonObjectRequest
                    (Request.Method.POST, App.getUrl(App.URL_SIGNIN),
                            data, response -> {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "Response : " + response.toString());
                        }
                        User user = null;

                        JSONObject obj = response.optJSONObject("data");
                        String token = response.optString("token");
                        Log.i(TAG, "Message : " + response.optString("message"));

                        if (obj != null) {
                            user = new Gson().fromJson(obj.toString(), User.class);
                            p.resolve(user);
                        }

                        if (user != null && !TextUtils.isEmpty(token)) {
                            App.saveToken(token);
                            App.saveUser(user);
                            Log.i(TAG, "Login success!");
                        }else{
                          p.reject("Login failed! Some internal Error, Try again.");
                        }

                    }, p::reject);

            App.volley().request(request);
        } else {
            p.reject("Invalid request body!");
        }

        return p;
    }
}
