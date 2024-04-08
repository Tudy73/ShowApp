package org.example.controller;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

@Getter
@Setter
public class ProductButton extends JButton {
    private long id;
    public ProductButton(String name, long id){
        super(name);
        this.id = id;
    }
}
