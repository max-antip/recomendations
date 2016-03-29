package kz.viaphone.research.domain;


import org.neo4j.graphdb.RelationshipType;

public enum RelTypes implements RelationshipType {
    KNOWS,
    PURCHASE;

    int purch_cnt;





}
