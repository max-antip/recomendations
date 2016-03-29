package kz.viaphone.research.anal;


import kz.viaphone.research.domain.Person;
import kz.viaphone.research.domain.Purchase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Analytics {

    private final List<Purchase> purchaseList;
    private List<Person> customersList;
    final Map<Long, Integer> totalPurchasesByCustomer = new HashMap<>();
    final Map<Long, Double> totalMoneyByCustomer = new HashMap<>();


    public Analytics(List<Purchase> purchaseList) {
        this.purchaseList = purchaseList;

        purchaseList.stream().forEach(pu -> {
            Person customer = pu.getCustomer();
            long customerId = customer.getId();
            if (!customersList.contains(customer)) {
                customersList.add(customer);
            }
            if (!totalPurchasesByCustomer.keySet().contains(customerId)) {

                totalMoneyByCustomer.put(customerId, pu.getAmount());
                totalPurchasesByCustomer.put(customerId, 1);
            } else {
                double totalMoneySpend = totalMoneyByCustomer.get(customerId);
                totalMoneyByCustomer.replace(customerId, totalMoneySpend + pu.getAmount());
                int totalCnt = totalPurchasesByCustomer.get(customerId);
                totalPurchasesByCustomer.replace(customerId, ++totalCnt);
            }
        });
        System.out.println(totalMoneyByCustomer);
        System.out.println(totalPurchasesByCustomer);

    }


    public List<Person> getLoyalCustomers(String product, List<Purchase> totalPurchases, int loyalThreshold) {
        List<Person> LoyalCustomersList = new ArrayList<>();
        for (Person p : customersList) {
            int dealCount = totalPurchasesByCustomer.get(p.getId());
            if (dealCount > 5) {

            }
        }
        return LoyalCustomersList;

    }

/*    public static List<Person> oneTimeCustomers() {

    }*/

}
