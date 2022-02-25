package com.example.myapplication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

public class NewLineView extends View {
  private Paint paint;
  private Path path;

  public NewLineView(Context context) {
    this(context, null);
  }

  public NewLineView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public NewLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    paint = new Paint();
    paint.setColor(Color.BLACK);
    paint.setAntiAlias(true);
    paint.setDither(true);
    path = new Path();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    path.moveTo(50, 50);
    path.lineTo(50, 100);
    path.lineTo(75, 75);
    canvas.drawPath(path, paint);
  }

  public void anim() {
    ObjectAnimator animator1 = ObjectAnimator.ofFloat(this, View.X, View.Y, getPath());
    ObjectAnimator animator2 = ObjectAnimator.ofFloat(this, "rotation", 30, 120);
    AnimatorSet set = new AnimatorSet();
    set.playTogether(animator1, animator2);
    set.setDuration(2500);
    set.start();
  }

  private Path getPath() {
    Path path = new Path();
    path.arcTo(new RectF(10, 10, 300, 300), 30, 120, true);
    return path;
  }
}
