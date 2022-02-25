package com.example.myapplication.torcard;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.myapplication.R;
import com.example.myapplication.torcard.TorCardParent.OnScrollListener;

public class ShowTorDialog extends Dialog implements CardView.OnClickChildListener {
  private final Activity activity;

  private TorCardParent fl;
  private LineView lineView;
  private TextView tv_move;
  private TextView tv_test;
  private ImageView iv_close;
  private TextView tv_context;
  private TextView tv_pick;
  private ImageView iv_arr;
  private View view_back;
  private androidx.cardview.widget.CardView cv_iv;
  private ConstraintLayout cl_body;

  private int cardSize;
  private int cardWidth = 0;
  private int cardHeight = 0;

  private double bigRadius;

  // 75度12张卡片 一个圈40张卡片 每张卡片旋转9度
  private float maxDegree;

  private int currentCardIndex; // 当前卡片位置
  private float mPreDistance; // 上一次滑动的距离
  private float maxAngle;
  private float minAngle;

  private int screenHeight;
  private boolean isFast = false;
  private int maxCardSizeOnePage;
  private int maxDegreeOnePage;
  private float bigRadiusRatio; // 卡片外径相对卡片的比例  例如2.5
  private float cardStandRation; // 中心卡片漏出的比例 例如0.5

  public static class Builder {
    private int maxCardSizeOnePage;
    private int maxDegreeOnePage;
    private int cardSize;
    private float bigRadiusRatio;
    private float cardStandRation;

    public Builder setMaxCardSizeOnePage(int maxCardSizeOnePage) {
      this.maxCardSizeOnePage = maxCardSizeOnePage;
      return this;
    }

    public Builder setMaxDegreeOnePage(int maxDegreeOnePage) {
      this.maxDegreeOnePage = maxDegreeOnePage;
      return this;
    }

    public Builder setBigRadiusRatio(float bigRadiusRatio) {
      this.bigRadiusRatio = bigRadiusRatio;
      return this;
    }

    public Builder setCardStandRation(float cardStandRation) {
      this.cardStandRation = cardStandRation;
      return this;
    }

    public Builder setCardSize(int cardSize) {
      this.cardSize = cardSize;
      return this;
    }

    public ShowTorDialog build(Context context) {
      ShowTorDialog showTorDialog = new ShowTorDialog(context);
      showTorDialog.maxCardSizeOnePage = maxCardSizeOnePage;
      showTorDialog.maxDegreeOnePage = maxDegreeOnePage;
      showTorDialog.cardSize = cardSize;
      showTorDialog.bigRadiusRatio = bigRadiusRatio;
      showTorDialog.cardStandRation = cardStandRation;
      return showTorDialog;
    }
  }

  public ShowTorDialog(@NonNull Context context) {
    super(context, R.style.mydialog);
    activity = (Activity) context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Window window = getWindow();
    window.setGravity(Gravity.BOTTOM);
    window.getDecorView().setPadding(0, 0, 0, 0);
    WindowManager.LayoutParams layoutParams = window.getAttributes();
    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
    layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    window.setAttributes(layoutParams);
    setContentView(R.layout.dialog_tor);
    initViews();
    maxDegree = maxDegreeOnePage * 1.0f / maxCardSizeOnePage;
    currentCardIndex = cardSize / 2;
    maxAngle = cardSize / 2.0f * maxDegree;
    minAngle = -(cardSize - cardSize / 2.0f - 1) * maxDegree;
    fillChildCard();
    bigRadius = cardHeight * bigRadiusRatio;
    new Handler().postDelayed(this::openAnim, 500);
    Display display = activity.getWindowManager().getDefaultDisplay();
    screenHeight = display.getHeight();
  }

  private void initViews() {
    lineView = findViewById(R.id.lineView);
    fl = findViewById(R.id.fl_cards);
    cl_body = findViewById(R.id.cl_body);
    iv_close = findViewById(R.id.iv_close);
    tv_context = findViewById(R.id.tv_context);
    iv_close.setOnClickListener(v -> dismiss());
    cl_body.setOnClickListener(v -> dismiss());
    tv_move = findViewById(R.id.tv_move);
    tv_test = findViewById(R.id.tv_test);
    Typeface mtypeface = Typeface.createFromAsset(activity.getAssets(), "american.ttf");
    tv_test.setTypeface(mtypeface);
    tv_pick = findViewById(R.id.tv_pick);
    iv_arr = findViewById(R.id.iv_arr);
    view_back = findViewById(R.id.view_back);
    view_back.setOnClickListener(v -> {});
  }

