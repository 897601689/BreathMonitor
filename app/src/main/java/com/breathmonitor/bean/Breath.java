package com.breathmonitor.bean;

/**
 * Created by admin on 2017/8/7.
 */

public class Breath {

    private String b_O2;
    private String b_Tidal;
    private String b_Hz;
    private String b_Mode = "control";

    public String getB_O2() {
        return b_O2;
    }

    public void setB_O2(String b_O2) {
        this.b_O2 = b_O2;
    }

    public String getB_Tidal() {
        return b_Tidal;
    }

    public void setB_Tidal(String b_Tidal) {
        this.b_Tidal = b_Tidal;
    }

    public String getB_Hz() {
        return b_Hz;
    }

    public void setB_Hz(String b_Hz) {
        this.b_Hz = b_Hz;
    }

    public String getB_Mode() {
        return b_Mode;
    }

    public void setB_Mode(String b_Mode) {
        this.b_Mode = b_Mode;
    }

    public Breath() {
    }

    public Breath(String b_Mode, String b_Tidal, String b_Hz, String b_O2) {
        this.b_Mode = b_Mode;
        this.b_Tidal = b_Tidal;
        this.b_Hz = b_Hz;
        this.b_O2 = b_O2;
    }

    @Override
    public String toString() {
        return "Breath{" +
                "b_O2='" + b_O2 + '\'' +
                ", b_Tidal='" + b_Tidal + '\'' +
                ", b_Hz='" + b_Hz + '\'' +
                ", b_Mode='" + b_Mode + '\'' +
                '}';
    }

}
