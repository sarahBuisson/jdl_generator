package fr.sbuisson.itgenerator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import fr.sbuisson.jdlgenerator.Utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.javaparser.utils.Utils.capitalize;

public class ReflexiveUtilsFromJsonBuilder {

    public String mainScript = "";
    public Map<String, String> otherMethodes = new HashMap<>();
    public int minLength = 5;

    public ReflexiveUtilsFromJsonBuilder(String json, String nodeName) {

        this(json, nodeName, 5);

    }

    public ReflexiveUtilsFromJsonBuilder(String json, String nodeName, int minLength) {
        this.minLength = minLength;
        try {
            var node = new ObjectMapper().readTree(json);
            mainScript =setBuilderFromJson(node, nodeName, capitalize(nodeName));


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private String setFromJson(JsonNode node, String nodeName) {


        if (node instanceof IntNode) {
            return node.asText();
        } else if (node instanceof TextNode) {
            return "\"" + node.asText() + "\"";
        } else if (node instanceof ArrayNode) {
            final StringBuilder builder = new StringBuilder();
            builder.append("var " + nodeName + " = new ArrayList<>();\n");
            AtomicReference<Integer> index = new AtomicReference<>(0);
            node.elements().forEachRemaining((nodeArray) -> {
                if (nodeArray instanceof ObjectNode) {
                    builder.append("var " + nodeName + index.get() + " = build" + Utils.capitalize(nodeName) + "();\n");

                    setBuilderFromJson(nodeArray, nodeName + index.get(), Utils.capitalize(nodeName));
                    builder.append(nodeName + ".add(" + nodeName + index.get() + ");\n");
                    index.set(index.get() + 1);

                } else {
                    builder.append(nodeName + ".add(" + nodeArray.asText() + ");\n");
                }

            });

            return builder.toString();

        } else if (node instanceof ObjectNode) {

            return setBuilderFromJson(node, nodeName, capitalize(nodeName));

        }
        return "";

    }

    private String setBuilderFromJson(JsonNode node, String nodeName, String className) {
        String methodName = "build" +className;
        if (!otherMethodes.containsKey(className)) {
            String script = "";

            for (Iterator<Map.Entry<String, JsonNode>> i = node.fields(); i.hasNext(); ) {
                var childNode = i.next();
                if (childNode.getValue() instanceof IntNode || node instanceof FloatNode || node instanceof LongNode || node instanceof DoubleNode || node instanceof BooleanNode) {
                    script += nodeName + ".set" + capitalize((childNode.getKey())) + "(" + childNode.getValue().asText() + ");\n";
                } else if (childNode.getValue() instanceof TextNode) {
                    script += nodeName + ".set" + capitalize((childNode.getKey())) + "(\"" + childNode.getValue().asText() + "\");\n";
                } else {
                    script += (setFromJson(childNode.getValue(), childNode.getKey()));
                    script += nodeName + ".set" + capitalize((childNode.getKey())) + "(" + childNode.getKey() + ");\n";
                }

            }
            if (node.size() > minLength) {
                return "var " + nodeName + " = new " + className + "();\n" + script;
            } else {
                script =  "var " + nodeName + " = new " + className + "();\n" + script;
                otherMethodes.put(methodName, script);
                return "var " + nodeName + " = " + methodName + "();\n";
            }
        }
        return "";
    }

}
