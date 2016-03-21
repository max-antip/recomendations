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
    public static final String DB_PATH = "db_path";
    public static final String DB_DEFAULT = "db/purchases";
    public static final String NO = "no";
    public static final String YES = "yes";
    public static final String Y = "y";
    public static final String GENERATE = "generate";
    public static final String GEN = "gen";
    public static final String PEOPEL_COUNT = "peopel_count";
    public static final String DEFAULT_PEOPEL_CNT = "100";
    public static final String GEN_NEW_DB = "gen_db";
    public static final String WITH_GUI = "with_gui";
    private static boolean dbInited;
    private GraphDatabaseService neoDB;

    public void stopNeoDb() {
        if (neoDB != null) {
            neoDB.shutdown();
        }
    }

    public void initNeoDB(File dbFile) {
        neoDB = new GraphDatabaseFactory().newEmbeddedDatabase(dbFile);
        dbInited = true;
    }

    public boolean isDbInit() {
        return dbInited;
    }

    private List<Node> findUsersNode(String property, String val) {
        Label label = DynamicLabel.label(Person.NAME);
        List<Node> nodes = new ArrayList<>();
        try (Transaction tx = neoDB.beginTx()) {
            ResourceIterator<Node> users = neoDB.findNodes(label, property, val);
            while (users.hasNext()) {
                nodes.add(users.next());
            }
            tx.success();
        }
        return nodes;
    }


    private void addIndexTo(String nodeName, String property) {
        try (Transaction tx = neoDB.beginTx()) {
            Schema schema = neoDB.schema();
            schema.indexFor(DynamicLabel.label(nodeName))
                    .on(property)
                    .create();
            tx.success();
        }
    }

    //todo add Logger please
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            String propsFile = args[0];
            Properties props = new Properties();
            props.load(new FileInputStream(propsFile));
            DataXmlLoader xmlData = null;
            String dbFilePath = props.getProperty(DB_PATH, DB_DEFAULT);
            File dbFile = new File(dbFilePath);

            String loadXml = props.getProperty(PROP_LOAD_XML_FILE, NO);
            if (loadXml.equalsIgnoreCase(YES) || loadXml.equalsIgnoreCase(Y)) {
                try {
                    xmlData = new DataXmlLoader(props);
                } catch (JDOMException e) {
                    e.printStackTrace();
                }
            }

            NeoReco main = new NeoReco();

            String regenStr = props.getProperty(GEN_NEW_DB, NO);
            if (regenStr.equalsIgnoreCase(YES) ||
                    regenStr.equalsIgnoreCase(Y)) {
                deleteFolder(dbFile);
                main.initNeoDB(dbFile);
                String peopleCnt = props.getProperty(PEOPEL_COUNT, DEFAULT_PEOPEL_CNT);
                DataGen dataGen = new DataGen();
                if (xmlData != null) {
                    dataGen.generateAndInsertUsers(main.neoDB, xmlData, Integer.parseInt(peopleCnt));
                    dataGen.insertProducts(main.neoDB, xmlData);
                } else {
                    dataGen.generateAndInsertUsers(main.neoDB, Integer.parseInt(peopleCnt));
                }
            }

            if (!main.isDbInit()) {
                main.initNeoDB(dbFile);
            }


            DbService scanner = new DbService(main.neoDB);
            List<Node> products = scanner.getAllProducts();
            scanner.printAllRelationships(products.get(0));
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
                mw = new MainWindow(scanner);
                mw.setVisible(true);
            }


            PurchaseGen purchaseGen = new PurchaseGen();

            purchaseGen.genNewPurchasesInDb(main.neoDB, mw, 50);


        } else {
            System.err.println("No porps found in arg [0]");
            System.exit(1);
        }


    }


    private List<Relationship> generatePurchases(int qty) {
        return null;
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }


}
