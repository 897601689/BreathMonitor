package com.breathmonitor.parsing;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.MySerialPort;

/**
 * Created by Administrator on 2017/4/11.
 */

public class Breath_Parsing {

    //<editor-fold desc="设置命令">
    /// <summary>
    /// 模式选择-呼吸机当前为急救模式
    /// </summary>
    public static byte[] bModeAid = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};
    /// <summary>
    /// 模式选择-呼吸机当前为普通模式
    /// </summary>
    public static byte[] bModeOrd = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0x81, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};

    /// <summary>
    /// 主机查询呼吸机启停状态
    /// </summary>
    public static byte[] bQState = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0x84, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};
    /// <summary>
    /// 主机查询呼吸机处于哪种潮气量
    /// </summary>
    public static byte[] bQTidal = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0x8b, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};

    //**********************设定工作模式
    /// <summary>
    /// 普通模式下主机设定呼吸机工作模式为 A/C
    /// </summary>
    public static byte[] bSAC = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0x8c, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};
    /// <summary>
    /// 普通模式下主机设定呼吸机工作模式为 A/C+Sigh
    /// </summary>
    public static byte[] bSSigh = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0x8d, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};
    /// <summary>
    /// 普通模式下主机设定呼吸机工作模式为 SIMV
    /// </summary>
    public static byte[] bSSIMV = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0x8e, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};
    /// <summary>
    /// 普通模式下主机设定呼吸机工作模式为 SPONT
    /// </summary>
    public static byte[] bSSPONT = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0x8f, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};
    /// <summary>
    /// 普通模式下主机设定呼吸机工作模式为 控制模式
    /// </summary>
    public static byte[] bSControl = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0x90, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};
    /// <summary>
    /// 普通模式下主机设定呼吸机工作模式为 辅助模式
    /// </summary>
    public static byte[] bSAuxiliary = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0x91, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};
    //**********************

    /// <summary>
    /// 普通模式下主机设定呼吸机工作频率,数据2、3是普通模式下呼吸机工作频率，范围8\10\12\15\20\25\40；
    /// </summary>
    public static byte[] bSHz = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x03, (byte) 0x92, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};
    /// <summary>
    /// 普通模式下主机设定呼吸机工作潮气量,数据2~数据5是普通模式下呼吸机当前潮气量,
    /// 数据2~数据5是潮气量千位到个位，范围是0~1200ml.
    /// </summary>
    public static byte[] bSTidal = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x05, (byte) 0x93, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};
    /// <summary>
    /// 普通模式下主机设定呼吸机工作气道最高压力,
    /// 数据2~数据3是普通模式下呼吸机当前最高气道压力，数据2~数据3是气道最高压力十位到个位，范围是0~99mbar.
    /// </summary>
    public static byte[] bSMaxP = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x03, (byte) 0x94, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};
    /// <summary>
    /// 普通模式下主机设定呼吸机工作吸呼比,
    /// 数据2~数据4是普通模式下呼吸机当前吸呼比分母，数据2~数据4是吸呼比分母百位到个位，
    /// 例如吸呼比 I:E=1:1.67数据2~4为167，设备收到该数后，进行除以100+余数处理
    /// </summary>
    public static byte[] bSThan = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x04, (byte) 0x95, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};
    /// <summary>
    /// 普通模式下主机设定呼吸机氧气浓度
    /// </summary>
    public static byte[] bSO2_21 = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0xb1, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};//21%
    public static byte[] bSO2_30 = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0xab, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};//30%
    public static byte[] bSO2_40 = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0xac, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};//40%
    public static byte[] bSO2_50 = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0xad, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};//50%
    public static byte[] bSO2_60 = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0xae, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};//60%
    public static byte[] bSO2_80 = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0xaf, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};//80%
    public static byte[] bSO2_100 = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0xb0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};//100%

    /// <summary>
    /// 主机查询呼吸机空氧混合模式
    /// </summary>
    public static byte[] bQMode = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0x98, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};
    /// <summary>
    /// 主机查询呼吸机气道压力
    /// </summary>
    public static byte[] bQP = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0x9a, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};
    /// <summary>
    /// 主机查询呼吸机氧气浓度,空气模式下，浓度为21%，不需要查询
    /// </summary>
    public static byte[] bQO2 = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0x9f, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};

    /// <summary>
    /// 主机设置呼吸机启动
    /// </summary>
    public static byte[] bOn = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0xa6, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};

    /// <summary>
    /// 主机设置呼吸机停止
    /// </summary>
    public static byte[] bOff = new byte[]{(byte) 0xff, (byte) 0x32, (byte) 0x01, (byte) 0xa7, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xee};
    //</editor-fold>

    List<Byte> buffer = new ArrayList<>();
    byte[] data;


    private String key;
    private String battery;

    /// 呼吸机起停状态
    private String b_State;
    /// 呼吸机潮气量
    private String b_Tidal;
    /// 呼吸机空氧混合模式
    private String b_Mode;
    /// 呼吸机氧气浓度
    private String b_O2;
    /// 呼吸机报警
    private String b_Alarm;
    /// 呼吸机气道压力
    private String b_Pmb;

    public String getB_State() {
        return b_State;
    }

    public String getB_Tidal() {
        return b_Tidal;
    }

    public String getB_Mode() {
        return b_Mode;
    }

    public String getB_O2() {
        return b_O2;
    }

    public String getB_Alarm() {
        return b_Alarm;
    }

    public void Parsing(List<byte[]> listData) {

        int num;
        /*int len = serialPortData.length;
        for (int i = 0; i < len; i++)   //全部读出
            buffer.add(serialPortData[i]);*/

        for (int i = 0; i < listData.size(); i++)   //全部读出
        {
            for (int j = 0; j < listData.get(i).length; j++) {
                buffer.add(listData.get(i)[j]);
            }
        }

        //每次断电 上电 第一次会有干扰 (byte)0xfe
        //   buffer.Remove((byte)0xfe);
        if (buffer.size() >= 12) {
            for (int i = 0; i < buffer.size(); i++) {
                if (i + 11 > buffer.size())//超出数组索引 跳出
                    break;
                if (buffer.get(i) == (byte) 0xff && buffer.get(i + 11) == (byte) 0xee) {
                    num = 12;
                    byte[] breath_data = GetData(i, num, buffer);
                    i = i - 1;//防止 i自加 跳过数据包
                    Parser(breath_data);
                }
            }
        }

    }


    private void Parser(byte[] by) {
        //飞梭
        if (by[1] == (byte) 0x30 && by[2] == (byte) 0x01) {
            switch (by[3]) {
                case (byte) 0x80:
                    key = "关机";
                    break;
                case (byte) 0x82:
                    key = "顺时针";
                    break;
                case (byte) 0x83:
                    key = "逆时针";
                    break;
                case (byte) 0x84:
                    key = "确定";
                    break;
                case (byte) 0x86:
                    battery = "充满";
                    break;
                case (byte) 0x87:
                    battery = "未充满";
                    break;
                case (byte) 0x88:
                    battery = "电量高";
                    break;
                case (byte) 0x89:
                    battery = "电量中";
                    break;
                case (byte) 0x8a:
                    battery = "电量低";
                    break;
                case (byte) 0x8b:
                    battery = "欠压报警";
                    break;
                case (byte) 0x8c:
                    key = "按键-强光";
                    break;
                case (byte) 0x8d:
                    key = "按键-模式";
                    break;
                case (byte) 0x8e:
                    key = "按键-冻结";
                    break;
                case (byte) 0x8f:
                    key = "按键-静音";
                    break;
                case (byte) 0x90:
                    key = "按键-血压";
                    break;
                case (byte) 0x91:
                    key = "按键-菜单";
                    break;
                default:
                    key = null;
                    battery = null;
                    break;
            }
        }

        //呼吸机
        if (by[1] == (byte) 0x32 && by[2] == (byte) 0x01) {
            //  Log.i("LB","1");
            switch (by[3]) {
                case (byte) 0x82:
                    b_State = "ON";
                    break;
                case (byte) 0x83:
                    b_State = "OFF";
                    break;
                case (byte) 0x85:
                    b_Tidal = "1500 8 1.67";
                    break;
                case (byte) 0x86:
                    b_Tidal = "1400 8 1.67";
                    break;
                case (byte) 0x87:
                    b_Tidal = "1300 8 1.67";
                    break;
                case (byte) 0x88:
                    b_Tidal = "1200 8 1.67";
                    break;
                case (byte) 0x89:
                    b_Tidal = "1100 10 1.67";
                    break;
                case (byte) 0x8a:
                    b_Tidal = "1000 10 1.67";
                    break;
                case (byte) 0x8b:
                    b_Tidal = "950 10 1.67";
                    break;
                case (byte) 0x8c:
                    b_Tidal = "800 10 1.67";
                    break;
                case (byte) 0x8d:
                    b_Tidal = "700 10 1.67";
                    break;
                case (byte) 0x8e:
                    b_Tidal = "600 10 1.67";
                    break;
                case (byte) 0x8f:
                    b_Tidal = "500 10 1.67";
                    break;
                case (byte) 0x90:
                    b_Tidal = "400 12 1.67";
                    break;
                case (byte) 0x91:
                    b_Tidal = "300 12 1.67";
                    break;
                case (byte) 0x92:
                    b_Tidal = "200 15 1.67";
                    break;
                case (byte) 0x96:
                    b_Mode = "空气";
                    break;
                case (byte) 0x97:
                    b_Mode = "空氧";
                    break;
                case (byte) 0xAA:
                    b_O2 = "21";
                    break;
                case (byte) 0x9b:
                    b_O2 = "30";
                    break;
                case (byte) 0x9c:
                    b_O2 = "40";
                    break;
                case (byte) 0x9d:
                    b_O2 = "50";
                    break;
                case (byte) 0x9e:
                    b_O2 = "60";
                    break;
                case (byte) 0xA8:
                    b_O2 = "80";
                    break;
                case (byte) 0xA9:
                    b_O2 = "100";
                    break;
                case (byte) 0xA0:
                    b_Alarm = "脱落报警";
                    break;
                case (byte) 0xA1:
                    b_Alarm = "阻塞报警";
                    break;
                case (byte) 0xA2:
                    b_Alarm = "氧气压力低报警";
                    break;
                case (byte) 0xA3:
                    b_Alarm = "氧气压力高报警";
                    break;
                case (byte) 0xA4:
                    b_Alarm = "脱落报警消除";
                    break;
                case (byte) 0xA5:
                    b_Alarm = "阻塞报警消除";
                    break;
                case (byte) 0xA6:
                    b_Alarm = "氧气压力低报警消除";
                    break;
                case (byte) 0xA7:
                    b_Alarm = "氧气压力高报警消除";
                    break;
                default:
                    b_State = null;
                    b_Tidal = null;
                    b_Mode = null;
                    b_O2 = null;
                    b_Alarm = null;

                    break;
            }
        }
        if (by[1] == (byte) 0x32 && by[2] == (byte) 0x04 && by[3] == (byte) 0x99)//呼吸机气道压力
        {
            float fdata = (((float) by[4] * 100 + (float) by[5] * 10 + (float) by[6]) / 10);
            b_Pmb = String.valueOf(fdata);
        }

    }


    public static void SendCmd(MySerialPort cmdPort, byte[] cmd, String paramete) {
        if (paramete != null) {
            List<Byte> ch = new ArrayList<>();
            double f = Double.parseDouble(paramete);
            DecimalFormat df = new DecimalFormat();
            switch (cmd[2]) {
                case (byte) 0x03:

                    df.applyPattern("00");
                    paramete = df.format(f);
                    for (char c : paramete.toCharArray()) {
                        String ss = "0123456789ABCDEF";
                        int num = ss.indexOf(c);
                        ch.add((byte) num);
                    }
                    cmd[4] = ch.get(0);
                    cmd[5] = ch.get(1);
                    break;
                case (byte) 0x04:
                    df.applyPattern("000");
                    paramete = df.format(f);
                    for (char c : paramete.toCharArray()) {
                        String ss = "0123456789ABCDEF";
                        int num = ss.indexOf(c);
                        ch.add((byte) num);
                    }
                    cmd[4] = ch.get(0);
                    cmd[5] = ch.get(1);
                    cmd[6] = ch.get(2);
                    break;
                case (byte) 0x05:
                    df.applyPattern("0000");
                    paramete = df.format(f);
                    for (char c : paramete.toCharArray()) {
                        String ss = "0123456789ABCDEF";
                        int num = ss.indexOf(c);
                        ch.add((byte) num);
                    }
                    cmd[4] = ch.get(0);
                    cmd[5] = ch.get(1);
                    cmd[6] = ch.get(2);
                    cmd[7] = ch.get(3);
                    break;
                case (byte) 0x06:
                    df.applyPattern("0000.0");
                    paramete = df.format(f);
                    for (char c : paramete.toCharArray()) {
                        String ss = "0123456789ABCDEF";
                        int num = ss.indexOf(c);
                        ch.add((byte) num);
                    }
                    cmd[4] = ch.get(0);
                    cmd[5] = ch.get(1);
                    cmd[6] = ch.get(2);
                    cmd[7] = ch.get(3);
                    cmd[8] = ch.get(5);
                    break;
            }
        }
        //  String ss = byte2HexStr(cmd,cmd.length);
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
            sb.append(mChars[(b[n] & (byte) 0xFF) >> 4]);
            sb.append(mChars[b[n] & (byte) 0x0F]);
            sb.append(' ');
        }
        return sb.toString().trim();
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

    public void GetBreathInfo(MySerialPort cmdPort){
        SendCmd(cmdPort,bQTidal,null);
        SendCmd(cmdPort,bQState,null);
        SendCmd(cmdPort,bQO2,null);
    }
}
