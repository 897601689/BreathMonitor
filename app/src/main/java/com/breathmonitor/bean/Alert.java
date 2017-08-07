package com.breathmonitor.bean;

/**
 * Created by admin on 2017/8/7.
 */

public class Alert {

    private boolean alarmSwitch;
    private int alert_H;
    private int alert_L;

    public boolean isAlarmSwitch() {
        return alarmSwitch;
    }

    public void setAlarmSwitch(boolean alarmSwitch) {
        this.alarmSwitch = alarmSwitch;
    }

    public int getAlert_H() {
        return alert_H;
    }

    public void setAlert_H(int alert_H) {
        this.alert_H = alert_H;
    }

    public int getAlert_L() {
        return alert_L;
    }

    public void setAlert_L(int alert_L) {
        this.alert_L = alert_L;
    }

    public Alert(Boolean alarmSwitch, int alert_H, int alert_L) {
        this.alarmSwitch = alarmSwitch;
        this.alert_H = alert_H;
        this.alert_L = alert_L;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "alarmSwitch=" + alarmSwitch +
                ", alert_H='" + alert_H + '\'' +
                ", alert_L='" + alert_L + '\'' +
                '}';
    }
}
