package fr.uga.iut2.genevent.modele;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

//A AJOUTER DANS DIAGRAMME DE CLASSE
//-UtilisateurCourant de type Utilisateur
//+getUtilisateurCourant

public class GenEvent implements Serializable {

    private static final long serialVersionUID = 1L;  // nécessaire pour la sérialisation
    private final Map<String, Utilisateur> utilisateurs;  // association qualifiée par l'email
    private final Map<String, Evenement> evenements;  // association qualifiée par le nom
    private Utilisateur utilisateurCourant;


    public Utilisateur getUtilisateurCourant() {
        return utilisateurCourant;
    }

    public void setUtilisateurCourant(Utilisateur utilisateurCourant) {
        if (utilisateurs.containsValue(utilisateurCourant)){

        }else {
            this.utilisateurs.put(utilisateurCourant.getEmail(),utilisateurCourant);
            this.utilisateurCourant = utilisateurCourant;
        }

    }

    public GenEvent() {
        this.utilisateurs = new HashMap<>();
        this.evenements = new HashMap<>();
    }

    public boolean ajouteUtilisateur(String email, String username, String password) {
        if (this.utilisateurs.containsKey(email)) {
            return false;
        } else {
            this.utilisateurs.put(email, new Utilisateur(email, username, password));
            return true;
        }
    }



    public boolean ajouteEvenement(String nom, LocalDate dateDebut, LocalDate dateFin, String desc, String lieu, String adminEmail, int nombreMax) {
        if (this.evenements.containsKey(nom)){
            return false;
        }else {

            this.evenements.put(nom,new Evenement(this,nom,dateDebut,dateFin,desc,lieu,adminEmail, nombreMax));
 //           this.utilisateurCourant.ajouteEvenementAdministre(evenements.get(nom));
            this.getUtilisateurs().get(adminEmail).ajouteEvenementAdministre(evenements.get(nom));
            return true;
        }
    }


    public Map<String, Evenement> getEvenements() {
        return this.evenements;
    }

    public void nouvelEvenement(String nom, LocalDate dateDebut, LocalDate dateFin, String desc, String lieu, String adminEmail, int nombreMax) {
        assert !this.evenements.containsKey(nom);
        assert this.utilisateurs.containsKey(adminEmail);
        Utilisateur admin = this.utilisateurs.get(adminEmail);
        Evenement evt = Evenement.initialiseEvenement(this, nom, dateDebut, dateFin, admin,desc,lieu,adminEmail,nombreMax);
        this.evenements.put(nom, evt);
    }

    public Map<String, Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }


}
