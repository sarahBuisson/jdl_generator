package fr.sbuisson.jdlgenerator;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithAccessModifiers;
import com.github.javaparser.ast.type.Type;
import fr.sbuisson.jdlgenerator.model.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.javaparser.utils.CodeGenerationUtils.packageToPath;
import static com.github.javaparser.utils.Utils.decapitalize;
import static com.github.javaparser.utils.Utils.capitalize;

public class Extractor {

    public static List<String> simpleTypes = Arrays.asList("String", "Integer", "BigDecimal", "Long", "Double", "Float");

    HeritageType heritageType;
    String rootPath;
    Map<String, EntityData> alreadyProcessed=new HashMap<>();


    public Extractor(HeritageType heritageType) {
        this.heritageType = heritageType;
    }

    public void extractDataFromFile(String path, JdlData jdlData) throws FileNotFoundException {
        File file = FileUtils.getFile(path);
        if (!file.exists()) {
            throw new FileNotFoundException(" file nor directory not found : " + file.getPath());
        } else if (file.isDirectory()) {
            for (File subfile : file.listFiles()) {
                extractDataFromFile(subfile.getPath(), jdlData);
            }
        } else {
            jdlData.add(extractDataFromFile(file));
        }

    }

    private JdlData extractDataFromFile(File file) {
        JdlData jdlData = new JdlData();
        try {
            String code = FileUtils.readFileToString(file, Charset.forName("UTF8"));
            CompilationUnit compilationUnit = StaticJavaParser.parse(code);
            rootPath = computeRootPath(file, compilationUnit);
            compilationUnit.getTypes().forEach(t -> jdlData.add(extractDataFromFile(t, compilationUnit)));


        } catch (IOException e) {
            e.printStackTrace();
        }
        return jdlData;
    }

    private String computeRootPath(File file, CompilationUnit compilationUnit) {
        return file.getParentFile().getPath().replace(packageToPath(compilationUnit.getPackageDeclaration().get().getNameAsString()), "");
    }

    private JdlData extractDataFromFile(TypeDeclaration<?> t, CompilationUnit compilationUnit) {
        JdlData jdlData = new JdlData();
        if(t.isEnumDeclaration()){
            EnumDeclaration enumDeclaration = t.asEnumDeclaration();

            EnumData data=new EnumData(enumDeclaration.getNameAsString());
            jdlData.addEnum(data);
            enumDeclaration.getEntries().forEach(e->data.addValues(e.getNameAsString()));
        }
        if (t instanceof ClassOrInterfaceDeclaration declaration) {
            if (t.asClassOrInterfaceDeclaration().isInterface()) {
                return jdlData;
            }
            EntityData entity = new EntityData(t.getNameAsString());
            this.alreadyProcessed.put(entity.getName(),entity);
            entity.setAbstractClass(((ClassOrInterfaceDeclaration) t).isAbstract());
            jdlData.addEntitie(entity);
            declaration.getExtendedTypes().forEach(extended -> {

                JdlData motherJdl = new JdlData();
                Optional<File> motherFile = getSourceFile(extended.getNameAsString(), compilationUnit);
                if (motherFile.isPresent()) {
                    try {
                        extractDataFromFile(motherFile.get().getPath(), motherJdl);
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
                    extractMemberData(jdlData, entity, fieldDeclaration, name, type, compilationUnit);

                } else if (memberDeclaration.isMethodDeclaration()) {
                    MethodDeclaration fieldDeclaration = memberDeclaration.asMethodDeclaration();
                    if (fieldDeclaration.getNameAsString().startsWith("get") || fieldDeclaration.getNameAsString().startsWith("is")) {
                        String name = decapitalize(fieldDeclaration.getNameAsString().substring(3));
                        Type type = fieldDeclaration.getType();
                        extractMemberData(jdlData, entity, fieldDeclaration, name, type, compilationUnit);
                    }

                }
            });

        }
        return jdlData;
    }

    private Optional<File> getSourceFile(String className, CompilationUnit compilationUnit) {
        return compilationUnit.getImports().stream().filter(imp -> imp.getNameAsString().equals(className))
                .findFirst()
                .map(directImport -> {
                            String path = packageToPath(directImport.getNameAsString());
                            return new File(path + ".java");
                        }
                ).or(() -> compilationUnit.getImports().stream().filter(imp -> imp.isAsterisk() && !imp.isStatic())
                        .map(imp -> FileUtils.getFile(rootPath, packageToPath(imp.getNameAsString()))).filter(f -> f.exists())
                        .flatMap(imp ->
                                Arrays.stream(imp.listFiles()))
                        .filter(f -> f.getName().equals(className)).findFirst()


                ).or(() -> compilationUnit.getPackageDeclaration().map(p -> packageToPath(p.getNameAsString()))
                        .flatMap(p -> Arrays.asList(FileUtils.getFile(rootPath, p).listFiles()).stream()
                                .filter(f -> f.getName().equals(className + ".java")).findFirst()));
    }


    private void extractMemberData(JdlData jdlData, EntityData entity, NodeWithAccessModifiers fieldDeclaration, String name, Type type, CompilationUnit compilationUnit) {
        if (!fieldDeclaration.isProtected() && !fieldDeclaration.isPrivate()) {
            if (type.isPrimitiveType() || simpleTypes.contains(type.asString())) {
                entity.getMembers().put(name, capitalize(type.asString()));
            } else {
                RelationshipType relationshipType;
                String fieldName;
                String leftName;
                Type rightType;

                if (type.asString().contains("<")) {
                    rightType= type.asClassOrInterfaceType().getTypeArguments().get().getFirst().get();
                    relationshipType= RelationshipType.OneToMany;
                }else{
                    rightType=type;
                    relationshipType= RelationshipType.OneToOne;
                }

                    String rightName = rightType.asString();
                    Optional<File> typeFile = getSourceFile(rightName, compilationUnit);
                    if (typeFile.isPresent()) {
                        var otherMemberData = new JdlData();
                        /*try {
                            extractDataFromFile(typeFile.get().getPath(), otherMemberData);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }*/
                        if (otherMemberData.getEntities().size() == 0 || (otherMemberData.getEntities().get(0).isAbstractClass() && heritageType == HeritageType.motherInsideDaugther)) {

                            if (type.asString().contains("<")) {
                                // entity.getMembers().put(name+"Ids", type.asString());
                            }else {
                              //  entity.getMembers().put(name+"Id", "String");
                            }
                        }else{
                            jdlData.addRelationship(new RelationshipData(relationshipType, entity.getName() + "{" + name + "}", type.asString()));
                        }
                    }else{
                        entity.getMembers().put(name, capitalize(type.asString()));
                       // jdlData.addRelationship(new RelationshipData(relationshipType, entity.getName() + "{" + name + "}", type.asString()));

                    }

            }
        }
    }


}



