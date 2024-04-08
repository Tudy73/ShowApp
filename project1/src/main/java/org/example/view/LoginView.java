package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static javax.swing.BoxLayout.Y_AXIS;

public class LoginView extends JFrame {
  private JTextField tfUsername;
  private JTextField tfPassword;
  private JButton btnLogin;
  private JButton btnGuest;

  public LoginView() throws HeadlessException {
    setSize(300, 300);
    setLocationRelativeTo(null);
    setLocation(610,400);
    setTitle("LogIn Page");
    initializeFields();
    setLayout(new BoxLayout(getContentPane(), Y_AXIS));
    add(tfUsername);
    add(tfPassword);
    add(btnLogin);
    add(btnGuest);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  private void initializeFields() {
    tfUsername = new JTextField();
    tfPassword = new JTextField();
    btnLogin = new JButton("Login");
    btnGuest = new JButton("Continue as Guest");
  }

  public String getUsername() {
    return tfUsername.getText();
  }

  public String getPassword() {
    return tfPassword.getText();
  }

  public void setLoginButtonListener(ActionListener loginButtonListener) {
    btnLogin.addActionListener(loginButtonListener);
  }

  public void setGuestButtonListener(ActionListener guestButtonListener){
    btnGuest.addActionListener(guestButtonListener);
  }

  public void setVisible() {
    this.setVisible(true);
  }

}
