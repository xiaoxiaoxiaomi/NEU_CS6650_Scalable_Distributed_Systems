import io.swagger.client.model.SwipeDetails;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

class Producer implements Runnable {

  private static final String[] LEFT_OR_RIGHT = {"left", "right"};
  private static final String[] COMMENTS = {"You are not my type, loser.", "I'm interested"};
  private final BlockingQueue<SwipeData> buffer;
  private final int total;
  private final int numOfThreads;

  public Producer(BlockingQueue<SwipeData> buffer, int total, int numOfThreads) {
    this.buffer = buffer;
    this.total = total;
    this.numOfThreads = numOfThreads;
  }

  @Override
  public void run() {
    produce();
  }

  private void produce() {
    try {
      for (int i = 0; i < total; i++) {
        SwipeData swipeData = generate();
        buffer.put(swipeData);
      }
      for (int j = 0; j < numOfThreads; j++) {
        SwipeData endOfData = new SwipeData("end", new SwipeDetails());
        buffer.put(endOfData);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private SwipeData generate() {
    String leftOrRight = LEFT_OR_RIGHT[ThreadLocalRandom.current().nextInt(2)];
    String swiper = String.valueOf(ThreadLocalRandom.current().nextInt(1, 5001));
    String swipee = String.valueOf(ThreadLocalRandom.current().nextInt(1, 1000001));
    String comment = COMMENTS[ThreadLocalRandom.current().nextInt(2)];
    SwipeDetails swipeDetails = new SwipeDetails();
    swipeDetails.setSwiper(swiper);
    swipeDetails.setSwipee(swipee);
    swipeDetails.setComment(comment);
    return new SwipeData(leftOrRight, swipeDetails);
  }
}
