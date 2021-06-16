package com.uuzuche.lib_zxing.core;

import android.graphics.PointF;

/**
 */
public class ScanResult {

    public  String result;
    PointF[] resultPoints;
    //1为二维码，0为其他
    public int type = 0;

    public ScanResult(String result) {
        this.result = result;
    }

    public ScanResult(String result, int type) {
        this.result = result;
        this.type = type;
    }

    public ScanResult(String result, PointF[] resultPoints) {
        this.result = result;
        this.resultPoints = resultPoints;
    }
}
