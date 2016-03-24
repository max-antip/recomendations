package kz.bgm.neo.reco;

import kz.bgm.neo.domain.Product;
import kz.bgm.neo.domain.Purchase;
import kz.bgm.neo.service.DbService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecoProcessor {

    int middleScore = 2;

    public RecoProcessor() {
    }


  /*  public List<Product> simpleCategoryRecomendation(String personName, DbService dbService) {
        Node userNode = dbService.getPerson(personName);
        Map<Integer, Node> purchaseHistory = new HashMap<>();
        Iterable<Relationship> relationships = userNode.getRelationships();

        for (Relationship r : relationships) {
            Node productNode = r.getEndNode();
            if (r.hasProperty(Purchase.PROP_CNT)) {
                purchaseHistory.put((Integer) r.getProperty(Purchase.PROP_CNT), r.getEndNode());
            }
        }

        for (Node n : purchaseHistory) {

        }

    }
*/

}
