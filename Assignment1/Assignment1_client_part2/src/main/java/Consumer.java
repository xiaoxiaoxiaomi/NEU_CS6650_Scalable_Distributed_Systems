import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

class Consumer implements Runnable {

  private static final String BASE_PATH = "http://52.41.211.173:8080/Assignment1_server_war/";
  private static final int MAX_RETRY_TIMES = 5;
  private BlockingQueue<SwipeData> buffer;
  private final AtomicInteger succCnt;
  private final AtomicInteger failCnt;
  private BlockingQueue<Record> records;

  private CountDownLatch latch;

  public Consumer(BlockingQueue<SwipeData> buffer, AtomicInteger succCnt, AtomicInteger failCnt,
      CountDownLatch latch, BlockingQueue<Record> records) {
    this.buffer = buffer;
    this.succCnt = succCnt;
    this.failCnt = failCnt;
    this.latch = latch;
    this.records = records;
  }

  @Override
  public void run() {
    consume();
  }

  private void consume() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(BASE_PATH);
    SwipeApi apiInstance = new SwipeApi(apiClient);
    int curSuccCnt = 0, curFailCnt = 0;
    while (true) {
      try {
        SwipeData swipeData = buffer.take();
        if (swipeData.getLeftOrRight().equals("end")) {
          latch.countDown();
          break;
        }
        if (post(swipeData, apiInstance)) {
          curSuccCnt++;
        } else {
          curFailCnt++;
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    this.succCnt.getAndAdd(curSuccCnt);
    this.failCnt.getAndAdd(curFailCnt);
  }

  private boolean post(SwipeData swipeData, SwipeApi apiInstance) {
    long startTime = System.currentTimeMillis();
    int cnt = 0;
    while (cnt < MAX_RETRY_TIMES) {
      try {
        SwipeDetails swipeDetails = swipeData.getSwipeDetails();
        String leftOrRight = swipeData.getLeftOrRight();
        ApiResponse<Void> res = apiInstance.swipeWithHttpInfo(swipeDetails, leftOrRight);
        if (res.getStatusCode() == 200 || res.getStatusCode() == 201) {
          long latency = System.currentTimeMillis() - startTime;
          Record record = new Record(startTime, "POST", latency, res.getStatusCode());
          records.put(record);
          return true;
        }
        cnt++;
      } catch (ApiException | InterruptedException e) {
        e.printStackTrace();
      }
    }
    return false;
  }
}
