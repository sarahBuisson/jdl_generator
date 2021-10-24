package fr.sbuisson.jdlgenerator.model;

import com.github.javaparser.ast.expr.SimpleName;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class EnumData {
    String name;
    List<String> values= new ArrayList<>();
    public EnumData(String name) {
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
    public void addValues(String ... values) {
        if(values!=null)
        this.values.addAll(asList(values));
    }
}
