package fr.sbuisson.itgenerator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import fr.sbuisson.jdlgenerator.Utils;

import java.util.Arrays;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.github.javaparser.utils.Utils.capitalize;

public class ReflexiveUtils {


    public static String setFromJson(String json) {
        try {
            var node = new ObjectMapper().readTree(json);
            return setFromJson(node, "request");


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        ;
        return "";
    }

    private static String setFromJson(JsonNode node, String nodeName) {
        Stream<Map.Entry<String, JsonNode>> stream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        node.fields(),
                        Spliterator.ORDERED)
                , false);

        if (node instanceof IntNode) {
            return node.asText();
        }
        if (node instanceof TextNode) {
            return "\"" + node.asText() + "\"";
        }
        if (node instanceof ArrayNode) {
            final StringBuilder builder = new StringBuilder();
            builder.append("var " + nodeName + "List = new ArrayList<>();\n");
            AtomicReference<Integer> index = new AtomicReference<>(0);
            node.elements().forEachRemaining((nodeArray) -> {


                if (nodeArray instanceof ObjectNode) {
                    builder.append("var " + nodeName + index.get() + " = new " + Utils.capitalize(nodeName) + "();\n");
                    builder.append(setFromJson(nodeArray, nodeName + index.get()) + ";\n");

                    builder.append(nodeName + "List.add(" + nodeName + index.get() + ");\n");
                    index.set(index.get() + 1);

                } else {
                    builder.append(nodeName + "List.add(" + nodeArray.asText() + ");\n");
                }

            });

            return builder.toString();

        }

        return stream.map((entry) -> {
            JsonNode childNode = entry.getValue();
            if (childNode instanceof ObjectNode) {
                String init = "var " + entry.getKey() + " = new " + Utils.capitalize(entry.getKey()) + "();\n";
                String set = nodeName + ".set" + Utils.capitalize(entry.getKey()) + "( " + (entry.getKey() + ");\n");

                return init + setFromJson(childNode, entry.getKey()) + set;
            } else if (childNode instanceof ArrayNode) {
                String initArray = setFromJson(entry.getValue(), entry.getKey());
                return initArray + nodeName + ".set" + Utils.capitalize(entry.getKey()) + "(" + entry.getKey() + "List );\n";

            } else if (childNode instanceof TextNode) {
                return nodeName + ".set" + Utils.capitalize(entry.getKey()) + "(\"" + childNode.asText() + "\");\n";
            } else {
                return nodeName + ".set" + Utils.capitalize(entry.getKey()) + "(" + childNode.asText() + ");\n";
            }
        }).collect(Collectors.joining("\n"));
    }


    public static String assertFromJson(String json) {
        try {
            var node = new ObjectMapper().readTree(json);
            return assertFromJson(node, "actual");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String assertFromJson(JsonNode node, String previousAccessor) {
        Stream<Map.Entry<String, JsonNode>> stream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        node.fields(),
                        Spliterator.ORDERED)
                , false);

        if (node instanceof IntNode) {
            return "assert.equals(" + previousAccessor + "," + node.asText() + ");\n";
        }
        if (node instanceof TextNode) {
            return "assert.equals(" + previousAccessor + ", \"" + node.asText() + "\");\n";
        }


        return stream.map((entry) -> {
            JsonNode childNode = entry.getValue();
            if (childNode instanceof ObjectNode) {
                String access = previousAccessor + ".get" + Utils.capitalize(entry.getKey()) + "()";

                return assertFromJson(childNode, access);
            } else if (childNode instanceof ArrayNode) {
                var init = setFromJson(childNode, "expected" + Utils.capitalize(entry.getKey()));
                return init + "assertThat(" + previousAccessor + ".get" + Utils.capitalize(entry.getKey()) + "()" + ", Matchers.containsInAnyOrder(" + "expected" + Utils.capitalize(entry.getKey()) + "List" + " ));";
            } else {
                return assertFromJson(childNode, previousAccessor + ".get" + Utils.capitalize(entry.getKey()) + "()");
            }
        }).collect(Collectors.joining("\n"));
    }


    public static String setFromClass(Class klass, String parentInstance, String name) {

        if (klass == String.class) {

            return parentInstance + ".set" + capitalize(name) + "(\"" + name + "\")";
        }
        if (klass == Integer.class) {

            return parentInstance + ".set" + capitalize(name) + "(" + Math.floor(Math.random() * 10) + ")";
        }
        var buffer = new StringBuffer();
        buffer.append("var "+name+" = new " + capitalize(name) + "();\n");
        Arrays.stream(klass.getMethods())
                .filter(m -> m.getName().startsWith("set")).forEach(
                        m -> {
                            setFromClass(m.getParameterTypes()[0], name, m.getName());
                        }
                );

        Arrays.stream(klass.getFields())
                .filter(m -> m.getName().startsWith("set")).forEach(
                        m -> {
                            setFromClass(m.getDeclaringClass(), name, m.getName());
                        }
                );
        return buffer.toString();
    }


}
