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

    public static boolean etCo2_alarm = true;
}
