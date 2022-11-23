package fr.uga.iut2.genevent.vue;

import java.time.LocalDate;
import java.util.Set;


public abstract class IHM {
    /**
     * Classe conteneur pour les informations saisies à propos d'un
     * {@link fr.uga.iut2.genevent.modele.Utilisateur}.
     *
     * <ul>
     * <li>Tous les attributs sont `public` par commodité d'accès.</li>
     * <li>Tous les attributs sont `final` pour ne pas être modifiables.</li>
     * </ul>
     */
    public static class InfosUtilisateur {
        public  String email;
        public final String nom;
        public final String password;
        public String descr = "";

        public InfosUtilisateur(final String email, final String nom, final String password) {
            this.email = email;
            this.nom = nom;
            this.password = password;
        }

        public InfosUtilisateur(final String nom, final String password) {
            this.nom = nom;
            this.password = password;
        }
        
        
        public InfosUtilisateur(final String email, final String nom, final String password, final String descr) {
            this.email = email;
            this.nom = nom;
            this.password = password;
            this.descr = descr;
        }

        public String getNom() {
            return nom;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getDescr() {
            return descr;
        }

        public void setDescr(String descr) {
            this.descr = descr;
        }
    }

    /**
     * Classe conteneur pour les informations saisies pour un nouvel
     * {@link fr.uga.iut2.genevent.modele.Evenement}.
     *
     * <ul>
     * <li>Tous les attributs sont `public` par commodité d'accès.</li>
     * <li>Tous les attributs sont `final` pour ne pas être modifiables.</li>
     * </ul>
     */
    public static class InfosNouvelEvenement {
        public final String nom;
        public final LocalDate dateDebut;
        public final LocalDate dateFin;
        public final InfosUtilisateur admin;
        public final String lieu;
        public String descr;
        public int nombreMax;

        public InfosNouvelEvenement(final String nom, final LocalDate dateDebut, final LocalDate dateFin, final InfosUtilisateur admin, final String lieu) {
            assert !dateDebut.isAfter(dateFin);
            this.nom = nom;
            this.dateDebut = dateDebut;
            this.dateFin = dateFin;
            this.admin = admin;
            this.lieu = lieu;
        }

        public InfosNouvelEvenement(final String nom, final LocalDate dateDebut, final LocalDate dateFin, final InfosUtilisateur admin, final String lieu, final String descr,final int nombreMax) {
            assert !dateDebut.isAfter(dateFin);
            this.nom = nom;
            this.dateDebut = dateDebut;
            this.dateFin = dateFin;
            this.admin = admin;
            this.lieu = lieu;
            this.descr = descr;
        }
    }

    /**
     * Rend actif l'interface Humain-machine.
     *
     * L'appel est bloquant : le contrôle est rendu à l'appelant une fois que
     * l'IHM est fermée.
     *
     */
    public abstract void demarrerInteraction();

    /**
     * Affiche un message d'information à l'attention de l'utilisa·teur/trice.
     *
     * @param msg Le message à afficher.
     *
     * @param succes true si le message informe d'une opération réussie, false
     *     sinon.
     */
    public abstract void informerUtilisateur(final String msg, final boolean succes);

    /**
     * Récupère les informations à propos d'un
     * {@link fr.uga.iut2.genevent.modele.Utilisateur}.
     *
     */
    public abstract void saisirUtilisateur();
    public abstract void creerUser();

    /**
     * Récupère les informations nécessaires à la création d'un nouvel
     * {@link fr.uga.iut2.genevent.modele.Evenement}.
     *
     * @param nomsExistants L'ensemble des noms d'évenements qui ne sont plus
     *     disponibles.
     *
     */
    public abstract void saisirNouvelEvenement(final Set<String> nomsExistants);


    public abstract void afficherVosEvenements();
}
