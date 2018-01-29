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


package com.ppn.authandroid;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.ppn.authandroid.activities.LoginActivity;
import com.ppn.authandroid.models.User;


public class App extends Application {
    public static final String FONT = "fonts/roboto/Roboto-Light.ttf";
    private static final String TAG = "App";
    private static final String KEY_AUTH_USER = "auth.user";
    private static final String KEY_AUTH_TOKEN = "auth.token";

    // private static final String URL_HOST = "http://localhost:3000";
    private static final String URL_HOST = "http://10.0.2.2:3000"; // from android Emulator

    public static final String URL_SIGNUP = "/api/users/password-signup";
    public static final String URL_SIGNIN = "/api/users/password-signin";

    public static SharedPreferences sPreferences;
    private static Context sContext;
    private static User sAuthUser;
    private static String sAuthToken;


    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        sPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (!TextUtils.isEmpty(FONT)) {
            Utils.overrideFont(getApplicationContext(), "SERIF", FONT);
        }
    }


    public static User getAuthUser() {
        return getAuthUser(false);
    }


    public static User getAuthUser(boolean refresh) {
        if (sAuthUser == null || refresh) {
            String str = sPreferences.getString(App.KEY_AUTH_USER, null);
            if (!TextUtils.isEmpty(str)) {
                sAuthUser = new Gson().fromJson(str, User.class);
            }
        }
        return sAuthUser;
    }


    public static void saveUser(User user) {
        String str = new Gson().toJson(user, User.class);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Saving user data : " + str + ", size : " + str.length());
        }
        sPreferences.edit().putString(App.KEY_AUTH_USER, str).apply();
        sAuthUser = user;
        Log.i(TAG, "User saved!");
    }


    public static String getAuthToken() {
        return getAuthToken(false);
    }


    public static String getAuthToken(boolean refresh) {
        if (sAuthToken == null || refresh) {
            sAuthToken = sPreferences.getString(App.KEY_AUTH_TOKEN, null);
        }
        return sAuthToken;
    }


    public static void saveToken(String token) {
        sPreferences.edit().putString(App.KEY_AUTH_TOKEN, token).apply();
        sAuthToken = token;
        Log.i(TAG, "Token info saved!");
    }


    public static PowerManager.WakeLock acquireWakeLock() {
        PowerManager powerManager = (PowerManager) sContext.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "AppWakeLock");
        wakeLock.acquire();
        Log.i(TAG, "Partial wakelock acquired! by AppWakeLock!");

        // You must release weak lock after your job done, See below loadUserDetails() for usage.
        return wakeLock;
    }

    public static VolleyClient volley() {
        return VolleyClient.getInstance(sContext);
    }

    public static String getUrl(String path) {
        String url = URL_HOST + path;

        if (!TextUtils.isEmpty(getAuthToken())) {
            url += "?token=" + getAuthToken();
        }

        return url;
    }

    public static boolean isSignin() {
        if (!TextUtils.isEmpty(getAuthToken()) && getAuthUser() != null) {
            return true;
        }
        return false;
    }

    public static void signout() {
        sPreferences.edit().remove(KEY_AUTH_TOKEN).remove(KEY_AUTH_USER).apply();
        sAuthUser = null;
        sAuthToken = null;
        sContext.startActivity(new Intent(sContext, LoginActivity.class));
    }
}
