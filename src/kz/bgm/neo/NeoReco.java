package kz.bgm.neo;

import kz.bgm.neo.domain.Person;
import kz.bgm.neo.gui.MainWindow;
import kz.bgm.neo.util.DataGen;
import kz.bgm.neo.util.DataXmlLoader;
import kz.bgm.neo.service.DbService;
import kz.bgm.neo.util.PurchaseGen;
import org.jdom2.JDOMException;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.Schema;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NeoReco {
    public static final String PROP_LOAD_XML_FILE = "load_xml_file";
    public static final String GENERATE = "generate";
    public static final String GEN = "gen";
    public static final String PEOPEL_COUNT = "peopel_count";
    public static final String DEFAULT_PEOPEL_CNT = "100";
    public static final String NO = "no";
    public static final String YES = "yes";
    public static final String Y = "y";
    public static final String WITH_GUI = "with_gui";
    private DbService dbService;





    //todo add Logger please
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            String propsFile = args[0];
            Properties props = new Properties();
            props.load(new FileInputStream(propsFile));
            DataXmlLoader xmlData = null;
            DbService dbService = new DbService(props);

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
            if (xmlData != null) {
                dataGen.generateAndInsertUsers(dbService.getDb(), xmlData, Integer.parseInt(peopleCnt));
                dataGen.insertProducts(dbService.getDb(), xmlData);
            } else {
                dataGen.generateAndInsertUsers(dbService.getDb(), Integer.parseInt(peopleCnt));
            }






            List<Node> products = dbService.getAllProducts();
            dbService.printAllRelationships(products.get(0));
/*
            List<Node> prods = scanner.getAllProducts();
            scanner.printProductNodes(prods);
*/

            String withGui = props.getProperty(WITH_GUI, NO);
            MainWindow mw = null;

            if (withGui.equalsIgnoreCase(YES) ||
                    withGui.equalsIgnoreCase(Y)) {
                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                mw = new MainWindow(dbService);
                mw.setVisible(true);
            }


            PurchaseGen purchaseGen = new PurchaseGen();

            purchaseGen.genNewPurchasesInDb(dbService.getDb(), mw, 50);


        } else {
            System.err.println("No porps found in arg [0]");
            System.exit(1);
        }


    }




}
