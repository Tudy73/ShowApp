package org.example.model.security;

import lombok.Builder;
import lombok.Data;

import static org.example.database.Constants.ADMIN_COM;
import static org.example.database.Constants.CUSTOMER_COM;
import static org.example.model.security.ERole.*;

@Data
@Builder
public class User {
  private Long id;
  private String username;
  private String password;
  @Builder.Default private Integer points = 0;
  @Builder.Default private Double money = 100.0;
  private Role role;
}
