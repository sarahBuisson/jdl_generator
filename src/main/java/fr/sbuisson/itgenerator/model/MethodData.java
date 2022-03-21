package fr.sbuisson.itgenerator.model;

import java.util.*;

public class MethodData {
    public String className;
    public String methodeName;
    public String testName;
    public String actionHttp;
    public String url;
    public String requestBodyClassName;
    public String responseBodyClassName;
    public Map<String, Object> paramMap = new HashMap<>();
    public List<String> extraMethods = new ArrayList<>();
    public String requestInit;

    public Set<Map.Entry<String, Object>> getParams() {
        return paramMap.entrySet();
    }
}
