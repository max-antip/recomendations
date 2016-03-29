package kz.viaphone.research.gui;


import kz.viaphone.research.domain.Person;
import kz.viaphone.research.domain.Product;
import kz.viaphone.research.reco.RecoProcessor;
import kz.viaphone.research.service.NeoDbService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class RecomendWindow extends JFrame {

    //todo make orders line count in user label in value place.
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 750;
    public static final Font ARIAL = new Font("Arial", Font.PLAIN, 14);
    public static final MigLayout LAYOUT_VERT_LIST = new MigLayout("", "[]", "[]10[]");

    private List<KeyValLabel> userList = new ArrayList<>();
    private ButtonGroup radioButGroup = new ButtonGroup();
    private JButton recButt = new JButton("Recommendation");
    private JPanel userPanel;
    private JPanel basketPanel;
    private JPanel recommendPanel;
    private JPanel scorePanel;
    private JScrollPane userScroll;
    private JScrollPane recommendScroll;
    private JScrollPane scoreScroll;
    private JScrollPane basketScroll;
    private JPanel buttonsPanel;
    private JPanel mainInfoPanel;

    private KeyValLabel prodCategories = new KeyValLabel("Categories", "");
    private NeoDbService dbService;

    private JLabel usersLab = new JLabel("Users");
    private JLabel basketLab = new JLabel("Basket");
    private JLabel recommendLab = new JLabel("Recomendation");
    private static final NumberFormat FORMATTER = new DecimalFormat("########0.00");

    private String userSelected = "";

    RecoProcessor recoProcessor;

    public RecomendWindow(NeoDbService dbService) {
        super("Neo4j-Reco");
        this.dbService = dbService;
        setSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocation(700, 300);
        setLayout(new MigLayout(
                "",
                "[]20[]20[]",
                "[top]20[top]"));

        mainInfoPanel = new JPanel(new MigLayout(
                "insets 0",
                "[grow]",
                "[grow]20[grow]"));

        userPanel = new JPanel(new MigLayout(
                "",
                "[]5[]",
                "[center]"));
        userScroll = new JScrollPane(userPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        userScroll.setLayout(new MigLayout("wrap 1"));

        basketPanel = new JPanel(LAYOUT_VERT_LIST);
        basketScroll = new JScrollPane(basketPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        recommendPanel = new JPanel(LAYOUT_VERT_LIST);
        recommendScroll = new JScrollPane(recommendPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scoreScroll = new JScrollPane(scorePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        buttonsPanel = new JPanel();
        buttonsPanel.add(recButt);
        recoProcessor = new RecoProcessor(dbService);
        recButt.addActionListener(e -> {
            RecoProcessor.RecommendationResult rc = recoProcessor.simpleCategoryRecommendation(userSelected);
            recommendPanel.removeAll();
            scorePanel.removeAll();
            for (Product p : rc.getRecoProducts()) {
                recommendPanel.add(new KeyValLabel(p.getName(), "(" + p.getCategory() + ")", " "), "wrap");
            }

            for (String catss : rc.getScoreByCat().keySet()) {
                double score = rc.getScoreByCat().get(catss);
                String scoreStr = FORMATTER.format(score * 100);
                scorePanel.add(new KeyValLabel(catss, scoreStr + "%"), "wrap");
            }
/*            recommendPanel.revalidate();
            recommendPanel.repaint();
            scorePanel.revalidate();
            scorePanel.repaint();*/
            mainInfoPanel.revalidate();
            mainInfoPanel.repaint();
        });


        mainInfoPanel.add(recommendScroll, "h 250, growx,wrap");
        mainInfoPanel.add(scoreScroll, "h 250, growx");
        add(usersLab);
        add(basketLab);
        add(recommendLab, "wrap");
        add(userScroll, "w 450, h 500");
        add(basketScroll, "w 450, h 500");
        add(mainInfoPanel, "w 450, h 500,span 2 ,wrap");
        add(buttonsPanel, "w 450");


    }


    public void addOrUpdatePurchase(Person person, String productName) {//todo add all persons with and without purchases
        boolean newUser = true;
        for (KeyValLabel kv : userList) {
            if (kv.getKey().equals(person.getName())) {
                kv.setVal("");
                newUser = false;
                break;
            }
        }
        if (newUser) {

            KeyValLabel userLabel = new KeyValLabel(person.getName(), productName);


            userList.add(userLabel);
            JRadioButton userButt = new JRadioButton();
            userButt.addActionListener(e -> {
                userSelected = person.getName();

            });


            userLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    userButt.doClick();
                    userLabel.setBackground(Color.LIGHT_GRAY);
                    /*showPurchasesByUserName(userLabel.getKey());*/
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    userLabel.setBackground(Color.WHITE);
                }
            });
            radioButGroup.add(userButt);
            userPanel.add(userButt);
            userPanel.add(userLabel, "wrap");
        }
        userPanel.repaint();
        userPanel.revalidate();

    }


/*    public void showPurchasesByUserName(String name) {
        if (dbService != null) {
            List<Purchase> purchaseList = dbService.getPurchasesByUserName(name);
            basketPanel.removeAll();

            for (Purchase p : purchaseList) {
                if (p.getProduct() != null) {
                    basketPanel.add(new KeyValLabel(p.getProduct().getName(), "(" + p.getProduct().getCatCode() + ")", " "), "wrap");
                }
            }
            basketPanel.repaint();
            basketPanel.revalidate();
        }

    }*/


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


    public static void main(String[] args) throws InterruptedException {
        RecomendWindow mw = new RecomendWindow(null);
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
