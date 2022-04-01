package fr.sbuisson.sample.building;

import fr.sbuisson.sample.mobilier.Aquarium;
import fr.sbuisson.sample.mobilier.Etalage;

public class Magasin {

    Aquarium aquarium;
    Etalage etalage;

    public void someFresh() {
        System.out.println(etalage.getFirstPoissons());
    }

    public void someColor() {
        System.out.println(etalage.getColorsEtal());
    }

    public void somePrix() {
        System.out.println(etalage.getPrixEtal());
    }

}
