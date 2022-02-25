package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.annotation.Nullable;

public class NewLineActivity extends Activity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_newline);
    NewLineView newLineView = findViewById(R.id.view_newline);

    findViewById(R.id.btn_start)
        .setOnClickListener(
            new OnClickListener() {
              @Override
              public void onClick(View v) {
                newLineView.anim();
              }
            });
  }
}
