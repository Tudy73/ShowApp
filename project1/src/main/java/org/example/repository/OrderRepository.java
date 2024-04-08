package org.example.repository;

import java.sql.SQLException;

public interface OrderRepository {

    void deleteForUser(Long id);
    boolean findOrder(Long id);

    void addOrder(Long id) throws SQLException;

    void deleteAll();
}
