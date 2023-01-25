import com.google.gson.Gson;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "SwipeServlet", value = "/swipe/*")
public class SwipeServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws IOException {
    res.setContentType("text/plain");
    String urlPath = req.getPathInfo();
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("Missing parameters!");
      return;
    }
    String[] urlParts = urlPath.split("/");
    if (urlParts.length != 2 || !(urlParts[1].equals("left") || urlParts[1].equals("right"))) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      res.getWriter().write("Invalid inputs!");
      return;
    }
    Gson gson = new Gson();
    try {
      StringBuilder sb = new StringBuilder();
      String s;
      while ((s = req.getReader().readLine()) != null) {
        sb.append(s);
      }
      Swipe swipe = gson.fromJson(sb.toString(), Swipe.class);
      if (!(Integer.parseInt(swipe.getSwiper()) >= 1
          && Integer.parseInt(swipe.getSwiper()) <= 5000)) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        res.getWriter().write("User not found!");
        return;
      }
      if (!(Integer.parseInt(swipe.getSwipee()) >= 1
          && Integer.parseInt(swipe.getSwipee()) <= 1000000)) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        res.getWriter().write("User not found!");
        return;
      }
      if (swipe.getComment().length() > 256) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        res.getWriter().write("Invalid inputs!");
        return;
      }
      res.setStatus(HttpServletResponse.SC_CREATED);
      res.getWriter().write("Write successfully!");
    } catch (Exception ex) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      res.getWriter().write("Invalid inputs!");
    }
  }
}
