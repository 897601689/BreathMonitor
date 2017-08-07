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
import android.widget.TextView;

import com.breathmonitor.R;
import com.breathmonitor.widgets.MyDialog;
import com.breathmonitor.widgets.SwitchView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

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


    private static final String[] limits = new String[300];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUIMenu();
        setContentView(R.layout.acticity_dialog);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        String name = bundle.getString("title");
        String limit_H = bundle.getString("limit_H");
        String limit_L = bundle.getString("limit_L");

        dialogTxt.setText(name);//设置标题文字
        txtH.setText(limit_H);
        txtL.setText(limit_L);
    }


    @OnClick({R.id.txt_h, R.id.txt_l, R.id.cancel_cancel_txt, R.id.cancel_sure_txt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_h:
                MyDialog myDialog = new MyDialog(DialogActivity.this,Integer.parseInt(txtH.getText().toString()),txtH);
                myDialog.show();
                break;
            case R.id.txt_l:
                myDialog = new MyDialog(DialogActivity.this,Integer.parseInt(txtL.getText().toString()),txtL);
                myDialog.show();
                break;
            case R.id.cancel_cancel_txt:
                finish();
                break;
            case R.id.cancel_sure_txt:
                Log.e(TAG, "onViewClicked: " + switchView.isOpened());

                finish();
                break;
        }
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
}
