package kz.viaphone.research.service;


import kz.viaphone.research.domain.Merchant;
import kz.viaphone.research.domain.Person;
import kz.viaphone.research.domain.Product;
import kz.viaphone.research.domain.Purchase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class DbService {

    public static final String URL = "jdbc.url";
    public static final String USER = "jdbc.username";
    public static final String PASS = "jdbc.password";

    private String url;
    private String user;
    private String pass;

    public DbService(Properties prop) {
        url = prop.getProperty(URL);
        user = prop.getProperty(USER);
        pass = prop.getProperty(PASS);


    }

    public List<Purchase> getPurchases(Date from, Date to) {
        List<Purchase> purchaseList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            Statement st = conn.createStatement();
            st.execute("SELECT\n" +
                    "  p.id,\n" +
                    "  c.id customer_id,\n" +
                    "  c.NAME,\n" +
                    "  si.FIRST_NAME customer_firstname,\n" +
                    "  si.LAST_NAME  customer_lastname,\n" +
                    "  si.BIRTH_DATE customer_birthdate,\n" +
                    "  si.COUNTRY    customer_country,\n" +
                    "  si.GENDER     customer_gender,\n" +
                    "  m.NAME        merchant_name\n" +
                    "\n" +
                    "FROM PAYMENT p\n" +
                    "  LEFT JOIN ACCOUNT acc ON acc.ID = p.FROMACCOUNT\n" +
                    "  LEFT JOIN ACCOUNT acc_merch ON acc_merch.ID = p.TOACCOUNT\n" +
                    "  LEFT JOIN CUSTOMER c ON c.ACCOUNT_ID = acc.ID\n" +
                    "  LEFT JOIN MERCHANT m ON m.ACCOUNT_ID = acc_merch.ID\n" +
                    "  LEFT JOIN SOCIAL_INFO si ON si.CUSTOMER_ID = c.ID");
            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                Purchase purchase = new Purchase();
                purchase.setId(rs.getInt("id"));
                Person p = parsePerson(rs);
                purchase.setCustomer(p);
                Merchant m = parseMerchant(rs);
                purchase.setMerchant(m);

                fillProducts(conn, purchase);
                purchaseList.add(purchase);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return purchaseList;
    }

    private static final LocalDate now = LocalDate.now();

    private Person parsePerson(ResultSet rs) throws SQLException {
        Person p = new Person();
        p.setId(rs.getLong("customer_id"));
        p.setFirstName(rs.getString("customer_firstname"));
        p.setSurname(rs.getString("customer_lastname"));
        Date birthDate = rs.getDate("customer_birthdate");
        int age = now.getYear() - birthDate.getYear();
        p.setAge(age);
        Person.Gender gender = rs.getString("customer_gender").equals("MALE") ? Person.Gender.M : Person.Gender.F;
        p.setGender(gender);
        return p;
    }

    private void fillProducts(Connection conn, Purchase p) throws SQLException {


        PreparedStatement ps = conn.prepareStatement("SELECT *\n" +
                "FROM PRODUCT WHERE PAYMENTID=?");

        ps.setLong(1, p.getId());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Product product = new Product();
            product.setName(rs.getString("name"));
            product.setPrice(rs.getDouble("price"));
            product.setQty(rs.getInt("qty"));
            p.addProduct(product);
        }


    }

    private Merchant parseMerchant(ResultSet rs) throws SQLException {
        Merchant m = new Merchant();

        String merName = rs.getString("merchant_name");
        m.setName(merName);

        return m;
    }


}
