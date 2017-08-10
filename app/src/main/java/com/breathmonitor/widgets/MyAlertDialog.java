package com.breathmonitor.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.breathmonitor.R;


/**
 * Created by Admin on 2016/2/3.
 */
public class MyAlertDialog {
    Context context;
    android.app.AlertDialog ad;
    TextView titleView;
    TextView messageView;
    LinearLayout buttonLayout;
    TextView txt_sure;
    TextView txt_cancel;

    public MyAlertDialog(Context context) {
        this.context = context;
        ad = new android.app.AlertDialog.Builder(context).create();
        ad.show();

        Window window = ad.getWindow();
        window.setContentView(R.layout.activity_dialog);
        titleView = (TextView) window.findViewById(R.id.title);
        messageView = (TextView) window.findViewById(R.id.dialog_txt);
        buttonLayout = (LinearLayout) window.findViewById(R.id.dialog_btn_layout);
        txt_sure = (TextView) window.findViewById(R.id.cancel_sure_txt);
        txt_cancel = (TextView) window.findViewById(R.id.cancel_cancel_txt);
    }

    public void setTitle(int resId) {
        titleView.setText(resId);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setMessage(int resId) {
        messageView.setText(resId);
    }

    public void setMessage(String message) {
        messageView.setText(message);
    }

    /**
     * 设置按钮
     *
     * @param text
     * @param listener
     */
    public void setPositiveButton(String text, final View.OnClickListener listener) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txt_sure.setLayoutParams(params);
        txt_sure.setBackgroundResource(R.drawable.btn_small);
        txt_sure.setHeight(45);
        txt_sure.setWidth(100);
        txt_sure.setText(text);
        txt_sure.setTextColor(Color.WHITE);
        txt_sure.setTextSize(25);
        txt_sure.setVisibility(View.VISIBLE);
        txt_sure.setOnClickListener(listener);
        //buttonLayout.addView(txt_sure);
    }

    /**
     * 设置按钮
     *
     * @param text
     * @param listener
     */
    public void setNegativeButton(String text, final View.OnClickListener listener) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txt_cancel.setLayoutParams(params);
        txt_sure.setHeight(45);
        txt_sure.setWidth(100);
        txt_cancel.setText(text);
        txt_cancel.setTextColor(Color.WHITE);
        txt_cancel.setTextSize(25);
        txt_cancel.setVisibility(View.VISIBLE);
        txt_cancel.setOnClickListener(listener);
        /*if (buttonLayout.getChildCount() > 0) {
            params.setMargins(0, 0, 60, 0);
        }*/
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        ad.dismiss();
    }

}
