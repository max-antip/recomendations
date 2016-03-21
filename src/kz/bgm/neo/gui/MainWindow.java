package kz.bgm.neo.gui;


import kz.bgm.neo.domain.Person;
import kz.bgm.neo.domain.Purchase;
import kz.bgm.neo.service.DbService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {

//todo make orders line count in user label in value place.
    public static final int WIDTH = 850;
    public static final int HEIGHT = 600;
    public static final Font ARIAL = new Font("Arial", Font.PLAIN, 14);
    public static final MigLayout LAYOUT_VERT_LIST = new MigLayout("wrap 1");
    private List<KeyValLabel> userPurchase = new ArrayList<>();

    private JPanel userPanel;
    private JPanel basketPanel;
    private JScrollPane userScroll;
    private JScrollPane basketScroll;
    private KeyValLabel prodCategories = new KeyValLabel("Categories", "");
    private DbService dbScanner;


    public MainWindow(DbService dbScanner) {
        super("Neo4j-Reco");
        this.dbScanner = dbScanner;
        setSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocation(700, 300);
        setLayout(new MigLayout(
                "",
                "[]20[]",
                "[top]"));

        userPanel = new JPanel(LAYOUT_VERT_LIST);
        userScroll = new JScrollPane(userPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        userScroll.setLayout(new MigLayout("wrap 1"));

        basketPanel = new JPanel(LAYOUT_VERT_LIST);
        basketScroll = new JScrollPane(basketPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(userScroll, "w 450, h 500,");
        add(basketScroll, "w 450, h 500,");


    }


    public void addOrUpdatePurchase(Person person, String productName) {//todo add all persons with and without purchases
        boolean newUser = true;
        for (KeyValLabel kv : userPurchase) {
            if (kv.getKey().equals(person.getName())) {
                kv.setVal(productName);
                newUser = false;
                break;
            }
        }
        if (newUser) {
            KeyValLabel userPurch = new KeyValLabel(person.getName(), productName);

            userPurch.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    userPurch.setBackground(Color.LIGHT_GRAY);
                    showPurchasesByUserName(userPurch.getKey());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    userPurch.setBackground(Color.WHITE);
                }
            });

            userPurchase.add(userPurch);
            userPanel.add(userPurch);
        }
        userPanel.repaint();
        userPanel.revalidate();

    }

    private class KeyValLabel extends JLabel {
        private String key;
        private String val;
        private String delimiter = ":  ";

        public KeyValLabel(String key, String val) {
            super();
            setOpaque(true);
            setFont(ARIAL);
            fillKeyVal(key, val);
            updateText();

        }

        private void fillKeyVal(String key, String val) {
            this.val = val != null ? val : "";
            this.key = key != null ? key : "";
        }

        public KeyValLabel(String key, String val, String delimiter) {
            super();
            setOpaque(true);
            this.delimiter = delimiter;
            setFont(ARIAL);
            fillKeyVal(key, val);
            updateText();
        }


        private void updateText() {
            setText(key + delimiter + val);
        }

        public String getKey() {
            return key;
        }

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
            updateText();
        }

        public void addVal(String val) {
            this.val = val.concat(", ").concat(val);
        }


    }

    public void showPurchasesByUserName(String name) {
        if (dbScanner != null) {
            List<Purchase> purchaseList = dbScanner.getPurchasesByUserName(name);
            basketPanel.removeAll();

            for (Purchase p : purchaseList) {
                if (p.getProduct() != null) {
                    basketPanel.add(new KeyValLabel(p.getProduct().getName(), "(" + p.getProduct().getCatCode() + ")", " "));
                }
            }
            basketPanel.repaint();
            basketPanel.revalidate();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        MainWindow mw = new MainWindow(null);
        mw.setVisible(true);
        Person max = new Person("Max", "Antipov", Person.Gender.M, 31);
        Person bob = new Person("Bobby", "Dillon", Person.Gender.M, 51);
        Person mark = new Person("Mark", "Dacascos", Person.Gender.M, 51);
        Person sasha = new Person("Sasha", "Grey", Person.Gender.F, 28);
        mw.addOrUpdatePurchase(max, "Water");
        mw.addOrUpdatePurchase(max, "Cola");
        mw.addOrUpdatePurchase(bob, "Cocoine");
        mw.addOrUpdatePurchase(mark, "Limone");
        mw.addOrUpdatePurchase(mark, "LSD");
        mw.addOrUpdatePurchase(sasha, "oil");

    }

}
