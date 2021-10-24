package fr.sbuisson.jdlgenerator.model;

public class RelationshipData {
    RelationshipType type;
    String left;
    String right;

    public RelationshipData(RelationshipType type) {
        this.type = type;
    }

    public RelationshipData(RelationshipType type, String left, String right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    public RelationshipType getType() {
        return type;
    }

    public void setType(RelationshipType type) {
        this.type = type;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }
}
