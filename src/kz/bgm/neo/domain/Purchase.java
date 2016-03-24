package kz.bgm.neo.domain;


import scala.io.StdIn;

import java.time.LocalDate;

public class Purchase {
    public static final String PROP_CNT = "purchaseCnt";

    private Person customer;
    private String merchant;
    private Product product;
    private String price;
    private LocalDate date;
    private PayType payType;

    public enum PayType {
        CARD, CASH
    }

    public PayType getPayType() {
        return payType;
    }

    public Person getCustomer() {
        return customer;
    }

    public void setCustomer(Person customer) {
        this.customer = customer;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}


