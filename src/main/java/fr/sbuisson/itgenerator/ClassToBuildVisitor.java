package fr.sbuisson.itgenerator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import fr.sbuisson.itgenerator.model.MethodData;

import java.util.*;

import static java.util.Arrays.asList;

public class ClassToBuildVisitor extends ClassToMoveVisitor {

    public String currentInstanceName;
    public String buildScript = "";


    @Override
    public Visitable visit(final MethodDeclaration method, final Map<String, String> arg) {
        super.visit(method, arg);

        if (method.getNameAsString().startsWith("set")) {
            String setterClass = method.getParameters().get(0).getTypeAsString();
            if (setterClass.equals("void")) {

            } else if (setterClass.equals("String")) {
                buildScript += currentInstanceName + "." + method.getNameAsString() + "(\"" + method.getNameAsString() + "\");\n";
            } else if (asList("int", "Integer", "Long", "long").contains(setterClass)) {
                buildScript += currentInstanceName + "." + method.getNameAsString() + "(" + (int) (Math.random() * 10) + ");\n";
            } else if (asList("float", "Float", "Double", "doube").contains(setterClass)) {
                buildScript += currentInstanceName + "." + method.getNameAsString() + "(" + ((int) (Math.random() * 1000)) / 100 + ");\n";
            } else if (setterClass.startsWith("List")) {
                buildScript += currentInstanceName + "." + method.getNameAsString() + "(new ArrayList<>());\n";
                imports.add("java.util.ArrayList");
                var templateClass = setterClass.split("<", -1)[1].replace(">", "");
                buildScript += "var " + templateClass.toLowerCase() + 0 + " = new " + templateClass + "();\n";
                ClassToBuildVisitor classToBuildVisitor = new ClassToBuildVisitor();
                classToBuildVisitor.srcPath = srcPath;
                classToBuildVisitor.currentInstanceName = templateClass.toLowerCase() + 0;
                CompilationUnit compilationUnitOf = this.getCompilationUnitOf(templateClass);
                classToBuildVisitor.visit(compilationUnitOf, new HashMap<>());
                buildScript += classToBuildVisitor.buildScript;
                buildScript += currentInstanceName + "." + method.getNameAsString() + "(" + templateClass.toLowerCase() + 0 + ");";

                imports.addAll(classToBuildVisitor.imports);
            } else if (setterClass.startsWith("Map")) {
                imports.add("java.util.HashMap");
                buildScript += currentInstanceName + "." + method.getNameAsString() + "(new HashMap<>());\n";
            } else if (setterClass.startsWith("Set")) {
                buildScript += currentInstanceName + "." + method.getNameAsString() + "(new HashSet<>());\n";
                imports.add("java.util.HashSet");

                var templateClass = setterClass.split("<", -1)[1].replace(">", "");
                buildScript += "var " + templateClass.toLowerCase() + 0 + " = new " + templateClass + "();\n";
                ClassToBuildVisitor classToBuildVisitor = new ClassToBuildVisitor();
                classToBuildVisitor.currentInstanceName = templateClass.toLowerCase() + 0;
                classToBuildVisitor.srcPath = srcPath;
                classToBuildVisitor.visit(this.getCompilationUnitOf(templateClass), new HashMap<>());
                buildScript += classToBuildVisitor.buildScript;
                buildScript += currentInstanceName + "." + method.getNameAsString() + "(" + templateClass.toLowerCase() + 0 + ");\n";
                imports.addAll(classToBuildVisitor.imports);
            } else {
                ClassToBuildVisitor classToBuildVisitor = new ClassToBuildVisitor();
                classToBuildVisitor.srcPath = srcPath;
                classToBuildVisitor.currentInstanceName = setterClass.toLowerCase();
                CompilationUnit compilationUnitOf = this.getCompilationUnitOf(setterClass);
                if (compilationUnitOf != null) {
                    classToBuildVisitor.visit(compilationUnitOf, new HashMap<>());
                    buildScript += "var " + setterClass.toLowerCase() + " = new " + setterClass + "();\n";
                    buildScript += currentInstanceName + "." + method.getNameAsString() + "(" + setterClass.toLowerCase() + ");\n";

                    buildScript += classToBuildVisitor.buildScript;
                    imports.addAll(classToBuildVisitor.imports);
                    imports.add(classToBuildVisitor.packageName + "." + setterClass);
                } else {
                    System.out.println("not found : " + setterClass);
                }

            }
        }
        return method;
    }


}
