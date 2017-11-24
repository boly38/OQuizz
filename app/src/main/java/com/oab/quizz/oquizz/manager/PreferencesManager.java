package com.oab.quizz.oquizz.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesManager {
    private static final String PREFERENCES_FILE = "prefs";

    private static final String KEY_HIGHSCORE = "highscore";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_FILE , Context.MODE_PRIVATE);
    }

    public static long getHighScore(Context context) {
        return getSharedPreferences(context).getLong(KEY_HIGHSCORE, 0);
    }

    public static void updateHighScore(Context context, long score) {
        long old = getHighScore(context);
        if (old > score) {
            return;
        }
        getSharedPreferences(context)
                .edit()
                .putLong(KEY_HIGHSCORE, score)
                .apply();
    }
}
