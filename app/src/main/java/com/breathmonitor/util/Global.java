package com.breathmonitor.util;

import com.breathmonitor.parsing.Breath_Parsing;

import android_serialport_api.MySerialPort;

/**
 * Created by admin on 2017/7/27.
 */

public class Global {
    public static MySerialPort mcu_Com = new MySerialPort();    //电源板串口
    public static MySerialPort breath_Com = new MySerialPort(); //呼吸机串口
    public static MySerialPort spo2_Com = new MySerialPort();   //血氧串口

    public static Breath_Parsing breath = new Breath_Parsing(); //呼吸机解析对象

    public static boolean BreathIsOpen = false; //呼吸机是否在工作
    public static boolean resp_alarm = true;    //报警开关
    public static boolean etCo2_alarm = true;   //报警开关
    public static boolean spo2_alarm = true;    //报警开关
    public static boolean pulse_alarm = true;   //报警开关

    public static boolean voice = true;         //静音开关
    public static boolean lock = false;         //界面锁

    public static boolean isAlarm1 = false;     //是否存在生理报警
    public static boolean isAlarm2 = false;     //是否存在技术报警

    public static boolean isAlarmH = false;     //是否存在高级报警
    public static boolean isAlarmM = false;     //是否存在中级报警
    public static boolean isAlarmOff = false;   //是否已经关闭报警

    public static MonitorApplication mApp;
}
