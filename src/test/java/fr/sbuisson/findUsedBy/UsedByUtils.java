package fr.sbuisson.findUsedBy;

import com.tngtech.archunit.core.domain.AccessTarget;
import com.tngtech.archunit.core.domain.JavaCodeUnit;
import com.tngtech.archunit.core.domain.JavaMethod;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import java.util.List;

public class UsedByUtils {

    static MultiMap callingMap(JavaMethod method, List<String> packages) {
        MultiMap callingMap = new MultiValueMap();

        method.getCallsFromSelf().forEach(call -> {
            AccessTarget.CodeUnitCallTarget target = call.getTarget();
            if (packages.stream().anyMatch(pack -> target.getOwner().getPackage().containsPackage(pack))) {
                JavaMethod calledMetho = target.getOwner().getMethods().stream().filter(targetMethod -> target.getFullName().equals(targetMethod.getFullName())).findFirst().get();
                callingMap.put(method.getFullName(), calledMetho);

                callingMap.putAll(callingMap(calledMetho, packages));
            }

        });
        return callingMap;


    }


    static MultiMap usedByMap(JavaMethod method, MultiMap callingMap) {
        MultiMap usedByMap = new MultiValueMap();
        callingMap.enforEach(e->{

    ()
});


        return usedByMap;

    }
}
