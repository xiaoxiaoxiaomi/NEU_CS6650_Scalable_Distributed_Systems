package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbcp2.*;
import datasource.DBCPDataSource;

public class SwipeDataDao {

  private static BasicDataSource dataSource;

  public SwipeDataDao() throws SQLException {
    dataSource = DBCPDataSource.getDataSource();
  }

  public List<String> getMatches(String swiper) throws SQLException {
    Connection connection = null;
    PreparedStatement statement = null;
    List<String> matches = new ArrayList<>();
    String sql = "SELECT swipee FROM user_swipe_right WHERE swiper = ? LIMIT 100";
    try {
      connection = dataSource.getConnection();
      statement = connection.prepareStatement(sql);
      statement.setString(1, swiper);
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        matches.add(resultSet.getString("swipee"));
      }
      resultSet.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (connection != null) {
          connection.close();
        }
        if (statement != null) {
          statement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
    return matches;
  }

  public int[] getStats(String swiper) throws SQLException {
    Connection connection = null;
    PreparedStatement statement = null;
    String sql = "SELECT numLikes, numDislikes FROM user_likes_dislikes WHERE swiper = ?";
    try {
      connection = dataSource.getConnection();
      statement = connection.prepareStatement(sql);
      statement.setString(1, swiper);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        int numLikes = resultSet.getInt("numLikes");
        int numDislikes = resultSet.getInt("numDislikes");
        return new int[]{numLikes, numDislikes};
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (connection != null) {
          connection.close();
        }
        if (statement != null) {
          statement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
    return null;
  }
}
