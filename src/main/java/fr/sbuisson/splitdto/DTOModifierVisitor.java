package fr.sbuisson.splitdto;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class DTOModifierVisitor<A> extends ModifierVisitor<A> {


    @Override
    public Visitable visit(final FieldDeclaration fd, final A arg) {
        super.visit(fd, arg);
        ClassOrInterfaceType classOrInterfaceType = fd.getElementType().asClassOrInterfaceType();
        if (isDto(classOrInterfaceType)) {
            fd.getVariables().get(0).setName(fd.getVariables().get(0).getName() + "Id");
            fd.getVariables().get(0).setType("String");

        }
        if (classOrInterfaceType.getTypeArguments().isPresent()) {
            classOrInterfaceType.getTypeArguments().get().forEach(typedArgument -> {
                if (isDto(typedArgument)) {
                    fd.getVariables().get(0).setName(fd.getVariables().get(0).getName() + "Ids");
                    typedArgument.getElementType().asClassOrInterfaceType().getName().setIdentifier("String");
                }
            });
        }
        return fd;
    }

    @Override
    public Visitable visit(final MethodDeclaration method, final A arg) {
        super.visit(method, arg);
        if (method.getName().asString().startsWith("get")) {
            if (isDto(method)) {
                method.setType("String");
                method.setName(method.getName() + "Id");
                BlockStmt blockStmt = method.getBody().get();
                String newReturn = blockStmt.getStatements().get(0).asReturnStmt().getExpression().get().toString() + "Id";
                blockStmt.setStatements(NodeList.nodeList(new ReturnStmt(newReturn)));


            }

            ClassOrInterfaceType returnType = method.getType().asClassOrInterfaceType();
            if (returnType.getTypeArguments().isPresent()) {
                returnType.getTypeArguments().get().forEach(ty -> {
                    if (isDto(ty.asClassOrInterfaceType())) {
                        method.setName(method.getName() + "Ids");
                        ty.getElementType().asClassOrInterfaceType().getName().setIdentifier("String");
                    }
                });
            }
        }
        if (method.getName().asString().startsWith("set")) {

            Parameter parameter = method.getParameter(0);
            if (isDto(parameter.getType().asClassOrInterfaceType())) {
                method.setName(method.getName() + "Id");
                parameter.setType("String");
                FieldAccessExpr fieldAccessExpr = method.getBody().get().getStatements().get(0).asExpressionStmt().getExpression().asAssignExpr().getTarget().asFieldAccessExpr();
                fieldAccessExpr.setName(fieldAccessExpr.getName() + "Id");
                parameter.setName(parameter.getNameAsString() + "Id");


            }

            if (parameter.getType().getElementType().asClassOrInterfaceType().getTypeArguments().isPresent()) {
                parameter.getType().getElementType().asClassOrInterfaceType().getTypeArguments().get().forEach(ty -> {
                    if (isDto(ty.getElementType().asClassOrInterfaceType())) {
                        method.setName(method.getName() + "Ids");
                        ty.getElementType().asClassOrInterfaceType().getName().setIdentifier("String");
                        parameter.setName(parameter.getNameAsString() + "Ids");
                    }
                });
            }
        }


        return method;
    }

    private boolean isDto(Node typeAsString) {
        if (typeAsString instanceof Type) {
            return ((Type) typeAsString).asClassOrInterfaceType().getNameAsString().endsWith("DTO");
        }
        if (typeAsString instanceof NodeWithSimpleName) {
            return ((NodeWithSimpleName<?>) typeAsString).getNameAsString().endsWith("DTO");
        }
        if (typeAsString instanceof NodeWithType) {
            return ((NodeWithType<?, ?>) typeAsString).getTypeAsString().endsWith("DTO");
        }
        return false;
    }

    @Override
    public Visitable visit(final AssignExpr n, final A arg) {
        super.visit(n, arg);
        return n;
    }


}
