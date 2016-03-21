package kz.bgm.neo.domain;


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

    public String getName() {
        return name;
    }

    public String getCatCode() {
        return catCode;
    }
}
