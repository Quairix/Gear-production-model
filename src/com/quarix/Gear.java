package com.quarix;

public class Gear {
    private boolean isConcrete = false;
    private boolean isHard = false;
    private double time = 0;

    void setConcrete(boolean concrete) {
        isConcrete = concrete;
    }

    void setHard(boolean hard) {
        isHard = hard;
    }

    double getTime() {
        return time;
    }

    void setTime(double time) {
        this.time = time;
    }

}
