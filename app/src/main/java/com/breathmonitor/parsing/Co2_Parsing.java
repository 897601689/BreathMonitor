package com.breathmonitor.parsing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/1.
 */

public class Co2_Parsing {


    /// <summary>
    /// 开始连续模式 命令
    /// </summary>
    public byte[] Start = new byte[]{(byte) 0x80, 0x02, 0x00};
    /// <summary>
    /// 进行零点校正
    /// </summary>
    public byte[] Adjust = new byte[]{(byte) 0x82, 0x01};
    /// <summary>
    /// 设置或读取模块参数
    /// </summary>
    public byte[] SetAndRead = new byte[]{(byte) 0x84, 0x01};
    /// <summary>
    /// 非应答
    /// </summary>
    public byte[] OnResponse = new byte[]{(byte) 0xC8, 0x01};
    /// <summary>
    /// 停止连续模式
    /// </summary>
    public byte[] Stop = new byte[]{(byte) 0xC9, 0x01};
    /// <summary>
    /// 清除窒息状态
    /// </summary>
    public byte[] Clear = new byte[]{(byte) 0xCC, 0x01};
    /// <summary>
    /// 复位命令
    /// </summary>
    public byte[] Reset = new byte[]{(byte) 0xF8, 0x01};

    /// <summary>
    /// 获取大气压
    /// </summary>
    public byte[] GetBaroPress = new byte[]{(byte) 0x84, 0x02, 0x01};
    /// <summary>
    /// 获取气体温度
    /// </summary>
    public byte[] GetGasTemp = new byte[]{(byte) 0x84, 0x02, 0x04};
    /// <summary>
    /// 获取ETCO2 时间周期
    /// </summary>
    public byte[] GetEtco2Period = new byte[]{(byte) 0x84, 0x02, 0x05};
    /// <summary>
    /// 获取窒息时间
    /// </summary>
    public byte[] GetApneaTime = new byte[]{(byte) 0x84, 0x02, 0x06};
    /// <summary>
    /// 获取零点气类型
    /// </summary>
    public byte[] GetZeroGasType = new byte[]{(byte) 0x84, 0x02, 0x09};
    /// <summary>
    /// 获取气体补偿
    /// </summary>
    public byte[] GetO2Compens = new byte[]{(byte) 0x84, 0x02, 0x0B};
    /// <summary>
    /// 获取采样气泵工作状态
    /// </summary>
    public byte[] GetPumpState = new byte[]{(byte) 0x84, 0x02, 0x1B};
    /// <summary>
    /// 设置大气压
    /// </summary>
    public byte[] SetBaroPress = new byte[]{(byte) 0x84, 0x04, 0x01, 0x05, 0x78};
    /// <summary>
    /// 设置气体温度
    /// </summary>
    public byte[] SetGasTemp = new byte[]{(byte) 0x84, 0x04, 0x04, 0x02, 0x5e};
    /// <summary>
    /// 设置ETCO2 时间周期
    /// </summary>
    public byte[] SetEtco2Period = new byte[]{(byte) 0x84, 0x03, 0x05, 0X0A};
    /// <summary>
    /// 设置窒息时间
    /// </summary>
    public byte[] SetApneaTime = new byte[]{(byte) 0x84, 0x03, 0x06, 0x14};
    /// <summary>
    /// 设置零点气类型
    /// </summary>
    public byte[] SetZeroGasType = new byte[]{(byte) 0x84, 0x03, 0x09, 0x01};
    /// <summary>
    /// 设置气体补偿
    /// </summary>
    public byte[] SetO2Compens = new byte[]{(byte) 0x84, 0x06, 0x0B, 0x10, 0x00, 0x00, 0x00};
    /// <summary>
    /// 设置采样气泵工作状态
    /// </summary>
    public byte[] SetPumpState = new byte[]{(byte) 0x84, 0x03, 0x1B};

    /// <summary>
    /// 气泵开启
    /// </summary>
    public byte[] Gas_Pump_Open = new byte[]{(byte) 0x84, 0x03, 0x1B, 0X00};
    /// <summary>
    /// 气泵关闭
    /// </summary>
    public byte[] Gas_Pump_Close = new byte[]{(byte) 0x84, 0x03, 0x1B, 0X01};

    public byte[] a = new byte[]{};


    //CO2曲线
    public List<Integer> co2_curve = new ArrayList<>();

