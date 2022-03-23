package sample.rest;

public class OtherClassDTO {
    String id;
    AndOtherClassDTO and;

    public AndOtherClassDTO getAnd() {
        return and;
    }

    public void setAnd(AndOtherClassDTO and) {
        this.and = and;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
