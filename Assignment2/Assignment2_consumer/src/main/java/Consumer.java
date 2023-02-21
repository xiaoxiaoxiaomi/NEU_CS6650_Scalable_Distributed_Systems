import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Consumer implements Runnable {

  private static final String QUEUE_NAME = "test";
  private Gson gson = new Gson();
  private Connection connection;
  private ConcurrentHashMap<String, int[]> userLikesDislikes;
  private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> userPotentialMatches;

  public Consumer(Connection connection, ConcurrentHashMap<String, int[]> userLikesDislikes,
      ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> userPotentialMatches) {
    this.connection = connection;
    this.userLikesDislikes = userLikesDislikes;
    this.userPotentialMatches = userPotentialMatches;
  }

  @Override
  public void run() {
    Channel channel;
    try {
      channel = connection.createChannel();
      channel.queueDeclare(QUEUE_NAME, false, false, false, null);
      channel.basicQos(1);
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        handleMessage(message, userLikesDislikes, userPotentialMatches);
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        System.out.println(" [x] Received '" + message + "'");
      };
      channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleMessage(String message, ConcurrentHashMap<String, int[]> userLikesDislikes,
      ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> userPotentialMatches) {
    SwipeDetails swipeDetails = gson.fromJson(message, SwipeDetails.class);
    String swiper = swipeDetails.getSwiper();
    String leftOrRight = swipeDetails.getLeftOrRight();
    userLikesDislikes.computeIfAbsent(swiper, k -> new int[2]);
    if (leftOrRight.equals("left")) {
      userLikesDislikes.get(swiper)[0]++;
    } else if (leftOrRight.equals("right")) {
      userLikesDislikes.get(swiper)[1]++;
      userPotentialMatches.computeIfAbsent(swiper, k -> new ConcurrentLinkedQueue<>());
      ConcurrentLinkedQueue<String> potentialMatches = userPotentialMatches.get(swiper);
      if (potentialMatches.size() >= 100) {
        potentialMatches.poll();
      }
      potentialMatches.offer(swipeDetails.getSwipee());
    }
  }
}
