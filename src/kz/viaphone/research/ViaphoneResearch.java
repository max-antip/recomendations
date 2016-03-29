package kz.viaphone.research;

import kz.viaphone.research.anal.Analytics;
import kz.viaphone.research.domain.Purchase;
import kz.viaphone.research.gui.AnalyticsWindow;
import kz.viaphone.research.gui.RecomendWindow;
import kz.viaphone.research.service.DbService;
import kz.viaphone.research.util.DataGen;
import kz.viaphone.research.util.DataXmlLoader;
import kz.viaphone.research.service.NeoDbService;
import org.jdom2.JDOMException;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class ViaphoneResearch {
    public static final String PROP_LOAD_XML_FILE = "load_xml_file";
    public static final String GENERATE = "generate";
    public static final String GEN = "gen";
    public static final String PEOPEL_COUNT = "peopel_count";
    public static final String DEFAULT_PEOPEL_CNT = "100";
    public static final String NO = "no";
    public static final String YES = "yes";
    public static final String Y = "y";
    public static final String JDBC = "jdbc";
    public static final String WITH_GUI = "with_gui";
    public static final String NEO_DB = "neo_db";
    public static final String ANALYTICS = "analytics";
    public static final String RECOMMENDATION = "recommendation";
    public static final String TYPE = "type";

    private NeoDbService dbService;


    //todo add Logger please
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            String propsFile = args[0];
            Properties props = new Properties();
            props.load(new FileInputStream(propsFile));
            DataXmlLoader xmlData = null;
            DbService dbService = null;
            String type = props.getProperty(TYPE, ANALYTICS);
            String withGui = props.getProperty(WITH_GUI, NO);

            if (type.equalsIgnoreCase(RECOMMENDATION)) {
                NeoDbService neoDbService = null;
                if (props.getProperty(NEO_DB, "no").equalsIgnoreCase("yes")) {
                    neoDbService = new NeoDbService(props);
                }
                String loadXml = props.getProperty(PROP_LOAD_XML_FILE, NO);
                if (loadXml.equalsIgnoreCase(YES) || loadXml.equalsIgnoreCase(Y)) {
                    try {
                        xmlData = new DataXmlLoader(props);
                    } catch (JDOMException e) {
                        e.printStackTrace();
                    }
                }
                String peopleCnt = props.getProperty(PEOPEL_COUNT, DEFAULT_PEOPEL_CNT);
                DataGen dataGen = new DataGen();
                if (neoDbService != null) {
                    if (xmlData != null) {
                        dataGen.generateAndInsertUsers(neoDbService.getDb(), xmlData, Integer.parseInt(peopleCnt));
                        dataGen.insertProducts(neoDbService.getDb(), xmlData);
                    } else {
                        dataGen.generateAndInsertUsers(neoDbService.getDb(), Integer.parseInt(peopleCnt));
                    }
                }


                RecomendWindow mw = null;

                if (withGui.equalsIgnoreCase(YES) ||
                        withGui.equalsIgnoreCase(Y)) {
                    try {
                        UIManager.setLookAndFeel(
                                UIManager.getSystemLookAndFeelClassName());
                    } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    mw = new RecomendWindow(neoDbService);
                    mw.setVisible(true);
                }
            } else {

                if (props.getProperty(JDBC, "no").equalsIgnoreCase("yes")) {
                    dbService = new DbService(props);

                    List<Purchase> purchases = dbService.getPurchases(new Date(), new Date());
                    Analytics analytics = new Analytics(purchases);

                    System.out.println("Test");

                    if (withGui.equalsIgnoreCase(YES) ||
                            withGui.equalsIgnoreCase(Y)) {
                        AnalyticsWindow anal = new AnalyticsWindow();

                    }

                }


            }


        } else {
            System.err.println("No porps found in arg [0]");
            System.exit(1);
        }


    }


}
