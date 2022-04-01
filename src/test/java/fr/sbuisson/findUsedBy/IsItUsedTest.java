package fr.sbuisson.findUsedBy;

import com.github.jknack.handlebars.internal.antlr.misc.Predicate;
import com.opencsv.CSVReader;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaAccess;
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

//TODO : heritage et lambda
public class IsItUsedTest {

    @Test
    public void TODO() throws Exception {
        //TODO : heritage et lambda
        throw new Exception("//TODO : heritage et lambda");
    }
    @Test
    public void sample() {
        List<JavaClasses> importedClassesPack = asList(new ClassFileImporter().importPackages("fr.sbuisson.sample"));

        var scrapper = new Scrapper();
        ArchRule rule =
                noClasses().should().accessTargetWhere(scrapper);
        // THEN
        importedClassesPack.forEach(importedClasses -> rule.evaluate(importedClasses));
        System.out.println(haveBeenCalledBy(scrapper.calledBy, "fr.sbuisson.sample.Poisson.dateNaissance", "fr.sbuisson.sample.building"));
        System.out.println(haveBeenCalledBy(scrapper.calledBy, "fr.sbuisson.sample.Poisson.poid", "fr.sbuisson.sample.building"));
        System.out.println(haveBeenCalledBy(scrapper.calledBy, "fr.sbuisson.sample.Poisson.couleur", "fr.sbuisson.sample.building"));
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
        importedClassesPack.forEach(importedClasses -> rule.evaluate(importedClasses));

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
                targetName = targetName.substring(0, targetName.indexOf("lambda$"));
            if (originName.contains("lambda$"))
                originName = originName.substring(0, originName.indexOf("lambda$"));

            if (targetName.contains("<init>"))
                targetName = targetName.substring(0, targetName.indexOf("<init>"));
            if (originName.contains("<init>"))
                originName = originName.substring(0, originName.indexOf("<init>"));
            System.out.println("---");
            System.out.println(access.getTarget().getFullName() + " " + access.getOrigin().getFullName());
            System.out.println(targetName + " " + originName);
            calledBy.putValues(targetName, originName);
            accessingTo.putValues(originName, targetName);
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
            callings.forEach(calling -> {
                List<String> c = allCalledBy.get(calling);
                if (c != null)
                    callings.addAll(c);
            });
        }

        return callings.stream().filter(calling -> calling.startsWith(packageCalling)).collect(Collectors.toList());


    }


}