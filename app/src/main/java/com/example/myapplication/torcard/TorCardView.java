package com.example.myapplication.torcard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.Nullable;

/** 塔罗子牌 */
public class TorCardView extends androidx.appcompat.widget.AppCompatImageView {
  private float lastestTranX;
  private float lastestTranY;
  private float lastestRotation;

  public TorCardView(Context context) {
    super(context);
  }

  public TorCardView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public TorCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public float getLastestTranX() {
    return lastestTranX;
  }

  public void setLastestTranX(float lastestTranX) {
    this.lastestTranX = lastestTranX;
  }

  public float getLastestTranY() {
    return lastestTranY;
  }

  public void setLastestTranY(float lastestTranY) {
    this.lastestTranY = lastestTranY;
  }

  public float getLastestRotation() {
    return lastestRotation;
  }

  public void setLastestRotation(float lastestRotation) {
    this.lastestRotation = lastestRotation;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        if (getRotation() == 0) {
          return true;
        }
        break;
      case MotionEvent.ACTION_MOVE:
        getParent().requestDisallowInterceptTouchEvent(false);
        return false;
      case MotionEvent.ACTION_UP:
        if (onClickChildListener != null
            && getRotation() == 0
            && ((TorCardParent) getParent()).isCanClickMid()) {
          onClickChildListener.clickChild();
          return true;
        }
        break;
    }
    return false;
  }

  private OnClickChildListener onClickChildListener;

  public void setOnClickChildListener(OnClickChildListener onClickChildListener) {
    this.onClickChildListener = onClickChildListener;
  }

  public interface OnClickChildListener {
    void clickChild();
  }
}
