package org.example.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.model.security.User;

import javax.swing.*;

@Getter
@Setter
public class UserButton extends JButton {
    private JTextField username, password,points,money;
    private JLabel id;

    public UserButton(String name){
        super(name);
    }
    @Builder
    public static UserButton createUserButton(String name, JTextField username, JTextField password, JTextField points, JTextField money, JLabel id) {
        UserButton button = new UserButton(name);
        button.username = username;
        button.password = password;
        button.points = points;
        button.money = money;
        button.id = id;
        return button;
    }

    public long getId(){
        return Long.parseLong(id.getText());
    }
    public int getPoints(){
        return Integer.parseInt(points.getText());
    }
    public double getMoney(){
        return Double.parseDouble(money.getText());
    }
    public String getPassword(){
        return password.getText();
    }
    public String getUsername(){
        return username.getText();
    }

}
