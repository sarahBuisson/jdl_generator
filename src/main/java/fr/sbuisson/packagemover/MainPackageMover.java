package fr.sbuisson.packagemover;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import fr.sbuisson.packagemover.model.Arguments;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MainPackageMover {


    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, InstantiationException, IOException {
        Arguments arguments = CommandLineParser.parse(Arguments.class, args, OptionStyle.SIMPLE);
        run(arguments);

    }

    public static void run(Arguments arguments) throws IOException {
        var paramStr = Files.readString(Path.of(arguments.getFilename()));

        Map<String, String> param = new ObjectMapper().readValue(paramStr, HashMap.class);

        explore(new File(arguments.getDirectoryInput()), arguments, param);
    }

    private static void explore(File file, Arguments arguments, Map<String, String> params) throws IOException {

        if (file.isDirectory()) {
            System.out.println(file);
            for (File sousfile : file.listFiles())
                explore(sousfile, arguments, params);
        } else {
            var str = FileUtils.readFileToString(file);
            var newFilePath = file.getPath().replace(arguments.getDirectoryInput(), arguments.getDirectoryOutput());
            for (var change : params.entrySet()) {
                String oldPackage = change.getKey();
                str = str.replaceAll(oldPackage, change.getValue());
                String oldPackagePath1 = oldPackage.replace(".", "/");
                String oldPackagePath2 = oldPackage.replace(".", "\\");
                String newPackagePath1 = change.getValue().replace(".", "/");
                String newPackagePath2 = change.getValue().replace(".", "\\");
                newFilePath = newFilePath.replace(oldPackagePath1, newPackagePath1).replace(oldPackagePath2, newPackagePath2);
                System.out.println(newFilePath);
            }
            FileUtils.write(new File(newFilePath), str);

        }
    }
}