    //同步计数器
    private int sync = 0;

    //ETCO2
    public float etco2;

    //RR 呼吸
    public int rr;


    //FiCO2
    public int fico2;

    //硬件状态
    private int state;

    //大气压
    private int baroPress;

    // 气体温度
    private float gasTemp;

    // ETCO2 时间周期
    private int etco2Period;

    // 窒息时间
    private int apneaTime;

    // 零点气类型
    private int zeroGasType;

    // 氧气补偿
    private int o2Compens;

    //平衡气补偿
    private int balanceGas;

    //麻醉气体浓度
    private float anesth;

    // 采样气泵工作状态
    private int pumpState;


    //非应答错误
    public String answer = "";


    List<Byte> buffer = new ArrayList<Byte>();
    byte[] data;

    //解析命令
    public void Parsing(byte[] serialPortData) {
        co2_curve.clear();
        answer = "";
        int len = serialPortData.length;
        int num;
        for (int i = 0; i < len; i++)   //全部读出
            buffer.add(serialPortData[i]);
        if (buffer.size() >= 6)        //10
        {
            // Log.i("LB","buffer size"+buffer.size());
            //Console.WriteLine(buffer.Count);
            for (int i = 0; i < buffer.size(); i++) {
                switch (buffer.get(i)) {
                    case (byte) 0x80:
                        // CO2波形数据

                        if (i + 1 >= buffer.size())
                            break;
                        switch (buffer.get(i + 1)) {
                            case (byte) 0x04:

                                if (i + 5 >= buffer.size())
                                    break;
                                num = 6;
                                data = new byte[num];
                                for (int j = 0; j < num; j++) {
                                    data[j] = buffer.get(i + j);
                                }
                                for (int j = 0; j < num; j++) {
                                    buffer.remove(i);
                                }
                                i = -1;
                                // Log.i("LB","data  "+(byte)data[0]+ "  " +(byte)data[3] + "  "+(byte)data[4]);

                                Parser(data);
                                break;
                            case (byte) 0x05:
                                if (i + 6 >= buffer.size())
                                    break;
                                num = 7;
                                data = new byte[num];
                                for (int j = 0; j < num; j++) {
                                    data[j] = buffer.get(i + j);
                                }
                                for (int j = 0; j < num; j++) {
                                    buffer.remove(i);
                                }
                                i = -1;
                                Parser(data);
                                break;
                            case (byte) 0x07:
                                if (i + 8 >= buffer.size())
                                    break;
                                num = 9;
                                data = new byte[num];
                                for (int j = 0; j < num; j++) {
                                    data[j] = buffer.get(i + j);
                                }
                                for (int j = 0; j < num; j++) {
                                    buffer.remove(i);
                                }
                                i = -1;
                                Parser(data);
                                break;
                            case (byte) 0x0A:
                                if (i + 11 >= buffer.size())
                                    break;
                                num = 12;
                                data = new byte[num];
                                for (int j = 0; j < num; j++) {
                                    data[j] = buffer.get(i + j);
                                }
                                for (int j = 0; j < num; j++) {
                                    buffer.remove(i);
                                }
                                i = -1;
                                Parser(data);
                                break;
                        }


                        break;
                    case (byte) 0x82:
                        if (i + 3 >= buffer.size())
                            break;
                        num = 4;
                        data = new byte[num];
                        for (int j = 0; j < num; j++) {
                            data[j] = buffer.get(i + j);
                        }
                        for (int j = 0; j < num; j++) {
                            buffer.remove(i);
                        }
                        i = -1;
                        Parser(data);
                        break;
                    case (byte) 0x84:
                        if (i + 1 >= buffer.size())
                            break;
                        switch (buffer.get(i)) {
                            case (byte) 0x02:
                                if (i + 3 >= buffer.size())
                                    break;
                                num = 4;
                                data = new byte[num];
                                for (int j = 0; j < num; j++) {
                                    data[j] = buffer.get(i + j);
                                }
                                for (int j = 0; j < num; j++) {
                                    buffer.remove(i);
                                }
                                i = -1;
                                Parser(data);
                                break;
                            case (byte) 0x03:
                                if (i + 4 >= buffer.size())
                                    break;
                                num = 5;
                                data = new byte[num];
                                for (int j = 0; j < num; j++) {
                                    data[j] = buffer.get(i + j);
                                }
                                for (int j = 0; j < num; j++) {
                                    buffer.remove(i);
                                }
                                i = -1;
                                Parser(data);
                                break;
                            case (byte) 0x04:
                                if (i + 5 >= buffer.size())
                                    break;
                                num = 6;
                                data = new byte[num];
                                for (int j = 0; j < num; j++) {
                                    data[j] = buffer.get(i + j);
                                }
                                for (int j = 0; j < num; j++) {
                                    buffer.remove(i);
                                }
                                i = -1;
                                Parser(data);
                                break;
                            case (byte) 0x06:
                                if (i + 7 >= buffer.size())
                                    break;
                                num = 8;
                                data = new byte[num];
                                for (int j = 0; j < num; j++) {
                                    data[j] = buffer.get(i + j);
                                }
                                for (int j = 0; j < num; j++) {
                                    buffer.remove(i);
                                }
                                i = -1;
                                Parser(data);
                                break;
                        }


                        break;
                    case (byte) 0xC8:
                        if (i + 3 >= buffer.size())
                            break;
                        num = 4;
                        data = new byte[num];
                        for (int j = 0; j < num; j++) {
                            data[j] = buffer.get(i + j);
                        }
                        for (int j = 0; j < num; j++) {
                            buffer.remove(i);
                        }
                        i = -1;
                        Parser(data);
                        break;
                    case (byte) 0xC9:
                        if (i + 3 >= buffer.size())
                            break;
                        num = 4;
                        data = new byte[num];
                        for (int j = 0; j < num; j++) {
                            data[j] = buffer.get(i + j);
                        }
                        for (int j = 0; j < num; j++) {
                            buffer.remove(i);
                        }
                        i = -1;
                        Parser(data);
                        break;
                    case (byte) 0xCC:
                        if (i + 3 >= buffer.size())
                            break;
                        num = 4;
                        data = new byte[num];
                        for (int j = 0; j < num; j++) {
                            data[j] = buffer.get(i + j);
                        }
                        for (int j = 0; j < num; j++) {
                            buffer.remove(i);
                        }
                        i = -1;
                        Parser(data);
                        break;
                }
            }
        }
        //}
        //catch(Exception ex)
        //{
        //    buffer.Clear();
        //    Console.WriteLine(ex.Message);
        //}

    }

