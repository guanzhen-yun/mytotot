package com.example.myapplication.torcard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.Nullable;

public class CardView extends androidx.appcompat.widget.AppCompatImageView {
  private float lastestTranX;
  private float lastestTranY;
  private float lastestRotation;
  private boolean isMiddle;

  private float downX;

  public CardView(Context context) {
    super(context);
  }

  public CardView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public CardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

  public boolean isMiddle() {
    return isMiddle;
  }

  public void setMiddle(boolean middle) {
    isMiddle = middle;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        if (getRotation() == 0) {
          downX = event.getX();
          return true;
        }
        break;
      case MotionEvent.ACTION_UP:
        if (onClickChildListener != null
            && getRotation() == 0
            && Math.abs(downX - event.getX()) < 5
            && ((TorCardParent) getParent()).isCanClickMid()) {
          onClickChildListener.clickChild();
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
