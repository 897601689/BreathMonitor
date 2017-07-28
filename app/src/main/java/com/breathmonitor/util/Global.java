package com.breathmonitor.util;

import android_serialport_api.MySerialPort;

/**
 * Created by admin on 2017/7/27.
 */

public class Global {
    public static MySerialPort mcu_Com = new MySerialPort();  //电源板
    public static MySerialPort breath_Com = new MySerialPort(); //呼吸机
    public static MySerialPort spo2_Com = new MySerialPort();  //血氧



}
