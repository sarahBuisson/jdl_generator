package fr.sbuisson.findUsedBy;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.apache.commons.collections.MultiMap;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;

public class IsItUsedTest2 {

    @Test
    public void sample() {
        JavaClass classPoisson = new ClassFileImporter().importPackages("fr.sbuisson.sample").get("Poisson");

        MultiMap callingMap = UsedByUtils.callingMap(classPoisson.getMethod("getDateNaissance"), asList("fr"));
        System.out.println(callingMap);

    }


}
