package org.example.repository.product;

import org.example.model.product.Pile;
import org.example.model.product.Product;
import org.example.model.security.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepositorySQL implements ProductRepository {

  private final Connection connection;

  public ProductRepositorySQL(Connection connection) {
    this.connection = connection;
  }

  @Override
  public List<Product> findAll() {
    final String sql = "Select * from product";

    List<Product> products = new ArrayList<>();

    try {
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(sql);

      while (resultSet.next()) {
        products.add(getProductFromResultSet(resultSet));
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }

    return products;
  }

  @Override
  public Optional<Product> findById(Long id) {
    final String sql = "Select * from product where id = ?";

    try {
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setLong(1, id);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        return Optional.of(getProductFromResultSet(resultSet));
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }

    return Optional.empty();
  }

  @Override
  public Product create(Product product) throws SQLException {
    String sql = "INSERT INTO product values (null, ?, ?)";

    try {
      PreparedStatement insertStatement = connection
          .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      insertStatement.setString(1, product.getName());
      insertStatement.setDouble(2, product.getPrice());
      insertStatement.executeUpdate();

      ResultSet generatedKeys = insertStatement.getGeneratedKeys();
      generatedKeys.next();
      long productId = generatedKeys.getLong(1);
      product.setId(productId);
      return product;
    } catch (SQLException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public List<Pile> findCart(long userId) {
    final String sql = "Select * from user_product where `user_id` =?";

    List<Pile> pile = new ArrayList<>();

    try {
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setLong(1, userId);
      ResultSet resultSet = statement.executeQuery();

      while (resultSet.next()) {
        pile.add(getPileFromResultSet(resultSet));
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }

    return pile;
  }

  @Override
  public Pile findProductAndUser(User user, Product product) {
    final String sql = "Select * from user_product where `user_id` = ? and `product_id` = ?";

    try {
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, user.getId().intValue());
      statement.setInt(2,product.getId().intValue());
      ResultSet resultSet = statement.executeQuery();
      if(resultSet.next()){
        return getPileFromResultSet(resultSet);
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    return null;
  }

  @Override
  public void decrementProductFromCart(long user_id, long product_id) {
    String updateSql = "UPDATE user_product SET amount = amount - 1 WHERE user_id = ? AND product_id = ?;";
    try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
      updateStatement.setInt(1, (int) user_id);
      updateStatement.setInt(2, (int) product_id);
      updateStatement.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
    String deleteSql = "DELETE FROM user_product WHERE user_id = ? AND product_id = ? AND amount <= 0;";
    try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
      deleteStatement.setInt(1, (int) user_id);
      deleteStatement.setInt(2, (int) product_id);
      deleteStatement.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
  }
  @Override
  public void incrementProductInCart(long user_id, long product_id) {
    String sql =
            "INSERT INTO user_product"+
            " values (null,?, ?, 1) "+
            "ON DUPLICATE KEY UPDATE amount = amount + 1;";
      try {
        PreparedStatement insertStatement = connection
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        insertStatement.setInt(1, (int)user_id);
        insertStatement.setInt(2, (int)product_id);
        insertStatement.executeUpdate();
      } catch (SQLException e) {
          throw new RuntimeException(e);
      }
  }

  @Override
  public void deleteCart(long user_id) {
    try {
      String sql = "DELETE FROM user_product WHERE `user_id` = ?";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, (int)user_id);
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private Product getProductFromResultSet(ResultSet rs) throws SQLException {
    return Product.builder()
        .id(rs.getLong("id"))
        .price(rs.getDouble("price"))
        .name(rs.getString("name"))
        .build();
  }
  private Pile getPileFromResultSet(ResultSet rs) throws SQLException {

    return Pile.builder()
            .amount((long) rs.getInt("amount"))
            .product(findById((long)rs.getInt("product_id")).get())
            .build();
  }
}
