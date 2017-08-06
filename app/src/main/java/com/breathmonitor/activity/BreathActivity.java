package com.breathmonitor.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.breathmonitor.R;
import com.breathmonitor.parsing.Breath_Parsing;
import com.breathmonitor.util.Global;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by i on 2017/8/6.
 */

public class BreathActivity extends Activity {
    @BindView(R.id.dialog_txt)
    TextView dialogTxt;
    @BindView(R.id.spinner1)
    Spinner spinner1;
    @BindView(R.id.spinner2)
    Spinner spinner2;
    @BindView(R.id.spinner3)
    Spinner spinner3;
    @BindView(R.id.spinner4)
    Spinner spinner4;
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


    String TAG ="BreathActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUIMenu();
        setContentView(R.layout.dialog);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
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

    @OnClick({R.id.cancel_cancel_txt, R.id.cancel_sure_txt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_cancel_txt:
                finish();
                break;
            case R.id.cancel_sure_txt:
                switch ((int) spinner1.getSelectedItemId()) {
                    case 0:
                        Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bSControl, null);
                        break;
                    case 1:
                        Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bSControl, null);
                        break;
                }
                switch ((int) spinner2.getSelectedItemId()) {
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
                Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bSHz, this.spinner3.getSelectedItem().toString());
                Breath_Parsing.SendCmd(Global.breath_Com, Breath_Parsing.bSTidal, this.spinner4.getSelectedItem().toString());

                finish();
                break;
        }
    }

    //</editor-fold>
}
