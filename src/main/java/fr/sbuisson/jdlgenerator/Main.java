package fr.sbuisson.jdlgenerator;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import fr.sbuisson.jdlgenerator.model.Arguments;
import fr.sbuisson.jdlgenerator.model.HeritageType;
import fr.sbuisson.jdlgenerator.model.JdlData;
import  org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

public class Main {

    static Domain domain = new Domain();

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, InstantiationException, IOException {
        JdlData jdlData = new JdlData();
        Arguments arguments = CommandLineParser.parse(Arguments.class, args, OptionStyle.SIMPLE);

        for (String path : arguments.getDirectories()) {



            HeritageType heritageType = arguments.isHeritageMotherInsideDaughter() ? HeritageType.motherInsideDaugther : HeritageType.motherLinkedToDaugher;
            domain.extractDataFromFile(path, jdlData, heritageType);
            FileUtils.write(new File(arguments.getFilename()), domain.toJdl(jdlData, heritageType), Charset.forName("utf-8"));

        }


    }
}
