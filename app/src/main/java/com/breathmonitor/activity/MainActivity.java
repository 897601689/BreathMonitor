package com.breathmonitor.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.breathmonitor.R;
import com.breathmonitor.parsing.Breath_Parsing;
import com.breathmonitor.parsing.CO2_Parsing;
import com.breathmonitor.parsing.Mcu_Parsing;
import com.breathmonitor.parsing.SpO2_Parsing;
import com.breathmonitor.parsing.co2Breath.BreathCO2;
import com.breathmonitor.util.Global;
import com.breathmonitor.widgets.MySurfaceView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    String TAG = "MainActivity";

    BreathCO2 BreathCo2 = new BreathCO2(Global.breath_Com);
    Breath_Parsing breath = new Breath_Parsing();
    CO2_Parsing co2 = new CO2_Parsing();
    SpO2_Parsing spo2 = new SpO2_Parsing(); //血氧协议解析
    Mcu_Parsing mcu = new Mcu_Parsing();//单片机协议解析
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.textView4)
    TextView textView4;
    @BindView(R.id.co2Curve)
    MySurfaceView co2Curve;
    @BindView(R.id.spo2Curve)
    MySurfaceView spo2Curve;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideSystemUIMenu();//隐藏系统菜单
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
    }


    private void init() {

        co2Curve.setBackColor(Color.TRANSPARENT);//设置背景颜色
        co2Curve.setPen(Color.rgb(0, 255, 255));//设置曲线颜色
        co2Curve.setAmplitude(0);
        co2Curve.setMax(150);

        spo2Curve.setBackColor(Color.TRANSPARENT);//设置背景颜色
        spo2Curve.setPen(Color.rgb(50, 255, 50));//设置曲线颜色
        spo2Curve.setAmplitude(0);
        spo2Curve.setMax(127);
        try {
            Global.mcu_Com.Open("/dev/ttyMT1", 9600);//电源板
            Global.breath_Com.Open("/dev/ttyMT2", 38400);//呼吸机
            Global.spo2_Com.Open("/dev/ttyMT3", 4800);//血氧
            Log.e(TAG, "串口打开成功！");
        } catch (Exception ex) {
            Log.e(TAG, "串口打开失败！");
        }
        new Thread(new Spo2Thread()).start();
        new Thread(new BreathCO2Thread()).start();
        new Thread(new McuThread()).start();
    }

    //<editor-fold desc="隐藏系统菜单">

    private void hideSystemUIMenu() {
        //实现无标题栏（但有系统自带的任务栏）
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        //实现全屏效果
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //应用运行时，保持屏幕高亮，不锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

    }

    //</editor-fold>

    @OnClick({R.id.textView3, R.id.textView4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.textView3:
                breath.SendCmd(Global.breath_Com, Breath_Parsing.bOn, null);
                Log.e(TAG, "发送");
                break;
            case R.id.textView4:
                breath.SendCmd(Global.breath_Com, Breath_Parsing.bOff, null);
                Log.e(TAG, "发送");
                break;
        }
    }


    //<editor-fold desc="消息线程处理">

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 101://呼吸机 CO2
                    //<editor-fold desc="呼吸机 CO2">
                    /*txtB_State.setText(String.valueOf(breath.getB_State()));
                    if (breath.getB_Tidal() != null) {
                        String[] arrayTd = breath.getB_Tidal().split(" ");
                        txtTidal.setText(arrayTd[0]);
                        txtPulse.setText(arrayTd[1]);
                        //Log.e(TAG, breath.getB_State() + "  " + arrayTd[0] + "  " + arrayTd[1] + "  " + breath.getB_O2());
                    }
                    txtB_O2.setText(breath.getB_O2());

                    txtEtco2.setText(String.valueOf(co2.getEtco2()));
                    txtFico2.setText(String.valueOf(co2.getFico2()));
                    txtRr.setText(String.valueOf(co2.getRr()));
                    */
                    //Log.e(TAG, co2.getEtco2() + "  " + co2.getFico2() + "  " + co2.getRr());
                    //</editor-fold>
                    break;
                case 201://血氧
                    //<editor-fold desc="血氧">
                    //txtSpo2.setText(String.valueOf(spo2.getSpo2_value()));
                    //txtPulse.setText(String.valueOf(spo2.getPulse_value()));
                    //mProgressBar.setProgress(spo2.getPi());
                    //Log.e(TAG,spo2.getSpo2_value()+" " +spo2.getPulse_value()+" "+spo2.getPi());
                    //</editor-fold>
                    break;
                case 301://MCU
                    //<editor-fold desc="MCU">
                    if (mcu.getAc_dc() == 1) {

                    }else {
                        switch (mcu.getBat_State()) {
                            case "电量高":
                                break;
                            case "电量中":
                                break;
                            case "电量低":
                                break;
                            case "欠压报警":
                                break;
                            case "充满":
                                break;
                            default:
                                break;
                        }
                    }
                    //</editor-fold>
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    //</editor-fold>

    //呼吸机CO2解析线程
    public class BreathCO2Thread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);

                while (true) {
                    Thread.sleep(20);
                    breath.GetBreathInfo(Global.breath_Com);
                    BreathCo2.Parsing();
                    List<byte[]> co2s = BreathCo2.getCo2Data();
                    List<byte[]> breaths = BreathCo2.getBreathData();
                    if (breaths.size() > 0) {
                        breath.Parsing(breaths);
                    }
                    if (co2s.size() > 0) {

                        co2.Parsing(co2s);//CO2上限150
                        List<Float> data_co2 = co2.getCo2_curve();
                        //CO2波形

                        if (data_co2.size() > 0) {
                            for (float i : data_co2) {
                                //Thread.sleep(10);
                                co2Curve.setCurve(i);
                            }
                        } else {
                            co2Curve.setCurve(-1);
                        }
                    }

                    // 发送这个消息到消息队列中
                    mHandler.sendEmptyMessage(101);
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }


    }

    //血氧解析线程
    public class Spo2Thread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                while (true) {
                    Thread.sleep(15);
                    spo2.Parsing(Global.spo2_Com);
                    mHandler.sendEmptyMessage(201);
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
            }

        }
    }

    byte[] mcuData = new byte[]{(byte) 0xff, 0x32, 0x01, (byte) 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xee};

    //单片机解析线程(电池状态)
    public class McuThread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                while (true) {
                    Thread.sleep(50);
                    Global.mcu_Com.Write(mcuData);
                    mcu.Parsing(Global.mcu_Com);

                    mHandler.sendEmptyMessage(301);
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }
    }

    //报警和时间显示
    public class AlarmAndTime implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(1000);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
