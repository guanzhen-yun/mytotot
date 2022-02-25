package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.Nullable;

public class CardView extends androidx.appcompat.widget.AppCompatImageView {
  private float lastestTranX;
  private float lastestTranY;
  private float lastestRotation;
  private boolean isMiddle;

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
}
