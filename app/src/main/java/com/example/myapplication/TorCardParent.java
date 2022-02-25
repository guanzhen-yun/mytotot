package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TorCardParent extends FrameLayout {
  private VelocityTracker mVelocityTracker;
  private float downX;
  private float initX;
  private float initY;

  public TorCardParent(@NonNull Context context) {
    super(context);
  }

  public TorCardParent(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public TorCardParent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
      return false;
    }
    if (null == mVelocityTracker) {
      mVelocityTracker = VelocityTracker.obtain(); // 手指抬起之后的速度变化
    }
    mVelocityTracker.addMovement(event);
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        downX = event.getX();
        initX = event.getX();
        initY = event.getY();
        break;
      case MotionEvent.ACTION_MOVE:
        float newDistance = event.getX() - downX;
        downX = event.getX();
        mOnScrollListener.scrollDistance(newDistance);
        break;
      case MotionEvent.ACTION_UP:
        mVelocityTracker.computeCurrentVelocity(200);
        if (Math.abs(initX - event.getX()) <= 5 && initY <= dp2px(getContext(), 260)) {
          mOnScrollListener.clickCard();
        } else {
          mOnScrollListener.handlePAnimScroll(mVelocityTracker.getXVelocity());
        }
        break;
    }
    return true;
  }

  private int dp2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  private OnScrollListener mOnScrollListener;

  public void setOnScrollListener(OnScrollListener onScrollListener) {
    this.mOnScrollListener = onScrollListener;
  }

  public interface OnScrollListener {
    void handlePAnimScroll(float xVelocity);

    void scrollDistance(float newDistance);

    void clickCard();
  }
}
