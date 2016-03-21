package kz.bgm.neo.util;

import kz.bgm.neo.domain.Interests;
import kz.bgm.neo.domain.Person;
import kz.bgm.neo.domain.Product;
import org.neo4j.graphdb.*;

import java.util.*;

public class DataGen {

    public void generateAndInsertUsers(GraphDatabaseService neoDB, int qty) {
        try (Transaction tx = neoDB.beginTx()) {
            Label userLabel = DynamicLabel.label(Person.NAME);
            for (int id = 0; id < qty; id++) {

                Node userNode = neoDB.createNode(userLabel);
                userNode.setProperty(Person.PROP_NAME, "user_" + id);
                userNode.setProperty(Person.PROP_AGE, randAge());
                userNode.setProperty(Person.PROP_GENDER, randSex().name());
                userNode.setProperty(Person.PROP_INTERESTS, randInterest(2));
            }

            tx.success();
        }
    }

    public void generateAndInsertUsers(GraphDatabaseService neoDB, DataXmlLoader xmlData, int qty) {
        if (xmlData.hasNamesAnGenders() && xmlData.hasSurname()) {
            try (Transaction tx = neoDB.beginTx()) {
                Label userLabel = DynamicLabel.label(Person.NAME);
                List<DataXmlLoader.NameAndGender> namesAndGenders = xmlData.getNamesAndGenders();
                List<String> surnames = xmlData.getSurnameList();
                for (int id = 0; id < qty; id++) {
                    String name;
                    String surname;
                    Person.Gender gender;

                    int idxName = getInt(namesAndGenders.size());
                    int idxSurname = getInt(surnames.size());
                    DataXmlLoader.NameAndGender nameAndGender = namesAndGenders.get(idxName);
                    name = nameAndGender.getTitle();
                    gender = Person.Gender.valueOf(nameAndGender.getGender().toUpperCase());
                    surname = surnames.get(idxSurname);

                    String end = gender.equals(Person.Gender.F) ? "a" : "";
                    Node userNode = neoDB.createNode(userLabel);
                    userNode.setProperty(Person.PROP_NAME, name + " " + surname + end);
                    userNode.setProperty(Person.PROP_AGE, randAge());
                    userNode.setProperty(Person.PROP_GENDER, gender.name());
                    userNode.setProperty(Person.PROP_INTERESTS, randInterestName());
                }

                tx.success();
            }
        }
    }

    public List<Person> generateAndInsertUsers(DataXmlLoader xmlData, int qty) {
        List<Person> personList = new ArrayList<>();
        if (xmlData.hasNamesAnGenders() && xmlData.hasSurname()) {

            List<DataXmlLoader.NameAndGender> namesAndGenders = xmlData.getNamesAndGenders();
            List<String> surnames = xmlData.getSurnameList();
            for (int id = 0; id < qty; id++) {
                String name;
                String surname;
                Person.Gender gender;

                int idxName = getInt(namesAndGenders.size());
                int idxSurname = getInt(surnames.size());
                DataXmlLoader.NameAndGender nameAndGender = namesAndGenders.get(idxName);
                name = nameAndGender.getTitle();
                gender = Person.Gender.valueOf(nameAndGender.getGender().toUpperCase());
                surname = surnames.get(idxSurname);

                String end = gender.equals(Person.Gender.F) ? "a" : "";
                Person person = new Person(name, surname + end, gender, randAge(), randInterest(2), null);
                personList.add(person);
            }
        }
        return personList;
    }


    public void insertProducts(GraphDatabaseService neoDB, DataXmlLoader xmlData) {
        if (xmlData.hasProducts()) {
            try (Transaction tx = neoDB.beginTx()) {
                Label prodLabel = DynamicLabel.label(Product.NAME);
                for (Product p : xmlData.getProductList()) {
                    String title = p.getName();
                    String catCode = p.getCatCode();
                    if (!title.isEmpty() && !catCode.isEmpty()) {
                        Node prodNode = neoDB.createNode(prodLabel);
                        prodNode.setProperty(Product.PROP_CATEGORY_CODE, catCode);
                        prodNode.setProperty(Product.PROP_NAME, title);
                    }
                }
                tx.success();
            }
        }
    }


    private static Random rand = new Random();

    public static int randAge() {
        return rand.nextInt(49) + 16;
    }

    public static Person.Gender randSex() {
        return rand.nextBoolean() ? Person.Gender.F : Person.Gender.M;

    }

    public static String randInterestName() {
        int interestIndex = rand.nextInt(Interests.values().length);
        return Interests.valueOf(interestIndex).name();
    }

    public static List<Interests> randInterest(int cnt) {
        Set<Interests> interestsSet = new HashSet<>();
        List<Interests> interestsList = new ArrayList<>();
        while (cnt >= 0) {
            int interestIndex = rand.nextInt(Interests.values().length);
            interestsSet.add(Interests.valueOf(interestIndex));
            cnt--;
        }
        interestsList.addAll(interestsSet);
        return interestsList;
    }

    public static int getInt(int bounds) {
        return rand.nextInt(bounds);
    }

}
