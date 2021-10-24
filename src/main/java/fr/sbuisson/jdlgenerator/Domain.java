package fr.sbuisson.jdlgenerator;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithAccessModifiers;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import fr.sbuisson.jdlgenerator.model.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Domain {

    public static List<String> simpleTypes = Arrays.asList("String", "Integer", "BigDecimal", "Long", "Double", "Float");

    public void extractDataFromFile(String path, JdlData jdlData, HeritageType heritageType) throws FileNotFoundException {
        File file = FileUtils.getFile(path);
        if (!file.exists()) {
            throw new FileNotFoundException(" file nor directory not found : " + file.getPath());
        } else if (file.isDirectory()) {
            for (File subfile : file.listFiles()) {

                extractDataFromFile(subfile.getPath(), jdlData, heritageType);
            }

        } else {
            jdlData.add(extractDataFromFile(file, heritageType));
        }

    }

    private JdlData extractDataFromFile(File file, HeritageType heritageType) {
        JdlData jdlData = new JdlData();
        try {
            String code = FileUtils.readFileToString(file, Charset.forName("UTF8"));
            CompilationUnit compilationUnit = StaticJavaParser.parse(code);
            String rootPath = file.getParentFile().getPath().replace(packageToPath(compilationUnit.getPackageDeclaration().get().getNameAsString()), "");
            compilationUnit.getTypes().forEach(t -> jdlData.add(extractDataFromFile(t, compilationUnit, rootPath, heritageType)));


        } catch (IOException e) {
            e.printStackTrace();
        }
        return jdlData;
    }

    private JdlData extractDataFromFile(TypeDeclaration<?> t, CompilationUnit compilationUnit, String rootPath, HeritageType heritageType) {
        JdlData jdlData = new JdlData();
        if (t instanceof ClassOrInterfaceDeclaration declaration) {
            if (t.asClassOrInterfaceDeclaration().isInterface()) {
                return jdlData;
            }
            EntityData entity = new EntityData(t.getNameAsString());
            entity.setAbstractClass(((ClassOrInterfaceDeclaration) t).isAbstract());
            jdlData.addEntitie(entity);
            declaration.getExtendedTypes().forEach(extended -> {

                JdlData motherJdl = new JdlData();
                Optional<File> motherFile = getFile(extended, compilationUnit, rootPath);
                if (motherFile.isPresent()) {
                    try {
                        extractDataFromFile(motherFile.get().getPath(), motherJdl, heritageType);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (heritageType == HeritageType.motherInsideDaugther) {
                        EntityData motherEntity = motherJdl.getEntities().get(0);
                        entity.getMembers().putAll(motherEntity.getMembers());
                        entity.addComment(" extends " + extended.getNameAsString());
                        jdlData.getRelationships().addAll(motherJdl.getRelationships().stream().map(r -> new RelationshipData(r.getType(), r.getLeft().replace(motherEntity.getName(), entity.getName()), r.getRight())).collect(Collectors.toList()));

                    } else {
                        jdlData.add(motherJdl);
                    }
                }

            });
            declaration.getMembers().stream().forEach(memberDeclaration -> {

                if (memberDeclaration.isFieldDeclaration()) {
                    FieldDeclaration fieldDeclaration = memberDeclaration.asFieldDeclaration();
                    String name = fieldDeclaration.getVariables().getFirst().get().getNameAsString();
                    Type type = fieldDeclaration.getElementType();
                    extractMemberData(jdlData, entity, fieldDeclaration, name, type);

                } else if (memberDeclaration.isMethodDeclaration()) {
                    MethodDeclaration fieldDeclaration = memberDeclaration.asMethodDeclaration();
                    String name = decaptialize(fieldDeclaration.getNameAsString().substring(3));
                    Type type = fieldDeclaration.getType();
                    extractMemberData(jdlData, entity, fieldDeclaration, name, type);

                }
            });

        }
        return jdlData;
    }

    private Optional<File> getFile(ClassOrInterfaceType extended, CompilationUnit compilationUnit, String rootPath) {
        return compilationUnit.getImports().stream().filter(imp -> imp.getNameAsString().equals(extended.getNameAsString()))
                .findFirst()
                .map(directImport -> {
                            String path = packageToPath(directImport.getNameAsString());
                            return new File(path + ".java");
                        }
                ).or(() -> compilationUnit.getImports().stream().filter(imp -> imp.isAsterisk() && !imp.isStatic())
                        .map(imp->FileUtils.getFile(rootPath, packageToPath(imp.getNameAsString()))).filter(f->f.exists())
                        .flatMap(imp ->
                                Arrays.stream(imp.listFiles()))
                        .filter(f -> f.getName().equals(extended.getNameAsString())).findFirst()


                ).or(() -> compilationUnit.getPackageDeclaration().map(p -> packageToPath(p.getNameAsString()))
                        .flatMap(p -> Arrays.asList(FileUtils.getFile(rootPath, p).listFiles()).stream()
                                .filter(f -> f.getName().equals(extended.getNameAsString() + ".java")).findFirst()));
    }

    private String packageToPath(String directImport) {
        String path = directImport.replace(".", "\\");
        return path;
    }

    private void extractMemberData(JdlData jdlData, EntityData entity, NodeWithAccessModifiers fieldDeclaration, String name, Type type) {
        if (!fieldDeclaration.isProtected() && !fieldDeclaration.isPrivate()) {
            if (type.isPrimitiveType() || simpleTypes.contains(type.asString())) {
                entity.getMembers().put(name, type.asString());
            } else if (type.asString().contains("<")) {
                jdlData.addRelationship(new RelationshipData(RelationshipType.OneToMany, entity.getName() + "{" + name + "}", type.asClassOrInterfaceType().getTypeArguments().get().getFirst().get().asString()));
            } else {
                jdlData.addRelationship(new RelationshipData(RelationshipType.OneToOne, entity.getName() + "{" + name + "}", type.asString()));
            }

        }
    }

    public String decaptialize(String asString) {
        return "" + Character.toLowerCase(asString.charAt(0)) + asString.substring(1);


    }


    public String toJdl(JdlData jdlData, HeritageType heritageType) {
        System.out.println("generate jdl content");
        StringBuffer stringBuffer = new StringBuffer();

        for (EntityData entity : jdlData.getEntities()) {
            if (heritageType == HeritageType.motherLinkedToDaugher || !entity.isAbstractClass()) {

                stringBuffer.append("\nentity " + entity.getName() + " {\n");
                for (Map.Entry<String, String> entry : entity.getMembers().entrySet()) {
                    stringBuffer.append("  "+entry.getKey() + " " + entry.getValue()+" \n");
                }

                stringBuffer.append("}\n");

            }

        }
        for (RelationshipData relationship : jdlData.getRelationships()) {
            stringBuffer.append("\nrelationship " + relationship.getType().name() + " {\n");
            stringBuffer.append(" "+relationship.getLeft() + " to " + relationship.getRight()+"\n");
            stringBuffer.append("}\n");
        }

        return stringBuffer.toString();
    }
}



