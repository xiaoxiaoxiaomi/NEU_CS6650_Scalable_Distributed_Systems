import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

public class Main {

  private static final String SERVER = "54.71.64.218";
  private static final int NUM_THREADS = 200;
  static ConcurrentHashMap<String, int[]> userLikesDislikes = new ConcurrentHashMap<>();
  static ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> userPotentialMatches = new ConcurrentHashMap<>();

  public static void main(String[] argv) throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(SERVER);
    factory.setUsername("rabbit");
    factory.setPassword("rabbit");
    Connection connection = factory.newConnection();
    for (int i = 0; i < NUM_THREADS; i++) {
      Thread thread = new Thread(new Consumer(connection, userLikesDislikes, userPotentialMatches));
      thread.start();
    }
  }
}
