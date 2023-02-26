import com.google.gson.Gson;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "SwipeServlet", value = "/swipe/*")
public class SwipeServlet extends HttpServlet {

  private static final int SWIPER_ID_LOWER_BOUND = 1;
  private static final int SWIPER_ID_UPPER_BOUND = 5000;
  private static final int SWIPEE_ID_LOWER_BOUND = 1;
  private static final int SWIPEE_ID_UPPER_BOUND = 1000000;
  private static final int COMMENT_MAX_LENGTH = 256;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws IOException {
    processRequest(req, res);
  }

  private void processRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
    res.setContentType("application/json");
    String urlPath = req.getPathInfo();
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("Missing parameters!");
      return;
    }
    String[] urlParts = urlPath.split("/");
    if (urlParts.length != 2 || !(urlParts[1].equals("left") || urlParts[1].equals("right"))) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      res.getOutputStream().println("Invalid inputs!");
      res.getOutputStream().flush();
      return;
    }
    Gson gson = new Gson();
    try {
      StringBuilder sb = new StringBuilder();
      String s;
      while ((s = req.getReader().readLine()) != null) {
        sb.append(s);
      }
      SwipeDetails swipeDetails = gson.fromJson(sb.toString(), SwipeDetails.class);
      if (!(Integer.parseInt(swipeDetails.getSwiper()) >= SWIPER_ID_LOWER_BOUND
          && Integer.parseInt(swipeDetails.getSwiper()) <= SWIPER_ID_UPPER_BOUND)) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        res.getOutputStream().println("User not found!");
        res.getOutputStream().flush();
        return;
      }
      if (!(Integer.parseInt(swipeDetails.getSwipee()) >= SWIPEE_ID_LOWER_BOUND
          && Integer.parseInt(swipeDetails.getSwipee()) <= SWIPEE_ID_UPPER_BOUND)) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        res.getOutputStream().println("User not found!");
        res.getOutputStream().flush();
        return;
      }
      if (swipeDetails.getComment().length() > COMMENT_MAX_LENGTH) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        res.getOutputStream().println("Invalid inputs!");
        res.getOutputStream().flush();
      }
      res.setStatus(HttpServletResponse.SC_CREATED);
      res.getOutputStream().println("Write successfully!");
      res.getOutputStream().flush();
    } catch (Exception ex) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      res.getOutputStream().println("Invalid inputs!");
      res.getOutputStream().flush();
    }
  }
}
