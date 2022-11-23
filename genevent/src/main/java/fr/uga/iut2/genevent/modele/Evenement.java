package fr.uga.iut2.genevent.modele;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

// /!\ A ENLEVER DU DIAGRAMME DE CLASSE/!\
//  getProduit()
//  setNomEvent()
//  setAdmin()
//  setProduit()
// d

// CHANGER LE NOM DE LA CLASSE DANS LE DIAGRAMME DE CLASSE PAR Evenement
public class Evenement implements Serializable {

    private static final long serialVersionUID = 1L;  // nécessaire pour la sérialisation
    private final GenEvent genevent;                 // Ajouter dans le diagramme de classe GenEvent
    private final String nom;
    private String descriptionEvent;                // AJOUT
    private String lieu;                            // AJOUT
    private LocalDate dateDebut;                     // Ajouter dans le diagramme de classe dateDebut
    private LocalDate dateFin;                       // Ajouter dans le diagramme de classe dateFin
    private final String email;
    private final ArrayList<Utilisateur> participants = new ArrayList<>();    // AJOUT
    private final ArrayList<Utilisateur> sponsorList = new ArrayList<>();         // AJOUT
    private final int nombreMax;

    private final Map<String, Utilisateur> administrateurs = new HashMap<>();  // association qualifiée par l'email



    // Invariant de classe : !dateDebut.isAfter(dateFin)
    //     On utilise la négation ici pour exprimer (dateDebut <= dateFin), ce
    //     qui est équivalent à !(dateDebut > dateFin).

    // Ajouter dans le diagramme de classe initialiseEvenement
    public static Evenement initialiseEvenement(GenEvent genevent, String nom, LocalDate dateDebut, LocalDate dateFin, Utilisateur admin,String descriptionEvent,String lieu,String email,int nombreMax) {
        Evenement evt = new Evenement(genevent, nom, dateDebut, dateFin,descriptionEvent,lieu,email,nombreMax);
        evt.ajouteAdministrateur(admin);
        genevent.getUtilisateurs().get(email).ajouteEvenementAdministre(evt);
        return evt;
    }

    // Ajouter dans le diagramme de classe (GenEvent genevent & dateDebut, dateFin) retirer en paramètre admin
    public Evenement(GenEvent genevent, String nom, LocalDate dateDebut, LocalDate dateFin, String descriptionEvent, String lieu, String email, int nombreMax) {
        assert !dateDebut.isAfter(dateFin);
        this.genevent = genevent;
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.administrateurs.put(email, genevent.getUtilisateurs().get(email));
        this.descriptionEvent=descriptionEvent;
        this.lieu = lieu;
        this.email = email;
        this.nombreMax = nombreMax;
    }

    public String getNom() {
        return this.nom;
    }

    // Ajouter dans le diagramme de classe getDateDebut
    public LocalDate getDateDebut() {
        return dateDebut;
    }

    // Ajouter dans le diagramme de classe setDateDebut
    public void setDateDebut(LocalDate dateDebut) {
        assert !dateDebut.isAfter(this.dateFin);
        this.dateDebut = dateDebut;
    }

    // Ajouter dans le diagramme de classe getDateFin
    public LocalDate getDateFin() {
        return dateFin;
    }

    // Ajouter dans le diagramme de classe setDateFin
    public void setDateFin(LocalDate dateFin) {
        assert !this.dateDebut.isAfter(dateFin);
        this.dateFin = dateFin;
    }

    // Ajouter dans le diagramme de classe ajouteAdministrateur(Utilisateur admin)
    public void ajouteAdministrateur(Utilisateur admin) {
        assert !this.administrateurs.containsKey(admin.getEmail());
        this.administrateurs.put(admin.getEmail(), admin);
        admin.ajouteEvenementAdministre(this);
    }

    // METHODES NON COMPRISES DANS LE SQUELETTE DE BASE (Mathieu)


    public String getDescriptionEvent() {
        return descriptionEvent;
    }

    public Map<String, Utilisateur> getAdministrateurs() {
        return administrateurs;
    }

    public String getLieu() {
        return lieu;
    }

    public ArrayList<Utilisateur> getParticipants() {
        return participants;
    }



    public ArrayList<Utilisateur> getSponsorList() {
        return sponsorList;
    }

    public void setDescriptionEvent(String descriptionEvent) {
        this.descriptionEvent = descriptionEvent;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public void addParticipant(Utilisateur user) {
        if (!participants.contains(user)){
            participants.add(user);
        }
    }

    public void addSponsor(Utilisateur user) {
        if (!sponsorList.contains(user)){
            sponsorList.add(user);
        }
    }

    public void removeParticipant(Utilisateur user){
            participants.remove(user);
    }

    public void removeSponsor(Utilisateur user){
            sponsorList.remove(user);
    }

    public boolean estAdmin(Utilisateur user) {
        if (administrateurs.containsKey(user.getEmail())) {
            for (String key : administrateurs.keySet()) {
                if (key.equals(user.getEmail())) {
                    return true;
                }
            }
        }
        return false;
    }


    public String getEmail() {
        return email;
    }

    public int getNombreMax() {
        return nombreMax;
    }

    @Override
    public String toString() {
        return "Evenement{" +
                ", nom='" + nom + '\'' +"\n"+
                ", description='" + descriptionEvent + '\'' +"\n"+
                ", lieu='" + lieu + '\'' +"\n"+
                ", dateDebut=" + dateDebut +"\n"+
                ", dateFin=" + dateFin +"\n"+
                ", administrateur=" + email+"\n"+
                ", participants max=" + nombreMax +"\n"+
                ", sponsorList=" + sponsorList +"\n"+
                '}'+"\n";
    }
}
