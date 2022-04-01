package fr.sbuisson.sample.building;

import fr.sbuisson.sample.mobilier.Aquarium;
import fr.sbuisson.sample.mobilier.Etalage;

public class Magasin {

    Aquarium aquarium;
    Etalage etalage;

    public void some(){
        System.out.println(etalage.getFirstPoissons());
    }

}
