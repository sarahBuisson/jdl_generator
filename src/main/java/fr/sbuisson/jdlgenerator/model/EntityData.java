package fr.sbuisson.jdlgenerator.model;

import java.util.HashMap;
import java.util.Map;

public class EntityData {
    String name;
    String comment;
    boolean abstractClass;
    Map<String, String> members= new HashMap<>();

    public EntityData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getMembers() {
        return members;
    }

    public void setMembers(Map<String, String> members) {
        this.members = members;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public void addComment(String comment) {
        this.comment += comment;
    }

    public boolean isAbstractClass() {
        return abstractClass;
    }

    public void setAbstractClass(boolean abstractClass) {
        this.abstractClass = abstractClass;
    }
}
