package org.example.database;

import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;

@Getter
public abstract class DbConnection {

  protected Connection connection;
  public abstract boolean testConnection() throws SQLException;
}
