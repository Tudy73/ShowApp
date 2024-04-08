package org.example.view;

import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HeadPanel extends JPanel {
    private Dimension defaultDimension = new Dimension(150,20);
    public HeadPanel(List<String> stringList) {
        this.setLayout(new FlowLayout(FlowLayout.LEFT)); // Set layout for one column

        for (String str : stringList) {
            JLabel label = new JLabel(str);
            label.setPreferredSize(defaultDimension);
            this.add(label);
        }
    }
    public HeadPanel(List<String> stringList,Dimension defaultDimension){
        this.defaultDimension = defaultDimension;
        this.setLayout(new FlowLayout(FlowLayout.LEFT)); // Set layout for one column

        for (String str : stringList) {
            JLabel label = new JLabel(str);
            label.setPreferredSize(defaultDimension);
            this.add(label);
        }
    }
}
