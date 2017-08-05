package com.breathmonitor.widgets;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.breathmonitor.R;

/**
 * 报警图片控件
 * Created by admin on 2017/8/5.
 */

public class AlarmImageView extends android.support.v7.widget.AppCompatImageView {


    private AnimationDrawable anim;

    private int level = 0;

    public void setLevel(int level) {

        if (level == 0) {
            setImageResource(R.mipmap.alarm_gray);
        } else if (level == 1) {
            if (this.level >= 1) return;
            setImageResource(R.drawable.anim_m);
            anim = (AnimationDrawable) getDrawable();
            anim.start();
        } else if (level == 2) {
            if (this.level == 2) return;
            setImageResource(R.drawable.anim_h);
            anim = (AnimationDrawable) getDrawable();
            anim.start();
        }
        this.level = level;//位置不可移动
    }

    public AlarmImageView(Context context) {
        super(context);
        setImageResource(R.mipmap.alarm_gray);
    }

    public AlarmImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setImageResource(R.mipmap.alarm_gray);
    }

    public AlarmImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.mipmap.alarm_gray);
    }


}

