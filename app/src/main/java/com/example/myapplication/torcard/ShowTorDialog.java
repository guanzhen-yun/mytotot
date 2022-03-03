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

/** 塔罗弹框 */
public class ShowTorDialog extends Dialog implements TorCardView.OnClickChildListener {
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
  private float maxAngle; // 最大旋转角度
  private float minAngle; // 最小旋转角度

  private int screenHeight;
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
    beforeDialog();
    setContentView(R.layout.dialog_tor);
    initViews();
    initDatas();
  }

  /** dialog之前属性设置 */
  private void beforeDialog() {
    Window window = getWindow();
    window.setGravity(Gravity.BOTTOM);
    window.getDecorView().setPadding(0, 0, 0, 0);
    WindowManager.LayoutParams layoutParams = window.getAttributes();
    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
    layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    window.setAttributes(layoutParams);
  }

  /** 初始化布局和点击事件 */
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

  /** 初始化数据 */
  private void initDatas() {
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

  /** 填充子卡片 */
  private void fillChildCard() {
    for (int i = 0; i < cardSize; i++) {
      TorCardView view = new TorCardView(activity);
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

  /** 塔罗展开卡片动画 */
  private void openAnim() {
    ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
    animator.addUpdateListener(
        animation -> {
          float currentValue = (float) animation.getAnimatedValue();
          for (int i = 0; i < cardSize; i++) {
            if (i == currentCardIndex) continue;
            TorCardView v = (TorCardView) fl.getChildAt(i);
            v.setRotation(maxDegree * (i - currentCardIndex) * currentValue);
            v.setTranslationX(getTransX(i - currentCardIndex, currentValue));
            v.setTranslationY(getTransY(Math.abs(i - currentCardIndex), currentValue));
            saveLatestAttr(v);
          }
          // 当卡片展开中间卡片凸起
          if (currentValue == 1) {
            midStand();
          }
        });
    animator.setDuration(500);
    animator.start();
  }

  /** 保存卡片上次的属性 */
  private void saveLatestAttr(TorCardView v) {
    v.setLastestRotation(v.getRotation());
    v.setLastestTranX(v.getLastestTranX());
    v.setLastestTranY(v.getTranslationY());
  }

  /** 中间卡片弹出动画 isMoveLine 中间卡片弹出动画执行完是否需要播放弧线动画 */
  private void midStand() {
    float maxCardTran = -cardHeight * cardStandRation;
    TorCardView v = (TorCardView) fl.getChildAt(currentCardIndex);
    ObjectAnimator animator = ObjectAnimator.ofFloat(v, "translationY", 0, maxCardTran);
    animator.addUpdateListener(
        animation -> {
          float currentValue = (float) animation.getAnimatedValue();
          // 当中间卡片弹出动画执行完 播放弧线动画
          if (currentValue == maxCardTran) {
            saveLatestAttr(v);
            lineView.setOnEndListener(this::showMoveText);
            lineView.startAnim();
          }
        });
    animator.setDuration(300);
    animator.start();
  }

  /** 弧线动画播放完播放下方文字展示动画 */
  private void showMoveText() {
    ObjectAnimator animator = ObjectAnimator.ofFloat(tv_move, "alpha", 0, 1);
    animator.setDuration(300);
    animator.addUpdateListener(
        animation -> {
          if ((float) animation.getAnimatedValue() == 1) {
            // 文字动画播放完 此时可以点击中间卡片了
            fl.setCanClickMid(true);
          }
        });
    animator.start();
  }

  /** 跟随手指滑动卡片 */
  private void scrollByDistance(float distance) {
    mPreDistance += distance;
    float m = getNewRotationDegree(); // 最新的旋转角度
    float r = (float) (bigRadius - cardHeight / 2.0f);
    for (int i = 0; i < cardSize; i++) {
      TorCardView v = (TorCardView) fl.getChildAt(i);
      v.setRotation(getInitRotation(i) + m);
      float currentRotation = getInitRotation(i) + m;
      float cR = getCurrentRadius(currentRotation);
      v.setTranslationX((float) (cR * Math.sin(currentRotation * Math.PI / 180.0f)));
      v.setTranslationY((float) (r - cR * Math.cos(currentRotation * Math.PI / 180.0f)));
      saveLatestAttr(v);
    }
  }

  private float getCurrentRadius(float currentRotation) {
    float rMax = (float) (bigRadius);
    float r = (float) (bigRadius - cardHeight / 2.0f);
    float cR;
    if (Math.abs(currentRotation) <= maxDegree) {
      float f = Math.abs(currentRotation);
      cR = rMax - (rMax - r) / maxDegree * f;
    } else {
      cR = r;
    }
    return cR;
  }

  /** 松开手指滑动处理 */
  private void handleAnimScroll(float xVelocity) {
    float startAngle = getNewRotationDegree(); // 松开手指时的角度
    mPreDistance += xVelocity / 8;
    float start2 = getNewRotationDegree(); // 应该旋转到的角度
    float endAngle = getEndAngle(start2); // 最终定格的角度
    float r = (float) (bigRadius - cardHeight / 2.0f);
    if (startAngle != endAngle) {
      ValueAnimator scrollAnimator = ValueAnimator.ofFloat(0, 1);
      scrollAnimator.setDuration(300);
      scrollAnimator.setInterpolator(new DecelerateInterpolator());
      scrollAnimator.addUpdateListener(
          animation -> {
            float current = (float) animation.getAnimatedValue();
            for (int i = 0; i < cardSize; i++) {
              TorCardView v = (TorCardView) fl.getChildAt(i);
              float currentRotation = v.getLastestRotation() + (endAngle - startAngle) * current;
              v.setRotation(currentRotation);
              float cR = getCurrentRadius(currentRotation);
              v.setTranslationX((float) (cR * Math.sin(currentRotation * Math.PI / 180.0f)));
              v.setTranslationY((float) (r - cR * Math.cos(currentRotation * Math.PI / 180.0f)));
            }
            if (current == 1) {
              mPreDistance = (float) (2 * Math.PI * r * endAngle) / 360;
              for (int i = 0; i < cardSize; i++) {
                TorCardView v = (TorCardView) fl.getChildAt(i);
                saveLatestAttr(v);
              }
            }
          });

      scrollAnimator.start();
    }
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
    TorCardView view = new TorCardView(activity);
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
    float r = (float) (bigRadius - cardHeight / 2.0f);
    float newRotation = (float) (mPreDistance * 360 / (2 * Math.PI * r));
    if (newRotation > maxAngle) {
      mPreDistance = (float) (maxAngle * 2 * Math.PI * r / 360);
      return maxAngle;
    }
    if (newRotation < minAngle) {
      mPreDistance = (float) (minAngle * 2 * Math.PI * r / 360);
      return minAngle;
    }
    return newRotation;
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
