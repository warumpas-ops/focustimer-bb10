package com.focustimer.app;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PreferencesManager {

    private static final String PREF_NAME = "focustimer_prefs";
    private static final String KEY_DARK_THEME = "dark_theme";
    private static final String KEY_FOCUS_MINUTES = "focus_minutes";
    private static final String KEY_SHORT_BREAK_MINUTES = "short_break_minutes";
    private static final String KEY_LONG_BREAK_MINUTES = "long_break_minutes";
    private static final String KEY_LONG_BREAK_INTERVAL = "long_break_interval";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final String KEY_VIBRATE_ENABLED = "vibrate_enabled";
    private static final String KEY_AUTO_START_BREAKS = "auto_start_breaks";

    private static final String KEY_STATS_DATE = "stats_date";
    private static final String KEY_STATS_TODAY_COUNT = "stats_today_count";
    private static final String KEY_STATS_TODAY_MINUTES = "stats_today_minutes";
    private static final String KEY_STATS_TODAY_SECONDS = "stats_today_seconds";
    private static final String KEY_STATS_TOTAL_COUNT = "stats_total_count";
    private static final String KEY_STATS_TOTAL_MINUTES = "stats_total_minutes";
    private static final String KEY_STATS_TOTAL_SECONDS = "stats_total_seconds";

    private final SharedPreferences prefs;

    public PreferencesManager(Context ctx) {
        this.prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        migrateLegacyMinutesIfNeeded();
        checkDayReset();
    }

    private void migrateLegacyMinutesIfNeeded() {
        if (!prefs.contains(KEY_STATS_TOTAL_SECONDS) && prefs.contains(KEY_STATS_TOTAL_MINUTES)) {
            int oldTotalMin = prefs.getInt(KEY_STATS_TOTAL_MINUTES, 0);
            int oldTodayMin = prefs.getInt(KEY_STATS_TODAY_MINUTES, 0);
            prefs.edit()
                .putInt(KEY_STATS_TOTAL_SECONDS, oldTotalMin * 60)
                .putInt(KEY_STATS_TODAY_SECONDS, oldTodayMin * 60)
                .commit();
        }
    }

    public boolean isDarkTheme() {
        return prefs.getBoolean(KEY_DARK_THEME, true);
    }

    public void setDarkTheme(boolean v) {
        prefs.edit().putBoolean(KEY_DARK_THEME, v).commit();
    }

    public int getFocusMinutes() {
        return prefs.getInt(KEY_FOCUS_MINUTES, 25);
    }

    public void setFocusMinutes(int v) {
        prefs.edit().putInt(KEY_FOCUS_MINUTES, Math.max(1, v)).commit();
    }

    public int getShortBreakMinutes() {
        return prefs.getInt(KEY_SHORT_BREAK_MINUTES, 5);
    }

    public void setShortBreakMinutes(int v) {
        prefs.edit().putInt(KEY_SHORT_BREAK_MINUTES, Math.max(1, v)).commit();
    }

    public int getLongBreakMinutes() {
        return prefs.getInt(KEY_LONG_BREAK_MINUTES, 15);
    }

    public void setLongBreakMinutes(int v) {
        prefs.edit().putInt(KEY_LONG_BREAK_MINUTES, Math.max(1, v)).commit();
    }

    public int getLongBreakInterval() {
        return prefs.getInt(KEY_LONG_BREAK_INTERVAL, 4);
    }

    public void setLongBreakInterval(int v) {
        prefs.edit().putInt(KEY_LONG_BREAK_INTERVAL, Math.max(1, v)).commit();
    }

    public boolean isSoundEnabled() {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true);
    }

    public void setSoundEnabled(boolean v) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, v).commit();
    }

    public boolean isVibrateEnabled() {
        return prefs.getBoolean(KEY_VIBRATE_ENABLED, true);
    }

    public void setVibrateEnabled(boolean v) {
        prefs.edit().putBoolean(KEY_VIBRATE_ENABLED, v).commit();
    }

    public boolean isAutoStartBreaks() {
        return prefs.getBoolean(KEY_AUTO_START_BREAKS, false);
    }

    public void setAutoStartBreaks(boolean v) {
        prefs.edit().putBoolean(KEY_AUTO_START_BREAKS, v).commit();
    }

    // --- Statistics ---

    private String getTodayDateString() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    private void checkDayReset() {
        String today = getTodayDateString();
        String savedDate = prefs.getString(KEY_STATS_DATE, "");
        if (!today.equals(savedDate)) {
            prefs.edit()
                .putString(KEY_STATS_DATE, today)
                .putInt(KEY_STATS_TODAY_COUNT, 0)
                .putInt(KEY_STATS_TODAY_MINUTES, 0)
                .putInt(KEY_STATS_TODAY_SECONDS, 0)
                .commit();
        }
    }

    public void recordCompletedFocusSession(int minutes) {
        checkDayReset();
        int secondsToAdd = minutes * 60;
        int todayCount = prefs.getInt(KEY_STATS_TODAY_COUNT, 0) + 1;
        int todaySeconds = getTodaySeconds() + secondsToAdd;
        int totalCount = prefs.getInt(KEY_STATS_TOTAL_COUNT, 0) + 1;
        int totalSeconds = getTotalSeconds() + secondsToAdd;

        prefs.edit()
            .putInt(KEY_STATS_TODAY_COUNT, todayCount)
            .putInt(KEY_STATS_TODAY_SECONDS, todaySeconds)
            .putInt(KEY_STATS_TODAY_MINUTES, todaySeconds / 60)
            .putInt(KEY_STATS_TOTAL_COUNT, totalCount)
            .putInt(KEY_STATS_TOTAL_SECONDS, totalSeconds)
            .putInt(KEY_STATS_TOTAL_MINUTES, totalSeconds / 60)
            .commit();
    }

    public void recordPartialFocusSeconds(int seconds) {
        if (seconds <= 0) return;
        checkDayReset();
        int todaySeconds = getTodaySeconds() + seconds;
        int totalSeconds = getTotalSeconds() + seconds;

        prefs.edit()
            .putInt(KEY_STATS_TODAY_SECONDS, todaySeconds)
            .putInt(KEY_STATS_TODAY_MINUTES, todaySeconds / 60)
            .putInt(KEY_STATS_TOTAL_SECONDS, totalSeconds)
            .putInt(KEY_STATS_TOTAL_MINUTES, totalSeconds / 60)
            .commit();
    }

    public int getTodayCount() {
        checkDayReset();
        return prefs.getInt(KEY_STATS_TODAY_COUNT, 0);
    }

    public int getTodaySeconds() {
        checkDayReset();
        return prefs.getInt(KEY_STATS_TODAY_SECONDS, prefs.getInt(KEY_STATS_TODAY_MINUTES, 0) * 60);
    }

    public int getTodayMinutes() {
        return getTodaySeconds() / 60;
    }

    public int getTotalCount() {
        return prefs.getInt(KEY_STATS_TOTAL_COUNT, 0);
    }

    public int getTotalSeconds() {
        return prefs.getInt(KEY_STATS_TOTAL_SECONDS, prefs.getInt(KEY_STATS_TOTAL_MINUTES, 0) * 60);
    }

    public int getTotalMinutes() {
        return getTotalSeconds() / 60;
    }
}
