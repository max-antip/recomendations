package kz.viaphone.research.service;

import kz.viaphone.research.domain.Person;
import kz.viaphone.research.domain.Product;
import kz.viaphone.research.domain.Purchase;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NeoDbService {


    public static final String GEN_NEW_DB = "gen_db";
    public static final String NO = "no";
    public static final String YES = "yes";
    public static final String Y = "y";
    public static final String DB_PATH = "neo_db_path";
    public static final String DB_DEFAULT = "db/purchases";

    private GraphDatabaseService neoDB;
    private boolean dbInited;

    public NeoDbService(Properties props) {
        String dbFilePath = props.getProperty(DB_PATH, DB_DEFAULT);
        File dbFile = new File(dbFilePath);
        String regenStr = props.getProperty(GEN_NEW_DB, NO);
        if (regenStr.equalsIgnoreCase(YES) ||
                regenStr.equalsIgnoreCase(Y)) {
            deleteFolder(dbFile);
            initNeoDB(dbFile);
        }

        if (!isDbInit()) {
            initNeoDB(dbFile);
        }

    }

    public List<Node> getProductByCategory(String catCode) {
        List<Node> nodes = new ArrayList<>();
        try (Transaction tx = neoDB.beginTx()) {
            ResourceIterator<Node> prodNodes = neoDB.findNodes(DynamicLabel.label(Product.NAME), Product.PROP_CATEGORY_CODE, catCode);
            while (prodNodes.hasNext()) {
                nodes.add(prodNodes.next());
            }

            tx.success();
        }
        return nodes;
    }

    public GraphDatabaseService getDb() {
        return neoDB;
    }

    public void initNeoDB(File dbFile) {
        neoDB = new GraphDatabaseFactory().newEmbeddedDatabase(dbFile);
        dbInited = true;
    }

    public void stopNeoDb() {
        if (neoDB != null) {
            neoDB.shutdown();
        }
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

    private void printDbIndex(String index) {
        Label label = DynamicLabel.label(index);
        try (Transaction tx = neoDB.beginTx()) {
            Iterable<IndexDefinition> indexes = neoDB.schema().getIndexes(label);
            if (!index.isEmpty()) {
                System.out.println("Found index for " + index);
            }
            for (IndexDefinition idef : indexes) {
                Iterable<String> props = idef.getPropertyKeys();
                for (String p : props) {
                    System.out.println("--" + p + "\t");
                }
            }
            tx.success();
        }
    }

    private List<Node> findNode(Label label, String property, String val) {
        List<Node> nodes = new ArrayList<>();

        ResourceIterator<Node> users = neoDB.findNodes(label, property, val);
        while (users.hasNext()) {
            nodes.add(users.next());
        }
        return nodes;
    }

    public List<Node> getAllProducts() {
        List<Node> nodes = new ArrayList<>();
        try (Transaction tx = neoDB.beginTx()) {
            ResourceIterator<Node> prods = neoDB.findNodes(DynamicLabel.label(Product.NAME));
            while (prods.hasNext()) {
                nodes.add(prods.next());
            }
            tx.success();
        }
        return nodes;
    }



    public void printAllRelationships(Node node) {
        try (Transaction tx = neoDB.beginTx()) {
            for (Relationship sp : node.getRelationships()) {
                System.out.println(sp);
            }
            tx.success();
        }
    }

    public Node getPerson(String name) {
        Node user;
        try (Transaction tx = neoDB.beginTx()) {
            user = neoDB.findNode(DynamicLabel.label(Person.NAME), Person.PROP_NAME, name);
            tx.success();
        }
        return user;
    }

    public void printAllNodes() {
        try (Transaction tx = neoDB.beginTx()) {
            Iterable<Node> nodes = neoDB.getAllNodes();
            for (Node n : nodes) {
                System.out.println("Node with id: " + n.getId());
                System.out.println(n.getAllProperties());
            }
            tx.success();
        }
    }

    public List<Relationship> getRelationshipsByUserName(String userName) {
        List<Relationship> relationshipList = new ArrayList<>();
        Node personNode = neoDB.findNode(DynamicLabel.label(Person.NAME), Person.PROP_NAME, userName);
        Iterable<Relationship> relationships = personNode.getRelationships();
        for (Relationship r : relationships) {
            relationshipList.add(r);
        }
        return relationshipList;
    }

    public List<Purchase> getPurchasesByUserName(String userName) {
        List<Purchase> purchaseList = new ArrayList<>();
        try (Transaction tx = neoDB.beginTx()) {
            Node personNode = neoDB.findNode(DynamicLabel.label(Person.NAME), Person.PROP_NAME, userName);
            if (personNode == null) {
                System.err.println("No person found with name " + userName);
                return purchaseList;
            }
            Iterable<Relationship> relationships = personNode.getRelationships();

            for (Relationship r : relationships) {
                Node n = r.getEndNode();
                if (n.hasProperty(Product.PROP_NAME)) {
                    Purchase purchase = new Purchase();

                    String productName = (String) n.getProperty(Product.PROP_NAME);
                    String productCat = "N/A";
                    if (n.hasProperty(Product.PROP_CATEGORY_CODE)) {
                        productCat = (String) n.getProperty(Product.PROP_CATEGORY_CODE);
                    }
                    purchase.setCustomer(Person.parse(personNode));
                    purchase.addProduct(new Product(productName, productCat));
                    purchaseList.add(purchase);
                }
            }
            tx.success();
        }
        return purchaseList;
    }
/*

    public List<Product> getProductsByCat(String prodCat) {
        List<Product> products = new ArrayList<>();
        try (Transaction tx = neoDB.beginTx()) {
            ResourceIterator<Node> prodNodes = neoDB.findNodes(DynamicLabel.label(Product.NAME), Product.PROP_CATEGORY_CODE, prodCat);

            while (prodNodes.hasNext()) {
                Node prodNode = prodNodes.next();
                if (prodNode.hasProperty(Product.PROP_NAME) && prodNode.hasProperty(Product.PROP_CATEGORY_CODE)) {
                    Product prod = Product.parse(prodNode);
                    if (prod != null) {
                        products.add(prod);
                    }
                }
            }
            tx.success();
        }
        return products;
    }
*/


    public void printProductNodes(List<Node> nodes) {
        try (Transaction tx = neoDB.beginTx()) {
            for (Node n : nodes) {
                System.out.println("--------------------------------");
                System.out.println("Product");
                System.out.println("ID: " + n.getId());
                System.out.println(Product.PROP_NAME + ": " + n.getProperty(Product.PROP_NAME));
                System.out.println(Product.PROP_CATEGORY_CODE + ": " + n.getProperty(Product.PROP_CATEGORY_CODE));
                System.out.println("--------------------------------");
            }
            tx.success();
        }
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
