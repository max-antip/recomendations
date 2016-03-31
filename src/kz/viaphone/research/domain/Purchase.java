package kz.viaphone.research.domain;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Purchase {
    public static final String PROP_CNT = "purchaseCnt";

    private long id;
    private Person customer;
    private Merchant merchant;
    private List<Product> products = new ArrayList<>();
    private String price;
    private double amount;
    private Date date;


    public Person getCustomer() {
        return customer;
    }

    public void setCustomer(Person customer) {
        this.customer = customer;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public List<Product> getProducts() {
        return products;
    }


    public void setProducts(List<Product> products) {
        this.products = products;
        for (Product p : products) {
            amount += p.getQty() * p.getPrice();
        }

    }

    public Product getProduct(String name) {
        for (Product p : products) {
            if (p.getName().contains(name)) return p;
        }
        return null;
    }

    public void addProduct(Product product) {
        products.add(product);
        amount += product.getQty() * product.getPrice();
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}


