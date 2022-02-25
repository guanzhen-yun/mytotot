package com.example.myapplication.torcard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import androidx.annotation.Nullable;
import com.example.myapplication.R;

public class LineNewActivity extends Activity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_line_new);
    Button btn_seprate = findViewById(R.id.btn_seprate);

    btn_seprate.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            ShowTorDialog dialog = new ShowTorDialog(LineNewActivity.this);
            dialog.show();
          }
        });
  }
}
