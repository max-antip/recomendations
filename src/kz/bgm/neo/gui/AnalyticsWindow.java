package kz.bgm.neo.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class AnalyticsWindow extends JFrame {

    public static final Font ARIAL = new Font("Arial", Font.PLAIN, 14);
    public static final int HEIGHT = 350;
    public static final int WIDTH = 500;
    public static final String SHOW = "show";
    JTextField searchField;
    JLabel searchLbl;
    JLabel loyalCustLbl;
    JLabel loyalCustField;
    JButton loyalCustBut;
    JLabel oneTimeCustLbl;
    JLabel oneTimeCustField;
    JButton oneTimeCustBut;
    JLabel newCustLbl;
    JLabel newCustField;
    JButton newCustBut;


    JPanel loyaltyPanel;
    JPanel searchPanel;

    public AnalyticsWindow() throws HeadlessException {
        super("Analytics");
        setSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new MigLayout(
                "",
                "[center]40[center]",
                "[center]"));


        searchPanel = new JPanel(new MigLayout("",
                "[center]5[center]",
                "[center]"));


        loyaltyPanel = new JPanel(new MigLayout("",
                "[right]5[center]5[right]",
                "[center]10[center]10[center]"));

        searchLbl = new JLabel("Search");
        searchLbl.setFont(ARIAL);
        searchField = new JTextField();
        searchPanel.add(searchLbl);
        searchPanel.add(searchField, "w 150");


        loyalCustLbl = new JLabel("Loyal customers");
        loyalCustLbl.setFont(ARIAL);
        loyalCustLbl.setForeground(Color.GREEN);
        loyalCustField = new JLabel();
        loyalCustBut = new JButton(SHOW);
        oneTimeCustLbl = new JLabel("One time customers");
        oneTimeCustLbl.setFont(ARIAL);

        oneTimeCustLbl.setForeground(Color.ORANGE);
        oneTimeCustField = new JLabel();
        oneTimeCustBut = new JButton(SHOW);
        newCustLbl = new JLabel("New customers");
        newCustLbl.setFont(ARIAL);
        newCustLbl.setForeground(Color.RED);
        newCustField = new JLabel();
        newCustBut = new JButton(SHOW);

        loyaltyPanel.add(loyalCustLbl);
        loyaltyPanel.add(loyalCustField);
        loyaltyPanel.add(loyalCustBut, "wrap");
        loyaltyPanel.add(oneTimeCustLbl);
        loyaltyPanel.add(oneTimeCustField);
        loyaltyPanel.add(oneTimeCustBut, "wrap");
        loyaltyPanel.add(newCustLbl);
        loyaltyPanel.add(newCustField);
        loyaltyPanel.add(newCustBut, "wrap");


        add(searchPanel);
        add(loyaltyPanel);
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }

        AnalyticsWindow anal = new AnalyticsWindow();
        anal.setVisible(true);
    }


}
