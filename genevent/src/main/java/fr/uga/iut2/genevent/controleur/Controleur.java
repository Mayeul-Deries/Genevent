package fr.uga.iut2.genevent.controleur;

import fr.uga.iut2.genevent.modele.Evenement;
import fr.uga.iut2.genevent.modele.GenEvent;
import fr.uga.iut2.genevent.modele.Utilisateur;
import fr.uga.iut2.genevent.vue.IHM;
import fr.uga.iut2.genevent.vue.JavaFXGUI;

import java.util.HashMap;
import java.util.Map;

//A ENLEVER DIAGRAMME DE CLASSE

/*
-utilisateurCourant
+ getUtilisataeurCourant
+ setUtilisateurCourant
mettre utilisateur partout (en français) et pas user : utilisateur
+ rechercher
+ remplacer seConnecter par saisirUtilisateur
+ creer evenement enlever InfosNouvelUtilisateur
*/

public class Controleur {

    private GenEvent genevent;
    private final IHM ihm;

    public Controleur(GenEvent genevent) {
        this.genevent = genevent;

        // choisir la classe CLI ou JavaFXGUI
//        this.ihm = new CLI(this);
        this.ihm = new JavaFXGUI(this);
    }


    public GenEvent getGenevent() {
        return genevent;
    }

    public void setGenevent(GenEvent genevent) {
        this.genevent = genevent;
    }

    public IHM getIhm() {
        return ihm;
    }

    public void demarrer() {
        this.ihm.demarrerInteraction();
    }

    public void saisirUtilisateur() {
        this.ihm.saisirUtilisateur();
    }
    public void creerUser(){
        this.ihm.creerUser();
    }

    public void creerUtilisateur(IHM.InfosUtilisateur infos) {
        boolean nouvelUtilisateur = this.genevent.ajouteUtilisateur(
                infos.email,
                infos.nom,
                infos.password
        );
        if (nouvelUtilisateur) {
            this.ihm.informerUtilisateur(
                    "Nouvel·le utilisa·teur/trice : " + infos.nom + " <" + infos.email + ">",
                    true
            );
        } else {
            this.ihm.informerUtilisateur(
                    "L'utilisa·teur/trice " + infos.email + " est déjà connu·e de GenEvent.",
                    false
            );
        }
    }

    public void saisirEvenement() {
        this.ihm.saisirNouvelEvenement(this.genevent.getEvenements().keySet());
    }

    public void creerEvenement(IHM.InfosNouvelEvenement infos) {
        //Pas besoin car les utilisateurs créeant des evenements seront déja connectés donc ne seront pas de nouveaux utilisateurs


        // création d'un Utilisateur si nécessaire
        boolean nouvelUtilisateur = this.genevent.ajouteUtilisateur(
                infos.admin.email,
                infos.admin.nom,
                infos.admin.password
        );
        if (nouvelUtilisateur) {
            this.ihm.informerUtilisateur("Vous etes un nouveau utilisateur ! \n mdp : " + infos.admin.password + "\n nom : " + infos.admin.nom + " \n e-mail : <" + infos.admin.email + ">",
                    true
            );
        }

        this.genevent.nouvelEvenement(
                infos.nom,
                infos.dateDebut,
                infos.dateFin,
                infos.admin.email,
                infos.descr,
                infos.lieu,
                infos.nombreMax
        );
        this.ihm.informerUtilisateur(
                infos.admin.email+" viens de créer un nouvel évenement portant le nom: " + infos.nom ,
                true
        );
    }


    public void modifierProfil(){

    }

    public void participer(Utilisateur utilisateur, Evenement evenement){

    }

    public void sponsoriser(Utilisateur utilisateur, Evenement evenement){

    }

    public void alerteUtilisateur() {
            this.ihm.informerUtilisateur(
                    "Les mots de passe ne correspondent pas.",
                    false
            );
    }

    public void alerteUtilisateurLog() {
        this.ihm.informerUtilisateur(
                "Adresse email ou mot de passe est incorrect. Veuillez essayer à nouveau.",
                false
        );

    }
    public void demanderSomme(){
        this.ihm.informerUtilisateur(
                "Veuillez renseignez une somme",
                true
        );
    }

}
