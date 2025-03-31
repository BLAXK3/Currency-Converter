package com.github.blaxk3.ui;

import com.github.blaxk3.api.CurrencyRateAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class UI extends javax.swing.JFrame {

    private static final Logger logger = LoggerFactory.getLogger(UI.class);
    private JTextField[] textField;
    JComboBox<String>[] comboBoxes;


    public String[] getComboBox() {
        return Stream.of(comboBoxes)
                .map(box -> (String) box.getSelectedItem())
                .toArray(String[]::new);
    }


    public String getTextField() {
        return textField[0].getText();
    }


    public void setTextField(String textField) {
        this.textField[1].setText(textField);
    }

    public UI () {
        add(panel());
        setTitle("Currency Converter");
        setSize(500,500);
        setLayout(new java.awt.GridLayout(1, 2));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Component panel() {
        JPanel framePanel = new JPanel();
        framePanel.setBackground(Color.GRAY);
        framePanel.setLayout(new javax.swing.BoxLayout(framePanel, javax.swing.BoxLayout.Y_AXIS));
        framePanel.add(new javax.swing.JLabel("Currency Rate = "));

        JPanel panelFramePanel1 = new JPanel();
        panelFramePanel1.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelFramePanel1.add(textField()[0]);
        panelFramePanel1.add(comboBox()[0]);
        panelFramePanel1.setBackground(Color.DARK_GRAY);

        JPanel panelFramePanel2 = new JPanel();
        panelFramePanel2.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelFramePanel2.add(textField()[1]);
        panelFramePanel2.add(comboBox()[1]);
        panelFramePanel2.setBackground(Color.DARK_GRAY);
        panelFramePanel2.add(button()[0]);
        panelFramePanel2.add(button()[1]);

        framePanel.add(panelFramePanel1);
        framePanel.add(panelFramePanel2);

        return framePanel;
    }

    private Component[] comboBox() {
        comboBoxes = new JComboBox[]{
                new JComboBox<>(),
                new JComboBox<>()
        };

        for (JComboBox<String> comboBox : comboBoxes) {
            comboBox.setPreferredSize(new Dimension(300, 30));
            new CurrencyCode(comboBox).execute();
        }

        return comboBoxes;
    }

    private Component[] button() {
        JButton[] button = new JButton[] {
                new JButton("Convert"),
                new JButton("Swap")
        };

        for (JButton buttons : button) {
            buttons.setPreferredSize(new Dimension(200, 35));
            buttons.addActionListener(e -> {
                try {
                    String input = getTextField();
                    System.out.println(getComboBox()[0]);
                    System.out.println(getComboBox()[1]);

                    if (input.isEmpty()) {
                        setTextField("0");
                        input = "0";
                    }

                    setTextField(new CurrencyRateAPI().convert(getComboBox()[0], getComboBox()[1], 120));
                } catch (MalformedURLException | URISyntaxException ex) {
                    logger.error("Error occurred during currency conversion", ex);
                }
            });

        }

        return button;
    }

    private Component[] textField() {
        textField = new JTextField[] {
                new JTextField(),
                new JTextField()
        };

        textField[1].setEditable(false);
        for (JTextField textFields : textField) {
            textFields.setFont(new Font("Arial", Font.BOLD, 24));
            textFields.setPreferredSize(new Dimension(300, 100));
            ((javax.swing.text.PlainDocument) textFields.getDocument()).setDocumentFilter(new NumericFilter());
        }

        return textField;
    }

    private static class NumericFilter extends DocumentFilter {

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

    public static class CurrencyCode extends SwingWorker<String[], Void> {
        private final JComboBox<String> comboBox;

        public CurrencyCode(JComboBox<String> comboBox) {
            this.comboBox = comboBox;
        }

        @Override
        protected String[] doInBackground() throws MalformedURLException, URISyntaxException {
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
