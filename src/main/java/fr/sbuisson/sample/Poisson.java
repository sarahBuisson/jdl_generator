package fr.sbuisson.sample;

import lombok.Getter;

import java.util.Date;

@Getter
public class Poisson {
    public Date dateNaissance;
    public String couleur;
    public String poid;
    public String prix;

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

}
