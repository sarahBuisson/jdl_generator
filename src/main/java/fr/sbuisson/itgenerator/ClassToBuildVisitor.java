package fr.sbuisson.itgenerator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.Visitable;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static java.util.Arrays.asList;

public class ClassToBuildVisitor extends ClassToMoveVisitor {

    public String currentInstanceName;
    public String buildScript = "";
    public int minLenghForMethod = 1;
    public Map<String, String> extraMethodes = new HashMap<>();

    @Override
    public Visitable visit(final MethodDeclaration method, final Map<String, String> arg) {
        super.visit(method, arg);

        String methodeName = method.getNameAsString();
        if (methodeName.startsWith("set")) {
            String setterClass = method.getParameters().get(0).getTypeAsString();

            String fieldName = method.getParameters().get(0).getNameAsString();
            if (setterClass.equals("void")) {

            } else if (setterClass.equals("String")) {
                buildScript += currentInstanceName + "." + methodeName + "(\"TODO\"); // TODO\n";
            } else if (asList("int", "Integer", "Long", "long").contains(setterClass)) {
                buildScript += currentInstanceName + "." + methodeName + "(" + (int) (Math.random() * 10) + "); // TODO\n";
            } else if (asList("float", "Float", "Double", "doube").contains(setterClass)) {
                buildScript += currentInstanceName + "." + methodeName + "(" + ((int) (Math.random() * 1000)) / 100 + "); // TODO\n";
            } else if (setterClass.contains("List<") || setterClass.contains("Set<")) {
                var templateClass = setterClass.split("<", -1)[1].replace(">", "");

                if (setterClass.contains("List<")) {
                    buildScript += "var " + fieldName + " = new ArrayList<" + templateClass + ">();\n";
                    imports.add("java.util.ArrayList");
                }
                if (setterClass.contains("Set<")) {
                    buildScript += "var " + fieldName + " = new HashSet<" + templateClass + ">();\n";
                    imports.add("java.util.HashSet");
                }
                buildScript += currentInstanceName + "." + methodeName + "(" + fieldName + ");// init list\n";

                ClassToBuildVisitor classToBuildVisitor = new ClassToBuildVisitor();
                classToBuildVisitor.srcPath = srcPath;
                classToBuildVisitor.currentInstanceName = fieldName + 0;
                CompilationUnit compilationUnitOf = this.getCompilationUnitOf(templateClass);
                classToBuildVisitor.visit(compilationUnitOf, new HashMap<>());

                decideIfScriptAddedOtPutInMethode(methodeName, templateClass, fieldName + 0, classToBuildVisitor, fieldName);

                buildScript += fieldName + ".add(" + fieldName + 0 + ");\n";

                imports.addAll(classToBuildVisitor.imports);
            } else if (setterClass.contains("Map")) {
                imports.add("java.util.HashMap");
                buildScript += currentInstanceName + "." + methodeName + "(new HashMap<>());\n";
            } else {
                ClassToBuildVisitor classToBuildVisitor = new ClassToBuildVisitor();
                classToBuildVisitor.srcPath = srcPath;
                classToBuildVisitor.currentInstanceName = fieldName;
                CompilationUnit compilationUnitOf = this.getCompilationUnitOf(setterClass);
                if (compilationUnitOf != null) {
                    classToBuildVisitor.visit(compilationUnitOf, new HashMap<>());

                    imports.addAll(classToBuildVisitor.imports);
                    imports.add(classToBuildVisitor.packageName + "." + setterClass);
                    extraMethodes.putAll(classToBuildVisitor.extraMethodes);
                    decideIfScriptAddedOtPutInMethode(methodeName, setterClass, fieldName, classToBuildVisitor, currentInstanceName);

                } else {
                    System.out.println("not found : " + setterClass);
                }

            }
        }
        return method;
    }

    private void decideIfScriptAddedOtPutInMethode(String methodeName, String setterClass, String fieldName, ClassToBuildVisitor classToBuildVisitor, String instanceToSet) {
        if (minLenghForMethod > StringUtils.countMatches(classToBuildVisitor.buildScript, ".set")) {
            buildScript += "var " + instanceToSet + " = new " + setterClass + "();\n";
            //buildScript += instanceToSet + "." + methodeName + "(" + fieldName + ");\n";
            buildScript += classToBuildVisitor.buildScript;


        } else {

            buildScript += "var " + fieldName + " = build" + setterClass + "();\n";
            //buildScript += instanceToSet + "." + methodeName + "(" + fieldName + ");\n";
            this.extraMethodes.put("build" + setterClass, "public " + setterClass + " build" + setterClass + "(){ //B\n"
                    + "var " + fieldName + " = new " + setterClass + "();\n"
                    + classToBuildVisitor.buildScript + "return " + fieldName + ";\n}");

        }
    }


}
