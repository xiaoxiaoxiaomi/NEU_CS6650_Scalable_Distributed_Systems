import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.MatchesApi;
import io.swagger.client.api.StatsApi;
import io.swagger.client.model.MatchStats;
import io.swagger.client.model.Matches;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class GetThread implements Runnable {

  private static final int GET_REQUESTS_PER_SECOND = 5;
  private static final int SWIPER_ID_LOWER_BOUND = 1;
  private static final int SWIPER_ID_UPPER_BOUND = 5000;
  private CountDownLatch finishLatch;
  private final List<Long> getLatencies;

  public GetThread(CountDownLatch finishLatch) {
    this.finishLatch = finishLatch;
    this.getLatencies = new ArrayList<>();
  }

  @Override
  public void run() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(Main.BASE_PATH);
    MatchesApi matchesApiInstance = new MatchesApi(apiClient);
    StatsApi statsApiInstance = new StatsApi(apiClient);
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    Runnable getTask = () -> {
      if (finishLatch.getCount() > 0) {
        get(matchesApiInstance, statsApiInstance);
      } else {
        printLatencyStats();
        scheduler.shutdown();
      }
    };
    long delayBetweenRequests = 1000 / GET_REQUESTS_PER_SECOND;
    scheduler.scheduleAtFixedRate(getTask, 0, delayBetweenRequests, TimeUnit.MILLISECONDS);
  }

  private void get(MatchesApi matchesApiInstance, StatsApi statsApiInstance) {
    String userID = String.valueOf(
        ThreadLocalRandom.current().nextInt(SWIPER_ID_LOWER_BOUND, SWIPER_ID_UPPER_BOUND + 1));
    boolean getMatches = ThreadLocalRandom.current().nextBoolean();
    long startTime = System.currentTimeMillis();
    if (getMatches) {
      try {
        matchesApiInstance.matchesWithHttpInfo(userID);
      } catch (ApiException e) {
      }
    } else {
      try {
        statsApiInstance.matchStatsWithHttpInfo(userID);
      } catch (ApiException e) {
      }
    }
    long latency = System.currentTimeMillis() - startTime;
    getLatencies.add(latency);
  }

  private void printLatencyStats() {
    DescriptiveStatistics stats = new DescriptiveStatistics();
    for (long latency : getLatencies) {
      stats.addValue(latency);
    }
    System.out.println("GETs min response time (millisecs): " + stats.getMin());
    System.out.println("GETs mean response time (millisecs): " + stats.getMean());
    System.out.println("GETs max response time (millisecs): " + stats.getMax());
  }
}
