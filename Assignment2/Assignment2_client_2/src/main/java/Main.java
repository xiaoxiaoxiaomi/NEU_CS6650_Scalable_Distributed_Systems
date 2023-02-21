import java.io.FileWriter;
import java.io.IOException;
import com.opencsv.CSVWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Main {

  private static final int TOTAL_REQUESTS = 500000;
  private static final int NUM_THREADS = 200;
  private static BlockingQueue<SwipeData> buffer;
  private static AtomicInteger succCnt;
  private static AtomicInteger failCnt;
  private static CountDownLatch latch;
  private static BlockingQueue<Record> records;

  public static void main(String[] args) throws InterruptedException, IOException {
    buffer = new LinkedBlockingQueue<>();
    (new Thread(new Producer(buffer, TOTAL_REQUESTS, NUM_THREADS))).start();
    succCnt = new AtomicInteger(0);
    failCnt = new AtomicInteger(0);
    latch = new CountDownLatch(NUM_THREADS);
    records = new LinkedBlockingQueue<>();
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < NUM_THREADS; i++) {
      Thread thread = new Thread(new Consumer(buffer, succCnt, failCnt, latch, records));
      thread.start();
    }
    latch.await();
    long wallTime = (System.currentTimeMillis() - startTime) / 1000;
    long throughput = (succCnt.get() + failCnt.get()) / wallTime;
    System.out.println("Number of successful requests: " + succCnt.get());
    System.out.println("Number of unsuccessful requests: " + failCnt.get());
    System.out.println("The total run time (secs): " + wallTime);
    System.out.println("--------------------------------------------------");
    DescriptiveStatistics stats = new DescriptiveStatistics();
    String csvFile = "records.csv";
    CSVWriter writer = new CSVWriter(new FileWriter(csvFile));
    String[] header = {"Start Time", "Request Type", "Latency", "Response Code"};
    writer.writeNext(header);
    for (Record record : records) {
      stats.addValue(record.getLatency());
      String[] line = {String.valueOf(record.getStartTime()), record.getRequestType(),
          String.valueOf(record.getLatency()), String.valueOf(record.getResponseCode())};
      writer.writeNext(line);
    }
    writer.flush();
    writer.close();
    System.out.println("Mean response time (millisecs): " + stats.getMean());
    System.out.println("Median response time (millisecs): " + stats.getPercentile(50));
    System.out.println(
        "P99 (99th percentile) response time (millisecs): " + stats.getPercentile(99));
    System.out.println("Min response time (millisecs): " + stats.getMin());
    System.out.println("Max response time (millisecs): " + stats.getMax());
    System.out.println("Throughput (requests/second): " + throughput);
  }
}
