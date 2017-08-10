package com.breathmonitor.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.breathmonitor.R;
import com.breathmonitor.bean.Breath;
import com.breathmonitor.parsing.Breath_Parsing;
import com.breathmonitor.util.Global;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by i on 2017/8/6.
 */

public class BreathActivity extends Activity {


    //<editor-fold desc="控件">
    @BindView(R.id.dialog_txt)
    TextView dialogTxt;
    @BindView(R.id.spinner_mode)
    Spinner spinnerMode;
    @BindView(R.id.spinner_o2)
    Spinner spinnerO2;
    @BindView(R.id.spinner_hz)
    Spinner spinnerHz;
    @BindView(R.id.spinner_tidal)
    Spinner spinnerTidal;
    @BindView(R.id.cancel_cancel_txt)
    TextView cancelCancelTxt;
    @BindView(R.id.cancel_sure_txt)
    TextView cancelSureTxt;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;
    @BindView(R.id.dialog_btn_layout)
    LinearLayout dialogBtnLayout;
    @BindView(R.id.cancel_layout)
    RelativeLayout cancelLayout;
    //</editor-fold>

    String[] mHzItems = new String[]{"8", "10", "12", "15"};
    String[] mO2Items = new String[]{"21", "30", "40", "50", "60", "80", "100"};
    String[] mTd8Items = new String[]{"1200", "1300", "1400", "1500"};
    String[] mTd10Items = new String[]{"500", "600", "700", "800", "950", "1000", "1100"};
    String[] mTd12Items = new String[]{"300", "400"};
    String[] mTd15Items = new String[]{"200"};
    final String TAG = "BreathActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUIMenu();
        setContentView(R.layout.activity_breath);
        ButterKnife.bind(this);

        init();
    }

    private void init() {

        final Breath mBreath = Global.mApp.getBreathShared();
        String ss = mBreath.getB_Hz();
        for (int i = 0; i < mHzItems.length; i++) {
            if (mHzItems[i].equals(ss)) {
                spinnerHz.setSelection(i);
            }
        }
        ss = mBreath.getB_O2();
        for (int i = 0; i < mO2Items.length; i++) {
            if (mO2Items[i].equals(ss)) {
                spinnerO2.setSelection(i);
            }
        }
        spinnerHz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> adapter;
                String[] array = null;
                // Log.i("LB",mHzItems[position]);
                if (mHzItems[position].equals("8")) {
                    array = mTd8Items;
                    adapter = new ArrayAdapter<>(BreathActivity.this, R.layout.simple_spinner_item, mTd8Items);
                    adapter.setDropDownViewResource(R.layout.my_drop_down_item);
                    //绑定 Adapter到控件
                    spinnerTidal.setAdapter(adapter);
                }
                if (mHzItems[position].equals("12")) {
                    array = mTd12Items;
                    adapter = new ArrayAdapter<>(BreathActivity.this, R.layout.simple_spinner_item, mTd12Items);
                    adapter.setDropDownViewResource(R.layout.my_drop_down_item);
                    //绑定 Adapter到控件
                    spinnerTidal.setAdapter(adapter);
                }
                if (mHzItems[position].equals("10")) {
                    array = mTd10Items;
                    adapter = new ArrayAdapter<>(BreathActivity.this, R.layout.simple_spinner_item, mTd10Items);
                    adapter.setDropDownViewResource(R.layout.my_drop_down_item);
                    //绑定 Adapter到控件
                    spinnerTidal.setAdapter(adapter);
                }
                if (mHzItems[position].equals("15")) {
                    array = mTd15Items;
                    //adapter = new ArrayAdapter<>(BreathActivity.this, R.layout.simple_spinner_item, mTd15Items);
                    //adapter.setDropDownViewResource(R.layout.my_drop_down_item);
                    adapter = new ArrayAdapter<>(BreathActivity.this, android.R.layout.simple_spinner_item, mTd15Items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //绑定 Adapter到控件
                    spinnerTidal.setAdapter(adapter);
                }
                if (array != null) {
                    String ss = mBreath.getB_Tidal();
                    for (int i = 0; i < array.length; i++) {
                        if (array[i].equals(ss)) {
                            spinnerTidal.setSelection(i);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //隐藏系统菜单
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

    @OnClick({R.id.cancel_cancel_txt, R.id.cancel_sure_txt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_cancel_txt:
                finish();
                break;
            case R.id.cancel_sure_txt:
                if (!Global.BreathIsOpen) {
                    //保存参数设置
                    Global.mApp.setBreathShared(new Breath(spinnerMode.getSelectedItem().toString(),
                            spinnerTidal.getSelectedItem().toString(), spinnerHz.getSelectedItem().toString(),
                            spinnerO2.getSelectedItem().toString()));

                    //<editor-fold desc="发送设置命令">
                    switch ((int) spinnerMode.getSelectedItemId()) {
                        case 0:
                            Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bSControl, null);
                            break;
                        case 1:
                            Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bSControl, null);
                            break;
                    }
                    switch ((int) spinnerO2.getSelectedItemId()) {
                        case 0:
                            Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bSO2_21, null);
                            break;
                        case 1:
                            Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bSO2_40, null);
                            break;
                        case 2:
                            Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bSO2_30, null);
                            break;
                        case 3:
                            Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bSO2_50, null);
                            break;
                        case 4:
                            Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bSO2_60, null);
                            break;

                    }
                    Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bSHz, this.spinnerHz.getSelectedItem().toString());
                    Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bSTidal, this.spinnerTidal.getSelectedItem().toString());
                    //</editor-fold>
                }
                finish();
                break;
        }
    }
}
