package com.breathmonitor.widgets;

import android.app.Dialog;
import android.content.Context;
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

import static android.content.ContentValues.TAG;

/**
 * Created by admin on 2017/8/7.
 */

public class MyDialog extends Dialog {

    private TextView button;
    private int SelectIndex =0;
    public MyDialog(Context context,int index,View view) {
        super(context);
        this.SelectIndex = index;
        this.button = (TextView)view;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置不显示对话框标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //隐藏虚拟按键，并且全屏
//        if (Build.VERSION.SDK_INT >= 19) {
//            //for new api versions.
//            View decorView = getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);
//        }
        //应用运行时，保持屏幕高亮，不锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //设置对话框显示哪个布局文件
        setContentView(R.layout.dialog2);
        //对话框也可以通过资源id找到布局文件中的组件，从而设置点击侦听
        final WheelPicker wheelPicker = (WheelPicker)findViewById(R.id.wheelView1) ;
        TextView sure = (TextView) findViewById(R.id.cancel_sure_txt);

        wheelPicker.setSelectedItemPosition(SelectIndex,false);//设置显示值
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setText(String.valueOf(wheelPicker.getCurrentItemPosition()));
                Log.e("TAG", "onClick: "+wheelPicker.getCurrentItemPosition() );
                dismiss();
            }
        });

    }

}
