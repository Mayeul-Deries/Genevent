package fr.uga.iut2.genevent.modele;

public class Sponsor {

    // Dans le diagramme de classe, de pas mettre sponsorship en unique
    private double sponsorship;
    private Utilisateur utilisateur;
    private Evenement evenement;

    public Sponsor(double sponsorship, Utilisateur utilisateur, Evenement evenement) {
        this.sponsorship = sponsorship;
        this.utilisateur = utilisateur;
        this.evenement = evenement;
    }

    public double getSponsorship() {
        return sponsorship;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public Evenement getEvenement() {
        return evenement;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    public void setSponsorship(double sponsorship) {
        this.sponsorship = sponsorship;
    }
}
