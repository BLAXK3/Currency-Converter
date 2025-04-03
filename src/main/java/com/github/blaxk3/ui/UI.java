package com.github.blaxk3.ui;

import com.github.blaxk3.api.CurrencyRateAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class UI extends javax.swing.JFrame {

    private static final Logger logger = LoggerFactory.getLogger(UI.class);
    private JLabel label;
    private JTextField textField;
    private JComboBox < String > comboBox1;
    private JComboBox < String > comboBox2;

    public JComboBox < String > getComboBox1() {
        return comboBox1;
    }

    public JComboBox < String > getComboBox2() {
        return comboBox2;
    }

    public JTextField getTextField() {
        return textField;
    }

    public void setTextField(String msg) {
        this.textField.setText(msg);
    }

    public void setLabel(String textField) {
        this.label.setText(textField);
    }

    public UI() {
        setIconImage(new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/icon/image/icon.png"))).getImage());
        add(panel());
        setTitle("Currency Converter");
        setSize(500, 500);
        setLayout(new java.awt.GridLayout(1, 2));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Component panel() {
        JPanel framePanel = new JPanel();
        framePanel.setLayout(new javax.swing.BoxLayout(framePanel, javax.swing.BoxLayout.Y_AXIS));

        JPanel panelFramePanel1 = new JPanel();
        panelFramePanel1.setBackground(Color.DARK_GRAY);
        panelFramePanel1.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelFramePanel1.add(textField());
        panelFramePanel1.add(comboBox1());

        JPanel panelFramePanel2 = new JPanel();
        panelFramePanel2.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelFramePanel2.add(label());
        panelFramePanel2.add(comboBox2());
        panelFramePanel2.setBackground(Color.DARK_GRAY);
        panelFramePanel2.add(button()[0]);
        panelFramePanel2.add(button()[1]);
        panelFramePanel2.add(button()[2]);

        framePanel.add(panelFramePanel1);
        framePanel.add(panelFramePanel2);

        return framePanel;
    }

    private Component comboBox1() {
        comboBox1 = new JComboBox < > ();
        comboBox1.setPreferredSize(new Dimension(300, 30));
        new CurrencyCode(comboBox1).execute();

        return comboBox1;
    }

    private Component comboBox2() {
        comboBox2 = new JComboBox < > ();
        comboBox2.setPreferredSize(new Dimension(300, 30));
        new CurrencyCode(comboBox2).execute();

        return comboBox2;
    }

    private Component[] button() {
        JButton[] button = new JButton[] {
                new JButton("Convert"),
                new JButton("Swap"),
                new JButton("Clear")
        };

        for (JButton buttons: button) {
            buttons.setPreferredSize(new Dimension(200, 35));
        }

        CurrencyRateAPI rate = new CurrencyRateAPI();
        button[0].addActionListener(convert -> {
        try {
            if (!getTextField().getText().isEmpty() && !getTextField().getText().equals(".")){
                setLabel(new DecimalFormat("#,###.###").format((Number) Double.parseDouble(rate.convert(Objects.requireNonNull(getComboBox1().getSelectedItem()).toString(), Objects.requireNonNull(getComboBox2().getSelectedItem()).toString(), BigDecimal.valueOf(Double.parseDouble(textField.getText()))))));
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "Please enter the amount you need", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } catch (MalformedURLException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
        });

        button[1].addActionListener(swap -> {
                String boxItem = Objects.requireNonNull(getComboBox1().getSelectedItem()).toString();
        getComboBox1().setSelectedItem(getComboBox2().getSelectedItem());
        getComboBox2().setSelectedItem(boxItem);
        });

        button[2].addActionListener(clear -> {
                setTextField("");
        setLabel("");
        });

        return button;
    }

    private Component textField() {
        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.BOLD, 24));
        textField.setPreferredSize(new Dimension(300, 100));
        ((javax.swing.text.PlainDocument) textField.getDocument()).setDocumentFilter(new NumericFilter());

        return textField;
    }

    private Component label() {
        label = new JLabel();
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setPreferredSize(new Dimension(300, 100));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        return label;
    }

    public static class NumericFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {

            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.insert(offset, string);

            if (isValid(sb.toString())) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.replace(offset, offset + length, text);

            if (isValid(sb.toString())) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isValid(String text) {

            if (text.isEmpty()) {
                return true;
            }

            byte decimalCount = 0;
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (ch == '.' && decimalCount != 1) {
                    decimalCount++;
                } else if (!Character.isDigit(ch)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class CurrencyCode extends SwingWorker < String[], Void > {
        private final JComboBox < String > comboBox;

        public CurrencyCode(JComboBox < String > comboBox) {
            this.comboBox = comboBox;
        }

        @Override
        protected String[] doInBackground() throws MalformedURLException,
                URISyntaxException {
            return new CurrencyRateAPI().getCurrencyCode();
        }

        @Override
        protected void done() {
            try {
                String[] currencyCodes = get();
                if (currencyCodes != null) {
                    Arrays.stream(currencyCodes)
                            .sorted()
                            .forEach(comboBox::addItem);
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error occurred while fetching currency codes", e);
            }
        }
    }
}