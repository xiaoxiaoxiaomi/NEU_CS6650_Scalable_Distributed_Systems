import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.SwipeApi;

import java.io.File;
import java.util.*;

public class SwipeApiExample {

  public static void main(String[] args) {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath("http://localhost:8080/Assignment1_server_war_exploded");
    SwipeApi apiInstance = new SwipeApi(apiClient);
    SwipeDetails body = new SwipeDetails();
    body.setSwiper("123");
    body.setSwipee("12345");
    body.setComment("you are not my type, loser");
    String leftOrRight = "left";
    try {
      ApiResponse<Void> res = apiInstance.swipeWithHttpInfo(body, leftOrRight);
      System.out.println(res.getStatusCode());
    } catch (ApiException e) {
      System.err.println("Exception when calling SwipeApi#swipe");
      e.printStackTrace();
    }
  }
}
