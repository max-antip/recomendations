package kz.viaphone.research.reco;

import kz.viaphone.research.domain.Product;
import kz.viaphone.research.domain.Purchase;
import kz.viaphone.research.service.NeoDbService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecoProcessor {

    NeoDbService dbService;


    Map<String, List<Node>> productsByCatMap = new HashMap<>();

    public RecoProcessor(NeoDbService dbService) {
        this.dbService = dbService;
        try (Transaction tx = dbService.getDb().beginTx()) {
            List<Node> allProducts = dbService.getAllProducts();
            for (Node n : allProducts) {
                if (n.hasProperty(Product.PROP_CATEGORY_CODE) &&
                        n.hasProperty(Product.PROP_NAME)) {
                    String cat = (String) n.getProperty(Product.PROP_CATEGORY_CODE);
                    List<Node> prods = productsByCatMap.get(cat);
                    if (prods == null) {
                        prods = new ArrayList<>();
                        prods.add(n);
                        productsByCatMap.put(cat, prods);
                    } else {
                        prods.add(n);
                    }

                }
            }
            tx.success();
        }

    }

    public RecommendationResult simpleCategoryRecommendation(String personName) {
        RecommendationResult rr;
        List<Product> recoProducts = new ArrayList<>();
        try (Transaction tx = dbService.getDb().beginTx()) {
            List<Relationship> purchases = dbService.getRelationshipsByUserName(personName);
            CategoryScore cat = new CategoryScore(purchases);
            rr = new RecommendationResult();
            rr.setScoreByCat(cat.getPurchaseScore());
            tx.success();
            Map<String, List<Node>> personCats = new HashMap<>();
            for (Relationship r : purchases) {
                Node prodNode = r.getEndNode();


                if (prodNode.hasProperty(Product.PROP_CATEGORY_CODE)) {
                    String persCat = (String) prodNode.getProperty(Product.PROP_CATEGORY_CODE);
                    List<Node> nodeList = personCats.get(persCat);

                    if (nodeList == null) {
                        nodeList = new ArrayList<>();
                        nodeList.add(prodNode);
                        personCats.put(persCat, nodeList);
                    } else {
                        nodeList.add(prodNode);
                    }

                }

            }
            for (String category : personCats.keySet()) {
                List<Node> allProdsByCat = productsByCatMap.get(category);
                List<Node> onlyPersonsProds = personCats.get(category);

                for (Node n : allProdsByCat) {
                    if (!onlyPersonsProds.contains(n)) {
                        //todo do this wright
                        /*recoProducts.add(Product.parse(n));*/
                    }
                }
            }
            rr.setRecoProducts(recoProducts);
        }

        return rr;
    }

    private class CategoryScore {

        private Map<String, Double> purchaseScore = new HashMap<>();
        private int totalPurchases;

        public CategoryScore(List<Relationship> relationships) {
            Map<String, Integer> temp = new HashMap<>();
            for (Relationship r : relationships) {
                int purchaseCnt = (int) r.getProperty(Purchase.PROP_CNT);
                Node prodNode = r.getEndNode();
                if (prodNode.hasProperty(Product.PROP_CATEGORY_CODE)) {
                    String prodCat = (String) prodNode.getProperty(Product.PROP_CATEGORY_CODE);
                    if (temp.containsKey(prodCat)) {
                        int score = temp.get(prodCat);
                        score += purchaseCnt;
                        totalPurchases += purchaseCnt;
                        temp.replace(prodCat, score);
                    } else {
                        totalPurchases += purchaseCnt;
                        temp.put(prodCat, purchaseCnt);
                    }
                }
            }
            for (String key : temp.keySet()) {
                int sc = temp.get(key);
                purchaseScore.put(key, (double) sc / totalPurchases);
            }
        }

        public Map<String, Double> getPurchaseScore() {
            return purchaseScore;
        }
    }

    public class RecommendationResult {
        Map<String, Double> scoreByCat;
        List<Product> recoProducts;
        List<String> recoCategs;

        public void setScoreByCat(Map<String, Double> scoreByCat) {
            this.scoreByCat = scoreByCat;
        }

        public void setRecoProducts(List<Product> recoProducts) {
            this.recoProducts = recoProducts;
        }

        public void setRecoCategs(List<String> recoCategs) {
            this.recoCategs = recoCategs;
        }

        public Map<String, Double> getScoreByCat() {
            return scoreByCat;
        }

        public List<Product> getRecoProducts() {
            return recoProducts;
        }

        public List<String> getRecoCategs() {
            return recoCategs;
        }
    }

}
