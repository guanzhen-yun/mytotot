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
    // ????????????Subject????????????????????????????????????EventBus????????????????????????????????????

    // AsyncSubject ????????????onComplete?????????????????????
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

    // BehaviorSubject ???????????????????????????????????????????????????????????????????????????????????????  subscribe??????????????????????????????

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

    // PublishSubject ??????subscribe???????????????
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

    // ReplaySubject ?????????????????????????????????????????????????????????????????????onComplete?????????  ????????????onComplete ????????????
    ReplaySubject<Integer> bs = ReplaySubject.create();
    // ??????????????????????????????1???2???3
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

  /** ????????? */
  private void caozuofu() {
    // map ??????????????????
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

    // flatMap ??????????????????????????????????????????????????????onNext()
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

    // filter()???????????????
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
        .take(2) // ?????????????????????????????????
        .doOnNext(
            new Consumer<Object>() {
              @Override
              public void accept(Object o) throws Exception {
                // ??????????????????????????????
                System.out.println("??????" + o);
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

  /** ?????? */
  private void createAObservable() {
    // ????????????Observable ??????????????????
    Observable<String> observable =

        // create????????????
        Observable.create(
            new ObservableOnSubscribe<String>() {
              @Override
              public void subscribe(ObservableEmitter<String> e) throws Exception {
                // ????????????????????????
                // ?????????????????????????????????????????????
                e.onNext("??????????????????");
              }
            });

    // just????????????
    Observable<String> observable2 = Observable.just("??????"); // just????????????onNext

    // fromIterable???????????? ????????????????????? ????????????onNext
    List<String> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      list.add("Hello" + i);
    }
    Observable<String> observable3 = Observable.fromIterable(list);

    // defer()?????? ???????????????????????????????????????Observable
    Observable<String> observable4 =
        Observable.defer(
            new Callable<ObservableSource<? extends String>>() {
              @Override
              public ObservableSource<? extends String> call() throws Exception {
                return Observable.just("hello");
              }
            });

    // interval()??????
    // ????????? ????????????????????????onNext  android?????????

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
    //                  disposable.dispose(); // ??????10????????????
    //                }
    //              }
    //
    //              @Override
    //              public void onError(Throwable e) {}
    //
    //              @Override
    //              public void onComplete() {}
    //            });

    // range()??????  (1--20) ????????????onNext
    //    Observable<Integer> observable6 = Observable.range(1, 20);
    //    observable6.subscribe(
    //        new Consumer<Integer>() {
    //          @Override
    //          public void accept(Integer integer) throws Exception {
    //            System.out.println(integer);
    //          }
    //        });

    // timer() ????????????
    //    Observable<Long> observable7 = Observable.timer(2, TimeUnit.SECONDS);
    //    observable7.subscribe(
    //        new Consumer<Long>() {
    //          @Override
    //          public void accept(Long aLong) throws Exception {
    //            System.out.println(aLong);
    //          }
    //        });

    Observable observable6 = Observable.just("123").repeat(); // ???????????? onNext

    // ??????Observer ?????????
    Observer<String> observer =
        new Observer<String>() {
          @Override
          public void onSubscribe(Disposable d) {}

          @Override
          public void onNext(String s) {
            System.out.println("?????????????????????" + s);
          }

          @Override
          public void onError(Throwable e) {}

          @Override
          public void onComplete() {}
        };

    observable6.subscribe(observer);
    // Observable (????????????)????????????Observer (?????????)?????????????????????????????????????????????
  }
}
