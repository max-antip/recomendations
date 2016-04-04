package kz.viaphone.research.gui;

import kz.viaphone.research.anal.Analytics;
import kz.viaphone.research.domain.Person;
import kz.viaphone.research.domain.Purchase;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class AnalyticsWindow extends JFrame {

    public static final Font ARIAL = new Font("Arial", Font.PLAIN, 14);
    public static final int HEIGHT = 350;
    public static final int WIDTH = 500;
    public static final String SHOW = "show";
    private JTextField searchField;
    private JLabel searchLbl;
    private JButton searchBtn;

    private static final NumberFormat formatter = new DecimalFormat("########0.00");


    private JButton showNewBut;

    private JPanel mainReportPanel;
    private List<JPanel> reportPanels;
    private JPanel searchPanel;
    private Analytics analytics;
    private List<Analytics.RFMResult> RFMResults;


    private JLabel productLbl;
    private JLabel prodName;
    private JLabel mostLoyalCustomersLbl;
    private JLabel mostLoyalCustomersField;
    private JLabel rfmLbl;
    private JLabel femaleLbl;
    private JLabel femaleField;
    private JLabel maleLbl;
    private JLabel maleField;
    private List<Purchase> purchaseList;


    public AnalyticsWindow(List<Purchase> purchaseList) throws HeadlessException {
        super("Analytics");
        this.purchaseList = purchaseList;
        setSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new MigLayout(
                "",
                "[center]40[center]",
                "[center]"));


        searchPanel = new JPanel(new MigLayout("",
                "[center]5[center]",
                "[center]"));

        searchField = new JTextField();
        searchBtn = new JButton("Search");

        searchBtn.setFont(ARIAL);
        searchPanel.add(searchBtn);
        searchPanel.add(searchField, "w 150");

        mainReportPanel = new JPanel(new MigLayout("", "[]5[]5[]", "[]10[]"));

        add(searchPanel, "wrap");
        add(mainReportPanel);

        analytics = new Analytics(purchaseList);


        searchBtn.addActionListener(e -> {
            mainReportPanel.removeAll();
            RFMResults = analytics.computeRFM(searchField.getText());
            productLbl = new ViaLabel("Product:");
//            mostLoyalCustomersLbl = new ViaLabel("Most Loyal customers:");
            GenderAnalyse ga = new GenderAnalyse(RFMResults);
            femaleLbl = new ViaLabel("Female:");
            femaleField = new ViaLabel(formatter.format(ga.getF()) + " %");
            mainReportPanel.add(femaleLbl);
            mainReportPanel.add(femaleField, "wrap");

            maleLbl = new ViaLabel("Male:");
            maleField = new ViaLabel(formatter.format(ga.getM()) + " %");
            mainReportPanel.add(maleLbl);
            mainReportPanel.add(maleField, "wrap");
            mainReportPanel.add(productLbl);
            prodName = new ViaLabel(searchField.getText());
            mainReportPanel.add(prodName, "wrap");
            rfmLbl = new ViaLabel("RFM analyse");
            mainReportPanel.add(rfmLbl);
            JButton showResultBut = new JButton("Show");
            showResultBut.setFont(ARIAL);
            mainReportPanel.add(showResultBut, "wrap");

            UsersAnalyticsTable at = new UsersAnalyticsTable(RFMResults, searchField.getText());
            showResultBut.addActionListener(e1 -> at.setVisible(true));

            revalidate();
            repaint();
        });

    }


    public void setPurchases(List<Purchase> purchases) {
        purchaseList = purchases;

    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }

        AnalyticsWindow anal = new AnalyticsWindow(new ArrayList<>());
        anal.setVisible(true);
    }


    private class GenderAnalyse {
        private double m;
        private double f;

        public GenderAnalyse(List<Analytics.RFMResult> results) {
            int fCnt = 0;
            int mCnt = 0;
            int totalCnt = 0;
            for (Analytics.RFMResult res : results) {
                if (!res.isEmpty()) {
                    totalCnt++;
                    if (res.getPerson().getGender() == Person.Gender.M) {
                        mCnt++;
                    } else {
                        fCnt++;
                    }
                }
            }

            if (mCnt == 0) {
                m = 0;
            } else {
                m = (double)(mCnt * 100) / totalCnt;
            }
            if (fCnt == 0) {
                f = 0;
            } else {
                f = (double)(fCnt * 100) / totalCnt;
            }

        }

        public double getF() {
            return f;
        }

        public double getM() {
            return m;
        }
    }

}
