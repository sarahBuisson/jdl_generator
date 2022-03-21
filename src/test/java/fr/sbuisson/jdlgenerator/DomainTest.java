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
        String expectedJdl= "";//""\nentity Daugther {\n  motherStuff String \n  daugherStuff String \n  privateDaugherStuff String \n  privateMotherStuff String \n}\n\nentity Friend {\n  friendStuff Integer \n}\n\nrelationship OneToMany {\n Daugther{friends} to Friend\n}\n\nrelationship OneToOne {\n Daugther{boyfriend} to Friend\n}\n\nrelationship OneToMany {\n Friend{friends} to Friend\n}\n\nrelationship OneToMany {\n Mother{friends} to Friend\n}\n".formatted();
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
        String expectedJdl= "";/*""\n" +
                "entity Acces {\n" +
                "  type String \n" +
                "}\n" +
                "\n" +
                "entity Aidant {\n" +
                "  name String \n" +
                "  lien String \n" +
                "}\n" +
                "\n" +
                "entity Docteur {\n" +
                "  name String \n" +
                "  specialite String \n" +
                "}\n" +
                "\n" +
                "entity Dossier {\n" +
                "}\n" +
                "\n" +
                "entity Fichier {\n" +
                "  name String \n" +
                "  fileType String \n" +
                "}\n" +
                "\n" +
                "entity Repertoire {\n" +
                "  name String \n" +
                "  icon String \n" +
                "}\n" +
                "\n" +
                "relationship OneToOne {\n" +
                " Acces{contributeur} to Contributeur\n" +
                "}\n" +
                "\n" +
                "relationship OneToOne {\n" +
                " Acces{contenu} to Contenu\n" +
                "}\n" +
                "\n" +
                "relationship OneToMany {\n" +
                " Dossier{contributeurs} to Contributeur\n" +
                "}\n" +
                "\n" +
                "relationship OneToMany {\n" +
                " Dossier{contenus} to Contenu\n" +
                "}\n".formatted();*/

        assertEquals(actualJDL, expectedJdl);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
