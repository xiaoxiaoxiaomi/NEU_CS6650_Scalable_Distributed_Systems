import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

  private static final String SERVER = "localhost";
  private static final int NUM_THREADS = 200;

  public static void main(String[] argv) throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(SERVER);
    factory.setUsername("guest");
    factory.setPassword("guest");
    Connection connection = factory.newConnection();
    for (int i = 0; i < NUM_THREADS; i++) {
      Thread thread = new Thread(new Consumer(connection));
      thread.start();
    }
  }
}
