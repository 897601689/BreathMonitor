package com.breathmonitor.parsing.co2Breath;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.MySerialPort;

/**
 * Created by Administrator on 2017/6/7.
 */

public class BreathCO2 {

    MySerialPort mSerialPort;

    int mDemoCo2Pos = 0;

    List<Byte> mSerialDatas = new ArrayList<Byte>();
    List<Byte> mCacheDatas = new ArrayList<Byte>();
    List<byte[]> mBreathDatas = new ArrayList<byte[]>();
    List<byte[]> mCo2Datas = new ArrayList<byte[]>();

    public List<byte[]> getBreathData() {
        return mBreathDatas;
    }

    public List<byte[]> getCo2Data() {
        return mCo2Datas;
    }

    List<TypeConfig> mFindTypes = new ArrayList<TypeConfig>();


    public BreathCO2(final MySerialPort mSerialPort) {

        //region 添加数据类型
        TypeConfig co2Config = new TypeConfig();
        co2Config.name = "co2";
        co2Config.start = (byte) 0xfd;
        co2Config.end = (byte) 0xdf;
        co2Config.maxLength = 10;      //12  + 头尾
        co2Config.minLength = 5;


        TypeConfig breathConfig = new TypeConfig();
        breathConfig.name = "breath";
        breathConfig.start = (byte) 0xff;
        breathConfig.end = (byte) 0xee;
        breathConfig.maxLength = 14;      //12  + 头尾
        breathConfig.minLength = 12;

        mFindTypes.add(breathConfig);
        mFindTypes.add(co2Config);
        //endregion
        //  mSerialPort=new MySerialPort();
        //  mSerialPort.Open("/dev/ttyMT1", 38400);
        this.mSerialPort = mSerialPort;
    }

    public void Parsing() {

        readSerialAll();
        dataItem();
    }

    /**
     * 解析数据
     * @param allData
     */
    public void Parsing(byte[] allData) {
        //readSerialAll();
        if (allData != null) {
            for (byte aByte : allData) {
                mSerialDatas.add(aByte);
            }
        }
        dataItem();
    }

    /**
     * 读取串口全部数据
     */
    private void readSerialAll() {
        try {
            byte[] allData = mSerialPort.Read();
            if (allData != null) {
                for (byte aByte : allData) {
                    mSerialDatas.add(aByte);
                }
            }
        } catch (Exception ex) {
            //System.out.print(ex.getMessage());
        }
    }

    //region 分开串口信息
    //分开串口信息
    TypeConfig findType(byte data) {
        for (int i = 0; i < mFindTypes.size(); i++) {
            if (mFindTypes.get(i).start == data) {
                return mFindTypes.get(i);
            }
        }
        return null;
    }

    List<Byte> mDatas = new ArrayList<>();
    boolean isFindType = false;
    TypeConfig mFindType;

    private void dataItem() {
        int serialDataLength = mSerialDatas.size();
        if (serialDataLength <= 0) return;
        mBreathDatas.clear();
        mCo2Datas.clear();
        for (int i = 0; i < mSerialDatas.size(); i++) {
            mCacheDatas.add(mSerialDatas.get(i));
        }
        mSerialDatas.clear();

        while (true) {
            if (mCacheDatas.size() <= 0) return;
            if (!isFindType) {
                mFindType = findType(mCacheDatas.get(0));
                if (mFindType != null) {
                    isFindType = true;
                }
            }

            if (isFindType) {
                int len = mCacheDatas.size();
                int maxLength = mFindType.maxLength;
                int minLength = mFindType.minLength;
                if (len >= maxLength)   //符合头而且长度够
                {
                    boolean isFind = false;
                    for (int i = 0; i < minLength - 1; i++) {
                        mDatas.add(mCacheDatas.get(0));
                        mCacheDatas.remove(0);
                    }
                    for (int i = 0; i < maxLength - minLength + 1; i++) {
                        if (mCacheDatas.get(0) == mFindType.end) {
                            isFind = true;
                            mDatas.add(mCacheDatas.get(0));
                            mCacheDatas.remove(0);

                            if (mFindType.name == "co2") {

                                Byte[] data = mDatas.toArray(new Byte[mDatas.size()]);
                                int length = data.length;
                                byte[] data1 = new byte[length - 2];
                                for (int num = 1; num < data.length - 1; num++) {
                                    data1[num - 1] = data[num];
                                }
                                //  Log.i("LB","serial_data "+ byte2HexStr(data1,data1.length));
                                mCo2Datas.add(data1);
                            }

                            if (mFindType.name == "breath") {
                                Byte[] data = mDatas.toArray(new Byte[mDatas.size()]);
                                int length = data.length;
                                byte[] data1 = new byte[length];
                                for (int num = 0; num < data.length; num++) {
                                    data1[num] = data[num];

                                }
                                mBreathDatas.add(data1);
                            }


                            break;
                        } else {
                            mDatas.add(mCacheDatas.get(0));
                            mCacheDatas.remove(0);
                        }
                    }
                    if (!isFind) {
                        //  mDatas.Clear();
                    }
                    mDatas.clear();
                    isFindType = false;
                } else {
                    break;
                }
            } else {
                mCacheDatas.remove(0);
            }

        }
        //  Log.i("LB","*******************************");
    }
    //endregion

    //region
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

    private String byte2HexStr(Byte[] b, int iLen) {
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < iLen; n++) {
            sb.append(mChars[(b[n] & 0xFF) >> 4]);
            sb.append(mChars[b[n] & 0x0F]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }
    //endregion

    //实体类
    private class TypeConfig {
        public String name;
        public int start;
        public int end;
        public int maxLength;
        public int minLength;
    }

}
