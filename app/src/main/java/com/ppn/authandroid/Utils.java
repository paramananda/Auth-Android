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


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


public class Utils {
    private static final String TAG = "Utils";

    public static String validatePhoneNumber(String number) {
        if (!TextUtils.isEmpty(number)) {
            PhoneNumberUtils.isGlobalPhoneNumber(number);
        }
        return null;
    }

    public static String normalizePhoneNumber(Context context, String phone) {
        if (TextUtils.isEmpty(phone)) {
            return null;
        }

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = manager.getNetworkCountryIso();
        PhoneNumberUtil.PhoneNumberType phoneType = PhoneNumberUtil.PhoneNumberType.MOBILE;
        boolean isValid = false;
        String normalize = "";
        if (TextUtils.isEmpty(countryCode) || BuildConfig.DEBUG) {
            countryCode = "IN";
        }
        try {
            Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(phone, countryCode);   //with default country
            isValid = PhoneNumberUtil.getInstance().isValidNumber(phoneNumber) & PhoneNumberUtils.isGlobalPhoneNumber(phone);
            normalize = PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        if (isValid && !TextUtils.isEmpty(normalize)) {
            return normalize;
        }
        return null;
    }


    public static boolean isValidPhone(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public static String readStringFromRaw(Context context, int id) {
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(id);
            byte[] buffer = new byte[is.available()];
            while (is.read(buffer) != -1) ;
            return new String(buffer, "utf-8");
        } catch (Exception e) {
            Log.e(TAG, "Error on readStringFromAsset : " + e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }


    public static void shareApp(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + context.getPackageName());
        context.startActivity(Intent.createChooser(intent, "Share via"));
    }

    public static void rateThisApp(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static void searchMoreApps(Context context, String query) {
        Uri uri = Uri.parse("market://search?q=" + query);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/search?c=apps&q=" + query)));
        }
    }

    public static String formatTimeDate(long millis) {
        Date date = new Date(millis);
        DateFormat formatter = new SimpleDateFormat("MM/d/yy  hh:mm a");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    public static String formatTimeDateForFile(long millis) {
        Date date = new Date(millis);
        DateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {

        final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Map<String, Typeface> newMap = new HashMap<String, Typeface>();
            newMap.put("serif", customFontTypeface);
            try {
                final Field staticField = Typeface.class
                        .getDeclaredField("sSystemFontMap");
                staticField.setAccessible(true);
                staticField.set(null, newMap);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
                defaultFontTypefaceField.setAccessible(true);
                defaultFontTypefaceField.set(null, customFontTypeface);
            } catch (Exception e) {
                //Log.e(TypefaceUtil.class.getSimpleName(), "Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
            }
        }
    }

    public static CharSequence getRelativeTime(long millis) {
        Date date = new Date(millis);
        long now = System.currentTimeMillis();
        if (Math.abs(now - date.getTime()) > 60000)
            return DateUtils.getRelativeTimeSpanString(date.getTime(), now,
                    DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                            | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_ABBREV_RELATIVE);
        else
            return "Just now";
    }


    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public static byte[] createThumbnail(File file, int width /*171*/, int height /*128*/) {
        Bitmap bmp = null;
        try {
            bmp = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeStream(new FileInputStream((File) file)), width, height);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bmp != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 90, stream);
            return stream.toByteArray();
        }
        return null;
    }

    public static long getTimeRollByMonth(int month) {
        GregorianCalendar cal = new GregorianCalendar(); // See note below.
        cal.setLenient(false);
        cal.roll(Calendar.MONTH, month);
        Log.i(TAG, "getTimeRollByMonth : " + cal.getTime());
        return cal.getTimeInMillis();
    }

    public static String ensureCamelCase(String str) {
        str = str.replaceAll("_", " ");
        char[] title = str.toCharArray();
        for (int i = 0; i < title.length; i++) {
            title[0] = Character.toUpperCase(title[0]);
            if (title[i] == ' ') {
                title[i + 1] = Character.toUpperCase(title[i + 1]);
            }
        }

        return new String(title);
    }

}
