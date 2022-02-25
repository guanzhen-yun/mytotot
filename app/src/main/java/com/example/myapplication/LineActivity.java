package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.example.myapplication.LineView.OnEndListener;

public class LineActivity extends Activity {
  private VelocityTracker mVelocityTracker;
  private FrameLayout fl;
  private LineView lineView;
  private TextView tv_move;
  private int cardSize = 22;
  private int cardWidth = 0;
  private int cardHeight = 0;

  private double bigRadius;

  // 90度10张卡片 一个圈40张卡片 每张卡片旋转9度
  private float maxDegree = 75 / 12.0f;

  private int currentCardIndex; // 当前卡片位置
  private boolean isFinishAnim = true; // 是否上一次滑动动画结束
  private float mPreDistance; // 上一次滑动的距离
  private float maxAngle;
  private float minAngle;

  private float downX;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_line);
    lineView = findViewById(R.id.lineView);
    tv_move = findViewById(R.id.tv_move);
    TextView tv_test = findViewById(R.id.tv_test);
    Button btn_rotate_left = findViewById(R.id.btn_rotate_left);
    Button btn_rotate_right = findViewById(R.id.btn_rotate_right);
    Typeface mtypeface = Typeface.createFromAsset(getAssets(), "american.ttf");
    tv_test.setTypeface(mtypeface);
    fl = findViewById(R.id.fl_cards);

    Button btn_seprate = findViewById(R.id.btn_seprate);
    currentCardIndex = cardSize / 2;

    for (int i = 0; i < cardSize; i++) {
      CardView view = new CardView(this);
      view.setImageResource(R.drawable.bg_card);
      if (cardWidth == 0) {
        view.measure(0, 0);
        cardWidth = view.getMeasuredWidth();
        cardHeight = view.getMeasuredHeight();
      }
      FrameLayout.LayoutParams layoutParams = new LayoutParams(cardWidth, cardHeight);
      layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
      layoutParams.topMargin = cardHeight / 2 - 100;
      layoutParams.bottomMargin = cardHeight / 2;
      view.setLayoutParams(layoutParams);
      fl.addView(view);
    }
    bigRadius = cardHeight * 2.5;
    btn_seprate.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            reset();
          }
        });

    fl.setOnTouchListener(
        new OnTouchListener() {
          @Override
          public boolean onTouch(View v, MotionEvent event) {
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
                scrollByDistance(newDistance, false);
                break;
              case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(200);
                handleAnimScroll();
                break;
            }
            return true;
          }
        });

    btn_rotate_left.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            //            rotate(true);
            scrollByDistance(-40, false);
          }
        });
    btn_rotate_right.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            //            rotate(false);
            scrollByDistance(40, true);
          }
        });

    maxAngle = cardSize / 2 * maxDegree;
    minAngle = -(cardSize - cardSize / 2 - 1) * maxDegree;
  }

  private float getRotationByIndex(int i) {
    return maxDegree * (i - currentCardIndex);
  }

  private void rotate(boolean isLeft) {
    if (currentCardIndex <= 0 && !isLeft) {
      return;
    }
    if (currentCardIndex >= cardSize - 1 && isLeft) {
      return;
    }
    if (!isFinishAnim) {
      return;
    }
    isFinishAnim = false;
    ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
    animator.setDuration(500);
    animator.addUpdateListener(
        new AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator animation) {
            float current = (float) animation.getAnimatedValue();
            for (int i = 0; i < cardSize; i++) {
              View v = fl.getChildAt(i);
              v.setRotation(getRotationByIndex(i) + ((isLeft ? -1 : (1)) * maxDegree * current));
              float tranX;
              float iTransX = getCardX(i, 1) * (i <= currentCardIndex ? -1 : 1); // 之前偏移量
              if (!isLeft) {
                tranX = getCardX(i + 1, 1) - getCardX(i, 1);
                tranX = iTransX + (i >= currentCardIndex ? 1 : -1) * tranX * current;
              } else {
                tranX = getCardX(i - 1, 1) - getCardX(i, 1);
                tranX = iTransX + (i <= currentCardIndex ? -1 : 1) * tranX * current;
              }
              v.setTranslationX(tranX);

              float tranY;
              float iTransY = getCardY(i, 1) - getCardY(currentCardIndex, 1);
              if (i == currentCardIndex + 1 && isLeft) { // 向左滑动 左边的卡片处理
                tranY = (-cardHeight / 3.0f + iTransY) * current;
              } else if (i == currentCardIndex - 1 && !isLeft) {
                tranY = (-cardHeight / 3.0f + iTransY) * current;
              } else if (i == currentCardIndex) {
                iTransY = -cardHeight / 3.0f;
                if (isLeft) {
                  tranY = getCardY(currentCardIndex, 1) - getCardY(i - 1, 1);
                } else {
                  tranY = getCardY(currentCardIndex, 1) - getCardY(i + 1, 1);
                }
                tranY = -cardHeight / 3.0f + tranY;
                tranY = iTransY - tranY * current;
              } else if (!isLeft) {
                tranY = getCardY(i, 1) - getCardY(i + 1, 1);
                tranY = -iTransY + tranY * current;
              } else {
                tranY = getCardY(i - 1, 1) - getCardY(i, 1);
                tranY = -iTransY - tranY * current;
              }
              v.setTranslationY(tranY);
            }

            if (current == 1) {
              if (isLeft) {
                currentCardIndex++;
              } else {
                currentCardIndex--;
              }
              isFinishAnim = true;
            }
          }
        });
    animator.start();
  }

  private float getNewRotationDegree() {
    if (mPreDistance > bigRadius - cardHeight / 2.0f) {
      mPreDistance = (float) (bigRadius - cardHeight / 2.0f);
    } else if (mPreDistance < cardHeight / 2.0f - bigRadius) {
      mPreDistance = (float) (cardHeight / 2.0f - bigRadius);
    }
    float angle = (float) Math.asin(mPreDistance / (bigRadius - cardHeight / 2.0f));
    return (float) (angle * 180 / Math.PI);
  }

  private float getNewTranX(View v) {
    float rotation = v.getRotation();
    return (float) (Math.sin(rotation * Math.PI / 180) * (bigRadius - cardHeight / 2.0f));
  }

  /**
   * @param distance 滑动一定距离
   * @param loose 是否松手
   */
  private void scrollByDistance(float distance, boolean loose) {
    mPreDistance += distance;
    float m = getNewRotationDegree();
    for (int i = 0; i < cardSize; i++) {
      View v = fl.getChildAt(i);
      v.setRotation(getInitRotation(i) + m);
      v.setTranslationX(getNewTranX(v));
      float x = Math.abs(v.getTranslationX());
      boolean isHandle = false;
      float tranY = 0;
      float radius = (float) (bigRadius - cardHeight / 2.0f);
      float maxX1 = (float) (radius * Math.sin(maxDegree * Math.PI / 180));
      if (mPreDistance < 0) { // 向左 找到当前的和 右的
        if (x < maxX1 && x >= 0) { // 中间的那个
          if (v.getRotation() > 0) { // 小--大 x 越来越小
            float f1 = (float) (radius - radius * Math.cos(maxDegree * Math.PI / 180));
            float f2 = -cardHeight / 3.0f;
            tranY = f1 + ((maxX1 - x) * (f2 - f1) / maxX1);
            isHandle = true;
          } else { // 大--小 x越来越大
            float f1 = (float) (radius - radius * Math.cos(maxDegree * Math.PI / 180));
            float f2 = -cardHeight / 3.0f;
            tranY = f2 + (x * (f1 - f2) / maxX1);
            isHandle = true;
          }
        }
      } else { // 向右 找到当前的 和 左的
        if (x < maxX1 && x >= 0) { // 中间的那个
          if (v.getRotation() < 0) { // 小--大 x 越来越小
            float f1 = (float) (radius - radius * Math.cos(maxDegree * Math.PI / 180));
            float f2 = -cardHeight / 3.0f;
            tranY = f1 + ((maxX1 - x) * (f2 - f1) / maxX1);
            isHandle = true;
          } else { // 大--小 x越来越大
            float f1 = (float) (radius - radius * Math.cos(maxDegree * Math.PI / 180));
            float f2 = -cardHeight / 3.0f;
            tranY = f2 + (x * (f1 - f2) / maxX1);
            isHandle = true;
          }
        }
      }
      if (!isHandle) {
        tranY =
            (float)
                (bigRadius
                    - cardHeight / 2.0f
                    - (bigRadius - cardHeight / 2.0f)
                        * (Math.cos(Math.abs(v.getRotation() * Math.PI / 180))));
      }
      v.setTranslationY(tranY);
    }

    if (loose) {
      handleAnimScroll();
    }
  }

  private boolean isFast = false;

  /** 松开手指滑动处理 */
  private void handleAnimScroll() {
    float xVelocity = mVelocityTracker.getXVelocity();
    float startAngle = getNewRotationDegree();
    mPreDistance += xVelocity / 11;
    if (Math.abs(xVelocity) > 100) {
      isFast = true;
    }
    float start2 = getNewRotationDegree();
    float endAngle = getEndAngle(start2);
    if (startAngle != endAngle) {
      ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
      animator.setDuration(500);
      animator.setInterpolator(new DecelerateInterpolator());
      animator.addUpdateListener(
          animation -> {
            float current = (float) animation.getAnimatedValue();
            for (int i = 0; i < cardSize; i++) {
              View v = fl.getChildAt(i);
              v.setRotation(getInitRotation(i) + startAngle + (endAngle - startAngle) * current);
              float startX =
                  (float)
                      ((bigRadius - cardHeight / 2.0f)
                          * Math.sin((getInitRotation(i) + startAngle) * Math.PI / 180));
              float endX =
                  (float)
                      ((bigRadius - cardHeight / 2.0f)
                          * Math.sin((getInitRotation(i) + endAngle) * Math.PI / 180));
              v.setTranslationX(startX + (endX - startX) * current);
              if (endX == 0 && !isFast) {
                float f1 =
                    (float)
                        ((bigRadius - cardHeight / 2.0f)
                            - (bigRadius - cardHeight / 2.0f)
                                * Math.cos(maxDegree * Math.PI / 180));
                float f2 = -cardHeight / 3.0f;
                float maxX1 =
                    (float) ((bigRadius - cardHeight / 2.0f) * Math.sin(maxDegree * Math.PI / 180));
                float x = Math.abs(startX);
                float startY = f1 + ((maxX1 - x) * (f2 - f1) / maxX1);
                float endY = -cardHeight / 3.0f;
                v.setTranslationY(startY + (endY - startY) * current);
              } else {
                float startY =
                    (float)
                        ((bigRadius - cardHeight / 2.0f)
                            - (bigRadius - cardHeight / 2.0f)
                                * Math.cos((startAngle + getInitRotation(i)) * Math.PI / 180));
                float endY =
                    (float)
                        ((bigRadius - cardHeight / 2.0f)
                            - (bigRadius - cardHeight / 2.0f)
                                * Math.cos((endAngle + getInitRotation(i)) * Math.PI / 180));
                v.setTranslationY(startY + (endY - startY) * current);
              }
            }

            if (current == 1) {
              mPreDistance =
                  (float) ((bigRadius - cardHeight / 2.0f) * Math.sin(endAngle * Math.PI / 180));
              if (isFast) {
                for (int i = 0; i < cardSize; i++) {
                  if (fl.getChildAt(i).getRotation() == 0) {
                    currentCardIndex = i;
                    break;
                  }
                }
                midStand(false);
              }
              isFast = false;
            }
          });
      animator.start();
    }
  }

  private float getEndAngle(float startAngle) {
    if (startAngle <= minAngle) {
      return minAngle;
    } else if (startAngle >= maxAngle) {
      return maxAngle;
    }
    float i = minAngle;
    while (i < maxAngle) {
      if (startAngle >= i && startAngle < i + maxDegree) {
        return i;
      }
      i = i + maxDegree;
    }
    return startAngle;
  }

  private void reset() {
    for (int i = 0; i < cardSize; i++) {
      View v = fl.getChildAt(i);
      v.setRotation(0);
      v.setTranslationX(0);
      v.setTranslationY(0);
    }
    mPreDistance = 0;
    lineView.restore();
    tv_move.setAlpha(0);
    currentCardIndex = cardSize / 2;
    toExpandCards();
  }

  private void toExpandCards() {
    ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
    animator.addUpdateListener(
        animation -> {
          float currentValue = (float) animation.getAnimatedValue();
          for (int i = 0; i < cardSize; i++) {
            if (i == currentCardIndex) continue;
            View v = fl.getChildAt(i);
            v.setRotation(maxDegree * (i - currentCardIndex) * currentValue);
            v.setTranslationX(getTransX(i - currentCardIndex, currentValue));
            v.setTranslationY(getTransY(Math.abs(i - currentCardIndex), currentValue));
          }
          if (currentValue == 1) {
            midStand(true);
          }
        });
    animator.setDuration(500);
    animator.start();
  }

  private void midStand(boolean isMoveLine) {
    ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
    animator.addUpdateListener(
        animation -> {
          float currentValue = (float) animation.getAnimatedValue();
          View v = fl.getChildAt(currentCardIndex);
          v.setTranslationY(-cardHeight / 3.0f * currentValue);
          if (currentValue == 1 && isMoveLine) {
            lineView.setOnEndListener(
                new OnEndListener() {
                  @Override
                  public void onEnd() {
                    showMoveText();
                  }
                });
            lineView.startAnim();
          }
        });
    animator.setDuration(300);
    animator.start();
  }

  private void showMoveText() {
    ObjectAnimator animator = ObjectAnimator.ofFloat(tv_move, "alpha", 0, 1);
    animator.setDuration(500);
    animator.start();
  }

  private float getTransY(int i, float currentValue) {
    return (float)
        ((bigRadius - cardHeight / 2)
            - (bigRadius - cardHeight / 2)
                * Math.cos(i * maxDegree * Math.PI / 180 * currentValue));
  }

  private float getTransX(int i, float currentValue) {
    return (float)
        ((bigRadius - cardHeight / 2) * Math.sin(i * maxDegree * Math.PI / 180 * currentValue));
  }

  private float getCardX(int i, float currentValue) {
    return getX(Math.abs(i - currentCardIndex), currentValue);
  }

  private float getCardY(int i, float currentValue) {
    return getY(Math.abs(i - currentCardIndex), currentValue);
  }

  private float getX(int mid, float currentValue) {
    return (float)
        (Math.sin(mid * maxDegree * currentValue * Math.PI / 180) * (bigRadius - cardHeight / 2));
  }

  private float getY(int mid, float currentValue) {
    return (float)
        (Math.cos(mid * maxDegree * currentValue * Math.PI / 180) * (bigRadius - cardHeight / 2));
  }

  /** 获取初始旋转角度 */
  private float getInitRotation(int i) {
    int mid = cardSize / 2;
    return (i - mid) * maxDegree;
  }
}
