import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleThreadTest {

  private static final int TOTAL_REQUESTS = 10000;

  public static void main(String[] args) throws InterruptedException {
    BlockingQueue<SwipeData> buffer = new LinkedBlockingQueue<>();
    int numOfThreads = 1;
    (new Thread(new Producer(buffer, TOTAL_REQUESTS, numOfThreads))).start();
    AtomicInteger succCnt = new AtomicInteger(0);
    AtomicInteger failCnt = new AtomicInteger(0);
    CountDownLatch latch = new CountDownLatch(numOfThreads);
    long startTime = System.currentTimeMillis();
    (new Thread(new Consumer(buffer, succCnt, failCnt, latch))).start();
    latch.await();
    long wallTime = System.currentTimeMillis() - startTime;
    long latency = wallTime / TOTAL_REQUESTS;
    System.out.println("Number of successful requests: " + succCnt.get());
    System.out.println("Number of unsuccessful requests: " + failCnt.get());
    System.out.println("The total run time (millisecs): " + wallTime);
    System.out.println("The average latency (millisecs): " + latency);
    System.out.println("Number of threads: " + Main.NUM_THREADS);
    System.out.println("The expected throughput using Little’s Law (requests/second): " + Main.NUM_THREADS * 1000 / latency);
  }
}
