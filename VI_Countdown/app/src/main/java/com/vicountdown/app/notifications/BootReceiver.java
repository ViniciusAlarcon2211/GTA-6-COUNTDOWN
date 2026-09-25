package com.vicountdown.app.notifications;
import android.content.*;
public class BootReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        NotificationHelper.schedule(context);
    }
}
