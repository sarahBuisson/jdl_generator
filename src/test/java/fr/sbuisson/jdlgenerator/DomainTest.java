package fr.sbuisson.jdlgenerator;

import fr.sbuisson.jdlgenerator.model.HeritageType;
import fr.sbuisson.jdlgenerator.model.JdlData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DomainTest {
    Extractor domain;
    Writter writter = new Writter();

    @Test
    public void testExtractJDLFromSampleUML() throws Exception {
        //Given
        domain = new Extractor(HeritageType.motherInsideDaugther);

        JdlData jdlData = new JdlData();
        domain.extractDataFromFile((".\\src\\test\\java\\sample\\uml"), jdlData);
        assertEquals(jdlData.getEntities().size(), 3);
        assertEquals(jdlData.getRelationships().size(), 4);
        String expectedJdl= "\nentity Daugther {\n  motherStuff String \n  daugherStuff String \n  privateDaugherStuff String \n  privateMotherStuff String \n}\n\nentity Friend {\n  friendStuff Integer \n}\n\nrelationship OneToMany {\n Daugther{friends} to List<Friend>\n}\n\nrelationship OneToOne {\n Daugther{boyfriend} to Friend\n}\n\nrelationship OneToMany {\n Friend{friends} to List<Friend>\n}\n\nrelationship OneToMany {\n Mother{friends} to List<Friend>\n}\n".formatted();
        assertEquals(writter.toJdl(jdlData, HeritageType.motherInsideDaugther), expectedJdl);
    }

    @Test
    public void testExtractJDLFromSampleDossierConfidentiel() throws Exception {
        //Given
        domain = new Extractor(HeritageType.motherInsideDaugther);

        JdlData jdlData = new JdlData();
        domain.extractDataFromFile((".\\src\\test\\java\\sample\\dossierConfidentiels"), jdlData);
        String actualJDL = writter.toJdl(jdlData, HeritageType.motherInsideDaugther);
        System.out.println(actualJDL);
        assertEquals(jdlData.getEntities().size(), 8);
        assertEquals(jdlData.getRelationships().size(), 4);
        String expectedJdl= "\nentity Daugther {\n  motherStuff String \n  daugherStuff String \n  privateDaugherStuff String \n  privateMotherStuff String \n}\n\nentity Friend {\n  friendStuff Integer \n}\n\nrelationship OneToMany {\n Daugther{friends} to List<Friend>\n}\n\nrelationship OneToOne {\n Daugther{boyfriend} to Friend\n}\n\nrelationship OneToMany {\n Friend{friends} to List<Friend>\n}\n\nrelationship OneToMany {\n Mother{friends} to List<Friend>\n}\n".formatted();

        assertEquals(actualJDL, expectedJdl);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
