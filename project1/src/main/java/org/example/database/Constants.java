package org.example.database;

public class Constants {


  public static final String ADMIN_COM = "@admin.com";
  public static final String CUSTOMER_COM = "@customer.com";
  public static final String ADMINISTRATOR = "ADMINISTRATOR";
  public static final String CASHIER = "CASHIER";
  public static final String CUSTOMER = "CUSTOMER";

  public static final String LOGIN_SUCCESSFUL = "Login successful!";
  public static final String PERCENTAGE = "percentage";

  public static class ERRORS{
    public static final String NO_USER_FOUND = "No user found.";
    public static final String NO_REPORT_FOUND = "No report found.";
    public static final String SOMETHING_IS_WRONG_WITH_THE_DATABASE = "Something is wrong with the database.";

    public static final String VALUE_NOT_FOUND = "Value not found";

  }

  public static class TESTING{
    public static final String SALT="Salt",MILK="Milk",SUGAR="Sugar";
    public static final String[] products = {SALT,SUGAR,MILK};

  }

  public static class SCHEMAS {
    public static final String TEST = "sd-basics-test";
    public static final String PRODUCTION = "sd-basics";

    public static final String[] SCHEMAS = new String[]{TEST, PRODUCTION};
  }

  public static class TABLES {
    public static final String PRODUCT = "product";
    public static final String USER = "user";
    public static final String ROLE = "role";
    public static final String USER_ROLE = "user_role";
    public static final String USER_PRODUCT = "user_product";
    public static final String REPORT = "report";
    public static final String ORDER = "order";
    public static final String CONFIG = "config";
    public static final String[] ORDERED_TABLES_FOR_CREATION = {CONFIG, PRODUCT, USER, ROLE, USER_ROLE, USER_PRODUCT, REPORT, ORDER};
  }

}
