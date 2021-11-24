package fr.sbuisson.replaceApi;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import fr.sbuisson.replaceApi.model.Arguments;
import fr.sbuisson.splitdto.DTOModifierVisitor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainApiReplacer {


    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, InstantiationException, IOException {
        Arguments arguments = CommandLineParser.parse(Arguments.class, args, OptionStyle.SIMPLE);
        run(arguments);

    }

    public static void run(Arguments arguments) throws IOException {

        explore(new File(arguments.getDirectoryInput()), arguments);
    }

    public static void explore(File file, Arguments arguments) throws IOException {
        var newFilePath = file.getPath().replace(arguments.getDirectoryInput(), arguments.getDirectoryOutput());

        if (file.isDirectory()) {
            System.out.println(file);
            new File(newFilePath).mkdir();
            for (File sousfile : file.listFiles())
                explore(sousfile, arguments);
        } else {

            var str = FileUtils.readFileToString(file);

            File newFile = new File(newFilePath);


            if (file.getName().contains("Resource") || file.getName().contains("Controller")) {
                String code = FileUtils.readFileToString(newFile, Charset.forName("UTF8"));
                File ResponseDTOFile = new File(newFile.getPath().replaceAll("DTO", "ResponseDTO"));
                FileUtils.write(ResponseDTOFile, code.replaceAll("DTO", "ResponseDTO"));
                File requestDTOFile = new File(newFile.getPath().replaceAll("DTO", "RequestDTO"));


                String codeRequest = FileUtils.readFileToString(requestDTOFile, Charset.forName("UTF8"));
                CompilationUnit compilationUnit = StaticJavaParser.parse(code);
                ArrayList<String> retour = new ArrayList<>();
                new DTOModifierVisitor<List<String>>().visit(compilationUnit, retour);
                FileUtils.write(requestDTOFile,compilationUnit.toString());

            } else {
                FileUtils.write(newFile, str);

                String code = FileUtils.readFileToString(newFile, Charset.forName("UTF8"));
                CompilationUnit compilationUnit = StaticJavaParser.parse(code);

                compilationUnit.getTypes().forEach(t -> t.asClassOrInterfaceDeclaration()
                        .getMembers().forEach(m -> {

                            if (m.isMethodDeclaration()) {
                                MethodDeclaration methodDeclaration = m.asMethodDeclaration();
                                if (methodDeclaration.getTypeAsString().endsWith("DTO")) {
                                    methodDeclaration.setType(methodDeclaration.getTypeAsString().replace("DTO", "ResponseDTO"));
                                }

                                methodDeclaration.getParameters().forEach(p -> {
                                    if (p.getTypeAsString().endsWith("DTO")) {
                                        p.setType("RequestDTO");

                                    }
                                });


                            }
                        }));

            }


        }
    }
}
