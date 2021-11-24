package fr.sbuisson.splitdto;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class ControllerModifierVisitor<A> extends ModifierVisitor<A> {



    @Override
    public Visitable visit(final MethodDeclaration method, final A arg) {
        super.visit(method, arg);


        if (method.getTypeAsString().endsWith("DTO")) {
            method.setType(method.getTypeAsString().replace("DTO", "ResponseDTO"));
        }

        method.getParameters().forEach(parameter -> {

            if (parameter.getTypeAsString().endsWith("DTO")) {
                parameter.getType().getElementType().asClassOrInterfaceType().setName(parameter.getTypeAsString().replace("DTO", "RequestDTO"));
            }
        });


        return method;
    }

}
