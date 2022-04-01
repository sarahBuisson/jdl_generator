package fr.sbuisson.sample.mobilier;

import fr.sbuisson.sample.Poisson;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Etalage {
    private List<Poisson> poissons;


    public List<Poisson> getPoissons() {
        return poissons;
    }

    public void setPoissons(List<Poisson> poissons) {
        this.poissons = poissons;
    }


    public String getFirstPoissons() {
        var f=poissons.get(0).dateNaissance;
        return poissons.get(0).getDateNaissance().toString();
    }
    Function<Poisson, String> poissonStringFunction = p -> p.poid;
    public String getPoidEtal() {


        return poissons.stream().map(poissonStringFunction).collect(Collectors.joining());
    }

    public String getColorsEtal() {


        return poissons.stream().map(p -> p.couleur).collect(Collectors.joining());
    }

}