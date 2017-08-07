package com.breathmonitor.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.breathmonitor.bean.Alert;
import com.breathmonitor.bean.Breath;

/**
 * Created by Administrator on 2017/4/10.
 */

public class MonitorApplication extends Application {
    SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(getClass().getName(), Context.MODE_PRIVATE);
    }

    public void setAlertShared(String name, Alert alert) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(name + "_Switch", alert.isAlarmSwitch());
        editor.putInt(name + "_HighLimit", alert.getAlert_H());
        editor.putInt(name + "_LowLimit", alert.getAlert_L());
        //editor.commit();
        editor.apply();//异步 不会返回是否操作成功
    }

    public Alert getAlertSharedModel(String name) {
        boolean Switch = sp.getBoolean(name + "_Switch", true);
        int high = sp.getInt(name + "_HighLimit", 100);
        int low = sp.getInt(name + "_LowLimit", 30);
        return new Alert(Switch, high, low);
    }

    public void setBreathShared(Breath breath) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("breath_Mode", breath.getB_O2());
        editor.putString("breath_O2", breath.getB_O2());
        editor.putString("breath_Hz", breath.getB_Hz());
        editor.putString("breath_Tidal", breath.getB_Tidal());
        //editor.commit();
        editor.apply();
    }

    public Breath getBreathShared() {
        String o2 = sp.getString("breath_Mode", "20");
        String mode = sp.getString("breath_O2", "20");
        String hz = sp.getString("breath_Tidal", "30");
        String tidal = sp.getString("breath_Hz", "30");
        return new Breath(o2, mode, hz, tidal);
    }


}
