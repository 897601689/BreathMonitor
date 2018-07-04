package com.breathmonitor.parsing;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.MySerialPort;

/**
 * Created by Admin on 2016/11/3. 电源板
 */
public class Mcu_Parsing2 {
    private int dataNum = 12;//数据长度
    private byte[] buffer;
    private List<Byte> list = new ArrayList<>();


    public String getBat_State() {
        return bat_State;
    }

    public String getBat_Level() {
        return bat_Level;
    }

    public int getAc_dc() {
        return ac_dc;
    }

    public int getCharge_State() {
        return charge_State;
    }

    private String bat_State = ""; // 电池状态
    private String bat_Level = ""; // 电池电量
    private int ac_dc = 0;        // 适配器状态
    private int charge_State = 0; // 充电状态

    public static byte[] DeviceOFF = new byte[]{(byte) 0x81};   // 主机发送关机命令


    //region 暂时停用
    public static byte[] UmOn = new byte[]{(byte) 0x81,0x00,0x00,0x00,0x01,0x00};    // 尿机模块上电
    public static byte[] UmOff = new byte[]{(byte) 0x81,0x01,0x01,0x01,0x00,0x01};   // 尿机模块断电

    public static byte[] EcgOn = new byte[]{(byte) 0x81,0x01,0x00,0x00,0x00,0x00};   // 心电模块上电
    public static byte[] EcgOff = new byte[]{(byte) 0x81,0x00,0x01,0x01,0x01,0x01};  // 心电模块断电

    public static byte[] Spo2On = new byte[]{(byte) 0x81,0x00,0x01,0x00,0x00,0x00};  // 血氧模块上电
    public static byte[] Spo2Off = new byte[]{(byte) 0x81,0x01,0x00,0x01,0x01,0x01}; // 血氧模块断电

    public static byte[] BpOn = new byte[]{(byte) 0x81,0x00,0x00,0x01,0x00,0x00};    // 血压模块上电
    public static byte[] BpOff = new byte[]{(byte) 0x81,0x01,0x01,0x01,0x00,0x01};   // 血压模块断电

    public static byte[] IDOn = new byte[]{(byte) 0x81,0x00,0x00,0x00,0x00,0x01};    // ID模块上电
    public static byte[] IDOff = new byte[]{(byte) 0x81,0x01,0x01,0x01,0x01,0x00};   // ID模块断电
    //endregion

    public  static byte[] AllDeviceOn = new byte[]{(byte) 0x81,0x01,0x01,0x01,0x01,0x01};    // 所有模块上电
    public  static byte[] AllDeviceOFF = new byte[]{(byte) 0x81,0x00,0x00,0x00,0x00,0x00};    // 所有模块断电

    public static byte[] cellCmd = new byte[]{(byte) 0x80,0x01,0x01};     // 主机询问当前电池状态,

    public static byte[] AlarmOff = new byte[]{(byte) 0x82,0x00};    // 报警 关
    public static byte[] AlarmHigh = new byte[]{(byte) 0x82,0x01};   // 报警 高
    public static byte[] AlarmMid = new byte[]{(byte) 0x82,0x02};    // 报警 中
    //public static byte[] AlarmLow = new byte[]{(byte) 0x9a};    // 报警 低
    public static byte[] voice_off = new byte[]{(byte) 0x82,0x03};    // 静音
    public static byte[] voice_on = new byte[]{(byte) 0x82,0x04};   // 取消静音

    //endregion

  
    public void Parsing(MySerialPort com) {
        try {

            buffer = com.Read();
            if (buffer != null) {
                list.clear();
                //Log.e("list",""+buffer.length);
                for (byte aByte : buffer) {
                    list.add(aByte);
                }
                for (int i = 0; i < list.size(); i++) {

                    //电源板 信息
                    if (list.get(i) == (byte)0xFF && list.get(i + 11) == (byte)0xEE) {
                        byte[] mcu_data = GetData(i, dataNum, list);
                        i =  - 1;
                        Parsing_Mcu(mcu_data);
                    }
                }
            }

        } catch (Exception ex) {
           // Log.e("ERROR", ex.toString());
        }

    }

    /**
     * 获取命令数组
     *
     * @param i
     * @param len
     * @param list
     * @return
     */
    private byte[] GetData(int i, int len, List<Byte> list) {
        byte[] data = new byte[len];
        if (list.size() >= len) {
            for (int index = 0; index < len; index++) {
                data[index] = list.get(i);
                list.remove(i);
            }
        } else {
            data = null;
        }
        return data;
    }

    /**
     * 电源板解析
     *
     * @param data 数据数组
     */
    private void Parsing_Mcu(byte[] data) {
        if (data[1] == 0x32 && data[2] == 0x01) {
            //Log.e("TAG", "Parsing_Mcu: "+data[3] );
            bat_Level = String.valueOf((168 - (data[4]&0x0FF)) / 33);//电池电量百分百 最低电压为13V 16.8-13 = 3.8

            Log.e("电池电压",String.valueOf(data[4]&0x0FF));
            //<editor-fold desc="电池状态">
            switch (data[5]) {
                case 0x01:
                    bat_State = "欠压报警";
                    break;
                case 0x02:
                    bat_State = "电量低";
                    break;
                case 0x03:
                    bat_State = "电量中";
                    break;
                case 0x04:
                    bat_State = "电量高";
                    break;
                case 0x05:
                    bat_State = "充满";
                    break;
                default:
                    bat_State = "电量高";
                    break;
            }
            Log.e("电池状态","11 "+bat_State);
            //</editor-fold>

            ac_dc = data[6];//0 没接适配器 ，1 连接适配器
            charge_State = data[7];//0 正在充电 ，1 未充满或未接电池

        }

    }


    /**
     * 发送单片机命令
     *
     * @param cmdPort 串口对象
     * @param cmd     命令
     */
    public static void SendMcuCmd(MySerialPort cmdPort, byte[] cmd) {
        try {
            byte[] cmdByte;
            cmdByte = new byte[]{(byte) 0xFF, 0x32, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xee};
            for(int i=0;i<cmd.length;i++){
                cmdByte[i+3] = cmd[i];
            }
            cmdPort.Write(cmdByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
