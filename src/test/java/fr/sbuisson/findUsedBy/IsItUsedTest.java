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

public class IsItUsedTest {
    @Test
    public void sample() {
        List<JavaClasses> importedClassesPack = asList(new ClassFileImporter().importPackages("fr.sbuisson.sample"));

        var scrapper = new Scrapper();
        ArchRule rule =
                noClasses().should().accessTargetWhere(scrapper);
        // THEN
        importedClassesPack.forEach(importedClasses -> rule.evaluate(importedClasses));
        System.out.println(haveBeenCalledBy(scrapper.calledBy, "fr.sbuisson.sample.Poisson.dateNaissance", "fr.sbuisson.sample.building"));
    }


    @Test
    public void SearchFromCsv() throws IOException {

        List<String> methodeToSearch = new ArrayList<String>();
        try (CSVReader csvReader = new CSVReader(new FileReader(FileUtils.toFile(getClass().getClassLoader().getResource("toKeep.csv")))) ){
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
        for(var methode : methodeToSearch)
        System.out.println(haveBeenCalledBy(scrapper.calledBy, methode, "fr.sbuisson.sample.building"));
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

                calledBy.putValues(access.getTarget().getFullName(), access.getOrigin().getFullName());
            accessingTo.putValues(access.getOrigin().getFullName(), access.getTarget().getFullName());
            return false;
        }


    }


    public List<String> haveBeenCalledBy(MultiMap<String> allCalledBy, String called, String packageCalling) {
        Set<String> callings = new HashSet<>();
        List<String> c1 = allCalledBy.get(called);
        if(c1!=null)
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
