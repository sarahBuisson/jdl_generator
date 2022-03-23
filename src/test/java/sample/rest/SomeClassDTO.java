package sample.rest;

import java.util.List;

public class SomeClassDTO {
    int id;
    String a;
    String b;
    OtherClassDTO otherClass;
    List<OtherClassDTO> otherClasses;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public OtherClassDTO getOtherClass() {
        return otherClass;
    }

    public void setOtherClass(OtherClassDTO otherClass) {
        this.otherClass = otherClass;
    }

    public List<OtherClassDTO> getOtherClasses() {
        return otherClasses;
    }

    public void setOtherClasses(List<OtherClassDTO> otherClasses) {
        this.otherClasses = otherClasses;
    }
}
