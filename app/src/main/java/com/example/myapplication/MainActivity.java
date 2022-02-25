package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
  //  Disposable disposable;
  float size;
  EditText editText;

  private void setEditHint(String str, float s) {
    // 设置hint字体大小
    editText.setTextSize(px2sp(MainActivity.this, s));
  }

  private int getWidth() {
    DisplayMetrics outMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
    int widthPixels = outMetrics.widthPixels;
    return widthPixels;
    //    int heightPixels = outMetrics.heightPixels;
  }

  public static int px2sp(Context context, float pxValue) {
    final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (pxValue / fontScale + 0.5f);
  }

  TextView tv_text;

  private void setT() {
    tv_text.post(
        new Runnable() {
          @Override
          public void run() {
            int lineCount = tv_text.getLineCount();
            if (lineCount > 1) {
              tv_text.setTextSize(px2sp(MainActivity.this, tv_text.getTextSize()) - 1);
              setT();
            } else {
              setEditHint(editText.getHint().toString(), tv_text.getTextSize());
            }
          }
        });
  }

  @SuppressLint("CheckResult")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    editText = findViewById(R.id.et_test);
    size = editText.getTextSize();
    tv_text = findViewById(R.id.tv_text);
    tv_text.setText(editText.getHint());
    setT();
    //    startActivity(new Intent(MainActivity.this, SecondActivity.class));

    //    Observable.interval(1, TimeUnit.SECONDS)
    //        .subscribe(
    //            new Observer<Long>() {
    //              @Override
    //              public void onSubscribe(Disposable d) {
    //                disposable = d;
    //              }
    //
    //              @Override
    //              public void onNext(Long aLong) {
    //                Log.d("Rxjava", aLong + "");
    //                if (aLong == 10) {
    //                  disposable.dispose(); // 等于10取消订阅
    //                }
    //              }
    //
    //              @Override
    //              public void onError(Throwable e) {}
    //
    //              @Override
    //              public void onComplete() {}
    //            });

    //    Observable<Long> observable7 = Observable.timer(2, TimeUnit.SECONDS);
    //    observable7.subscribe(
    //        new Consumer<Long>() {
    //          @Override
    //          public void accept(Long aLong) throws Exception {
    //            Log.d("Rxjava", aLong + ""); // 输出的是0 延时2秒
    //          }
    //        });

    // Scheduler 线程调度器
    //    Schedulers.immediate(); 默认 用当前的线程
    //      Schedulers.newThread();//每次都创建新的线程
    // Schedulers.io();//使用一个无数量上限的线程池 io工作(数据库 网络 文件操作)，重用空闲的线程，不要用于(图形)计算操作，以免浪费线程
    // Schedulers.computation();//固定数量的线程池 图形计算操作，CPU核数 不要用于io 以免io等待时间浪费CPU
    // AndroidSchedulers.mainThread() ui线程
    // subscribeOn(): 指定Observable(被观察者)所在的线程，或者叫做事件产生的线程。
    // observeOn(): 指定 Observer(观察者)所运行在的线程，或者叫做事件消费的线程。

    // 线程切换
    //    Observable.create(
    //            new ObservableOnSubscribe<Integer>() {
    //              @Override
    //              public void subscribe(ObservableEmitter<Integer> e) throws Exception {
    //                Log.d("所在的线程: ", Thread.currentThread().getName());
    //                Log.d("发送的数据:", 1 + "");
    //                e.onNext(1);
    //              }
    //            })
    //        .subscribeOn(Schedulers.io())
    //        .observeOn(AndroidSchedulers.mainThread())
    //        .subscribe(
    //            new Consumer<Integer>() {
    //              @Override
    //              public void accept(Integer integer) throws Exception {
    //                Log.d("所在的线程：", Thread.currentThread().getName());
    //                Log.d("接收到的数据:", "integer:" + integer);
    //              }
    //            });

    // 网络请求
    //    Retrofit retrofit = create();
    //    Api api = retrofit.create(Api.class);
    //    Observable<AllCity> observable = api.getAllCity("ce777fb1ee43686d7e78dfa112c2c7ce");
    //    observable
    //        .subscribeOn(Schedulers.io())
    //        .flatMap(
    //            new Function<AllCity, ObservableSource<City>>() {
    //              @Override
    //              public ObservableSource<City> apply(AllCity city) throws Exception {
    //                ArrayList<City> result = city.getResult();
    //                return Observable.fromIterable(result);
    //              }
    //            })
    //        .filter(
    //            new Predicate<City>() {
    //              @Override
    //              public boolean test(City city) throws Exception {
    //                String id = city.getId();
    //                if (Integer.parseInt(id) < 5) {
    //                  return true;
    //                }
    //                return false;
    //              }
    //            })
    //        .observeOn(AndroidSchedulers.mainThread())
    //        .subscribe(
    //            new Consumer<City>() {
    //              @Override
    //              public void accept(City city) throws Exception {
    //                System.out.println(city);
    //              }
    //            });

    // 背压 Flowable BackPressure  当生产者速度大于消费者速度带来的问题  异步情况下，Backpressure问题才会存在
  }

  //  private static Retrofit create() {
  //    OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
  //    builder.readTimeout(10, TimeUnit.SECONDS);
  //    builder.connectTimeout(9, TimeUnit.SECONDS);
  //    String baseUrl = "http://v.juhe.cn/weather/";
  //    return new Retrofit.Builder()
  //        .baseUrl(baseUrl)
  //        .client(builder.build())
  //        .addConverterFactory(GsonConverterFactory.create())
  //        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
  //        .build();
  //  }
}
