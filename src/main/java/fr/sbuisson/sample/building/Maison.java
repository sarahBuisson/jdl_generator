package fr.sbuisson.sample.building;

import fr.sbuisson.sample.mobilier.Aquarium;

public class Maison {
    Aquarium aquarium;

    public Aquarium getAquarium() {
        return aquarium;
    }

    public String getCouleur(){
        return aquarium.getPoissons().get(0).couleur;
    }
}
