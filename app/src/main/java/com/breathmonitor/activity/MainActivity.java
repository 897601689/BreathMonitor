package com.breathmonitor.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.breathmonitor.bean.Alert;
import com.breathmonitor.bean.Breath;
import com.breathmonitor.parsing.Breath_Parsing;
import com.breathmonitor.parsing.CO2_Parsing;
import com.breathmonitor.parsing.Mcu_Parsing;
import com.breathmonitor.parsing.Mcu_Parsing2;
import com.breathmonitor.parsing.SpO2_Parsing;
import com.breathmonitor.parsing.co2Breath.BreathCO2;
import com.breathmonitor.util.Global;
import com.breathmonitor.util.MonitorApplication;
import com.breathmonitor.widgets.AlarmImageView;
import com.breathmonitor.widgets.MySurfaceView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    //<editor-fold desc="控件">
    @BindView(R.id.txt_lock)
    Button txtLock;
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
    @BindView(R.id.co2_Waveform)
    TextView co2Waveform;
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

    String TAG = "Main Activity";

    BreathCO2 BreathCo2 = new BreathCO2(Global.breath_Com);
    CO2_Parsing co2 = new CO2_Parsing();
    SpO2_Parsing spo2 = new SpO2_Parsing(); //血氧协议解析
    Mcu_Parsing mcu = new Mcu_Parsing();//单片机协议解析
    Mcu_Parsing2 mcu2 = new Mcu_Parsing2();//单片机协议解析
    SimpleDateFormat format;

    List<String> mSafeAlertMessage = new ArrayList<>();//生理参数报警
    List<String> mTechnologyMessage = new ArrayList<>();//技术报警
    @BindView(R.id.layout_breath_set)
    LinearLayout layoutBreathSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideSystemUIMenu();//隐藏系统菜单
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();

        initInfo();
    }

    //Activity创建或者从被覆盖、后台重新回到前台时被调用
    @Override
    protected void onResume() {
        super.onResume();
        //Log.e(TAG, "onResume called.");
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    //Activity被覆盖到下面或者锁屏时被调用
    @Override
    protected void onPause() {
        super.onPause();
        //Log.e(TAG, "onPause called.");
        //有可能在执行完onPause或onStop后,系统资源紧张将Activity杀死,所以有必要在此保存持久数据
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        try {
            Thread.sleep(50);
            Mcu_Parsing.SendCmd(Global.mcu_Com, Mcu_Parsing.alarm_off);
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
        try {
            Global.mcu_Com.Open("/dev/ttysWK1", 9600);//电源板 ttysWK1
            Global.breath_Com.Open("/dev/ttysWK3", 38400);//呼吸机
            Global.spo2_Com.Open("/dev/ttyMT3", 4800);//血氧
            Log.e(TAG, "串口打开成功！");

            //Global.mcu_Com.Write(Mcu_Parsing.breathAndSpo2On);//血氧和呼吸机上电
            //Thread.sleep(50);
            //Global.mcu_Com.Write(Mcu_Parsing.breathAndSpo2On);//血氧和呼吸机上电
        } catch (Exception ex) {
            Log.e(TAG, "串口打开失败！");
        }

        new Thread(new Spo2Thread()).start();
        new Thread(new BreathCO2Thread()).start();
        new Thread(new McuThread()).start();
        new Thread(new AlarmAndTime()).start();


        IntentFilter filter = new IntentFilter(AlertActivity.action);
        registerReceiver(broadcastReceiver, filter);
    }

    private void initInfo() {
        Global.mApp = (MonitorApplication) getApplication();
        Breath mBreath = Global.mApp.getBreathShared();
        if (mBreath != null) {
            Log.e(TAG, "initInfo: " + mBreath.toString());
            Breath_Parsing.InitBreath(Global.breath_Com, mBreath.getB_Mode(),
                    mBreath.getB_Tidal(), mBreath.getB_Hz(), mBreath.getB_O2());
        }
        Alert alert = Global.mApp.getAlertShared("Resp");
        Global.resp_alarm = alert.isAlarmSwitch();
        txtRespAlertH.setText(String.valueOf(alert.getAlert_H()));
        txtRespAlertL.setText(String.valueOf(alert.getAlert_L()));

        alert = Global.mApp.getAlertShared("EtCO2");
        Global.etCo2_alarm = alert.isAlarmSwitch();
        txtEtCo2AlertH.setText(String.valueOf(alert.getAlert_H()));
        txtEtCo2AlertL.setText(String.valueOf(alert.getAlert_L()));

        alert = Global.mApp.getAlertShared("SpO2");
        Global.spo2_alarm = alert.isAlarmSwitch();
        txtSpo2AlertH.setText(String.valueOf(alert.getAlert_H()));
        txtSpo2AlertL.setText(String.valueOf(alert.getAlert_L()));

        alert = Global.mApp.getAlertShared("Pulse");
        Global.pulse_alarm = alert.isAlarmSwitch();
        txtPulseAlertH.setText(String.valueOf(alert.getAlert_H()));
        txtPulseAlertL.setText(String.valueOf(alert.getAlert_L()));
    }


    //<editor-fold desc="隐藏系统菜单">

    public void hideSystemUIMenu() {
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

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 101://呼吸机 CO2
                    //<editor-fold desc="呼吸机 CO2">
                    txtSwitch.setText(String.valueOf(Global.breath.getB_State()));
                    if (Global.breath.getB_Tidal() != null) {
                        String[] arrayTd = Global.breath.getB_Tidal().split(" ");
                        txtTidal.setText(arrayTd[0]);
                        txtFrequency.setText(arrayTd[1]);
                        //Log.e(TAG, Breath.getB_State() + "  " + arrayTd[0] + "  " + arrayTd[1] + "  " + Breath.getB_O2());
                    }
                    txtO2.setText(String.valueOf(Global.breath.getB_O2()));

                    //if (co2.getEtco2() != 0) {
                    txtEtCo2.setText(String.valueOf(co2.getEtco2()));
                    //} else {
                    //    txtEtCo2.setText("--");
                    //}
                    //txtFico2.setText(String.valueOf(co2.getFico2()));
                    //if (co2.getRr() != 0) {
                    txtResp.setText(String.valueOf(co2.getRr()));
                    //} else {
                    //    txtResp.setText("--");
                    //}
                    //CO2曲线瞬时值
                    co2Waveform.setText(String.valueOf(Global.co2Waveform));

                    //Log.e(TAG, co2.getEtco2() + "  " + co2.getFico2() + "  " + co2.getRr());
                    if (!"".equals(Global.breath.getB_Alarm())) {
                        if (!mTechnologyMessage.contains(Global.breath.getB_Alarm()))
                            mTechnologyMessage.add(Global.breath.getB_Alarm());
                    }


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

                    /*if (!"".equals(spo2.getError())) {
                        if (!mTechnologyMessage.contains(spo2.getError())) {
                            mTechnologyMessage.add(spo2.getError());
                        }
                    }*/
                    //</editor-fold>
                    break;
                case 301://MCU

                    //imgBattery.setImageResource(R.mipmap.battery100);
                    //<editor-fold desc="MCU电池状态">
                    //Log.e("McuData", "" + mcu2.getBat_State());


                    /*if (mcu.getAc_dc() == 1) {
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
                    }*/

                    if (mcu2.getAc_dc() == 1) {
                        imgBattery.setImageResource(R.mipmap.battery);
                    } else {
                       // Log.e(TAG, "handleMessage: "+mcu2.getBat_State() );
                        switch (mcu2.getBat_State()) {
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

                    //<editor-fold desc="自动解除静音">
                    if (Global.Mute) {
                        if (Global.pause_time == Global.alarm_pause_time) {
                            Mute();
                            Global.pause_time = 0;
                        } else {
                            Global.pause_time++;
                        }

                    }
                    //</editor-fold>

                    //<editor-fold desc="自动锁屏">
                    if (!Global.lock) {
                        if (Global.lock_time == Global.lock_start_time) {
                            Lock();
                            Global.lock_time = 0;
                        } else {
                            Global.lock_time++;
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

    @OnClick({R.id.layout_breath_set,R.id.txt_time, R.id.img_voice, R.id.img_lock, R.id.breath_set, R.id.txt_switch, R.id.layout_resp, R.id.layout_etCo2, R.id.layout_spo2, R.id.layout_pulse})
    public void onViewClicked(View view) {
        Bundle bundle;
        Intent intent;
        switch (view.getId()) {
            case R.id.txt_time:
                //Log.e(TAG, "onViewClicked: 时间设置");

                break;
            case R.id.img_voice:
                //Log.e(TAG, "onViewClicked: 静音");
                Mute();

                break;
            case R.id.img_lock:
                //Log.e(TAG, "onViewClicked: 锁");
                Lock();

                break;
            case R.id.breath_set:
                //Log.e(TAG, "onViewClicked: Breath");
                if (IsMinClickTime()) {
                    intent = new Intent(MainActivity.this, BreathActivity.class);
                    bundle = new Bundle();
                    bundle.putString("name", "Breath");
                    bundle.putString("mode", "");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.layout_breath_set:
                if (IsMinClickTime()) {
                    intent = new Intent(MainActivity.this, BreathActivity.class);
                    bundle = new Bundle();
                    bundle.putString("name", "Breath");
                    bundle.putString("mode", "");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.txt_switch:
                if ("OFF".equals(txtSwitch.getText())) {
                    Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bOn, null);
                } else {
                    Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bOff, null);
                }
                break;
            case R.id.layout_resp:
                if (IsMinClickTime()) {
                    intent = new Intent(MainActivity.this, AlertActivity.class);
                    bundle = new Bundle();
                    bundle.putString("title", "呼吸");
                    bundle.putString("name", "Resp");
                    bundle.putString("limit_H", txtRespAlertH.getText().toString());
                    bundle.putString("limit_L", txtRespAlertL.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.layout_etCo2:
                if (IsMinClickTime()) {
                    intent = new Intent(MainActivity.this, AlertActivity.class);
                    bundle = new Bundle();
                    bundle.putString("title", "CO2");
                    bundle.putString("name", "EtCO2");
                    bundle.putString("limit_H", txtEtCo2AlertH.getText().toString());
                    bundle.putString("limit_L", txtEtCo2AlertL.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.layout_spo2:
                if (IsMinClickTime()) {
                    intent = new Intent(MainActivity.this, AlertActivity.class);
                    bundle = new Bundle();
                    bundle.putString("title", "血氧");
                    bundle.putString("name", "SpO2");
                    bundle.putString("limit_H", txtSpo2AlertH.getText().toString());
                    bundle.putString("limit_L", txtSpo2AlertL.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.layout_pulse:
                if (IsMinClickTime()) {
                    intent = new Intent(MainActivity.this, AlertActivity.class);
                    bundle = new Bundle();
                    bundle.putString("title", "脉率");
                    bundle.putString("name", "Pulse");
                    bundle.putString("limit_H", txtPulseAlertH.getText().toString());
                    bundle.putString("limit_L", txtPulseAlertL.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
        }
    }

    //锁
    private void Lock() {
        Global.lock = !Global.lock;
        if (Global.lock) {
            imgLock.setImageResource(R.mipmap.lock);
            txtLock.setVisibility(View.VISIBLE);
        } else {
            imgLock.setImageResource(R.mipmap.unlock);
            txtLock.setVisibility(View.GONE);
        }
    }

    //静音
    private void Mute() {
        try {
            Global.Mute = !Global.Mute;
            if (Global.Mute) {
                imgVoice.setImageResource(R.mipmap.sound_off);
                Thread.sleep(100);
                Mcu_Parsing.SendCmd(Global.mcu_Com, Mcu_Parsing.voice_off);
            } else {
                imgVoice.setImageResource(R.mipmap.sound_on);
                Thread.sleep(100);
                Mcu_Parsing.SendCmd(Global.mcu_Com, Mcu_Parsing.voice_on);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    //呼吸机CO2解析线程
    private class BreathCO2Thread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);

                while (true) {
                    Thread.sleep(100);
                    Global.breath.GetBreathInfo(Global.breath_Com);

                    BreathCo2.Parsing();
                    List<byte[]> co2s = BreathCo2.getCo2Data();
                    List<byte[]> breaths = BreathCo2.getBreathData();
                    if (breaths.size() > 0) {
                        Global.breath.Parsing(breaths);
                    }
                    if (co2s.size() > 0) {

                        co2.Parsing(co2s);//CO2上限150
                        List<Float> data_co2 = co2.getCo2_curve();
                        //CO2波形
                        if (data_co2.size() > 0) {
                            for (float i : data_co2) {
                                Thread.sleep(5);
                                Global.co2Waveform = i;
                                co2Curve.setCurve(i);
                            }
                        } else {
                            Global.co2Waveform = 0;
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

    //单片机解析线程(电池状态)
    private class McuThread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(2000);
                while (true) {
                    Thread.sleep(2000);
                    //Global.mcu_Com.Write(Mcu_Parsing.mcuData);
                    //mcu.Parsing(Global.mcu_Com);
                    Mcu_Parsing2.SendMcuCmd(Global.mcu_Com, Mcu_Parsing2.cellCmd);//查询电池信息
                    mcu2.Parsing(Global.mcu_Com);
                    mHandler.sendEmptyMessage(301);
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }
    }

    //报警和时间显示
    private class AlarmAndTime implements Runnable {

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


    //<editor-fold desc="报警判断">
    boolean isOneOrTwo = false;

    private void Alarm() {
        //呼吸率报警
        AddAlarmInfo(txtResp, txtRespAlertH, txtRespAlertL, imgRespAlarm, Global.resp_alarm, "呼吸率");
        //CO2报警
        AddAlarmInfo(txtEtCo2, txtEtCo2AlertH, txtEtCo2AlertL, imgEtCo2Alarm, Global.etCo2_alarm, "CO2");
        //血氧报警
        AddAlarmInfo(txtSpo2, txtSpo2AlertH, txtSpo2AlertL, imgSpo2Alarm, Global.spo2_alarm, "血氧");
        //脉率报警
        AddAlarmInfo(txtPulse, txtPulseAlertH, txtPulseAlertL, imgPulseAlarm, Global.pulse_alarm, "脉率");

        if (isOneOrTwo) {
            if (mSafeAlertMessage.size() > 0) {
                txtAlarm.setText(mSafeAlertMessage.get(0));
                //Log.e("生理",mSafeAlertMessage.get(0));
                mSafeAlertMessage.remove(0);
                Global.isAlarm1 = true;
            } else {
                txtAlarm.setText("");
                Global.isAlarm1 = false;
            }
        } else {
            if (mTechnologyMessage.size() > 0) {
                txtAlarm.setText(mTechnologyMessage.get(0));
                //Log.e("技术",mTechnologyMessage.get(0));
                mTechnologyMessage.remove(0);
                Global.isAlarm2 = true;
            } else {
                txtAlarm.setText("");
                Global.isAlarm2 = false;
            }
        }
        isOneOrTwo = !isOneOrTwo;


        AlarmVoice();
    }

    /**
     * 添加报警信息
     *
     * @param value   当前显示值
     * @param alertH  上限值
     * @param alertL  下限值
     * @param img     报警图片
     * @param mSwitch 报警开关
     * @param name    报警项
     */
    private void AddAlarmInfo(TextView value, TextView alertH, TextView alertL, AlarmImageView img, boolean mSwitch, String name) {
        float now = 0;
        int H = Integer.parseInt(alertH.getText().toString());
        int L = Integer.parseInt(alertL.getText().toString());
        if (!"--".equals(value.getText())) {
            now = Float.parseFloat(value.getText().toString());

            if (now > H) {
                img.setLevel(2);
                if (mSwitch) {
                    if (!mSafeAlertMessage.contains(name + "过高"))
                        mSafeAlertMessage.add(name + "过高");
                }
            } else if (now < L) {
                img.setLevel(2);
                if (mSwitch) {
                    if (!mSafeAlertMessage.contains(name + "过低"))
                        mSafeAlertMessage.add(name + "过低");
                }
            } else {
                img.setLevel(0);
            }
        } else {
            img.setLevel(0);
        }
    }

    private void AlarmVoice() {
        //Log.e("tag", Global.isAlarm1 + " " +Global.isAlarm2 + " " + Global.isAlarmH + " " + Global.isAlarmM + " " + Global.isAlarmOff);

        try {
            if (Global.isAlarm1) {
                if (!Global.isAlarmH) {
                    Thread.sleep(50);
                    Mcu_Parsing.SendCmd(Global.mcu_Com, Mcu_Parsing.alarm_h);
                    Thread.sleep(50);
                    Mcu_Parsing.SendCmd(Global.mcu_Com, Mcu_Parsing.alarm_h);
                    Thread.sleep(50);
                    Mcu_Parsing.SendCmd(Global.mcu_Com, Mcu_Parsing.alarm_h);
                    Global.isAlarmH = true;

                    Global.isAlarmM = false;
                    Global.isAlarmOff = false;
                }
            } else if (Global.isAlarm2) {
                if (!Global.isAlarmM) {
                    Thread.sleep(50);
                    Mcu_Parsing.SendCmd(Global.mcu_Com, Mcu_Parsing.alarm_m);
                    Thread.sleep(50);
                    Mcu_Parsing.SendCmd(Global.mcu_Com, Mcu_Parsing.alarm_m);
                    Thread.sleep(50);
                    Mcu_Parsing.SendCmd(Global.mcu_Com, Mcu_Parsing.alarm_m);
                    Global.isAlarmM = true;

                    Global.isAlarmH = false;
                    Global.isAlarmOff = false;
                }
            } else {
                if (!Global.isAlarmOff) {
                    Thread.sleep(50);
                    Mcu_Parsing.SendCmd(Global.mcu_Com, Mcu_Parsing.alarm_off);
                    Thread.sleep(50);
                    Mcu_Parsing.SendCmd(Global.mcu_Com, Mcu_Parsing.alarm_off);
                    Thread.sleep(50);
                    Mcu_Parsing.SendCmd(Global.mcu_Com, Mcu_Parsing.alarm_off);
                    Global.isAlarmOff = true;

                    Global.isAlarmH = false;
                    Global.isAlarmM = false;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //</editor-fold>

    //<editor-fold desc="广播用于接收上下限设置">
    //广播用于接收上下限设置
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String name = intent.getExtras().getString("name");
            Log.e("TAG12", name);
            switch (name) {
                case "Breath":
                    txtMode.setText(intent.getExtras().getString("mode"));
                    break;
                case "Resp":
                    txtRespAlertH.setText(intent.getExtras().getString("limitH"));
                    txtRespAlertL.setText(intent.getExtras().getString("limitL"));
                    break;
                case "EtCO2":
                    txtEtCo2AlertH.setText(intent.getExtras().getString("limitH"));
                    txtEtCo2AlertL.setText(intent.getExtras().getString("limitL"));
                    break;
                case "SpO2":
                    txtSpo2AlertH.setText(intent.getExtras().getString("limitH"));
                    txtSpo2AlertL.setText(intent.getExtras().getString("limitL"));
                    break;
                case "Pulse":
                    txtPulseAlertH.setText(intent.getExtras().getString("limitH"));
                    txtPulseAlertL.setText(intent.getExtras().getString("limitL"));
                    break;
            }
        }
    };
    //</editor-fold>

    //<editor-fold desc="判断按钮点击时间">
    //设置点击间隔
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    //上次点击时间
    private long lastClickTime = 0;

    //是否在最小点击时间内（防止多次点击造成程序多次响应）
    private boolean IsMinClickTime() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            //Log.e("tag",""+lastClickTime);
            //做你需要的点击事件
            return true;
        }
        return false;

    }
    //</editor-fold>
}
