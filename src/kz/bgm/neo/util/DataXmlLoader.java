package kz.bgm.neo.util;

import kz.bgm.neo.domain.Category;
import kz.bgm.neo.domain.Person;
import kz.bgm.neo.domain.Product;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DataXmlLoader {

    private static final String PEOPEL = "peopel";
    private static final String AT_NAMES = "names";
    private static final String AT_SURNAMES = "surnames";
    private static final String PRODUCTS = "products";

    private final String peopleFilePath;
    private final String productFilePath;
    private List<NameAndGender> nameList = new ArrayList<>();
    private List<String> surnameList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();

    public DataXmlLoader(Properties props) throws JDOMException, IOException {
        peopleFilePath = props.getProperty(PEOPEL);
        productFilePath = props.getProperty(PRODUCTS);

        fillProductList();
        fillPeopelList();

    }

    private void fillProductList() throws JDOMException, IOException {
        if (productFilePath != null) {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(productFilePath);
            Element allCatgories = document.getRootElement();
            for (Element catEl : allCatgories.getChildren()) {
                String catCode = catEl.getAttributeValue(Category.CODE);
                String catName = catEl.getAttributeValue(Category.TITLE);
                if (catCode != null && catName != null) {
                    categoryList.add(new CategoryImpl(catCode, catName));
                    for (Element prodEl : catEl.getChildren()) {
                        String prodName = prodEl.getValue();
                        if (prodName != null && !prodName.equals("")) {
                            Product prod = new Product(prodName, catCode);
                            productList.add(prod);
                        }
                    }

                }
            }

        } else {
            System.err.println("Products file path is null in opts");
        }
    }

    private void fillPeopelList() throws JDOMException, IOException {
        if (peopleFilePath != null) {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(peopleFilePath);
            Element rootNode = document.getRootElement();
            Element allNames = rootNode.getChild(AT_NAMES);
            for (Element el : allNames.getChildren()) {
                String gender = el.getAttributeValue(Person.PROP_GENDER);
                String name = el.getValue();
                if (gender != null && name != null) {
                    nameList.add(new NameAndGender(name, gender));
                }
            }
            Element allSurnames = rootNode.getChild(AT_SURNAMES);
            for (Element el : allSurnames.getChildren()) {
                String surname = el.getValue();
                if (surname != null) {
                    surnameList.add(surname);
                }
            }

        } else {
            System.err.println("Peopel file path is null in opts");
        }
    }

    public boolean hasProducts() {
        return productList.size() > 0;
    }

    public boolean hasCategories() {
        return categoryList.size() > 0;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public boolean hasNamesAnGenders() {
        return nameList.size() > 0;
    }

    public boolean hasSurname() {
        return surnameList.size() > 0;
    }

    public List<NameAndGender> getNamesAndGenders() {
        return nameList;
    }

    public List<String> getSurnameList() {
        return surnameList;
    }

    public class NameAndGender {

        private final String title;
        private final String gender;

        public NameAndGender(String title, String gender) {
            this.title = title;
            this.gender = gender;
        }

        public String getGender() {
            return gender;
        }

        public String getTitle() {
            return title;
        }
    }

    class CategoryImpl implements Category {
        private final String code;
        private final String name;

        public CategoryImpl(String code, String name) {
            this.code = code;
            this.name = name;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getTitle() {
            return name;
        }
    }

    ;
}
