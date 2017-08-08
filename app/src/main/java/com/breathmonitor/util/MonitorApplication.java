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
    String TAG = "MonitorApplication";
    SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(getClass().getName(), Context.MODE_PRIVATE);
        if (getAppNum() == 0) {
            setBreathShared(new Breath("控制", "200", "15", "21"));
            setAlertShared("Resp", new Alert(true, 30, 8));
            setAlertShared("EtCO2", new Alert(true, 40, 10));
            setAlertShared("SpO2", new Alert(true, 100, 90));
            setAlertShared("Pulse", new Alert(true, 120, 50));
        }
        setAppNum(getAppNum() + 1);//增加一次启动次数
    }

    public void setAlertShared(String name, Alert alert) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(name + "_Switch", alert.isAlarmSwitch());
        editor.putInt(name + "_HighLimit", alert.getAlert_H());
        editor.putInt(name + "_LowLimit", alert.getAlert_L());
        //editor.commit();
        editor.apply();//异步 不会返回是否操作成功
    }

    public Alert getAlertShared(String name) {
        boolean Switch = sp.getBoolean(name + "_Switch", true);
        int high = sp.getInt(name + "_HighLimit", 100);
        int low = sp.getInt(name + "_LowLimit", 30);
        return new Alert(Switch, high, low);
    }

    public void setBreathShared(Breath breath) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("breath_Mode", breath.getB_Mode());
        editor.putString("breath_Tidal", breath.getB_Tidal());
        editor.putString("breath_Hz", breath.getB_Hz());
        editor.putString("breath_O2", breath.getB_O2());
        //editor.commit();
        editor.apply();
    }

    public Breath getBreathShared() {

        String mode = sp.getString("breath_Mode", "控制");
        String tidal = sp.getString("breath_Tidal", "700");
        String hz = sp.getString("breath_Hz", "10");
        String o2 = sp.getString("breath_O2", "21");
        return new Breath(mode, tidal, hz, o2);
    }


    public void setAppNum(int num) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("AppNum", num);
        //editor.commit();
        editor.apply();
    }

    public int getAppNum() {
        return sp.getInt("AppNum", 0);
    }

}
