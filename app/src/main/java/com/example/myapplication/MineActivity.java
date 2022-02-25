package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.test.TextUtils;

public class MineActivity extends Activity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_mine);
    ((TextView) findViewById(R.id.tv_time)).setText(TextUtils.getName());
  }
}
