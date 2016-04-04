package kz.viaphone.research.gui;


import kz.viaphone.research.anal.Analytics;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class UsersAnalyticsTable extends JFrame {

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 700;
    private JPanel mainPanel;
    private JScrollPane scroll;
    private static final NumberFormat formatter = new DecimalFormat("########0.00");


    private List<Analytics.RFMResult> results;

    public UsersAnalyticsTable(List<Analytics.RFMResult> results, String productName) throws HeadlessException {
        super(productName + " analytics table");
        this.results = results;
        setSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        mainPanel = new JPanel();
        scroll = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);


        mainPanel.setLayout(new MigLayout("", "[]5[]5[]5[]", "[]10[]"));
        JLabel person = new ViaLabel("Person");
        mainPanel.add(person);
        JLabel r = new ViaLabel("R");
        mainPanel.add(r);
        JLabel f = new ViaLabel("F");
        mainPanel.add(f);
        JLabel m = new ViaLabel("M");
        mainPanel.add(m, "wrap");
        addResults(results);
        add(scroll);
    }

    private void addResults(List<Analytics.RFMResult> results) {
        this.results = results;
        for (Analytics.RFMResult res : results) {
            if (!res.isEmpty()) {
                JLabel comp = new ViaLabel(res.getPerson().getName() + " " + res.getPerson().getSurname());
                mainPanel.add(comp);
                JLabel comp1 = new ViaLabel(Integer.toString(res.getR()));
                mainPanel.add(comp1);
                JLabel comp2 = new ViaLabel(Integer.toString(res.getF()));
                mainPanel.add(comp2);
                JLabel comp3 = new ViaLabel(formatter.format(res.getM()));
                mainPanel.add(comp3, "wrap");
            }
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

}