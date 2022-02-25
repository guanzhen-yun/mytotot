package com.example.myapplication;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.ReplaySubject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
  Disposable disposable;

  @Test
  public void test() {
    //    createAObservable();
    //    caozuofu();
    //    subject();
    ListNode node1 = getNodeData(new int[] {4, 1, 8, 4, 5});
    ListNode node2 = getNodeData(new int[] {5, 6, 1, 8, 4, 5});
    //    ListNode n = node2;
    //    while (n != null) {
    //      System.out.println(n.val);
    //      n = n.next;
    //    }
    ListNode node = getIntersectionNode(node1, node2);
    if (node != null) {
      System.out.println(node.val + "");
    }
    //    ListNode a0 = new ListNode(4);
    //
    //    ListNode c0 = new ListNode(1);
    //    ListNode c1 = new ListNode(8);
    //    ListNode c2 = new ListNode(4);
    //    ListNode c3 = new ListNode(5);
    //
    //    ListNode b0 = new ListNode(5);
    //    ListNode b1 = new ListNode(6);
    //
    //    a0.next = c0;
    //    b0.next = b1;
    //    b1.next = c0;
    //    c0.next = c1;
    //    c1.next = c2;
    //    c2.next = c3;
    //
    //    ListNode n = b0;
    //    while (n != null) {
    //      System.out.println(n.val);
    //      n = n.next;
    //    }

    //    ListNode node = getIntersectionNode(a0, b0);
    //    System.out.println(node.val);
  }

  // 4 1 8 4 5
  // 5 6 1 8 4 5

  public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
    if (headA == null || headB == null) {
      return null;
    }
    ListNode pA = headA, pB = headB;
    while (pA != pB) {
      pA = pA.next;
      pB = pB.next;

      if (pA != pB && pA == null) {
        pA = headB;
      }
      if (pA != pB && pB == null) {
        pB = headA;
      }
    }
    return pA;
  }

  ListNode getNodeData(int[] nodes) {
    ListNode node = null;
    ListNode temp = null;
    for (int i = 0; i < nodes.length; i++) {
      if (temp == null) {
        node = new ListNode();
        temp = node;
        temp.val = nodes[i];
      } else {
        ListNode n = new ListNode();
        n.val = nodes[i];
        temp.next = n;
        temp = temp.next;
      }
    }
    return node;
  }

  class ListNode {
    //    public ListNode(int val) {
    //      this.val = val;
    //    }

    public int val;
    public ListNode next;
  }

  ListNode getNode(ListNode head1, ListNode head2) {
    ListNode p1 = head1;
    ListNode p2 = head2;
    if (p1 == null) {
      return null;
    }
    if (p2 == null) {
      return null;
    }
    while (p1 != p2) {
      if (p1 == null) {
        p1 = head2;
      } else {
        p1 = p1.next;
      }
      if (p2 == null) {
        p2 = head1;
      } else {
        p2 = p2.next;
      }
    }
    return p1;
  }

  private void subject() {
    // 总的来说Subject没法指定异步线程，更像是EventBus通过订阅来实现事件通知。

    // AsyncSubject 只会输出onComplete之前的最后一个
    //    AsyncSubject<Integer> as = AsyncSubject.create();
    //    as.onNext(1);
    //    as.onNext(2);
    //    as.onNext(3);
    //    as.onComplete();
    //    as.subscribe(
    //        new Consumer<Integer>() {
    //          @Override
    //          public void accept(Integer o) throws Exception {
    //            System.out.println(o);
    //          }
    //        });

    // BehaviorSubject 会发送离订阅最近的上一个值，没有上一个值的时候会发送默认值  subscribe之前最近的还有之后的

    //    BehaviorSubject<Integer> as = BehaviorSubject.create();
    //    as.onNext(1);
    //
    //    //    as.onComplete();
    //    as.subscribe(
    //        new Consumer<Integer>() {
    //          @Override
    //          public void accept(Integer o) throws Exception {
    //            System.out.println(o);
    //          }
    //        });
    //    as.onNext(2);
    //    as.onNext(3);

    // PublishSubject 接收subscribe之后的数据
    //    PublishSubject<Integer> as = PublishSubject.create();
    //    as.onNext(1);
    //
    //    //    as.onComplete();
    //    as.subscribe(
    //        new Consumer<Integer>() {
    //          @Override
    //          public void accept(Integer o) throws Exception {
    //            System.out.println(o);
    //          }
    //        });
    //    as.onNext(2);
    //    as.onNext(3);

    // ReplaySubject 无论何时订阅，都会将所有历史订阅内容全部发出。onComplete之前的  如果没有onComplete 则都发出
    ReplaySubject<Integer> bs = ReplaySubject.create();
    // 无论何时订阅都会收到1，2，3
    bs.onNext(1);
    bs.onNext(2);

    bs.onComplete();
    bs.subscribe(
        new Consumer<Integer>() {
          @Override
          public void accept(Integer o) {
            System.out.println(o);
          }
        });
    bs.onNext(3);
  }

  /** 操作符 */
  private void caozuofu() {
    // map 数据类型转换
    //    Observable<Integer> observable =
    //        Observable.just("hello")
    //            .map(
    //                new Function<String, Integer>() {
    //                  @Override
    //                  public Integer apply(String s) throws Exception {
    //                    return s.length();
    //                  }
    //                });
    //    observable.subscribe(
    //        new Consumer<Integer>() {
    //          @Override
    //          public void accept(Integer integer) throws Exception {
    //            System.out.println(integer);
    //          }
    //        });

    // flatMap 相当于将集合打开后按需求重组依次调用onNext()
    //    List<Integer> list = new ArrayList<>();
    //    list.add(1);
    //    list.add(2);
    //    list.add(3);
    //    Observable<Integer> observable1 =
    //        Observable.just(list)
    //            .flatMap(
    //                new Function<List<Integer>, ObservableSource<Integer>>() {
    //                  @Override
    //                  public ObservableSource<Integer> apply(List<Integer> integers) throws
    // Exception {
    //                    return Observable.fromIterable(integers);
    //                  }
    //                });
    //
    //    observable1.subscribe(
    //        new Consumer<Integer>() {
    //          @Override
    //          public void accept(Integer integer) throws Exception {
    //            System.out.println(integer);
    //          }
    //        });

    // filter()过滤操作符
    List<Integer> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      list.add(i);
    }

    Observable.just(list)
        .flatMap(
            new Function<List<Integer>, ObservableSource<?>>() {
              @Override
              public ObservableSource<?> apply(List<Integer> integers) throws Exception {
                return Observable.fromIterable(integers);
              }
            })
        .filter(
            new Predicate<Object>() {
              @Override
              public boolean test(Object o) throws Exception {
                Integer i = (Integer) o;
                return i % 2 == 0;
              }
            })
        .take(2) // 输出最多制定数量的结果
        .doOnNext(
            new Consumer<Object>() {
              @Override
              public void accept(Object o) throws Exception {
                // 输出数据之前做的事情
                System.out.println("我是" + o);
              }
            })
        .subscribe(
            new Consumer<Object>() {
              @Override
              public void accept(Object o) throws Exception {
                System.out.println(o);
              }
            });
  }

  /** 创建 */
  private void createAObservable() {
    // 创建一个Observable 被观察者对象
    Observable<String> observable =

        // create形式创建
        Observable.create(
            new ObservableOnSubscribe<String>() {
              @Override
              public void subscribe(ObservableEmitter<String> e) throws Exception {
                // 执行一些其他操作
                // 执行完毕，触发回调，通知观察者
                e.onNext("我来发射数据");
              }
            });

    // just形式创建
    Observable<String> observable2 = Observable.just("你好"); // just自动触发onNext

    // fromIterable形式创建 相当于便利集合 依次调用onNext
    List<String> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      list.add("Hello" + i);
    }
    Observable<String> observable3 = Observable.fromIterable(list);

    // defer()方式 在观察者订阅时候，才会创建Observable
    Observable<String> observable4 =
        Observable.defer(
            new Callable<ObservableSource<? extends String>>() {
              @Override
              public ObservableSource<? extends String> call() throws Exception {
                return Observable.just("hello");
              }
            });

    // interval()方式
    // 定时器 一定时间间隔调用onNext  android中应用

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
    //                System.out.println(aLong);
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

    // range()方式  (1--20) 分别执行onNext
    //    Observable<Integer> observable6 = Observable.range(1, 20);
    //    observable6.subscribe(
    //        new Consumer<Integer>() {
    //          @Override
    //          public void accept(Integer integer) throws Exception {
    //            System.out.println(integer);
    //          }
    //        });

    // timer() 延迟发射
    //    Observable<Long> observable7 = Observable.timer(2, TimeUnit.SECONDS);
    //    observable7.subscribe(
    //        new Consumer<Long>() {
    //          @Override
    //          public void accept(Long aLong) throws Exception {
    //            System.out.println(aLong);
    //          }
    //        });

    Observable observable6 = Observable.just("123").repeat(); // 反复执行 onNext

    // 创建Observer 观察者
    Observer<String> observer =
        new Observer<String>() {
          @Override
          public void onSubscribe(Disposable d) {}

          @Override
          public void onNext(String s) {
            System.out.println("我接收到数据了" + s);
          }

          @Override
          public void onError(Throwable e) {}

          @Override
          public void onComplete() {}
        };

    observable6.subscribe(observer);
    // Observable (被观察者)只有在被Observer (观察者)订阅后才能执行其内部的相关逻辑
  }
}
