package kz.viaphone.research.anal;


import kz.viaphone.research.domain.Person;
import kz.viaphone.research.domain.Product;
import kz.viaphone.research.domain.Purchase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Analytics {

    private final List<Purchase> purchaseList;
    private final List<Person> customers = new ArrayList<>();
    final Map<Long, List<Purchase>> customerPurchases = new HashMap<>();


    public Analytics(List<Purchase> purchaseList) {
        this.purchaseList = purchaseList;
        purchaseList.stream().forEach(pu -> {
                    Person customer = pu.getCustomer();
                    customers.add(customer);
                    long customerId = customer.getId();
                    fillPurchasesByCustomer(pu, customerId);
                }

        );

        computeRFM("Apple", purchaseList);
    }

    private void fillPurchasesByCustomer(Purchase pu, long customerId) {
        if (!customerPurchases.containsKey(customerId)) {
            List<Purchase> purchases = new ArrayList<>();
            purchases.add(pu);
            customerPurchases.put(customerId, purchases);
        } else {
            List<Purchase> purchase = customerPurchases.get(customerId);
            purchase.add(pu);
        }
    }

    private int computeCustomerByRequency(List<Purchase> purchaseList, Long customerId, String productName) {
        int total = 0;
        for (Purchase p : purchaseList) {
            Product prod = p.getProduct(productName);
            if (prod != null && customerId == p.getCustomer().getId()) {
                total += prod.getQty();
            }
        }
        return total;
    }

    private Double computeCustomerByMonetary(List<Purchase> purchaseList, Long customerId, String productName) {
        double totalMoneySpend = 0;
        for (Purchase p : purchaseList) {
            Product prod = p.getProduct(productName);
            if (prod != null && customerId == p.getCustomer().getId()) {
                totalMoneySpend += prod.getCashVol();
            }
        }
        return totalMoneySpend;
    }



    public List<RFMResult> computeRFM(String productName, List<Purchase> totalPurchases) {
        List<RFMResult> results = new ArrayList<>();
        for (long customId : customerPurchases.keySet()) {
            List<Purchase> purchases = customerPurchases.get(customId);
            int r = computeCustomerByRequency(purchases, customId, productName);
            double m = computeCustomerByMonetary(purchases, customId, productName);

            RFMResult rfmResult = new RFMResult(getCustomer(customId), r, m);
            results.add(rfmResult);
        }

        return results;

    }

    private Person getCustomer(long id) {
        for (Person p : customers) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public class RFMResult {
        final Person person;
        final int r;
        final double m;

        public RFMResult(Person person, int r, double m) {
            this.person = person;
            this.r = r;
            this.m = m;
        }

        public Person getPerson() {
            return person;
        }

        public int getR() {
            return r;
        }

        public double getM() {
            return m;
        }
    }

/*    public static List<Person> oneTimeCustomers() {

    }*/

}
