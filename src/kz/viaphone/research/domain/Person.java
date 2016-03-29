package kz.viaphone.research.domain;


import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import java.util.List;

public class Person {

    public enum Gender {
        F, M
    }

    public static final String NAME = "Person";
    public static final String PROP_NAME = "name";
    public static final String PROP_SURNAME = "surname";
    public static final String PROP_AGE = "age";
    public static final String PROP_GENDER = "gender";
    public static final String PROP_INTERESTS = "interests";

    private long id;
    private String name = "";
    private String surname = "";
    private Gender gender;
    private int age;
    private List<Interests> interestsList;
    private List<RelationshipType> relationshipTypeList;

    public Person() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        String hashStr = name + surname + Long.toString(id);
        return hashStr.hashCode();
    }

    public static Person parse(Node personNode) {
        String personName = "";
        String personSurname = "";
        Gender gender = null;
        int age = 0;
        if (personNode.hasProperty(Person.PROP_NAME)) {
            personName = (String) personNode.getProperty(Person.PROP_NAME);
        }
        if (personNode.hasProperty(Person.PROP_SURNAME)) {
            personSurname = (String) personNode.getProperty(Person.PROP_SURNAME);
        }
        if (personNode.hasProperty(Person.PROP_GENDER)) {
            gender = Gender.valueOf((String) personNode.getProperty(Person.PROP_GENDER));

        }
        if (personNode.hasProperty(Person.PROP_AGE)) {
            age = (int) personNode.getProperty(Person.PROP_AGE);
        }
        return new Person(personName, personSurname, gender, age);
    }

    public Person(String name, String surname,
                  Gender gender, int age,
                  List<Interests> interestsList,
                  List<RelationshipType> relationshipTypeList) {
        this.gender = gender;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.interestsList = interestsList;
        this.relationshipTypeList = relationshipTypeList;
    }

    public Person(String name, String surname,
                  Gender gender, int age) {
        if (gender == null)
            gender = null;
        this.gender = gender;
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    public void setFirstName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setInterestsList(List<Interests> interestsList) {
        this.interestsList = interestsList;
    }

    public void setRelationshipTypeList(List<RelationshipType> relationshipTypeList) {
        this.relationshipTypeList = relationshipTypeList;
    }

    public List<RelationshipType> getRelationshipTypeList() {
        return relationshipTypeList;
    }

    public Gender getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age;
    }

    public List<Interests> getInterestsList() {
        return interestsList;
    }
}