    //开始解析命令
    private void Parser(byte[] data) {
        switch (data[0]) {
            case (byte) 0x80://CO2波形
                //  Log.i("LB","1");
                co2_curve.add((128 * data[3] + data[4]) - 1000);
                sync = data[2];

                switch (data[1]) {
                    case (byte) 0x04:
                        //  Log.i("LB","data  "+(byte)data[0]+ "  " +(byte)data[3] + "  "+(byte)data[4]);
                        break;
                    case (byte) 0x05://检测到呼吸标志
                        // Console.WriteLine("检测到呼吸");
                        break;
                    case (byte) 0x07:
                        switch (data[5]) {
                            case (byte) 0x02://ETCO2值
                                //etco2 = data[6] * 27 + data[7];
                                etco2 = (data[6] * 128 + data[7]) / 10.0f;
                                break;
                            case (byte) 0x03://呼吸率
                                rr = data[6] * 27 + data[7];
                                break;
                            case (byte) 0x04://FiCO2值
                                fico2 = data[6] * 27 + data[7];
                                break;
                            case (byte) 0x07://硬件状态
                                //   Console.WriteLine("硬件状态" + data[6] + "  " + data[7]);
                                break;
                        }
                        break;
                    case (byte) 0x0A://CO2状态或错误
                        if (data[5] == 0x01) {
                            int state0 = data[6] & 0x01;
                            int state1 = data[6] << 1 & 0x01;
                            int state2 = data[6] << 2 & 0x01;
                            int state3 = data[6] << 3 & 0x01;
                            int state4 = data[6] << 4 & 0x01;
                            int state5 = data[6] << 5 & 0x01;
                            int state6 = data[6] << 6 & 0x01;
                            int state7 = data[6] << 7 & 0x01;

                            int state10 = data[7] & 0x01;
                            int state11 = data[7] << 1 & 0x01;
                            int state12 = data[7] << 2 & 0x01;
                            int state13 = data[7] << 3 & 0x01;
                            int state14 = data[7] << 4 & 0x01;
                            int state15 = data[7] << 5 & 0x01;
                            int state16 = data[7] << 6 & 0x01;
                            int state17 = data[7] << 7 & 0x01;

                            //  Console.WriteLine("CO2状态" + data[6] + "  " + data[7]);
                        }

                        break;
                }
                break;
            case (byte) 0x82://校正零点
                switch (data[2]) {
                    case (byte) 0x00:
                        //    Console.WriteLine("较零开始");
                        break;
                    case (byte) 0x01:
                        //   Console.WriteLine("模块还未准备好较零");
                        break;
                    case (byte) 0x02:
                        //   Console.WriteLine("模块已经在较零");
                        break;
                    case (byte) 0x03:
                        //   Console.WriteLine("模块尝试较零而且在过去的20 秒内检测到呼吸");
                        break;
                }
                break;
            case (byte) 0x84://读取或更改模块设置
                switch (data[2]) {
                    case (byte) 0x00://
                        //  Console.WriteLine("无效的仪器或参数设置");
                        break;
                    case (byte) 0x01://大气压
                        baroPress = data[3] << 7 | data[4];
                        break;
                    case (byte) 0x04://气体温度
                        gasTemp = (data[3] << 7 | data[4]) / 10.0f;
                        ;
                        break;
                    case (byte) 0x05://ETCO2 时间周期
                        etco2Period = data[3];
                        break;
                    case (byte) 0x06://窒息时间
                        apneaTime = data[3];
                        break;
                    case (byte) 0x09://零点气类型
                        zeroGasType = data[3];
                        //switch (data[3])
                        //{
                        //    case 0x00:
                        //        zeroGasType = "N2";//在氮气中较零
                        //        break;
                        //    case 0x01:
                        //        zeroGasType = "Air";//在空气中较零
                        //        break;
                        //}
                        break;
                    case (byte) 0x0B://读取/设置 气体补偿
                        o2Compens = data[3];// 氧气补偿 默认值16% 分辨率 1%
                        balanceGas = data[4];
                        //switch (data[4])
                        //{
                        //    case 0x00:
                        //        Balance_Gas = "Air";// 空气
                        //        break;
                        //    case 0x01:
                        //        Balance_Gas = "N20";// 笑气
                        //        break;
                        //    case 0x02:
                        //        Balance_Gas = "Helium";//氦气
                        //        break;
                        //}
                        anesth = (data[5] << 7 + data[6]) / 10f;
                        break;
                    case (byte) 0x1B://停止采样气泵
                        int air_pump = data[3];
                        break;
                }
                break;
            case (byte) 0xC8://非应答错误
                switch (data[2]) {
                    case (byte) 0x01:
                        answer = "无效的指令";//当收到不支持的指令
                        break;
                    case (byte) 0x02:
                        answer = "校验和错误";//收到不正确的校验和
                        break;
                    case (byte) 0x03:
                        answer = "超时";//接收同一帧中的第一个字节和最后一个字节之间的间隔超过500ms
                        break;
                    case (byte) 0x04:
                        answer = "无效的字节长度";//当NBF 的长度小于该指令期望的数据长度
                        break;
                    case (byte) 0x05:
                        answer = "无效的数据";//数据的最高位为1
                        break;
                    case (byte) 0x06:
                        answer = "系统故障";
                        break;
                }


                break;
            case (byte) 0xC9://停止连续模式
                if (data[2] == 0x36)
                    //   Console.WriteLine("开始或停止连续模式");
                    break;
            case (byte) 0xCC://消除窒息状态
                if (data[2] == 0x33)
                    //   Console.WriteLine("消除窒息状态");
                    break;

            default:
                break;
        }
    }


    //计算校验和
    private static byte CheckSum(byte[] cmd) {
        byte sum = 0;
        for (int i = 0; i < cmd.length; i++)
            sum += cmd[i];
        sum = (byte) ((~sum + 1) & 0x7F);
        return sum;
    }

    //发送命令
    public static byte[] SendCmd(byte[] cmd) {
        byte[] cmd_new = new byte[cmd.length + 1];
        for (int i = 0; i < cmd.length; i++)
            cmd_new[i] = cmd[i];
        cmd_new[cmd.length] = CheckSum(cmd);
        return cmd_new;
        // com.Write(cmd_new,0,cmd_new.Length);
    }
}
