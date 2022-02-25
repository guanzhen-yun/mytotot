package com.example.myapplication.torcard;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

public class LineView extends View {
  private Paint mPointPaint;
  private float marginLeft = 30.0f; // 左边的间距
  private float marginTop = 50.0f; // 上边的间距
  private float bigRadius = 500.0f;
  private float smallLeft; // 小圆圆心左边距
  private float smallTop; // 小圆圆心上边距
  private float smallRadius = 10.0f; // 小圆半径
  private int color = Color.WHITE;

  private Paint mLinePaint;
  private int lineWidth = 5;

  private float triangleLineWidth = 20; // 三角边长度
  private Paint trianglePaint;
  private Path mTrianglePath;

  private int duringTime = 800; // 执行完动画时间
  private float startAngle; // 初始角度
  private float currentAngle = 1.0f; // 当前旋转角度
  private float maxAngle = 70.0f; // 最大旋转角度
  private ValueAnimator animator;
  private boolean isEnd;

  private RectF rect;
  private float leftAngle; // 左右的角度

  private OnEndListener onEndListener;

  public void setOnEndListener(OnEndListener onEndListener) {
    this.onEndListener = onEndListener;
  }

  public LineView(Context context) {
    this(context, null);
  }

  public LineView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension(
        (int) (bigRadius * 2 + marginLeft * 2),
        (int) (marginTop * 3 + bigRadius - bigRadius * Math.sin(leftAngle * Math.PI / 180)));
  }

  private void init() {
    mPointPaint = new Paint();
    mPointPaint.setColor(color);
    mPointPaint.setStyle(Style.FILL);
    mPointPaint.setDither(true);
    mPointPaint.setAntiAlias(true);

    mLinePaint = new Paint();
    mLinePaint.setColor(color);
    mLinePaint.setStrokeWidth(lineWidth);
    mLinePaint.setStyle(Style.STROKE);
    mLinePaint.setDither(true);
    mLinePaint.setAntiAlias(true);
    leftAngle = (180 - maxAngle) / 2;
    startAngle = leftAngle - 180;

    smallLeft = (float) (marginLeft + bigRadius - bigRadius * Math.cos(leftAngle * Math.PI / 180));
    smallTop = (float) (marginTop + bigRadius - bigRadius * Math.sin(leftAngle * Math.PI / 180));

    trianglePaint = new Paint();
    trianglePaint.setColor(color);
    trianglePaint.setStyle(Style.FILL);
    trianglePaint.setDither(true);
    trianglePaint.setAntiAlias(true);
    mTrianglePath = new Path();

    animator = ValueAnimator.ofFloat(currentAngle, maxAngle);
    animator.setDuration(duringTime);
    animator.addUpdateListener(
        animation -> {
          currentAngle = (float) (Float) animation.getAnimatedValue();
          if (currentAngle == maxAngle) {
            isEnd = true;
            onEndListener.onEnd();
          }
          invalidate();
        });

    rect = new RectF(marginLeft, marginTop, marginLeft + 2 * bigRadius, marginTop + 2 * bigRadius);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawCircle(smallLeft, smallTop, smallRadius, mPointPaint);
    canvas.drawArc(rect, startAngle, currentAngle, false, mLinePaint);
    float heighMin =
        (float)
            ((bigRadius - triangleLineWidth / 2.0f)
                * Math.sin((leftAngle + currentAngle) * Math.PI / 180.0f));
    float widthMin =
        (float)
            ((bigRadius - triangleLineWidth / 2.0f)
                * Math.cos((leftAngle + currentAngle) * Math.PI / 180.0f));
    float heighMax =
        (float)
            ((bigRadius + triangleLineWidth / 2.0f)
                * Math.sin((leftAngle + currentAngle) * Math.PI / 180.0f));
    float widthMax =
        (float)
            ((bigRadius + triangleLineWidth / 2.0f)
                * Math.cos((leftAngle + currentAngle) * Math.PI / 180.0f));
    float x1 = marginLeft + bigRadius - widthMin; // B
    float y1 = bigRadius - heighMin + marginTop; // B
    float x2 = marginLeft + bigRadius - widthMax; // A
    float y2 = bigRadius - heighMax + marginTop; // A
    mTrianglePath.moveTo(x1, y1);
    mTrianglePath.lineTo(x2, y2);
    float cX = (float) (triangleLineWidth * Math.sin((15 + currentAngle) * Math.PI / 180.0f));
    float cY = (float) (triangleLineWidth * Math.cos((15 + currentAngle) * Math.PI / 180.0f));
    float x3 = x1 + cX;
    float y3 = y1 - cY;
    mTrianglePath.lineTo(x3, y3);
    mTrianglePath.close();
    canvas.drawPath(mTrianglePath, trianglePaint);
    mTrianglePath.reset();
  }

  public void startAnim() {
    if (animator != null) {
      animator.start();
    }
  }

  public void restore() {
    if (isEnd) {
      currentAngle = 1;
      isEnd = false;
      if (animator.isRunning()) {
        animator.cancel();
      }
    }
  }

  public interface OnEndListener {
    void onEnd();
  }
}
