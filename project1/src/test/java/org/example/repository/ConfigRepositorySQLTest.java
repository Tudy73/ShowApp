package org.example.repository;

import org.example.database.DatabaseConnectionFactory;
import org.example.database.DbConnection;
import org.example.repository.security.RoleRepositorySQL;
import org.example.repository.security.UserRepositorySQL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.example.database.Constants.PERCENTAGE;
import static org.example.database.SupportedDatabase.MYSQL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfigRepositorySQLTest {
    ConfigRepository configRepository;

    @BeforeAll
    public void setupClass() {
        DbConnection connectionWrapper = DatabaseConnectionFactory.getConnectionWrapper(MYSQL, true);
        configRepository = new ConfigRepositorySQL(connectionWrapper.getConnection());
    }

    @Test
    public void updateSetting() {
        configRepository.updateValue(PERCENTAGE, 33.3);
        assertEquals(33.0,configRepository.findValue(PERCENTAGE));
        assertThrows(RuntimeException.class,()->configRepository.findValue("DA"));
    }

    @Test
    public void findSetting() {

    }
}
