package kz.bgm.neo.service;

import kz.bgm.neo.domain.Person;
import kz.bgm.neo.domain.Product;
import kz.bgm.neo.domain.Purchase;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.schema.IndexDefinition;

import java.util.ArrayList;
import java.util.List;

public class DbService {

    private final GraphDatabaseService neoDB;

    public DbService(GraphDatabaseService neoDB) {
        this.neoDB = neoDB;
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
                    purchase.setProduct(new Product(productName, productCat));
                    purchaseList.add(purchase);
                }
            }
            tx.success();
        }
        return purchaseList;
    }


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
}
