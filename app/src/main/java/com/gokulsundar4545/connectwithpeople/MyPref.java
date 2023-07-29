package com.gokulsundar4545.connectwithpeople;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.lang.ref.WeakReference;

public class MyPref {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static MyPref mInstance;

    @SuppressLint("CommitPrefEdits")
    private MyPref(Context context) {
        sharedPreferences = context.getSharedPreferences(
                "setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    /**
     * Get default instance of the class to keep it a singleton
     */
    public static MyPref getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyPref(context);
        }
        return mInstance;
    }

    public static final String TOKEN = "TOKEN";
    public static final String EMAIL = "EMAIL";



    public static void saveToPrefs(Context context, String key, Object value) {
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        if (contextWeakReference.get() != null) {
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(
                            contextWeakReference.get());
            final SharedPreferences.Editor editor = prefs.edit();
            if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof String) {
                editor.putString(key, value.toString());
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Long) {
                editor.putLong(key, (Long) value);
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Double) {
                editor.putLong(key, Double.doubleToRawLongBits((double) value));
            }
            editor.apply();
        }
    }

    public static Object getFromPrefs(Context context, String key, Object defaultValue) {
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        if (contextWeakReference.get() != null) {
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(
                            contextWeakReference.get());
            try {
                if (defaultValue instanceof String) {
                    return sharedPrefs.getString(key, defaultValue.toString());
                } else if (defaultValue instanceof Integer) {
                    return sharedPrefs.getInt(key, (Integer) defaultValue);
                } else if (defaultValue instanceof Boolean) {
                    return sharedPrefs.getBoolean(key, (Boolean) defaultValue);
                } else if (defaultValue instanceof Long) {
                    return sharedPrefs.getLong(key, (Long) defaultValue);
                } else if (defaultValue instanceof Float) {
                    return sharedPrefs.getFloat(key, (Float) defaultValue);
                } else if (defaultValue instanceof Double) {
                    return Double.longBitsToDouble(sharedPrefs.getLong(key, Double.doubleToLongBits((double) defaultValue)));
                }
            } catch (Exception e) {
                return defaultValue;
            }


        }
        return defaultValue;
    }

    public static void removeFromPrefs(Context context, String key) {
        try {
            WeakReference<Context> contextWeakReference = new WeakReference<>(context);
            if (contextWeakReference.get() != null) {
                SharedPreferences prefs =
                        PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get());
                final SharedPreferences.Editor editor = prefs.edit();
                editor.remove(key);
                editor.apply();
            }
        }catch (Exception ignored){

        }

    }

    public void saveToPref(String str) {
        editor.putBoolean("in_code", true);
        editor.putString("code", str);
        editor.apply();
    }

    public String getCode() {
        return sharedPreferences.getString("code","");
    }

    public Boolean getIn_Code() {
        return sharedPreferences.getBoolean("in_code", false);
    }
}
