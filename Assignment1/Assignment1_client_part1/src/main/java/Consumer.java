import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

class Consumer implements Runnable {

  private static final String BASE_PATH = "http://localhost:8080/Assignment1_server_war_exploded/";
  private static final int MAX_RETRY_TIMES = 5;
  private BlockingQueue<SwipeData> buffer;
  private final AtomicInteger succCnt;
  private final AtomicInteger failCnt;

  private CountDownLatch latch;

  public Consumer(BlockingQueue<SwipeData> buffer, AtomicInteger succCnt, AtomicInteger failCnt, CountDownLatch latch) {
    this.buffer = buffer;
    this.succCnt = succCnt;
    this.failCnt = failCnt;
    this.latch = latch;
  }

  @Override
  public void run() {
    consume();
  }

  private void consume() {
    while (true) {
      try {
        SwipeData swipeData = buffer.take();
        if (swipeData.getLeftOrRight().equals("end")) {
          latch.countDown();
          return;
        }
        if (post(swipeData)) {
          succCnt.getAndIncrement();
        } else {
          failCnt.getAndIncrement();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
//    for (int i = 0; i < numOfRequests; i++) {
//      try {
//        SwipeData swipeData = buffer.take();
//        post(swipeData);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//    }
  }

  private boolean post(SwipeData swipeData) {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(BASE_PATH);
    SwipeApi apiInstance = new SwipeApi(apiClient);
    int cnt = 0;
    while (cnt < MAX_RETRY_TIMES) {
      try {
        SwipeDetails swipeDetails = swipeData.getSwipeDetails();
        String leftOrRight = swipeData.getLeftOrRight();
        ApiResponse<Void> res = apiInstance.swipeWithHttpInfo(swipeDetails, leftOrRight);
        if (res.getStatusCode() == 200 || res.getStatusCode() == 201) {
          return true;
        }
        cnt++;
      } catch (ApiException e) {
        e.printStackTrace();
      }
    }
    return false;
  }
}