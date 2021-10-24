package fr.sbuisson.jdlgenerator.model;

import java.util.ArrayList;
import java.util.List;

public class FileData {
    EntityData entityData;
    EnumData enumData;
    List<RelationshipData> relationships = new ArrayList<>();

    public EntityData getEntityData() {
        return entityData;
    }

    public void setEntityData(EntityData entityData) {
        this.entityData = entityData;
    }

    public EnumData getEnumData() {
        return enumData;
    }

    public void setEnumData(EnumData enumData) {
        this.enumData = enumData;
    }

    public List<RelationshipData> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<RelationshipData> relationships) {
        this.relationships = relationships;
    }
}
