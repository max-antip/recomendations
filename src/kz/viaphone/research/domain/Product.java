package kz.viaphone.research.domain;


import org.neo4j.graphdb.Node;

public class Product {

    public static final String NAME = "product";
    public static final String PROP_NAME = "name";
    public static final String PROP_CATEGORY_CODE = "catCode";

    private String name;
    private String category;
    private int qty;
    private double price;


    public Product() {
    }

    public Product(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQty() {
        return qty;
    }

    public double getCashVol() {
        return qty * price;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }


}
