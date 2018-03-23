package com.frannyzhao.mqttlib.utils.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.frannyzhao.mqttlib.utils.MLog;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by zhaofengyi on 2017/10/11.
 */

class SharePreferenceBase {
    protected String spName;

    public SharePreferenceBase(String spName) {
        this.spName = spName;
    }
    public boolean getBoolean(Context context, String prefKey,
            boolean defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        return getSharedPreferences(context).getBoolean(prefKey, defaultValue);
    }

    public float getFloat(Context context, String prefKey,
            float defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        return getSharedPreferences(context).getFloat(prefKey, defaultValue);
    }

    public int getInt(Context context, String prefKey, int defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        return getSharedPreferences(context).getInt(prefKey, defaultValue);
    }

    public long getLong(Context context, String prefKey, long defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        return getSharedPreferences(context).getLong(prefKey, defaultValue);
    }

    public String getString(Context context, String prefKey,
            String defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        return getSharedPreferences(context).getString(prefKey, defaultValue);
    }

    public Set<String> getStringSet(Context context, String prefKey,
            Set<String> defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        return getSharedPreferences(context).getStringSet(prefKey, defaultValue);
    }

    public LinkedHashSet<String> getStringLinkSet(Context context,
            String prefKey,
            LinkedHashSet<String> defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        return (LinkedHashSet<String>) getSharedPreferences(context).getStringSet(prefKey,
                defaultValue);
    }

    public void putBoolean(Context context, String prefKey, boolean value) {
        if (context == null) {
            return;
        }
        getSharedPreferences(context).edit().putBoolean(prefKey, value).apply();
    }

    public void putFloat(Context context, String prefKey, float value) {
        if (context == null) {
            return;
        }
        getSharedPreferences(context).edit().putFloat(prefKey, value).apply();
    }

    public void putInt(Context context, String prefKey, int value) {
        if (context == null) {
            return;
        }
        getSharedPreferences(context).edit().putInt(prefKey, value).apply();
    }

    public void putLong(Context context, String prefKey, long value) {
        if (context == null) {
            return;
        }
        getSharedPreferences(context).edit().putLong(prefKey, value).apply();
    }

    public void putString(Context context, String prefKey, String value) {
        if (context == null) {
            return;
        }
        if (value == null) {
            value = "";
        }
        if (MLog.isDebug() && !TextUtils.isEmpty(value) && value.length() > 256) {
            throw new IllegalArgumentException("the length of value exceed max limit: 256");
        }
        getSharedPreferences(context).edit().putString(prefKey, value).apply();
    }

    public void putStringSet(Context context, String prefKey,
            Set<String> value) {
        if (context == null) {
            return;
        }
        for (String str : value) {
            if (MLog.isDebug() && !TextUtils.isEmpty(str) && str.length() > 256) {
                throw new IllegalArgumentException(
                        "the length of value in set exceed max limit: 256");
            }
        }
        getSharedPreferences(context).edit().putStringSet(prefKey, value).apply();
    }


    public void remove(Context context, String prefKey) {
        if (context == null) {
            return;
        }
        getSharedPreferences(context).edit().remove(prefKey).apply();
    }

    public SharedPreferences getSharedPreferences(String spName, Context context) {
        return getSharedPreferences(context, spName);
    }

    public SharedPreferences getSharedPreferences(Context context) {
        return getSharedPreferences(context, spName);
    }

    public SharedPreferences getSharedPreferences(Context context, String prefName) {
        if (TextUtils.isEmpty(prefName)) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            return context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        }
    }

    public SharedPreferences.Editor getEditor(String spName, Context context) {
        return getSharedPreferences(context, spName).edit();
    }
}
