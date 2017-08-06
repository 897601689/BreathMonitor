package com.breathmonitor.activity;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.breathmonitor.R;
import com.breathmonitor.parsing.Breath_Parsing;
import com.breathmonitor.parsing.CO2_Parsing;
import com.breathmonitor.parsing.Mcu_Parsing;
import com.breathmonitor.parsing.SpO2_Parsing;
import com.breathmonitor.parsing.co2Breath.BreathCO2;
import com.breathmonitor.util.Global;
import com.breathmonitor.widgets.AlarmImageView;
import com.breathmonitor.widgets.MySurfaceView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    //<editor-fold desc="控件">
    @BindView(R.id.co2Curve)
    MySurfaceView co2Curve;
    @BindView(R.id.spo2Curve)
    MySurfaceView spo2Curve;
    @BindView(R.id.txt_alarm)
    TextView txtAlarm;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.img_voice)
    ImageView imgVoice;
    @BindView(R.id.img_battery)
    ImageView imgBattery;
    @BindView(R.id.img_lock)
    ImageView imgLock;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.breath_set)
    Button breathSet;
    @BindView(R.id.txt_switch)
    TextView txtSwitch;
    @BindView(R.id.txt_mode)
    TextView txtMode;
    @BindView(R.id.txt_tidal)
    TextView txtTidal;
    @BindView(R.id.txt_frequency)
    TextView txtFrequency;
    @BindView(R.id.txt_o2)
    TextView txtO2;
    @BindView(R.id.img_resp_alarm)
    AlarmImageView imgRespAlarm;
    @BindView(R.id.layout_resp)
    LinearLayout layoutResp;
    @BindView(R.id.txt_resp_alert_h)
    TextView txtRespAlertH;
    @BindView(R.id.txt_resp_alert_l)
    TextView txtRespAlertL;
    @BindView(R.id.txt_resp)
    TextView txtResp;
    @BindView(R.id.img_etCo2_alarm)
    AlarmImageView imgEtCo2Alarm;
    @BindView(R.id.layout_etCo2)
    LinearLayout layoutEtCo2;
    @BindView(R.id.txt_etCo2_alert_h)
    TextView txtEtCo2AlertH;
    @BindView(R.id.txt_etCo2_alert_l)
    TextView txtEtCo2AlertL;
    @BindView(R.id.txt_etCo2)
    TextView txtEtCo2;
    @BindView(R.id.img_spo2_alarm)
    AlarmImageView imgSpo2Alarm;
    @BindView(R.id.layout_spo2)
    LinearLayout layoutSpo2;
    @BindView(R.id.txt_spo2_alert_h)
    TextView txtSpo2AlertH;
    @BindView(R.id.txt_spo2_alert_l)
    TextView txtSpo2AlertL;
    @BindView(R.id.txt_spo2)
    TextView txtSpo2;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.img_pulse_alarm)
    AlarmImageView imgPulseAlarm;
    @BindView(R.id.layout_pulse)
    LinearLayout layoutPulse;
    @BindView(R.id.txt_pulse_alert_h)
    TextView txtPulseAlertH;
    @BindView(R.id.txt_pulse_alert_l)
    TextView txtPulseAlertL;
    @BindView(R.id.txt_pulse)
    TextView txtPulse;
    //</editor-fold>

    String TAG = "MainActivity";

    BreathCO2 BreathCo2 = new BreathCO2(Global.breath_Com);
    Breath_Parsing breath = new Breath_Parsing();
    CO2_Parsing co2 = new CO2_Parsing();
    SpO2_Parsing spo2 = new SpO2_Parsing(); //血氧协议解析
    Mcu_Parsing mcu = new Mcu_Parsing();//单片机协议解析
    SimpleDateFormat format;

    List<String> mSafeAlertMessage = new ArrayList<>();//生理参数报警
    List<String> mTechnologyMessage = new ArrayList<>();//技术报警

    byte[] breathOn = new byte[]{(byte) 0xff, 0x32, 0x01, (byte) 0x81, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xee};
    byte[] spo2On = new byte[]{(byte) 0xff, 0x32, 0x01, (byte) 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xee};
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
        spo2Curve.setmCurveType(1);
        spo2Curve.setAmplitude(0);
        spo2Curve.setMax(127);
        /*try {
            Global.mcu_Com.Open("/dev/ttyMT1", 9600);//电源板
            Global.breath_Com.Open("/dev/ttyMT2", 38400);//呼吸机
            Global.spo2_Com.Open("/dev/ttyMT3", 4800);//血氧
            Log.e(TAG, "串口打开成功！");
        } catch (Exception ex) {
            Log.e(TAG, "串口打开失败！");
        }
        //new Thread(new Spo2Thread()).start();
        //new Thread(new BreathCO2Thread()).start();
        //new Thread(new McuThread()).start();
        //new Thread(new AlarmAndTime()).start();
        try {
            Global.mcu_Com.Write(breathOn);
            Global.mcu_Com.Write(spo2On);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
        if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

    }

    //</editor-fold>

    //<editor-fold desc="消息线程处理">

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 101://呼吸机 CO2
                    //<editor-fold desc="呼吸机 CO2">
                    txtSwitch.setText(String.valueOf(breath.getB_State()));
                    if (breath.getB_Tidal() != null) {
                        String[] arrayTd = breath.getB_Tidal().split(" ");
                        txtTidal.setText(arrayTd[0]);
                        txtFrequency.setText(arrayTd[1]);
                        Log.e(TAG, breath.getB_State() + "  " + arrayTd[0] + "  " + arrayTd[1] + "  " + breath.getB_O2());
                    }
                    txtO2.setText(String.valueOf(breath.getB_O2()));

                    txtEtCo2.setText(String.valueOf(co2.getEtco2()));
                    //txtFico2.setText(String.valueOf(co2.getFico2()));
                    txtResp.setText(String.valueOf(co2.getRr()));

                    //Log.e(TAG, co2.getEtco2() + "  " + co2.getFico2() + "  " + co2.getRr());
                    //</editor-fold>
                    break;
                case 201://血氧
                    //<editor-fold desc="血氧">
                    if (spo2.getSpo2_value() == 0) {
                        txtSpo2.setText("--");
                    } else {
                        txtSpo2.setText(String.valueOf(spo2.getSpo2_value()));
                    }
                    if (spo2.getPulse_value() == 0) {
                        txtPulse.setText("--");
                    } else {
                        txtPulse.setText(String.valueOf(spo2.getPulse_value()));
                    }
                    progressBar.setProgress(spo2.getPi());
                    //Log.e(TAG,spo2.getSpo2_value()+" " +spo2.getPulse_value()+" "+spo2.getPi());
                    //</editor-fold>
                    break;
                case 301://MCU
                    //<editor-fold desc="MCU电池状态">
                    if (mcu.getAc_dc() == 1) {
                        imgBattery.setImageResource(R.mipmap.battery);
                    } else {
                        switch (mcu.getBat_State()) {
                            case "电量高":
                                imgBattery.setImageResource(R.mipmap.battery80);
                                break;
                            case "电量中":
                                imgBattery.setImageResource(R.mipmap.battery60);
                                break;
                            case "电量低":
                                imgBattery.setImageResource(R.mipmap.battery40);
                                break;
                            case "欠压报警":
                                imgBattery.setImageResource(R.mipmap.battery20);
                                break;
                            case "充满":
                                imgBattery.setImageResource(R.mipmap.battery100);
                                break;
                            default:
                                imgBattery.setImageResource(R.mipmap.battery0);
                                break;
                        }
                    }
                    //</editor-fold>
                    break;
                case 401:
                    format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    txtTime.setText(format.format(new Date()));
                    Alarm();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @OnClick({R.id.txt_time, R.id.img_voice, R.id.img_lock, R.id.breath_set, R.id.txt_switch, R.id.layout_resp, R.id.layout_etCo2, R.id.layout_spo2, R.id.layout_pulse})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_time:
                Log.e(TAG, "onViewClicked: 时间设置");
                break;
            case R.id.img_voice:
                Log.e(TAG, "onViewClicked: 静音");
                break;
            case R.id.img_lock:
                Log.e(TAG, "onViewClicked: 锁");
                break;
            case R.id.breath_set:
                Log.e(TAG, "onViewClicked: breath");
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, BreathActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_switch:
                if ("OFF".equals(txtSwitch.getText())) {
                    Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bOn, null);
                } else {
                    Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bOff, null);
                }
                break;
            case R.id.layout_resp:
                Log.e(TAG, "onViewClicked: resp");
                break;
            case R.id.layout_etCo2:
                Log.e(TAG, "onViewClicked: etCO2");
                break;
            case R.id.layout_spo2:
                Log.e(TAG, "onViewClicked: spo2");
                break;
            case R.id.layout_pulse:
                Log.e(TAG, "onViewClicked: pulse");
                break;
        }
    }

    //</editor-fold>

    //呼吸机CO2解析线程
    private class BreathCO2Thread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);

                while (true) {
                    Thread.sleep(10);
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
    private class Spo2Thread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                while (true) {
                    Thread.sleep(15);
                    spo2.Parsing(Global.spo2_Com);

                    List<Integer> data_spo2 = spo2.getSpo2_Curve();

                    if (data_spo2.size() > 0) {
                        if (spo2.getPulse_value() == 0) {
                            for (float i : data_spo2) {
                                spo2Curve.setCurve(1);
                            }
                        } else {
                            for (float i : data_spo2) {
                                //Thread.sleep(20);
                                spo2Curve.setCurve(i);
                            }
                        }
                    } else {
                        spo2Curve.setCurve(-1);
                    }

                    mHandler.sendEmptyMessage(201);
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }
    }

    byte[] mcuData = new byte[]{(byte) 0xff, 0x32, 0x01, (byte) 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xee};

    //单片机解析线程(电池状态)
    private class McuThread implements Runnable {

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
                    mHandler.sendEmptyMessage(401);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void Alarm() {
        float now = 0;
        int H = Integer.parseInt(txtEtCo2AlertH.getText().toString());
        int L = Integer.parseInt(txtEtCo2AlertL.getText().toString());
        if (!"--".equals(txtEtCo2.getText())) {
            now = Float.parseFloat(txtEtCo2.getText().toString());
        }
        if (now > H) {
            imgEtCo2Alarm.setLevel(2);
            if (Global.etCo2_alarm) {
                if (!mSafeAlertMessage.contains("Co2过高"))
                    mSafeAlertMessage.add("Co2过高");
            }
        } else if (now < L) {
            imgEtCo2Alarm.setLevel(2);
            if (Global.etCo2_alarm) {
                if (!mSafeAlertMessage.contains("Co2过低"))
                    mSafeAlertMessage.add("Co2过低");
            }
        } else {
            imgEtCo2Alarm.setLevel(0);
        }
    }

}
