package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;
import androidx.annotation.Nullable;

public class ThirdAct extends Activity {
  TextView tv_ti;
  TextView tv_tip;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_third);
    tv_tip = findViewById(R.id.tv_tip);
    tv_ti = findViewById(R.id.tv_ti);
    setTT();
    for (int i = 0; i < 1000; i++) {}

    long t1 = System.currentTimeMillis();
  }

  private void setTT() {
    tv_ti.post(
        new Runnable() {
          @Override
          public void run() {
            if (tv_ti.getWidth() + tv_tip.getWidth() * 2 + dip2px(ThirdAct.this, 80) > getWidth()) {
              tv_ti.setTextSize(px2sp(ThirdAct.this, tv_ti.getTextSize()) - 1);
              setTT();
            } else {
              tv_tip.setTextSize(px2sp(ThirdAct.this, tv_ti.getTextSize()));
            }
          }
        });
  }

  private int getWidth() {
    DisplayMetrics outMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
    int widthPixels = outMetrics.widthPixels;
    return widthPixels;
    //    int heightPixels = outMetrics.heightPixels;
  }

  public static int dip2px(Context context, float dipValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dipValue * scale + 0.5f);
  }

  public static int px2sp(Context context, float pxValue) {
    final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (pxValue / fontScale + 0.5f);
  }
}
