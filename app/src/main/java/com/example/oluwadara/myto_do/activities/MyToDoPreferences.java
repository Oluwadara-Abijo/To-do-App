package com.example.oluwadara.myto_do.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.oluwadara.myto_do.R;

class MyToDoPreferences {

    public static void setPreferredTheme(Context context) {
        int lightTheme = R.style.AppTheme;
        int nightModeTheme = R.style.NightModeTheme;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isNightMode = sharedPreferences.getBoolean(context.getString(R.string.pref_night_mode_key),
                context.getResources().getBoolean(R.bool.pref_night_mode_default_value));
        if (isNightMode) {
            context.setTheme(nightModeTheme);
        } else {
            context.setTheme(lightTheme);
        }
    }

}
