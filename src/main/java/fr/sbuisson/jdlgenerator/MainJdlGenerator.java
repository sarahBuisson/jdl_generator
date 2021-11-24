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

public class MainJdlGenerator {

    static Extractor extractor;
    static Writter writter = new Writter();

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, InstantiationException, IOException {
        JdlData jdlData = new JdlData();
        Arguments arguments = CommandLineParser.parse(Arguments.class, args, OptionStyle.SIMPLE);
        HeritageType heritageType = arguments.isHeritageMotherInsideDaughter() ? HeritageType.motherInsideDaugther : HeritageType.motherLinkedToDaugher;

        for (String path : arguments.getDirectories()) {
            extractor = new Extractor(heritageType);
            extractor.extractDataFromFile(path, jdlData);
        }
        FileUtils.write(new File(arguments.getFilename()), writter.toJdl(jdlData, heritageType), Charset.forName("utf-8"));


    }
}
