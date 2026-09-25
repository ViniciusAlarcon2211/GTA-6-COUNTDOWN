package com.vicountdown.app.notifications;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.vicountdown.app.MainActivity;
import com.vicountdown.app.R;
import com.vicountdown.app.countdown.CountdownEngine;
import com.vicountdown.app.data.AppPrefs;
import com.vicountdown.app.messages.MessageRepository;
import java.util.Calendar;

public final class NotificationHelper {
    private NotificationHelper() {}

    private static String channelId(AppPrefs p) {
        return "daily_" + (p.sound() ? "s" : "q") + (p.vibration() ? "v" : "n");
    }

    public static void createChannel(Context c) {
        if (Build.VERSION.SDK_INT < 26) return;
        AppPrefs p = new AppPrefs(c);
        String id = channelId(p);
        NotificationManager nm = c.getSystemService(NotificationManager.class);
        NotificationChannel ch = new NotificationChannel(id, "Contagem diária", NotificationManager.IMPORTANCE_DEFAULT);
        ch.setDescription("Atualizações diárias do VI Countdown");
        ch.enableVibration(p.vibration());
        if (!p.sound()) ch.setSound(null, null);
        nm.createNotificationChannel(ch);
    }

    public static void show(Context c, boolean test) {
        AppPrefs p = new AppPrefs(c);
        createChannel(c);
        if (Build.VERSION.SDK_INT >= 33 && ActivityCompat.checkSelfPermission(c, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;
        CountdownEngine.Snapshot s = CountdownEngine.now();
        String title = test ? "Notificação de teste" : (s.launched() ? "Chegou o dia." : s.days() + " dias para o lançamento");
        String body = s.launched() ? "A espera chegou ao fim. Aproveite o lançamento." :
                (isMilestone(s.days()) ? MessageRepository.milestone(s.days()) : MessageRepository.daily());
        Intent open = new Intent(c, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(c, 0, open, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder b = new NotificationCompat.Builder(c, channelId(p))
                .setSmallIcon(R.drawable.ic_notification).setContentTitle(title).setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body)).setContentIntent(pi)
                .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT);
        c.getSystemService(NotificationManager.class).notify(test ? 77 : 76, b.build());
    }

    private static boolean isMilestone(long d) {
        return d==100||d==90||d==60||d==30||d==14||d==7||d==3||d==2||d==1;
    }

    public static void schedule(Context c) {
        AppPrefs p = new AppPrefs(c);
        cancel(c);
        if (!p.notifications()) return;
        Calendar when = Calendar.getInstance();
        when.set(Calendar.HOUR_OF_DAY, p.hour());
        when.set(Calendar.MINUTE, p.minute());
        when.set(Calendar.SECOND, 0);
        when.set(Calendar.MILLISECOND, 0);
        if (when.getTimeInMillis() <= System.currentTimeMillis()) when.add(Calendar.DAY_OF_YEAR, 1);
        Intent i = new Intent(c, NotificationReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(c, 100, i, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pi);
    }

    public static void cancel(Context c) {
        Intent i = new Intent(c, NotificationReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(c, 100, i, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE);
        if (pi != null) {
            ((AlarmManager)c.getSystemService(Context.ALARM_SERVICE)).cancel(pi);
            pi.cancel();
        }
    }
}
