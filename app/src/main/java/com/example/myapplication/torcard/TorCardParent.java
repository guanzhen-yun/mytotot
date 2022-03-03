package com.example.myapplication.torcard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** 塔罗父布局控件 */
public class TorCardParent extends FrameLayout {
  private VelocityTracker mVelocityTracker;
  private float downX;
  private boolean isCanClickMid;

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
        break;
      case MotionEvent.ACTION_MOVE:
        float newDistance = event.getX() - downX;
        downX = event.getX();
        mOnScrollListener.scrollDistance(newDistance);
        break;
      case MotionEvent.ACTION_UP:
        mVelocityTracker.computeCurrentVelocity(200);
        mOnScrollListener.handlePAnimScroll(mVelocityTracker.getXVelocity());
        break;
    }
    return true;
  }

  private OnScrollListener mOnScrollListener;

  public void setOnScrollListener(OnScrollListener onScrollListener) {
    this.mOnScrollListener = onScrollListener;
  }

  public boolean isCanClickMid() {
    return isCanClickMid;
  }

  public void setCanClickMid(boolean canClickMid) {
    isCanClickMid = canClickMid;
  }

  public interface OnScrollListener {
    void handlePAnimScroll(float xVelocity);

    void scrollDistance(float newDistance);
  }
}
