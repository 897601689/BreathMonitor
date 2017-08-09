package com.breathmonitor.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.breathmonitor.activity.SplashActivity;


/**
 * Created by Admin on 2017/08/09.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent mainActivityIntent = new Intent(context, SplashActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }
    }
}
