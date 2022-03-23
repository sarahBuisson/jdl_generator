package fr.sbuisson.itgenerator;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import fr.sbuisson.itgenerator.model.MethodData;

import java.util.*;

public class ClassToTestVisitor extends ClassToBuildVisitor {

    public String classUrl;

    public List<MethodData> tests = new ArrayList<MethodData>();


    @Override
    public Visitable visit(final AnnotationDeclaration annotation, final Map<String, String> arg) {
        super.visit(annotation, arg);
        if (annotation.getName().equals("RestMapping")) {
            classUrl = annotation.getChildNodes().get(1).toString();

        }

        return annotation;
    }


    @Override
    public Visitable visit(final MethodDeclaration method, final Map<String, String> arg) {
        super.visit(method, arg);

        method.getAnnotationByName("Description");
        Optional<AnnotationExpr> getMapping = method.getAnnotationByName("GetMapping");
        Optional<AnnotationExpr> postMapping = method.getAnnotationByName("PostMapping");
        var mapping = getMapping.orElse(postMapping.orElse(null));
        if (mapping != null) {
            var data = new MethodData();
            tests.add(data);
            data.methodeName = method.getNameAsString();
            var requestBody = method.getParameters().stream().filter(p -> p.getAnnotationByName("RequestBody").isPresent()).findAny();

            if (requestBody.isPresent()) {
                data.requestBodyClassName = requestBody.get().getChildNodes().get(1).toString();
                String init = "var request = new " + data.requestBodyClassName + "();\n";

                ClassToBuildVisitor classToBuildVisitor = new ClassToBuildVisitor();
                classToBuildVisitor.currentInstanceName = "request";
                classToBuildVisitor.srcPath = srcPath;
                classToBuildVisitor.visit(this.getCompilationUnitOf(data.requestBodyClassName), new HashMap<>());
                init += classToBuildVisitor.buildScript;
                data.requestInit = "var request = build" + data.requestBodyClassName + "();\n";
                extraMethodes.put("build" + data.requestBodyClassName, "public " + data.requestBodyClassName + " build" + data.requestBodyClassName + "(){\n" + init + "return request;\n}\n");

            }
            data.testName = "should_" + method.getNameAsString();
            data.responseBodyClassName = method.getTypeAsString();
            data.url = mapping.getChildNodes().get(1).toString().replaceAll("\"", "");

            classToImports.add(method.getTypeAsString());
            if (getMapping.isPresent()) {
                data.actionHttp = "get";
            }
            if (postMapping.isPresent()) {
                data.actionHttp = "post";
            }

        }
        return method;
    }

    public String getClassUrl() {
        return classUrl;
    }

    public List<MethodData> getTests() {
        return tests;
    }

    public Set<String> getImports() {
        return imports;
    }

    public Set<String> getClassToImports() {
        return classToImports;
    }
}
