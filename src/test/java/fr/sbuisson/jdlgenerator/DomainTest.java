package fr.sbuisson.jdlgenerator;

import fr.sbuisson.jdlgenerator.model.HeritageType;
import fr.sbuisson.jdlgenerator.model.JdlData;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DomainTest {
    Domain domain = new Domain();

    @Test
    public void testExtractData() throws Exception {
        JdlData jdlData = new JdlData();
        domain.extractDataFromFile((".\\src\\test\\resources\\sample"), jdlData, HeritageType.motherInsideDaugther);
        assertEquals(jdlData.getEntities().size(), 3);
        assertEquals(jdlData.getRelationships().size(), 4);
        assertEquals(domain.toJdl(jdlData, HeritageType.motherInsideDaugther), "");
    }

    @Test
    public void shouldDecapitalize() {


        assertEquals(domain.decaptialize("test"), "test");
        assertEquals(domain.decaptialize("TeSt"), "teSt");
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
