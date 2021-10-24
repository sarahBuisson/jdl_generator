package fr.sbuisson.jdlgenerator;

import fr.sbuisson.jdlgenerator.model.*;

import java.util.Map;

public class Writter {


    public String toJdl(JdlData jdlData, HeritageType heritageType) {
        System.out.println("generate jdl content");
        StringBuffer stringBuffer = new StringBuffer();

        for (EntityData entity : jdlData.getEntities()) {
            if (heritageType == HeritageType.motherLinkedToDaugher || !entity.isAbstractClass()) {

                stringBuffer.append("\nentity " + entity.getName() + " {\n");
                for (Map.Entry<String, String> entry : entity.getMembers().entrySet()) {
                    stringBuffer.append("  " + entry.getKey() + " " + entry.getValue() + " \n");
                }

                stringBuffer.append("}\n");

            }

        }

        for (EnumData en : jdlData.getEnums()) {
            stringBuffer.append("\nenums " + en.getName() + " {\n");
            for (String val : en.getValues()) {
                stringBuffer.append("  " + val + ", \n");
            }
            stringBuffer.append("}\n");
        }
        for (RelationshipData relationship : jdlData.getRelationships()) {
            stringBuffer.append("\nrelationship " + relationship.getType().name() + " {\n");
            stringBuffer.append(" " + relationship.getLeft() + " to " + relationship.getRight() + "\n");
            stringBuffer.append("}\n");
        }

        return stringBuffer.toString();
    }
}
