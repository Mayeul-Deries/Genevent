package fr.uga.iut2.genevent.modele;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.validator.routines.EmailValidator;

// /!\ A ENLEVER DU DIAGRAMME DE CLASSE/!\
//  setEmail()


public class Utilisateur implements Serializable {

    private static final long serialVersionUID = 1L;  // nécessaire pour la sérialisation
    private  String email;
    private String nom;
    private String password;
    private String descriptionUtilisateur;
    private Map<String, Evenement> evenementsAdministres;  // association qualifiée par le nom
    private Map<String, Evenement> evenementParticipe; // les evenements pour lesquel l'utilisateur participe
    private Map<String, Evenement> evenementSponsorise; // les evenements que l'utilisateur sponsorise

    // Modifier dans le diagramme de classe l'ordre (email, nom, password)
    public Utilisateur(String email, String nom, String password) {
        assert EmailValidator.getInstance(false, false).isValid(email);
        this.email = email;
        this.nom = nom;
        this.password = password;
        this.evenementsAdministres = new HashMap<>();
        this.evenementParticipe = new HashMap<>();
        this.evenementSponsorise = new HashMap<>();
    }

    public Utilisateur(String nom, String password) {
        this.nom = nom;
        this.password = password;
        this.evenementsAdministres = new HashMap<>();
        this.evenementParticipe = new HashMap<>();
        this.evenementSponsorise = new HashMap<>();
    }


    public String getEmail() {
        return email;
    }

    // Dans le diagramme de classe, modifier le nom du getter getNom
    public String getNom() {
        return nom;
    }

    // Dans le diagramme de classe, modifier le nom du setter setNom
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescriptionUtilisateur() {
        return descriptionUtilisateur;
    }

    // Dans le diagramme de classe, modifier le paramètre en String descriptionUtilisateur
    public void setDescriptionUtilisateur(String descriptionUtilisateur) {
        this.descriptionUtilisateur = descriptionUtilisateur;
    }

    public Map<String, Evenement> getEvenementsAdministres() {
        return evenementsAdministres;
    }
    public Map<String, Evenement> getEvenementParticipe() {
        return evenementParticipe;
    }
    public Map<String, Evenement> getEvenementSponsorise() {
        return evenementSponsorise;
    }


    public void ajouteEvenementAdministre(Evenement evt) {
        assert !this.evenementsAdministres.containsKey(evt.getNom());
        this.evenementsAdministres.put(evt.getNom(), evt);
    }
    public void removeEvenementAdministre(Evenement evt) {
        assert !this.evenementsAdministres.containsKey(evt.getNom());
        this.evenementsAdministres.remove(evt.getNom(), evt);
    }


    public void ajouteEvenementParticipe(Evenement evt) {
        this.evenementParticipe = new HashMap<>();
        assert !this.evenementParticipe.containsKey(evt.getNom());
        this.evenementParticipe.put(evt.getNom(),evt);

    }
    public void removeEvenementParticipe(Evenement evt) {
        assert !this.evenementParticipe.containsKey(evt.getNom());
        this.evenementParticipe.remove(evt.getNom(), evt);
    }

    public void ajouteEvenementSponsorise(Evenement evt) {
        assert !this.evenementSponsorise.containsKey(evt.getNom());
        this.evenementSponsorise.put(evt.getNom(), evt);
    }
    public void removeEvenementSponsorise(Evenement evt) {
        assert !this.evenementSponsorise.containsKey(evt.getNom());
        this.evenementSponsorise.remove(evt.getNom(), evt);
    }





    @Override
    public String toString() {
        return "Utilisateur{" +
                "email='" + email + '\'' +
                ", nom='" + nom + '\'' +
                '}';
    }
}
