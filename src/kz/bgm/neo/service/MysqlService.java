package kz.bgm.neo.service;


import kz.bgm.neo.domain.Interests;
import kz.bgm.neo.domain.Purchase;

import java.util.List;
import java.util.Properties;

public class MysqlService {

    public static final String HOST = "db_host";
    public static final String NAME = "db_name";
    public static final String USER = "db_user";
    public static final String PASS = "db_pass";
    public static final String PORT = "db_port";

    private String url;
    private String user;
    private String pass;
    private int port;

    public MysqlService(Properties prop) {
        String host = prop.getProperty(HOST);
        url = "jdbc:mysql://" + host + "/" + prop.getProperty(NAME);
        user = prop.getProperty(USER);
        pass = prop.getProperty(PASS);
        port = Integer.parseInt(prop.getProperty(PORT, "0"));

    }

    public void addPurchses(List<Purchase> purchaseList) {

    }

}
