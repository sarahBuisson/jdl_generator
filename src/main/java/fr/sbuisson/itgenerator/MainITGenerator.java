package fr.sbuisson.itgenerator;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import fr.sbuisson.itgenerator.model.Arguments;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class MainITGenerator {

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, InstantiationException, IOException {
        Arguments arguments = CommandLineParser.parse(Arguments.class, args, OptionStyle.SIMPLE);
        run(arguments);

    }

    public static void run(Arguments arguments) throws IOException {

        explore(new File(arguments.getInputDirectory()), arguments);
    }

    public static void explore(File file, Arguments arguments) throws IOException {
        var newFilePath = file.getPath().replace(arguments.getInputDirectory(), arguments.getOutputDirectory());


        if (file.isDirectory()) {
            System.out.println(file);
            new File(newFilePath).mkdir();
            for (File sousfile : file.listFiles())
                explore(sousfile, arguments);
        } else {

            var str = FileUtils.readFileToString(file);

            File newFile = new File(newFilePath);

            CompilationUnit compilationUnit = StaticJavaParser.parse(str);
            ClassToTestVisitor classDataForTest = new ClassToTestVisitor();
            classDataForTest.srcPath = arguments.getSrcDirectory();
            classDataForTest.visit(compilationUnit, new HashMap());
            if (!classDataForTest.tests.isEmpty()) {
                String className = file.getName().replace(".java", "");
                System.out.println("template");

                System.out.println("templateEnd ");
                System.out.println("mustache");
                MustacheFactory mf = new DefaultMustacheFactory();
                File templateFile = new File("./src/main/resources/IT.java.template");
                Mustache mustache = mf.compile(new StringReader(FileUtils.readFileToString(templateFile)), "IT.java.template");

                var scopes = new HashMap<>();
                scopes.put("packageName", classDataForTest.packageName);
                scopes.put("className", classDataForTest.className);
                scopes.put("classUrl", classDataForTest.classUrl);
                scopes.put("tests", classDataForTest.tests);
                scopes.put("imports", classDataForTest.imports);
                scopes.put("", classDataForTest.packageName);

                StringWriter writer = new StringWriter();
                mustache.execute(writer, scopes);

                System.out.println(writer);

                String newFileName = arguments.getOutputDirectory() + "/" + className + "IT.java";
                File newFileItTest = new File(newFileName);
                FileUtils.write(newFileItTest, writer.toString());
            }

        }
    }
}
