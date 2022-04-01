package fr.sbuisson.findUsedBy;

import com.github.jknack.handlebars.internal.antlr.misc.Predicate;
import com.opencsv.CSVReader;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaAccess;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.util.MultiMap;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static java.util.Arrays.asList;

public class IsItUsedTest {

    @Test
    public void TODO() throws Exception {
        //TODO : les differentes lambda s'emele
        throw new Exception("    //TODO : les differentes lambda s'emele");
    }

    @Test
    public void sample() {
        List<JavaClasses> importedClassesPack = asList(new ClassFileImporter().importPackages("fr.sbuisson.sample"));

        var scrapper = new Scrapper();


        // la rule sert juste de pretexte pour le traitement du scrapper
        ArchRule rule =
                noClasses().should().accessTargetWhere(scrapper);


        // THEN
        importedClassesPack.forEach(importedClasses -> rule.evaluate(importedClasses));


        // la rule sert juste de pretexte pour le traitement du scrapper
        ArchRule ruleHeritance =
                noClasses().should().beAssignableFrom(new ScrapperInherit(scrapper.calledBy));

        importedClassesPack.forEach(importedClasses -> ruleHeritance.evaluate(importedClasses));

        System.out.println("dateNaissance");
        System.out.println(haveBeenCalledBy(scrapper.calledBy, "fr.sbuisson.sample.Poisson.dateNaissance", "fr.sbuisson.sample.building"));
        System.out.println("poid");
        System.out.println(haveBeenCalledBy(scrapper.calledBy, "fr.sbuisson.sample.Poisson.poid", "fr.sbuisson.sample.building"));
        System.out.println("couleur");
        System.out.println(haveBeenCalledBy(scrapper.calledBy, "fr.sbuisson.sample.Poisson.couleur", "fr.sbuisson.sample.building"));
        System.out.println("prix");
        System.out.println(haveBeenCalledBy(scrapper.calledBy, "fr.sbuisson.sample.Poisson.prix", "fr.sbuisson.sample.building"));
    }


    @Test
    public void SearchFromCsv() throws IOException {

        List<String> methodeToSearch = new ArrayList<String>();
        try (CSVReader csvReader = new CSVReader(new FileReader(FileUtils.toFile(getClass().getClassLoader().getResource("toKeep.csv"))))) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                methodeToSearch.add(values[0]);
            }
        }


        List<JavaClasses> importedClassesPack = asList(new ClassFileImporter().importPackages("fr.sbuisson.sample"));

        var scrapper = new Scrapper();
        ArchRule rule =
                noClasses().should().accessTargetWhere(scrapper);
        // THEN
        // la rule sert juste de pretexte pour le traitement du scrapper
        importedClassesPack.forEach(importedClasses -> rule.evaluate(importedClasses));

        // la rule sert juste de pretexte pour le traitement du scrapper
        ArchRule ruleHeritance =
                noClasses().should().beAssignableFrom(new ScrapperInherit(scrapper.calledBy));

        importedClassesPack.forEach(importedClasses -> ruleHeritance.evaluate(importedClasses));

        System.out.println(methodeToSearch);
        System.out.println(methodeToSearch.size());
        for (var methode : methodeToSearch) {
            System.out.println(haveBeenCalledBy(scrapper.calledBy, methode, "fr.sbuisson.sample.building"));
        }
    }

    class Scrapper extends DescribedPredicate<JavaAccess<?>> {

        MultiMap<String> accessingTo = new MultiMap<>();

        MultiMap<String> calledBy = new MultiMap<>();
        private Predicate<String> filter;

        public Scrapper() {
            super("Scrapper");
        }


        @Override
        public boolean apply(JavaAccess<?> access) {


            String targetName = access.getTarget().getFullName();
            String originName = access.getOrigin().getFullName();
            if (targetName.contains("lambda$"))
                targetName = targetName.substring(0, targetName.indexOf("lambda$")) + "lambda";
            if (originName.contains("lambda$"))
                originName = originName.substring(0, originName.indexOf("lambda$")) + "lambda";

            if (targetName.contains("<init>"))
                targetName = targetName.substring(0, targetName.indexOf("<init>")) + "lambda";
            if (originName.contains("<init>")){
                //permet la declaration des lambda dans le corps
                //TODO : les differentes lambda s'emele
                originName = originName.substring(0, originName.indexOf("<init>")) + "lambda";
                accessingTo.add(targetName, originName);
                calledBy.add(originName, targetName);

                return false;
            }

            if (targetName.contains("java.lang.Object")) {
                return false;
            }
            System.out.println("---");
            System.out.println(access.getTarget().getFullName() + " " + access.getOrigin().getFullName());
            System.out.println(targetName + " " + originName);
            calledBy.add(targetName, originName);
            accessingTo.add(originName, targetName);
            return false;
        }


    }


    public List<String> haveBeenCalledBy(MultiMap<String> allCalledBy, String called, String packageCalling) {
        Set<String> callings = new HashSet<>();
        List<String> c1 = allCalledBy.get(called);
        if (c1 != null)
            callings.addAll(c1);
        var n = 0;
        while (n < callings.size()) {

            n = callings.size();
            new ArrayList<>(callings).forEach(calling -> {
                List<String> c = allCalledBy.get(calling);
                if (c != null)
                    callings.addAll(c);
            });
        }

        return callings.stream().filter(calling -> calling.startsWith(packageCalling)).collect(Collectors.toList());


    }

    public class ScrapperInherit extends DescribedPredicate<JavaClass> {
        MultiMap<String> calledBy;

        public ScrapperInherit(MultiMap<String> calledBy) {
            super("ScrapperInherit");
            this.calledBy = calledBy;
        }

        @Override
        public boolean apply(JavaClass klass) {
            for (String key : new ArrayList<>(calledBy.keySet())) {
                var otherKlass = key.substring(0, key.lastIndexOf("."));
                var otherMethod = key.substring(key.lastIndexOf("."));
                if (klass.isAssignableTo(otherKlass) && !klass.getFullName().equals(otherKlass)) {

                    calledBy.add(key.replace(otherKlass, klass.getFullName()), key);
                    calledBy.add(key, key.replace(otherKlass, klass.getFullName()));

                }
                if (klass.isAssignableFrom(otherKlass) && !klass.getFullName().equals(otherKlass)) {

                    calledBy.add(key.replace(otherKlass, klass.getFullName()), key);
                    calledBy.add(key, key.replace(otherKlass, klass.getFullName()));
                }
            }


            return false;
        }


    }


}
