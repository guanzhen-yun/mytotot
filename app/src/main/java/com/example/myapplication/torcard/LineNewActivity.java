package com.example.myapplication.torcard;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.Nullable;
import com.example.myapplication.R;

/** 塔罗牌入口 */
public class LineNewActivity extends Activity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_line_new);
    Button btn_seprate = findViewById(R.id.btn_seprate);

    btn_seprate.setOnClickListener(
        v ->
            new ShowTorDialog.Builder()
                .setMaxDegreeOnePage(75)
                .setMaxCardSizeOnePage(12)
                .setCardSize(22)
                .setBigRadiusRatio(2.5f)
                .setCardStandRation(0.5f)
                .build(LineNewActivity.this)
                .show());
  }
}
