package fr.sbuisson.itgenerator;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ClassToMoveVisitor extends ModifierVisitor<Map<String, String>> {

    public Set<String> imports = new HashSet<>();
    public Set<String> classToImports = new HashSet<>();
    public String packageName;
    public String className;
    public String srcPath;

    @Override
    public Visitable visit(final ClassOrInterfaceDeclaration klass, final Map<String, String> arg) {
        Optional<AnnotationExpr> restController = klass.getAnnotationByName("RestController");
        if (restController.isPresent()) {
            className = klass.getNameAsString();
            imports.add(this.packageName + "." + this.className);
        }
        super.visit(klass, arg);

        return klass;
    }

    @Override
    public Visitable visit(final PackageDeclaration pack, final Map<String, String> arg) {
        super.visit(pack, arg);
        packageName = pack.getNameAsString();
        return pack;
    }

    @Override
    public Node visit(final ImportDeclaration importD, final Map<String, String> arg) {
        super.visit(importD, arg);
        imports.add(importD.getNameAsString());

        return importD;
    }


    public Visitable visit(final CompilationUnit n, final Map<String, String> arg) {
        n.getImports().forEach(i -> this.imports.add(i.getNameAsString()));
        return super.visit(n, arg);
    }


    public CompilationUnit getCompilationUnitOf(String className) {
        var classImport = imports.stream()
                .filter(str -> str.replaceAll(";", "").endsWith("." +className))
                .findFirst()
                .orElse(this.packageName + "." + className);

        String filePath = classImport.replace("import", "").replace(";", "").replaceAll(" ", "").replaceAll("\\.", "/");

        try {

            String fileContent = FileUtils.readFileToString(new File(srcPath + "\\" + filePath + ".java"));
            return StaticJavaParser.parse(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
