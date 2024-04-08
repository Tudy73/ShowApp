package org.example.database;

import org.example.model.product.EProduct;
import org.example.model.security.ERole;
import org.example.model.security.Role;
import org.example.model.security.User;
import org.example.repository.product.ProductRepository;
import org.example.repository.product.ProductRepositorySQL;
import org.example.repository.security.RoleRepository;
import org.example.repository.security.RoleRepositorySQL;
import org.example.repository.security.UserRepository;
import org.example.repository.security.UserRepositorySQL;
import org.example.service.security.SecurityService;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import static org.example.database.Constants.SCHEMAS.SCHEMAS;

public class Bootstrap {
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;

    public void bootstrap() throws SQLException {
        dropAll();
        createTables();
        createUserData();
    }

    private void dropAll() throws SQLException {
        for (String schema : SCHEMAS) {
            System.out.println("Dropping all tables in schema: " + schema);
            Connection connection = new JDBConnectionWrapper(schema).getConnection();
            Statement statement = connection.createStatement();

            String[] dropStatements = {
                    "TRUNCATE `user_role`;",
                    "DROP TABLE IF EXISTS `user_role`;",
                    "TRUNCATE `user_product`;",
                    "DROP TABLE IF EXISTS `user_product`;",
                    "TRUNCATE `report`;",
                    "DROP TABLE IF EXISTS `report`;",
                    "TRUNCATE `order`;",
                    "DROP TABLE IF EXISTS `order`;",
                    "TRUNCATE `role`;",
                    "DROP TABLE IF EXISTS `product`, `role`, `user`, `config`;"
            };


            Arrays.stream(dropStatements).forEach(dropStatement -> {
                try {
                    statement.execute(dropStatement);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void createTables() throws SQLException {
        SQLTableCreationFactory sqlTableCreationFactory = new SQLTableCreationFactory();

        for (String schema : SCHEMAS) {
            System.out.println("Bootstrapping " + schema + " schema");

            JDBConnectionWrapper connectionWrapper = new JDBConnectionWrapper(schema);
            Connection connection = connectionWrapper.getConnection();

            Statement statement = connection.createStatement();

            for (String table : Constants.TABLES.ORDERED_TABLES_FOR_CREATION) {
                String createTableSQL = sqlTableCreationFactory.getCreateSQLForTable(table);
                statement.execute(createTableSQL);
            }
        }
    }

    private void createUserData() {
        for (String schema : SCHEMAS) {
            System.out.println("Bootstrapping user data for " + schema);

            createRoles(schema);
            createUsers(schema);
            createProducts(schema);
            createConfig(schema);
        }
    }

    private void createConfig(String schema) {
        JDBConnectionWrapper connectionWrapper = new JDBConnectionWrapper(schema);
    }

    private void createProducts(String schema) {
        JDBConnectionWrapper connectionWrapper = new JDBConnectionWrapper(schema);
        productRepository = new ProductRepositorySQL(connectionWrapper.getConnection());
        for (EProduct product : EProduct.values()) {
            try {
                productRepository.create(product.getProduct());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void createRoles(String schema) {
        JDBConnectionWrapper connectionWrapper = new JDBConnectionWrapper(schema);
        roleRepository = new RoleRepositorySQL(connectionWrapper.getConnection());
        for (ERole role : ERole.values()) {
            roleRepository.create(role);
        }
    }

    private void createUsers(String schema) {
        // ...
    }
}
