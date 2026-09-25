package com.vicountdown.app.notifications;
import android.content.*;
public class NotificationReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        NotificationHelper.show(context, false);
        NotificationHelper.schedule(context);
    }
}
