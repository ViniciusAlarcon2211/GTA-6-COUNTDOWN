package com.vicountdown.app;

import android.Manifest;
import android.app.*;
import android.content.pm.PackageManager;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vicountdown.app.countdown.CountdownEngine;
import com.vicountdown.app.data.AppPrefs;
import com.vicountdown.app.messages.MessageRepository;
import com.vicountdown.app.notifications.NotificationHelper;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private FrameLayout content;
    private AppPrefs prefs;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable ticker;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        prefs = new AppPrefs(this);
        content = findViewById(R.id.content);
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.inflateMenu(R.menu.bottom_nav);
        nav.setItemIconTintList(null);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_messages) showMessages();
            else if (id == R.id.nav_settings) showSettings();
            else showHome();
            return true;
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) NotificationHelper.schedule(this);
        });
        showHome();
    }

    private void showHome() {
        stopTicker();
        View v = getLayoutInflater().inflate(R.layout.view_home, content, false);
        content.removeAllViews(); content.addView(v);
        TextView days = v.findViewById(R.id.days);
        TextView time = v.findViewById(R.id.time);
        TextView tagline = v.findViewById(R.id.tagline);
        TextView daily = v.findViewById(R.id.dailyMessage);
        TextView date = v.findViewById(R.id.releaseDate);
        ProgressBar progress = v.findViewById(R.id.progress);
        daily.setText(MessageRepository.daily());
        date.setText("19 NOV 2026  •  LANÇAMENTO");
        progress.setVisibility(prefs.showProgress() ? View.VISIBLE : View.GONE);
        progress.setProgress(CountdownEngine.progress1000());
        ticker = new Runnable() {
            public void run() {
                CountdownEngine.Snapshot s = CountdownEngine.now();
                days.setText(String.valueOf(s.days()));
                time.setText(String.format(Locale.getDefault(), "%02d : %02d : %02d", s.hours(), s.minutes(), s.seconds()));
                if (s.launched()) {
                    tagline.setText("A ESPERA CHEGOU AO FIM");
                    days.setText("0");
                    time.setText("00 : 00 : 00");
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(ticker);
        v.setAlpha(0f); v.animate().alpha(1f).setDuration(250).start();
    }

    private void showMessages() {
        stopTicker();
        View v = getLayoutInflater().inflate(R.layout.view_messages, content, false);
        LinearLayout list = v.findViewById(R.id.messageList);
        for (MessageRepository.Item item : MessageRepository.all()) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(18),dp(16),dp(18),dp(16));
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1,-2);
            cp.setMargins(0,0,0,dp(12)); card.setLayoutParams(cp);
            card.setBackgroundResource(R.drawable.card_bg);
            TextView meta = tv(item.category().toUpperCase() + "  •  " + item.date(), 11, 0xFF48E7FF);
            TextView body = tv(item.text(), 17, 0xFFF7F5FA);
            body.setPadding(0,dp(8),0,0);
            card.addView(meta); card.addView(body); list.addView(card);
        }
        content.removeAllViews(); content.addView(v);
        v.setAlpha(0f); v.animate().alpha(1f).setDuration(220).start();
    }

    private void showSettings() {
        stopTicker();
        View v = getLayoutInflater().inflate(R.layout.view_settings, content, false);
        Switch notifications = v.findViewById(R.id.notifications);
        Switch sound = v.findViewById(R.id.sound);
        Switch vibration = v.findViewById(R.id.vibration);
        Switch progress = v.findViewById(R.id.showProgress);
        Button time = v.findViewById(R.id.timeButton);
        Button test = v.findViewById(R.id.testNotification);
        Spinner theme = v.findViewById(R.id.themeSpinner);
        Spinner effect = v.findViewById(R.id.effectSpinner);
        notifications.setChecked(prefs.notifications()); sound.setChecked(prefs.sound());
        vibration.setChecked(prefs.vibration()); progress.setChecked(prefs.showProgress());
        updateTimeButton(time);
        theme.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Vice Night","Midnight","Sunset"}));
        effect.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Efeito: baixo","Efeito: médio","Efeito: alto"}));
        theme.setSelection(prefs.theme()); effect.setSelection(prefs.effect());
        notifications.setOnCheckedChangeListener((b, checked) -> {
            prefs.notifications(checked);
            if (checked && Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            else NotificationHelper.schedule(this);
        });
        sound.setOnCheckedChangeListener((b,c)-> prefs.sound(c));
        vibration.setOnCheckedChangeListener((b,c)-> prefs.vibration(c));
        progress.setOnCheckedChangeListener((b,c)-> prefs.showProgress(c));
        time.setOnClickListener(x -> new TimePickerDialog(this, (picker,h,m)->{
            prefs.time(h,m); updateTimeButton(time); NotificationHelper.schedule(this);
        }, prefs.hour(), prefs.minute(), true).show());
        test.setOnClickListener(x -> {
            if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            else NotificationHelper.show(this, true);
        });
        theme.setOnItemSelectedListener(new SimpleSelection(i -> prefs.theme(i)));
        effect.setOnItemSelectedListener(new SimpleSelection(i -> prefs.effect(i)));
        content.removeAllViews(); content.addView(v);
        v.setAlpha(0f); v.animate().alpha(1f).setDuration(220).start();
    }

    private void updateTimeButton(Button b) { b.setText(String.format(Locale.getDefault(),"Horário: %02d:%02d",prefs.hour(),prefs.minute())); }
    private TextView tv(String s, int sp, int color) { TextView t=new TextView(this);t.setText(s);t.setTextSize(sp);t.setTextColor(color);return t; }
    private int dp(int v) { return (int)(v*getResources().getDisplayMetrics().density+.5f); }
    private void stopTicker(){ if(ticker!=null) handler.removeCallbacks(ticker); ticker=null; }
    @Override protected void onDestroy(){ stopTicker(); super.onDestroy(); }

    private static class SimpleSelection implements AdapterView.OnItemSelectedListener {
        interface C { void call(int i); } private final C c; SimpleSelection(C c){this.c=c;}
        public void onItemSelected(AdapterView<?> p, View v, int pos, long id){c.call(pos);}
        public void onNothingSelected(AdapterView<?> p){}
    }
}
