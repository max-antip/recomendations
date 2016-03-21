package kz.bgm.neo.util;

import kz.bgm.neo.domain.Person;
import kz.bgm.neo.domain.Product;
import kz.bgm.neo.domain.Purchase;
import kz.bgm.neo.domain.RelTypes;
import kz.bgm.neo.gui.MainWindow;
import org.neo4j.graphdb.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PurchaseGen {
    private static final Random random = new Random();
    public static final String PURCHASE_CNT = "purchaseCnt";

    private List<Person> personList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();


    public PurchaseGen() {
    }

    public PurchaseGen(List<Person> persons, List<Product> products) {
        personList = persons;
        productList = products;
    }

    public List<Purchase> genNewPurchasesInDb(GraphDatabaseService neoDB, MainWindow mainWindow, int cnt) {
        List<Purchase> prList = new ArrayList<>();
        try (Transaction tx = neoDB.beginTx()) {

            ResourceIterator<Node> persons = null;
            ResourceIterator<Node> products = null;
            persons = neoDB.findNodes(DynamicLabel.label(Person.NAME));
            products = neoDB.findNodes(DynamicLabel.label(Product.NAME));
            if (persons != null && products != null) {
                List<Node> productList = new ArrayList<>();
                List<Node> personList = new ArrayList<>();
                int productsSize;
                int personsSize;
                while (products.hasNext()) {
                    productList.add(products.next());
                }
                while (persons.hasNext()) {
                    personList.add(persons.next());
                }
                personsSize = personList.size();
                productsSize = productList.size();
                while (cnt >= 0) {
                    int persidx = random.nextInt(personsSize);
                    Node personNode = personList.get(persidx);
//                if (random.nextBoolean()) {//todo more complex want or not yo buy
                    Purchase purchase = new Purchase();
                    int idx = random.nextInt(productsSize);
                    Node product = productList.get(idx);
                    if (product.hasProperty(Product.PROP_NAME)) {
                        String prodName = (String) product.getProperty(Product.PROP_NAME);
                        String prodCat = "";
                        if (product.hasProperty(Product.PROP_CATEGORY_CODE)) {
                            prodCat = (String) product.getProperty(Product.PROP_CATEGORY_CODE);
                        }
                        Product prod = new Product(prodName, prodCat);
                        purchase.setProduct(prod);

                    }
                    Iterable<Relationship> relationships = personNode.getRelationships();
                    Relationship oldRelationship = null;
                    boolean alreadyHasRelatioship = false;
                    for (Relationship r : relationships) {
                        if (r.getType().name().equals(RelTypes.PURCHASE.name())) {
                            Node endNode = r.getEndNode();
                            if (endNode.hasProperty(Product.PROP_NAME)) {
                                String prodName = (String) endNode.getProperty(Product.PROP_NAME);
                                if (prodName.equals(product.getProperty(Product.PROP_NAME))) {
                                    alreadyHasRelatioship = true;
                                    oldRelationship = r;
                                    break;
                                }
                            }
                        }
                    }
                    if (alreadyHasRelatioship) {
                        if (oldRelationship.hasProperty(PURCHASE_CNT)) {
                            int cntPurchases = (int) oldRelationship.getProperty(PURCHASE_CNT);
                            oldRelationship.setProperty(PURCHASE_CNT, ++cntPurchases);
                        }
                    } else {
                        Relationship newRel = personNode.createRelationshipTo(product, RelTypes.PURCHASE);
                        newRel.setProperty(PURCHASE_CNT, 1);
                    }
                    purchase.setCustomer(Person.parse(personNode));
                    prList.add(purchase);
                    if (mainWindow != null) {
                        mainWindow.addOrUpdatePurchase(purchase.getCustomer(), purchase.getProduct().getName());
                    }
                    cnt--;
                }

            }
            tx.success();
        }
        return prList;
    }

    public List<Purchase> genNewPurchases(MainWindow mainWindow, int cnt) {

        List<Purchase> prList = new ArrayList<>();


        if (personList.size() > 0 && productList.size() > 0) {
            int productsSize;
            int personsSize;
            personsSize = personList.size();
            productsSize = productList.size();
            while (cnt >= 0) {
                int persidx = random.nextInt(personsSize);
                Person person = personList.get(persidx);
                Purchase purchase = new Purchase();
                int idx = random.nextInt(productsSize);
                //todo set person characteristic tom make decision to buy or not to buy
                Product product = productList.get(idx);
                purchase.setProduct(product);
                purchase.setCustomer(person);
                prList.add(purchase);
                if (mainWindow != null) {
                    mainWindow.addOrUpdatePurchase(purchase.getCustomer(), purchase.getProduct().getName());
                }
                cnt--;
            }
        }
        return prList;
    }

/*    public static void main(String[] args) {
        int k = 50;
        while (k >=0) {
            System.out.println(random.nextInt(5));
            k--;
        }

    }*/

}
