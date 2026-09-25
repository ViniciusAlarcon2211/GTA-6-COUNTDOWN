package com.vicountdown.app.data;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefs {
    private final SharedPreferences p;
    public AppPrefs(Context c) { p = c.getSharedPreferences("vi_prefs", Context.MODE_PRIVATE); }
    public boolean notifications() { return p.getBoolean("notifications", false); }
    public void notifications(boolean v) { p.edit().putBoolean("notifications", v).apply(); }
    public int hour() { return p.getInt("hour", 19); }
    public int minute() { return p.getInt("minute", 0); }
    public void time(int h, int m) { p.edit().putInt("hour", h).putInt("minute", m).apply(); }
    public boolean sound() { return p.getBoolean("sound", true); }
    public void sound(boolean v) { p.edit().putBoolean("sound", v).apply(); }
    public boolean vibration() { return p.getBoolean("vibration", true); }
    public void vibration(boolean v) { p.edit().putBoolean("vibration", v).apply(); }
    public boolean showProgress() { return p.getBoolean("progress", true); }
    public void showProgress(boolean v) { p.edit().putBoolean("progress", v).apply(); }
    public int theme() { return p.getInt("theme", 0); }
    public void theme(int v) { p.edit().putInt("theme", v).apply(); }
    public int effect() { return p.getInt("effect", 1); }
    public void effect(int v) { p.edit().putInt("effect", v).apply(); }
}
