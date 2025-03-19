package com.github.blaxk3.converter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import java.awt.Color;
import java.awt.Component;

public class UI extends JFrame {

    public UI () {


        add(panel());
        setSize(500,500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    static Component panel() {
        JPanel panel = new JPanel();
        JPanel panel1 = new JPanel();
        panel1.setBackground(Color.black);
//        panel.add(textField());
//        panel.add(panel1);
//        panel.add(textField());
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    static Component textField() {
        return new JTextField();
    }
}
