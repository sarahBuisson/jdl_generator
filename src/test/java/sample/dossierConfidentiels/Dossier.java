package sample.dossierConfidentiels;

import java.util.ArrayList;
import java.util.List;

public class Dossier {

    List<Contributeur> contributeurs = new ArrayList<>();
    List<Contenu> contenus = new ArrayList<>();

    public List<Contributeur> getContributeurs() {
        return contributeurs;
    }

    public void setContributeurs(List<Contributeur> contributeurs) {
        this.contributeurs = contributeurs;
    }

    public List<Contenu> getContenus() {
        return contenus;
    }

    public void setContenus(List<Contenu> contenus) {
        this.contenus = contenus;
    }
}
