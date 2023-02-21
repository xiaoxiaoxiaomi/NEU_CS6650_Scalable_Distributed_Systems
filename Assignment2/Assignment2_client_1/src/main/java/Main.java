import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

  private static final int TOTAL_REQUESTS = 500000;
  public static final int NUM_THREADS = 200;
  private static BlockingQueue<SwipeData> buffer;
  private static AtomicInteger succCnt;
  private static AtomicInteger failCnt;
  private static CountDownLatch latch;
  public static void main(String[] args) throws InterruptedException {
    buffer = new LinkedBlockingQueue<>();
    (new Thread(new Producer(buffer, TOTAL_REQUESTS, NUM_THREADS))).start();
    succCnt = new AtomicInteger(0);
    failCnt = new AtomicInteger(0);
    latch = new CountDownLatch(NUM_THREADS);
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < NUM_THREADS; i++) {
      Thread thread = new Thread(new Consumer(buffer, succCnt, failCnt, latch));
      thread.start();
    }
    latch.await();
    long wallTime = (System.currentTimeMillis() - startTime) / 1000;
    long throughput = (succCnt.get() + failCnt.get()) / wallTime;
    System.out.println("Number of successful requests: " + succCnt.get());
    System.out.println("Number of unsuccessful requests: " + failCnt.get());
    System.out.println("The total run time (secs): " + wallTime);
    System.out.println("The total throughput in requests per second: " + throughput);
  }
}
