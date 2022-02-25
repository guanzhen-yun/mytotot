package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/** Flowable和BackPressure */
public class SecondActivity extends AppCompatActivity {
  private static final String TAG = "SecondActivity";
  private Subscription mSubscription;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    handleOne();
  }

  /**
   * ERROR 第一种方式 BackpressureStrategy.ERROR 背压策略 MissingBackpressureException
   * ERROR即保证在异步操作中，事件累积不能超过128，超过即出现异常。消费者不能再接收事件了，但生产者并不会停止。
   */
  private void handleOne() {
    Flowable<Integer> flowable =
        Flowable.create(
            new FlowableOnSubscribe<Integer>() {
              @Override
              public void subscribe(@NonNull FlowableEmitter<Integer> emitter) throws Exception {
                //                Log.d(TAG, "emit 1");
                //                emitter.onNext(1);
                //                Log.d(TAG, "emit 2");
                //                emitter.onNext(2);
                //                Log.d(TAG, "emit 3");
                //                emitter.onNext(3);
                //                Log.d(TAG, "emit complete");
                //                emitter.onComplete();
                for (int i = 0; i < 129; i++) {
                  Log.d(TAG, "emit " + i);
                  emitter.onNext(i);
                }
              }
            },
            BackpressureStrategy.ERROR);

    Subscriber<Integer> subscriber =
        new Subscriber<Integer>() {
          @Override
          public void onSubscribe(Subscription s) {
            //            Log.d(TAG, "onSubscribe");
            //            s.request(Long.MAX_VALUE);
            mSubscription = s;
          }

          @Override
          public void onNext(Integer integer) {
            Log.d(TAG, "onNext: " + integer);
          }

          @Override
          public void onError(Throwable t) {
            Log.w(TAG, "onError", t);
          }

          @Override
          public void onComplete() {
            //            Log.d(TAG, "onComplete");
          }
        };
    flowable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(subscriber);
  }

  // BUFFER 第二种背压策略 将128最大数量改变  比较消耗内存 慎用

  // DROP 第三种背压策略 当消费者处理不了事件，就丢弃
  // LATEST 总能使消费者能够接收到生产者产生的最后一个事件 Flowable不再无限发事件，只发送1000000个
}
