package fr.sbuisson.jdlgenerator.model;

import java.util.ArrayList;
import java.util.List;

public class JdlData {
    private List<EntityData> entities = new ArrayList<>();
    private List<RelationshipData> relationships = new ArrayList<>();
    private List<EnumData> enums = new ArrayList<>();

    public void addEntitie(EntityData entityData) {
        entities.add(entityData);
    }

    public void addRelationship(RelationshipData relationshipData) {
        relationships.add(relationshipData);
    }

    public List<EntityData> getEntities() {
        return entities;
    }

    public List<RelationshipData> getRelationships() {
        return relationships;
    }

    public void add(JdlData extractData) {
        this.entities.addAll(extractData.entities);
        this.relationships.addAll(extractData.relationships);
    }

    public void addEnum(EnumData data) {
        this.enums.add(data);
    }

    public List<EnumData> getEnums() {
        return enums;
    }
}
