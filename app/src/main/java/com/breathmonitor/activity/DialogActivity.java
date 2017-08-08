package com.breathmonitor.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.breathmonitor.R;
import com.breathmonitor.bean.Alert;
import com.breathmonitor.util.Global;
import com.breathmonitor.widgets.MyDialog;
import com.breathmonitor.widgets.SwitchView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/8/7.
 */

public class DialogActivity extends Activity {
    @BindView(R.id.dialog_txt)
    TextView dialogTxt;
    @BindView(R.id.switchView)
    SwitchView switchView;
    @BindView(R.id.txt_h)
    TextView txtH;
    @BindView(R.id.txt_l)
    TextView txtL;
    @BindView(R.id.cancel_cancel_txt)
    TextView cancelCancelTxt;
    @BindView(R.id.cancel_sure_txt)
    TextView cancelSureTxt;


    //private static final String[] limits = new String[300];
    public static final String action = "jason.broadcast.action";

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUIMenu();
        setContentView(R.layout.acticity_dialog);
        ButterKnife.bind(this);

        init();
    }

    //Activity创建或者从被覆盖、后台重新回到前台时被调用
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TAG", "onResume called.");
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
        Log.e("TAG", "onPause called.");
        //有可能在执行完onPause或onStop后,系统资源紧张将Activity杀死,所以有必要在此保存持久数据
    }


    private void init() {
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        String title = bundle.getString("title");
        name = bundle.getString("name");
        String limit_H = bundle.getString("limit_H");
        String limit_L = bundle.getString("limit_L");

        dialogTxt.setText(title + "报警限设置");//设置标题文字
        txtH.setText(limit_H);
        txtL.setText(limit_L);
    }


    @OnClick({R.id.txt_h, R.id.txt_l, R.id.cancel_cancel_txt, R.id.cancel_sure_txt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_h:
                MyDialog myDialog = new MyDialog(DialogActivity.this, Integer.parseInt(txtH.getText().toString()), txtH);
                myDialog.show();
                break;
            case R.id.txt_l:
                myDialog = new MyDialog(DialogActivity.this, Integer.parseInt(txtL.getText().toString()), txtL);
                myDialog.show();
                break;
            case R.id.cancel_cancel_txt:
                finish();
                break;
            case R.id.cancel_sure_txt:
                Global.mApp.setAlertShared(name, new Alert(switchView.isOpened(),
                        Integer.parseInt(txtH.getText().toString()),
                        Integer.valueOf(txtL.getText().toString())));

                Intent intent = new Intent(action);
                intent.putExtra("name",name);
                intent.putExtra("limitH", txtH.getText().toString());
                intent.putExtra("limitL", txtL.getText().toString());
                sendBroadcast(intent);
                finish();
                break;
        }
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


}
