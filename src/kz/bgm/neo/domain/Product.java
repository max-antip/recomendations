package kz.bgm.neo.domain;


import org.neo4j.graphdb.Node;

public class Product {

    public static final String NAME = "product";
    public static final String PROP_NAME = "name";
    public static final String PROP_CATEGORY_CODE = "catCode";

    private final String name;
    private final String catCode;

    public Product(String name, String catCode) {
        this.name = name;
        this.catCode = catCode;
    }

    public static Product parse(Node prodNode) {
        if (prodNode.hasProperty(Product.PROP_NAME) && prodNode.hasProperty(Product.PROP_CATEGORY_CODE)) {
            return new Product((String) prodNode.getProperty(Product.PROP_NAME),
                    (String) prodNode.getProperty(Product.PROP_CATEGORY_CODE));
        }
        return null;
    }


    public String getName() {
        return name;
    }

    public String getCatCode() {
        return catCode;
    }
}
