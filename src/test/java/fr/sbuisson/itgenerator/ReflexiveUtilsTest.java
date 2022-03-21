package fr.sbuisson.itgenerator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sample.rest.SomeClassDTO;

class ReflexiveUtilsTest {

    @Test
    void testSetFromJson() {
        String result = ReflexiveUtils.setFromJson("{\"a\":\"b\",\"c\":{\"d\":4}, \"e\":[4,5], \"f\":[{\"g\":\"h\"}]}");
        Assertions.assertEquals("replaceMeWithExpectedResult", result);
    }


    @Test
    void testSetFromJson2() {
        String result = ReflexiveUtils.setFromJson("{ \"f\":[{\"g\":\"h\"}]}");
        Assertions.assertEquals("replaceMeWithExpectedResult", result);
    }

    @Test
    void testSetFromJson3() {
        String result = ReflexiveUtils.setFromJson("{ \"f\":[1,2,3]}");
        Assertions.assertEquals("replaceMeWithExpectedResult", result);
    }



    @Test
    void testAssertFromJson() {
        String result = ReflexiveUtils.assertFromJson("{\"a\":\"b\",\"c\":{\"d\":4}, \"e\":[4,5], \"f\":[{\"g\":\"h\"}]}");
        Assertions.assertEquals("replaceMeWithExpectedResult", result);
    }

    @Test
    void testAssertFromJson2() {
        String result = ReflexiveUtils.assertFromJson("{ \"f\":[{\"g\":\"h\"}]}");
        Assertions.assertEquals("replaceMeWithExpectedResult", result);
    }


    @Test
    void testSetForClass() {
        String result = ReflexiveUtils.setFromClass(SomeClassDTO.class,"dto","dto");
        Assertions.assertEquals("replaceMeWithExpectedResult", result);
    }


}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
