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
      Swipe swipe = gson.fromJson(sb.toString(), Swipe.class);
      if (!(Integer.parseInt(swipe.getSwiper()) >= 1
          && Integer.parseInt(swipe.getSwiper()) <= 5000)) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        res.getOutputStream().println("User not found!");
        res.getOutputStream().flush();
        return;
      }
      if (!(Integer.parseInt(swipe.getSwipee()) >= 1
          && Integer.parseInt(swipe.getSwipee()) <= 1000000)) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        res.getOutputStream().println("User not found!");
        res.getOutputStream().flush();
        return;
      }
      res.setStatus(HttpServletResponse.SC_CREATED);
      res.getOutputStream().println("Write successfully!");
      res.getOutputStream().flush();
    } catch (Exception ex) {
      res.getOutputStream().println("Invalid inputs!");
      res.getOutputStream().flush();
    }
  }
}
