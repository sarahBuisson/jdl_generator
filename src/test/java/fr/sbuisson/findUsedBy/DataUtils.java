package fr.sbuisson.findUsedBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataUtils {
    public static List<Map<String, Object>> mergeAndDistinct(List<Map<String, Object>>... datas) {

        List<Map<String, Object>> retour = new ArrayList<>();

        for (List<Map<String, Object>> arr : datas)
            arr.forEach(d -> {
                if (!retour.stream().anyMatch(r -> r.get("service").equals(d.get("service")))) {
                    retour.add(d);
                }


            });
        return retour;
    }
}
