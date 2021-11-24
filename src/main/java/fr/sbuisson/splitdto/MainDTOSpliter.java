package fr.sbuisson.splitdto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import fr.sbuisson.splitdto.model.Arguments;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MainDTOSpliter {


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


            if (file.getName().contains("DTO")) {
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

                CompilationUnit compilationUnit = StaticJavaParser.parse(str);
                var imports = new ArrayList<ImportDeclaration>();
                imports.addAll(compilationUnit.getImports());

                        imports.forEach(importDeclaration -> {

                    String importName = importDeclaration.getNameAsString();
                    if (importName.endsWith("DTO")) {
                        importDeclaration.setName(importName.replace("DTO", "RequestDTO"));
                        compilationUnit.getImports().add(new ImportDeclaration(importName.replace("DTO", "ResponseDTO"), false, false));
                    }

                });

                ArrayList<String> retour = new ArrayList<>();
                new ControllerModifierVisitor<List<String>>().visit(compilationUnit, retour);
                FileUtils.write(newFile, compilationUnit.toString());

            }


        }
    }
}
