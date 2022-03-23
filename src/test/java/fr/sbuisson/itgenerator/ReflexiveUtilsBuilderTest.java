package fr.sbuisson.itgenerator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReflexiveUtilsBuilderTest {

    @Test
    public void testSimpleJson() {

        var actual = new ReflexiveUtilsFromJsonBuilder("{\"a\":\"b\",\"c\":\"d\"}", "request");
        Assertions.assertEquals(1, actual.otherMethodes.size());
        Assertions.assertEquals("var request = buildRequest();\n", actual.mainScript);
        Assertions.assertEquals("var request = new Request();\n" +
                "request.setA(\"b\");\nrequest.setC(\"d\");\n", actual.otherMethodes.get("buildRequest"));


    }

   @Test
    public void testComplexeJson() {

        var actual = new ReflexiveUtilsFromJsonBuilder("{\"a\":\"b\",\"c\":{\"d\":4}, \"e\":[4,5], \"f\":[{\"g\":\"h\"}]}", "request");
        Assertions.assertEquals(3, actual.otherMethodes.size());
        Assertions.assertEquals("var request = buildRequest();\n", actual.mainScript);
        Assertions.assertEquals("var request = new Request();\n" +
                "request.setA(\"b\");\n" +
                "var c = buildC();\n" +
                "request.setC(c);\n" +
                "var e = new ArrayList<>();\n" +
                "e.add(4);\n" +
                "e.add(5);\n" +
                "request.setE(e);\n" +
                "var f = new ArrayList<>();\n" +
                "var f0 = buildF();\n" +
                "f.add(f0);\n" +
                "request.setF(f);\n", actual.otherMethodes.get("buildRequest"));


    }

    @Test
    public void testSimple2Json() {

        var actual = new ReflexiveUtilsFromJsonBuilder("{\"a\":{\"b\":2}}", "request", 1);
        Assertions.assertEquals(2, actual.otherMethodes.size());

        Assertions.assertEquals("var request = buildRequest();\n", actual.mainScript);

        Assertions.assertEquals("var request = new Request();\n" +
                "var a = buildA();\n" +
                "request.setA(a);\n", actual.otherMethodes.get("buildRequest"));

        Assertions.assertEquals("var a = new A();\n" +
                "a.setB(2);\n", actual.otherMethodes.get("buildA"));

    }


    @Test
    public void testSimpleArrayJson() {

        var actual = new ReflexiveUtilsFromJsonBuilder("{\"a\":[{\"b\":2},{\"b\":3}]}", "request", 1);


        Assertions.assertEquals("var request = buildRequest();\n", actual.mainScript);

        Assertions.assertEquals("var request = new Request();\n" +
                "var a = new ArrayList<>();\n" +
                "var a0 = buildA();\n" +
                "a.add(a0);\n" +
                "var a1 = buildA();\n" +
                "a.add(a1);\n" +
                "request.setA(a);\n",
                actual.otherMethodes.get("buildRequest"));

        Assertions.assertEquals("var a1 = new A();\n" +
                "a1.setB(3);\n", actual.otherMethodes.get("buildA"));
        Assertions.assertEquals(2, actual.otherMethodes.size());

    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