  private void fillChildCard() {
    for (int i = 0; i < cardSize; i++) {
      CardView view = new CardView(activity);
      view.setImageResource(R.drawable.bg_card);
      view.setOnClickChildListener(this);
      if (cardWidth == 0) {
        view.measure(0, 0);
        cardWidth = view.getMeasuredWidth();
        cardHeight = view.getMeasuredHeight();
      }
      LayoutParams layoutParams2 = new LayoutParams(cardWidth, cardHeight);
      layoutParams2.gravity = Gravity.CENTER_HORIZONTAL;
      layoutParams2.topMargin = cardHeight / 2;
      layoutParams2.bottomMargin = cardHeight / 2;
      view.setLayoutParams(layoutParams2);
      fl.addView(view);
    }

    fl.setOnScrollListener(
        new OnScrollListener() {
          @Override
          public void handlePAnimScroll(float xVelocity) {
            handleAnimScroll(xVelocity);
          }

          @Override
          public void scrollDistance(float newDistance) {
            scrollByDistance(newDistance);
          }
        });
  }

  /** 水平平移卡片 */
  private void translationHor(boolean isLeft) {
    cv_iv.setRotation(isLeft ? -5 : 5);
    cv_iv.setPivotX(isLeft ? cv_iv.getWidth() : 0);
    cv_iv.setPivotY(cv_iv.getHeight());
    findViewById(R.id.cl_body).setBackgroundColor(Color.parseColor("#00000000"));
    ObjectAnimator animator =
        ObjectAnimator.ofFloat(
            findViewById(R.id.cl_body),
            "translationX",
            0,
            (isLeft ? (-1) : 1) * cv_iv.getWidth() * 2);
    animator.setInterpolator(new AccelerateInterpolator());
    animator.addListener(
        new AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animation) {}

          @Override
          public void onAnimationEnd(Animator animation) {
            dismiss();
          }

          @Override
          public void onAnimationCancel(Animator animation) {}

          @Override
          public void onAnimationRepeat(Animator animation) {}
        });
    animator.setDuration(500);
    animator.start();
  }

  private void showChildUpAnim() {
    CardView view = new CardView(activity);
    view.setImageResource(R.drawable.bg_card);
    ConstraintLayout.LayoutParams layoutParams =
        new ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
    layoutParams.topToBottom = R.id.iv_arr;
    layoutParams.leftToLeft = R.id.cl_body;
    layoutParams.rightToRight = R.id.cl_body;
    view.setLayoutParams(layoutParams);
    cl_body.addView(view);
    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
    valueAnimator.setDuration(500);
    int[] location = new int[2];
    iv_arr.getLocationOnScreen(location);

    float tranY =
        (location[1] + iv_arr.getHeight())
            + cardHeight / 2.0f
            - screenHeight / 2.0f
            - (screenHeight - cl_body.getHeight()) * 8;

    valueAnimator.addUpdateListener(
        animation -> {
          float current = (float) animation.getAnimatedValue();
          view.setTranslationY(-tranY * current);
          setViewAlpha(
              1 - current, lineView, tv_move, fl, tv_test, iv_close, tv_context, tv_pick, iv_arr);
          if (current == 1) {
            showBigAnim(view);
          }
        });
    valueAnimator.start();
  }

  private void setViewAlpha(float alpha, View... views) {
    for (View view : views) {
      view.setAlpha(alpha);
    }
  }

  private void showBigAnim(View view) {
    View view_mid = View.inflate(activity, R.layout.layout_mine_card, null);
    ConstraintLayout.LayoutParams layoutParams =
        new ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
    layoutParams.topToTop = R.id.cl_body;
    layoutParams.bottomToBottom = R.id.cl_body;
    layoutParams.leftToLeft = R.id.cl_body;
    layoutParams.rightToRight = R.id.cl_body;
    view_mid.setLayoutParams(layoutParams);

    ImageView iv_like = view_mid.findViewById(R.id.iv_like);
    cv_iv = view_mid.findViewById(R.id.cv_iv);
    ImageView iv_dislike = view_mid.findViewById(R.id.iv_dislike);
    iv_dislike.setOnClickListener(v -> translationHor(true));
    iv_like.setOnClickListener(v -> translationHor(false));
    ConstraintLayout cl_card_body = view_mid.findViewById(R.id.cl_card_body);

    view_mid.setAlpha(0);
    view_mid.setVisibility(View.VISIBLE);
    cl_body.addView(view_mid);
    cl_card_body.postDelayed(
        () -> {
          cl_card_body.setScaleX(cardWidth / (cl_card_body.getWidth() * 1.0f));
          cl_card_body.setScaleY(cardHeight / (cl_card_body.getHeight() * 1.0f));
          displayBigCardAnim(view, cl_card_body);
        },
        200);
  }

  private void displayBigCardAnim(View view, ConstraintLayout cl_card_body) {
    ObjectAnimator animator3 = ObjectAnimator.ofFloat(view, "rotationY", 0, -90);
    animator3.addUpdateListener(
        animation -> {
          float f = (float) animation.getAnimatedValue();
          if (f == -90) {
            cl_card_body.setRotationY(-270);
            cl_card_body.setAlpha(1);
            ObjectAnimator animator31 =
                ObjectAnimator.ofFloat(cl_card_body, "rotationY", -270, -360);
            animator31.addUpdateListener(
                animation1 -> {
                  if ((float) animation1.getAnimatedValue() <= -272) {
                    view_back.setAlpha(0);
                  }
                });
            ObjectAnimator animator4 =
                ObjectAnimator.ofFloat(
                    cl_card_body, "scaleX", cardWidth / (cl_card_body.getWidth() * 1.0f), 1);
            ObjectAnimator animator5 =
                ObjectAnimator.ofFloat(
                    cl_card_body, "scaleY", cardHeight / (cl_card_body.getHeight() * 1.0f), 1);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(animator31, animator4, animator5);
            set.setDuration(300);
            set.start();
          }
        });
    animator3.setDuration(300);
    animator3.start();
  }

  private void scrollByDistance(float distance) {
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
            float f2 = -cardHeight / 2.0f;
            tranY = f1 + ((maxX1 - x) * (f2 - f1) / maxX1);
            isHandle = true;
          } else { // 大--小 x越来越大
            float f1 = (float) (radius - radius * Math.cos(maxDegree * Math.PI / 180));
            float f2 = -cardHeight / 2.0f;
            tranY = f2 + (x * (f1 - f2) / maxX1);
            isHandle = true;
          }
        }
      } else { // 向右 找到当前的 和 左的
        if (x < maxX1 && x >= 0) { // 中间的那个
          if (v.getRotation() < 0) { // 小--大 x 越来越小
            float f1 = (float) (radius - radius * Math.cos(maxDegree * Math.PI / 180));
            float f2 = -cardHeight / 2.0f;
            tranY = f1 + ((maxX1 - x) * (f2 - f1) / maxX1);
            isHandle = true;
          } else { // 大--小 x越来越大
            float f1 = (float) (radius - radius * Math.cos(maxDegree * Math.PI / 180));
            float f2 = -cardHeight / 2.0f;
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
  }

  /** 松开手指滑动处理 */
  private void handleAnimScroll(float xVelocity) {
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
                float f2 = -cardHeight / 2.0f;
                float maxX1 =
                    (float) ((bigRadius - cardHeight / 2.0f) * Math.sin(maxDegree * Math.PI / 180));
                float x = Math.abs(startX);
                float startY = f1 + ((maxX1 - x) * (f2 - f1) / maxX1);
                float endY = -cardHeight / 2.0f;
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

  public void openAnim() {
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
          v.setTranslationY(-cardHeight / 2.0f * currentValue);
          if (currentValue == 1 && isMoveLine) {
            lineView.setOnEndListener(this::showMoveText);
            lineView.startAnim();
          }
        });
    animator.setDuration(300);
    animator.start();
  }

  private void showMoveText() {
    ObjectAnimator animator = ObjectAnimator.ofFloat(tv_move, "alpha", 0, 1);
    animator.setDuration(300);
    animator.addUpdateListener(
        animation -> {
          if ((float) animation.getAnimatedValue() == 1) {
            fl.setCanClickMid(true);
          }
        });
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

  /** 获取初始旋转角度 */
  private float getInitRotation(int i) {
    int mid = cardSize / 2;
    return (i - mid) * maxDegree;
  }

  @Override
  public void clickChild() {
    showChildUpAnim();
  }
}
