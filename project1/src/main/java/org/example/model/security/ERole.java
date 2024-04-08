package org.example.model.security;

import lombok.Getter;

public enum ERole {
  ADMINISTRATOR(0),
  CASHIER(1),
  CUSTOMER(2);
  @Getter
  private final int value;
  ERole(int value) {
    this.value = value;
  }
}
