package org.example.repository;

public interface ConfigRepository {
    void updateValue(String name,double value);
    double findValue(String name);
}
