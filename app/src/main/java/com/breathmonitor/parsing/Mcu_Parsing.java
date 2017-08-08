package com.breathmonitor.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.MySerialPort;

/**
 * Created by admin on 2017/8/2.
 */

public class Mcu_Parsing {
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

    byte[] mcuData = new byte[]{(byte) 0xff, 0x32, 0x01, (byte) 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xee};
    public static byte[] alarm_off = new byte[]{(byte) 0xff, 0x32, 0x01, (byte) 0x82, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xee};
    public static byte[] alarm_h = new byte[]{(byte) 0xff, 0x32, 0x01, (byte) 0x82, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xee};
    public static byte[] alarm_m = new byte[]{(byte) 0xff, 0x32, 0x01, (byte) 0x82, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xee};
    public static byte[] voice_off = new byte[]{(byte) 0xff, 0x32, 0x01, (byte) 0x82, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xee};
    public static byte[] voice_on = new byte[]{(byte) 0xff, 0x32, 0x01, (byte) 0x82, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xee};

    /**
     * 获取串口数据
     *
     * @param com 串口对象
     */
    public void Parsing(MySerialPort com) {
        try {

            buffer = com.Read();
            ac_dc =0;
            if (buffer != null) {
                list.clear();
                //Log.e("McuData", "" + buffer.length);
                for (byte aByte : buffer) {
                    list.add(aByte);
                }
                for (int i = 0; i < list.size(); i++) {

                    //按键板 信息
                    if (list.get(i) == (byte) 0xFF && list.get(i + 11) == (byte) 0xEE) {
                        byte[] mcu_data = GetData(i, dataNum, list);
                        i = i - 1;
                        Parsing_Mcu(mcu_data);
                        //String str = byte2HexStr(mcu_data, mcu_data.length);
                        //Log.e("MCU", str);
                    }
                }
            }

        } catch (Exception ex) {
            // Log.e("ERROR", ex.toString());
        }

    }

    /**
     * 电源解析
     *
     * @param data 数据数组
     */
    private void Parsing_Mcu(byte[] data) {
        if (data[1] == 0x32 && data[2] == 0x01) {

            bat_Level = String.valueOf((168 - (int) data[3]) / 33);//电池电量百分百

            //<editor-fold desc="电池状态">
            switch (data[5]) {
                case 0x01:
                    bat_State = "电量高";
                    break;
                case 0x02:
                    bat_State = "电量中";
                    break;
                case 0x03:
                    bat_State = "电量低";
                    break;
                case 0x04:
                    bat_State = "欠压报警";
                    break;
                case 0x05:
                    bat_State = "充满";
                    break;
                default:
                    bat_State = "";
                    break;
            }
            //</editor-fold>

            ac_dc = data[6];//0 没接适配器 ，1 连接适配器
            charge_State = data[7];//0 正在充电 ，1 未充满或未接电池
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

    public static void SendCmd(MySerialPort cmdPort, byte[] cmd) {
        //  System.out.print(ss);
        try {
            cmdPort.Write(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final static char[] mChars = "0123456789ABCDEF".toCharArray();

    private String byte2HexStr(byte[] b, int iLen) {
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < iLen; n++) {
            sb.append(mChars[(b[n] & 0xFF) >> 4]);
            sb.append(mChars[b[n] & 0x0F]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

}
