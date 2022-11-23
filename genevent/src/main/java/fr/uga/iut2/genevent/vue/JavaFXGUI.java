package fr.uga.iut2.genevent.vue;

import fr.uga.iut2.genevent.controleur.Controleur;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import fr.uga.iut2.genevent.modele.Evenement;
import fr.uga.iut2.genevent.modele.GenEvent;
import fr.uga.iut2.genevent.modele.Utilisateur;
import fr.uga.iut2.genevent.util.Persisteur;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.apache.commons.validator.routines.EmailValidator;


/**
 * La classe JavaFXGUI est responsable des interactions avec
 * l'utilisa·teur/trice en mode graphique.
 * <p>
 * Attention, pour pouvoir faire le lien avec le
 * {@link fr.uga.iut2.genevent.controleur.Controleur}, JavaFXGUI n'est pas une
 * sous-classe de {@link javafx.application.Application} !
 * <p>
 * Le démarrage de l'application diffère des exemples classiques trouvés dans
 * la documentation de JavaFX : l'interface est démarrée à l'initiative du
 * {@link fr.uga.iut2.genevent.controleur.Controleur} via l'appel de la méthode
 * {@link #demarrerInteraction()}.
 */
public class JavaFXGUI extends IHM {

    //attributs

    private final Controleur controleur;
    private final CountDownLatch eolBarrier;  // /!\ ne pas supprimer /!\ : suivi de la durée de vie de l'interface

    // éléments vue nouvel·le utilisa·teur/trice

    @FXML
    private Label loginConnecte, loginEmailLabel, myLocalDateStart, myLocalDateEnd, myEventNameLabel, myPlaceLabel, myNumberLabel, myDescEventLabel;

    @FXML
    private TextField newUserForenameTextField, loginPseudotf, newUserEmailTextField, newUserPasswordTextField, newUserPasswordTextFieldConfirmation,
            loginMDPtf, loginEmailtf, tfEvtName, tfEvtNumber, tfLieuEvent, tfEvtDesc;

    @FXML
    private Button newUserOkButton, newUserCreate, newUserCancelButton, createEvtBtnCancel, createEvtCreate, disconnectButton,
            btnEvents, btnAttendingTo, btnSponsoring, btnMyEvent, btnNewEvent;

    @FXML
    private DatePicker localDateStart, localDateEnd;

    @FXML
    private CheckBox checkConditions;

    @FXML
    HBox hboxEvents;

    @FXML
    Button btnAcceptCondition;


    public JavaFXGUI(Controleur controleur) {
        this.controleur = controleur;
        this.eolBarrier = new CountDownLatch(1);  // /!\ ne pas supprimer /!\
    }

    /**
     * Point d'entrée principal pour le code de l'interface JavaFX.
     *
     * @param primaryStage stage principale de l'interface JavaFX, sur laquelle
     *                     définir des scenes.
     * @throws IOException si le chargement de la vue FXML échoue.
     * @see javafx.application.Application#start(Stage)
     */
    private void start(Stage primaryStage) throws IOException, ClassNotFoundException {
        FXMLLoader mainViewLoader = new FXMLLoader(getClass().getResource("Connexion.fxml"));
        mainViewLoader.setController(this);
        Scene mainScene = new Scene(mainViewLoader.load());

        primaryStage.setTitle("Connexion a seavent");
        primaryStage.setScene(mainScene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    //-----  Éléments du dialogue  -------------------------------------------------
    private void exitAction() {
        // fermeture de l'interface JavaFX : on notifie sa fin de vie
        this.eolBarrier.countDown();
    }
//---------menu principal  ----------

    /**
     * méthode qui permet de se connecter
     */
    @FXML
    private void newUserMenuItemAction() {
        this.controleur.saisirUtilisateur();
    }

    /**
     * méthode qui permet de créer un utilisateur
     */
    @FXML
    private void newUserMenuItemAction2() {
        this.controleur.creerUser();
    }


    // vue nouvel·le utilisa·teur/trice  -----

    /**
     * méthode qui permet à l'utilisateur de créer un compte lors d'un
     * clique sur le bouton créer un compte et d'ouvrir l'interface principale de l'appilication (onglet événements)
     *
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @FXML
    private void createNewUserAction() throws ClassNotFoundException, IOException {

        IHM.InfosUtilisateur data = new IHM.InfosUtilisateur(
                this.newUserEmailTextField.getText().strip().toLowerCase(),
                this.newUserForenameTextField.getText().strip(),
                this.newUserPasswordTextField.getText().strip()
        );
        if (data.getPassword().equals(newUserPasswordTextFieldConfirmation.getText().strip())) {
            this.controleur.creerUtilisateur(data);

            GenEvent temporaire = Persisteur.lireEtat(); //on créer une copie de la BD

            if (!(temporaire.getUtilisateurs().containsKey(data.email) || temporaire.getUtilisateurs().containsKey((data.nom)))) {
                Stage stage = ((Stage) newUserCreate.getScene().getWindow());
                stage.close();

                temporaire.ajouteUtilisateur(this.newUserEmailTextField.getText().strip().toLowerCase(),
                        this.newUserForenameTextField.getText().strip(),
                        this.newUserPasswordTextField.getText().strip()); // on ajoute l'utilisateur à la BD à la copie

                Persisteur.sauverEtat(temporaire); // On sauvegarde la BD avec le nouvel utilisateur

                FXMLLoader newUserViewLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
                newUserViewLoader.setController(this);
                Scene newUserScene = new Scene(newUserViewLoader.load());

                Stage newUserWindow = new Stage();
                newUserWindow.setTitle("Seavent");
                newUserWindow.initModality(Modality.APPLICATION_MODAL);
                newUserWindow.setScene(newUserScene);
                newUserWindow.show();
                newUserWindow.setFullScreen(true);          //Ne surtout pas enlever (jvous expliquerai)
                newUserWindow.setFullScreen(false);         //Ne surtout pas enlever (jvous expliquerai)
                loginConnecte.setText(newUserEmailTextField.getText());
                newUserWindow.setResizable(false);
                //problème
                temporaire.setUtilisateurCourant(new Utilisateur(newUserEmailTextField.getText(), newUserForenameTextField.getText(), newUserPasswordTextField.getText()));
                Persisteur.sauverEtat(temporaire);
                afficherEvents();
            }
        } else {
            this.controleur.alerteUtilisateur();
            newUserPasswordTextField.setText("");
            newUserPasswordTextFieldConfirmation.setText("");
            newUserPasswordTextField.setStyle("-fx-control-inner-background: f8d7da");
            newUserPasswordTextFieldConfirmation.setStyle("-fx-control-inner-background: f8d7da");
        }

    }

    /**
     * méthode qui permet à l'utilisateur de se connecter lors qu clique sur le bouton connexion
     * et d'ouvrir l'interface principale de l'appilication (onglet événements)
     *
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @FXML
    private void connectUserAction() throws ClassNotFoundException, IOException {
        boolean connexion_reussi = false;
        GenEvent data = Persisteur.lireEtat();
        //System.out.println(data.getUtilisateurs().get(loginPseudotf.getText()));


        for (Utilisateur unUtilisateur : data.getUtilisateurs().values()) {// on parcours la liste des utlisateurs enregistré
            if (unUtilisateur.getEmail().compareTo(loginEmailtf.getText()) == 0) {       // on compare le login de  enregistré dans la base de donné avec celui rentré par l'utilisateur

                if (unUtilisateur.getPassword().compareTo(loginMDPtf.getText()) == 0) {



                    //On compare les mpd enregistrés dans la base avec celui rentré par l'utilisateur

                    connexion_reussi = true;                                             // si tout login et mot de passe correspondent ouverture de la page
                    FXMLLoader newUserViewLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
                    newUserViewLoader.setController(this);
                    Scene newUserScene = new Scene(newUserViewLoader.load());

                    Stage stage = (Stage) newUserOkButton.getScene().getWindow();           //Fermeture de la fenetre de connexion
                    stage.close();

                    Stage newUserWindow = new Stage();
                    newUserWindow.setTitle("Seavent");
                    newUserWindow.initModality(Modality.APPLICATION_MODAL);
                    newUserWindow.setScene(newUserScene);
                    newUserWindow.show();
                    newUserWindow.setFullScreen(true);          //Ne surtout pas enlever (jvous expliquerai)
                    newUserWindow.setFullScreen(false);         //Ne surtout pas enlever (jvous expliquerai)
                    loginConnecte.setText(loginEmailtf.getText());

                    System.out.println("connexion réussie");
                    newUserWindow.setResizable(false);
                    if (loginEmailtf.getText() == null) {
                        data.setUtilisateurCourant(data.getUtilisateurCourant());
                        Persisteur.sauverEtat(data);
                    } else {
                        data.setUtilisateurCourant(data.getUtilisateurs().get(loginEmailtf.getText()));
                        Persisteur.sauverEtat(data);
                    }


                    Persisteur.sauverEtat(data);

                    afficherEvents();
                    break;
                }
            }
        }
        if (!connexion_reussi) {
            this.controleur.alerteUtilisateurLog();
        }
    }


    /**
     * méthode qui permet de déconécter l'utilisateur courant et ramène à l'interface de connexion
     *
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @FXML
    private void onDeconnectButton() throws ClassNotFoundException, IOException {
        FXMLLoader newUserViewLoader = new FXMLLoader(getClass().getResource("Connexion.fxml"));
        newUserViewLoader.setController(this);
        Scene newUserScene = new Scene(newUserViewLoader.load());
        Stage stage = (Stage) disconnectButton.getScene().getWindow();
        stage.close();
        Stage newUserWindow = new Stage();
        newUserWindow.setTitle("Seavent");
        newUserWindow.initModality(Modality.APPLICATION_MODAL);
        newUserWindow.setScene(newUserScene);
        newUserWindow.show();
        newUserWindow.setFullScreen(true);
        newUserWindow.setFullScreen(false);
        newUserWindow.setResizable(false);

    }

    @FXML
    private void createEvtCancel() throws IOException {
        FXMLLoader newUserViewLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        newUserViewLoader.setController(this);
        Scene newUserScene = new Scene(newUserViewLoader.load());
        Stage newUserWindow = new Stage();
        Stage stage = (Stage) createEvtBtnCancel.getScene().getWindow();
        stage.close();
        newUserWindow.setTitle("Seavent");
        newUserWindow.initModality(Modality.APPLICATION_MODAL);
        newUserWindow.setScene(newUserScene);
        newUserWindow.show();
        newUserWindow.setFullScreen(true);
        newUserWindow.setFullScreen(false);
        newUserWindow.setResizable(false);

    }


//----- Méthode qui permet de mettre le cursor en mode click -----

    /**
     * methode qui permet de passer le curseur de la souris en mode hover d'un élément cliquable
     *
     * @param me
     */
    @FXML
    private void cursorHand(MouseEvent me) {
        newUserCancelButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Scene scene = newUserCancelButton.getScene();
                scene.setCursor(Cursor.HAND);
            }
        });
    }

    /**
     * méthode qui permet de passer le curseur de la souris en mode normal, par défaut
     *
     * @param me
     */
    @FXML
    private void cursorNormal(MouseEvent me) {
        newUserCancelButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Scene scene = newUserCancelButton.getScene();
                scene.setCursor(Cursor.DEFAULT);
            }
        });
    }


    /**
     * méthode qui permet d'activer le bouton de connexion uniquement
     * si les champs email et mot de passe sont xonnus de la bdd adaptés aux attentes
     * (email ("@",".com"...), mdp.longueur > 7)
     */
    @FXML
    private void validateTextFields() {
        boolean isValid = true;

        isValid &= validatePasswordTextField(this.loginMDPtf);
        isValid &= validateEmailTextField(this.loginEmailtf);

        this.newUserOkButton.setDisable(!isValid);
    }

    /**
     * méthode qui permet d'activer le bouton de création d'un nouvel utilisateur uniquement
     * si les champs email, login et mot de passe sont adaptés aux attentes
     * (email ("@",".com"...), login.longueur > 0, mdp.longueur > 7)
     */
    @FXML
    private void validateTextFields2() {
        boolean isValid = true;

        isValid &= validateEmailTextField(this.newUserEmailTextField);
        isValid &= validateNonEmptyTextField(this.newUserForenameTextField);
        isValid &= validatePasswordTextField(this.newUserPasswordTextField);
        isValid &= validatePasswordTextField(this.newUserPasswordTextFieldConfirmation);
        isValid &= validatePolicyCheckBox(this.checkConditions);
        this.newUserCreate.setDisable(!isValid);
    }

    /**
     * vérifier l'état (sélectionnée ou non) d'une CheckBox
     *
     * @param checkBox la CheckBox dont on veut vérifier l'état
     * @return true si la CheckBox est sélectionnée, false sinon
     */
    private static boolean validatePolicyCheckBox(CheckBox checkBox) {
        return checkBox.isSelected();
    }

    /**
     * méthode générale qui permet de colorer une textField de couleur #f8d7da
     *
     * @param textField la texteField dont on veut modifier la couleur
     * @param isValid   le booléen à vérifier avant de changer la couleur
     */
    private static void markTextFieldErrorStatus(TextField textField, boolean isValid) {
        if (isValid) {
            textField.setStyle(null);
        } else {
            textField.setStyle("-fx-control-inner-background: f8d7da");
        }
    }

    /**
     * vérifier l'état (complétée ou non) d'une TextField et la colorer en #f8d7da (rouge) si elle est vide
     *
     * @param textField la TextField dont on veut vérifier l'état
     * @return true si la TextFiled n'est pas vide, false sinon
     */
    private static boolean validateNonEmptyTextField(TextField textField) {
        boolean isValid = textField.getText().strip().length() > 0;

        markTextFieldErrorStatus(textField, isValid);

        return isValid;
    }

    /**
     * vérifier l'état (complétée ou non) d'une TextField de mail
     * et la colorer en #f8d7da (rouge) si elle n'est pas conforme à la structure d'un mail
     *
     * @param textField la TextField dont on veut vérifier l'état
     * @return true si le TextFiled est conforme à un mail ("@",".com"...), false sinon
     */
    private static boolean validateEmailTextField(TextField textField) {
        EmailValidator validator = EmailValidator.getInstance(false, false);
        boolean isValid = validator.isValid(textField.getText().strip().toLowerCase());

        markTextFieldErrorStatus(textField, isValid);

        return isValid;
    }

    /**
     * caractères vérifier l'état (complétée ou non) d'une TextField de mot de passe et la colorer en #f8d7da
     * (rouge) si elle est inférieure à 7 caractères (sécurité)
     *
     * @param textField la TextField dont on veut vérifier l'état
     * @return true si TextFiled.length > 7 caractères, false sinon
     */
    private static boolean validatePasswordTextField(TextField textField) {
        boolean isValid = textField.getText().strip().length() > 7;

        markTextFieldErrorStatus(textField, isValid);

        return isValid;
    }

    /**
     * méthode qui permet de naviguer entre les différentes pages en cliquant sur leurs boutons associés
     * événement @param
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @FXML
    private void changePageOnAction(Event event) throws IOException, ClassNotFoundException {

        String btnID = ((Button) event.getSource()).getId();

        if (btnID.equals("btnEvents")) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            fxmlLoader.setController(this);
            Stage fenetre = (Stage) btnEvents.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
            fenetre.setScene(scene);
            fenetre.show();
            afficherEvents();
            fenetre.setResizable(false);
        }
        if (btnID.equals("btnAttendingTo")) {
           afficherEventsJoin();
        }
        if (btnID.equals("btnSponsoring")) {
            afficherEventsSponso();
        }
        if (btnID.equals("btnMyEvent")) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("my-events.fxml"));
            fxmlLoader.setController(this);
            Stage fenetre = (Stage) btnMyEvent.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
            fenetre.setScene(scene);
            fenetre.show();
            afficherMyEvent();
            fenetre.setResizable(false);
        }
        if (btnID.equals("btnNewEvent")) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("New-event.fxml"));
            fxmlLoader.setController(this);
            Stage fenetre = (Stage) btnNewEvent.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
            fenetre.setScene(scene);
            fenetre.show();
            fenetre.setResizable(false);
        }
        if (btnID.equals("createEvtBtnCancel")) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            fxmlLoader.setController(this);
            Stage fenetre = (Stage) createEvtBtnCancel.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
            fenetre.setScene(scene);
            fenetre.show();
            fenetre.setResizable(false);
            afficherEvents();
        }


        if (loginEmailtf.getLength() == 0) {
            loginConnecte.setText(newUserEmailTextField.getText());
        } else {
            loginConnecte.setText(loginEmailtf.getText());
        }
    }

//-----  Implémentation des méthodes abstraites  -------------------------------

    /**
     * méthode qui permet de démarrer l'application (ne pas modifier !)
     */
    @Override
    public void demarrerInteraction() {
        // démarrage de l'interface JavaFX
        Platform.startup(() -> {
            Stage primaryStage = new Stage();
            primaryStage.setOnCloseRequest((WindowEvent t) -> this.exitAction());
            try {
                this.start(primaryStage);
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        // attente de la fin de vie de l'interface JavaFX
        try {
            this.eolBarrier.await();
        } catch (InterruptedException exc) {
            System.err.println("Erreur d'exécution de l'interface.");
            System.err.flush();
        }
    }

    /**
     * méthode qui permet de faire un pop-up pour informer l'utilisateur d'une action (nouvel utilisateur, erreur...)
     *
     * @param msg    Le message à afficher.
     * @param succes true si le message informe d'une opération réussie, false
     */
    @Override
    public void informerUtilisateur(String msg, boolean succes) {
        final Alert alert = new Alert(
                succes ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING
        );
        alert.setTitle("GenEvent");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * méthode qui redirige vers la page connexion quand on clique sur "se connecter"
     * dans la page "créer un compte"
     */
    @Override
    public void saisirUtilisateur() {
        try {
            FXMLLoader newUserViewLoader = new FXMLLoader(getClass().getResource("Connexion.fxml"));
            newUserViewLoader.setController(this);
            Scene newUserScene = new Scene(newUserViewLoader.load());

            Stage stage = (Stage) newUserCreate.getScene().getWindow();           //Fermeture de la fenetre de connexion
            stage.close();

            Stage newUserWindow = new Stage();
            newUserWindow.setTitle("Connexion");
            newUserWindow.initModality(Modality.APPLICATION_MODAL);
            newUserWindow.setScene(newUserScene);
            newUserWindow.show();
            newUserWindow.setFullScreen(true);          //Ne surtout pas enlever (jvous expliquerai)
            newUserWindow.setFullScreen(false);
            newUserWindow.setResizable(false);

        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    /**
     * méthode qui redirige vers la page créer un compte quand on clique sur "créer un utilisateur"
     * dans la page "connexion"
     */
    @Override
    public void creerUser() {
        try {
            FXMLLoader newUserViewLoader = new FXMLLoader(getClass().getResource("New-user.fxml"));
            newUserViewLoader.setController(this);
            Scene newUserScene = new Scene(newUserViewLoader.load());

            Stage stage = (Stage) newUserOkButton.getScene().getWindow();           //Fermeture de la fenetre de connexion
            stage.close();

            Stage newUserWindow = new Stage();
            newUserWindow.setTitle("Création de compte");
            newUserWindow.initModality(Modality.APPLICATION_MODAL);
            newUserWindow.setScene(newUserScene);
            newUserWindow.show();
            newUserWindow.setFullScreen(true);          //Ne surtout pas enlever (jvous expliquerai)
            newUserWindow.setFullScreen(false);
            newUserWindow.setResizable(false);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    /**
     * méthode qui permet de renseigner un nouvel événement
     *
     * @param nomsExistants L'ensemble des noms d'événements qui ne sont plus
     */
    @Override
    public void saisirNouvelEvenement(Set<String> nomsExistants) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * méthode d'affichage des événements
     */
    @Override
    public void afficherVosEvenements() {
    }


    //-----  Implémentation de méthodes spécial pour Seavent -------------------------------

    @FXML private Button btnJoinEvents, btnSponsoEvents;
    @FXML private TextField sommeSponso;

    /**
     * méthode de création d'un événement
     * dans la rubrique "+"
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @FXML private void creerEvent() throws IOException, ClassNotFoundException {
        //Récupération du genEvent
        GenEvent data = Persisteur.lireEtat();
        //Ajout d'un user et d'un évenement a celui-ci


        if(!localDateStart.getValue().isAfter(localDateEnd.getValue())) {
        if (loginEmailtf.getLength() == 0) {
            Evenement eve = new Evenement(data, tfEvtName.getText(), localDateStart.getValue(), localDateEnd.getValue(), tfEvtDesc.getText(), tfLieuEvent.getText(), newUserEmailTextField.getText(), Integer.parseInt(tfEvtNumber.getText()));
            data.ajouteEvenement(tfEvtName.getText(), localDateStart.getValue(), localDateEnd.getValue(), tfEvtDesc.getText(), tfLieuEvent.getText(), newUserEmailTextField.getText(), Integer.parseInt(tfEvtNumber.getText()));
            data.ajouteUtilisateur(this.newUserEmailTextField.getText(), this.loginConnecte.getText(), this.newUserPasswordTextField.getText());
            data.getUtilisateurCourant().ajouteEvenementAdministre(eve);
        } else {
            Evenement eve = new Evenement(data, tfEvtName.getText(), localDateStart.getValue(), localDateEnd.getValue(), tfEvtDesc.getText(), tfLieuEvent.getText(), loginEmailtf.getText(), Integer.parseInt(tfEvtNumber.getText()));
            data.ajouteEvenement(tfEvtName.getText(), localDateStart.getValue(), localDateEnd.getValue(), tfEvtDesc.getText(), tfLieuEvent.getText(), loginEmailtf.getText(), Integer.parseInt(tfEvtNumber.getText()));
            data.ajouteUtilisateur(this.loginEmailtf.getText(), this.loginConnecte.getText(), this.loginMDPtf.getText());
            data.getUtilisateurCourant().ajouteEvenementAdministre(eve);
        }

            //Sauvegarde du persisteur après création de l'évènement
            Persisteur.sauverEtat(data);
            //Redirection sur my-events une fois l'évenement créer
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("my-events.fxml"));
            fxmlLoader.setController(this);
            Stage fenetre = (Stage) createEvtCreate.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
            fenetre.setScene(scene);
            fenetre.show();
            fenetre.setResizable(false);
            //Afichage du email pour montrer qui est co

            if (loginEmailtf.getLength() == 0) {
                loginConnecte.setText(newUserEmailTextField.getText());
            } else {
                loginConnecte.setText(loginEmailtf.getText());
            }
            informerUtilisateur("Votre événement a bien été créer",true);
            afficherMyEvent();
        }else {
                informerUtilisateur("L'événement n'a pu être créé car les dates ne sont pas cohérentes",false);
            }
    }

    /**
     * méthode d'affichage des événements créée par un autre utilisateur
     * dans la rubrique "événements"
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @FXML private void afficherEvents() throws IOException, ClassNotFoundException {


        GenEvent data = Persisteur.lireEtat();

        boolean entreeBoucle = false;

        VBox vBoxPrincipale = new VBox();
        VBox vBoxPrincipaleMAIN = new VBox();
        ScrollPane scroller = new ScrollPane(vBoxPrincipaleMAIN);
        vBoxPrincipaleMAIN.getChildren().add(vBoxPrincipale);
        hboxEvents.getChildren().add(vBoxPrincipale);
        hboxEvents.getChildren().add(scroller);
        int x = 0;
        Label l = new Label();

        for (Evenement eve : data.getEvenements().values()) {


            if (!(eve.getEmail().contains(data.getUtilisateurCourant().getEmail()))) {
                entreeBoucle = true;

                if (eve.getParticipants().contains(data.getUtilisateurCourant())){
                    entreeBoucle = false;

                } else {

                    File file;
                    if (x == 0) {
                        file = new File("src/main/resources/fr/uga/iut2/genevent/vue/icons/mouette-bleue.png");
                        x += 1;
                    } else if (x == 1) {
                        file = new File("src/main/resources/fr/uga/iut2/genevent/vue/icons/mouette-rosée.png");
                        x += 1;
                    } else {
                        file = new File("src/main/resources/fr/uga/iut2/genevent/vue/icons/mouette-verte.png");
                        x = 0;
                    }


                    String localUrl = file.toURI().toURL().toString();
                    Image img = new Image(localUrl);
                    ImageView imageView = new ImageView(img);
                    imageView.setFitWidth(120);
                    imageView.setFitHeight(120);


                    Label myLocalDateEnd = new Label();
                    Label myEventNameLabel = new Label();
                    Label myLocalDateStart = new Label();
                    Label myPlaceLabel = new Label();
                    Label myNumberLabel = new Label();
                    Label myDescEventLabel = new Label();
                    btnJoinEvents = new Button();


                    //Assignements des labels
                    myLocalDateStart.setText(eve.getDateDebut().toString());
                    myLocalDateEnd.setText(eve.getDateFin().toString());
                    myPlaceLabel.setText(eve.getLieu());
                    myEventNameLabel.setText(eve.getNom());
                    myDescEventLabel.setText(eve.getDescriptionEvent());
                    myNumberLabel.setText(eve.getParticipants().size() + "/" + eve.getNombreMax());
                    btnJoinEvents.setText("Rejoindre");


                    btnJoinEvents.setOnAction(e -> {


                        if (eve.getParticipants().size()==eve.getNombreMax()){
                            informerUtilisateur("Le nombre de participants est déja plein",false);
                        }else {
                            data.getUtilisateurCourant().getEvenementParticipe().put(eve.getNom(),eve);
                            eve.addParticipant(data.getUtilisateurCourant());

                            try {
                                Persisteur.sauverEtat(data);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                afficherEventsJoin();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (ClassNotFoundException ex) {
                                ex.printStackTrace();
                            }

                        }
                    });


                    //Création de la gridPane
                    VBox vBox1 = new VBox(myEventNameLabel, myDescEventLabel, myPlaceLabel, btnJoinEvents);
                    VBox vBox0 = new VBox();
                    VBox vBox2 = new VBox(myLocalDateStart, myLocalDateEnd, myNumberLabel);
                    HBox hbox = new HBox(vBox0, vBox1, vBox2);
                    HBox hBox0 = new HBox(hbox);

                    vBox0.getChildren().add(imageView);
                    vBox0.setAlignment(Pos.CENTER);
                    vBox0.setPadding(new Insets(10, 10, 10, 10));
                    VBox.setMargin(myEventNameLabel,new Insets(25, 0, 0, 0));

                    vBox2.setAlignment(Pos.CENTER);

                    hBox0.setPrefSize(975, 720);
                    hbox.setMinWidth(700);
                    hbox.setMinHeight(200);
                    hbox.setMaxWidth(200);
                    hbox.setMaxHeight(200);
                    hbox.setPrefSize(700, 200);
                    hbox.setBorder(Border.stroke(Color.BLACK));
                    hbox.setBackground(Background.fill(Color.WHITE));
                    vBox2.setSpacing(15);

                    hBox0.setStyle("-fx-background-color: #00B4D8;");

                    hBox0.setAlignment(Pos.CENTER);
                    HBox.setMargin(hbox, new Insets(60, 0, 20, 0));
                    vBox1.setPrefSize(500, 200);
                    vBox2.setPrefSize(200, 200);

                    vBoxPrincipale.getChildren().add(hBox0);

                    scroller.setFitToHeight(true);
                    scroller.setContent(vBoxPrincipale);
                    scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                    scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

                    vBox2.setAlignment(Pos.CENTER);
                    myEventNameLabel.setStyle("-fx-font: 24 arial;");
                    myEventNameLabel.setWrapText(true);
                    myEventNameLabel.setPadding(new Insets(10));

                    myDescEventLabel.setStyle("-fx-font: 14 arial;");
                    myDescEventLabel.setWrapText(true);
                    myDescEventLabel.setPadding(new Insets(10));

                    myPlaceLabel.setStyle("-fx-font: 14 arial;");
                    myPlaceLabel.setWrapText(true);
                    myPlaceLabel.setPadding(new Insets(5));

                    myNumberLabel.setStyle("-fx-font: 18 arial;");
                    myNumberLabel.setWrapText(true);
                    myNumberLabel.setPadding(new Insets(5));

                    myLocalDateStart.setStyle("-fx-font: 18 arial;");
                    myLocalDateStart.setWrapText(true);
                    myLocalDateStart.setPadding(new Insets(5));

                    myLocalDateEnd.setStyle("-fx-font: 18 arial;");
                    myLocalDateEnd.setWrapText(true);
                    myLocalDateEnd.setPadding(new Insets(5));

                }


            }
        }

        if (!entreeBoucle) {
            vBoxPrincipale.setPrefSize(975, 720);
            l.setText("Aucun événement n'est disponible pour le moment.");
            vBoxPrincipale.getChildren().add(l);
            l.setStyle("-fx-font: 28 arial;");
            vBoxPrincipale.setAlignment(Pos.CENTER);
            l.setPadding(new Insets(10));
            hboxEvents.getChildren().remove(scroller);
        }
        Persisteur.sauverEtat(data);

    }

    /**
     * méthode d'affichage des événements propres à l'utilisateur courant
     * dans la rubrique "mes événements"
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @FXML private void afficherMyEvent() throws IOException, ClassNotFoundException {

        GenEvent data = Persisteur.lireEtat();

        boolean entreeBoucle = false;

        VBox vBoxPrincipale = new VBox();
        VBox vBoxPrincipaleMAIN = new VBox();
        ScrollPane scroller = new ScrollPane(vBoxPrincipaleMAIN);
        vBoxPrincipaleMAIN.getChildren().add(vBoxPrincipale);
        hboxEvents.getChildren().add(vBoxPrincipale);
        hboxEvents.getChildren().add(scroller);
        int x = 0;
        Label l = new Label();

        for (Evenement eve : data.getUtilisateurCourant().getEvenementsAdministres().values()) {
            entreeBoucle = true;

            File file;
            if (x == 0) {
                file = new File("src/main/resources/fr/uga/iut2/genevent/vue/icons/mouette-bleue.png");
                x += 1;
            } else if (x == 1) {
                file = new File("src/main/resources/fr/uga/iut2/genevent/vue/icons/mouette-rosée.png");
                x += 1;
            } else {
                file = new File("src/main/resources/fr/uga/iut2/genevent/vue/icons/mouette-verte.png");
                x = 0;
            }

            String localUrl = file.toURI().toURL().toString();
            Image img = new Image(localUrl);
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(120);
            imageView.setFitHeight(120);

            Label myLocalDateEnd = new Label();
            Label myEventNameLabel = new Label();
            Label myLocalDateStart = new Label();
            Label myPlaceLabel = new Label();
            Label myNumberLabel = new Label();
            Label myDescEventLabel = new Label();

            //Assignements des labels
            myLocalDateStart.setText(eve.getDateDebut().toString());
            myLocalDateEnd.setText(eve.getDateFin().toString());
            myPlaceLabel.setText(eve.getLieu());
            myEventNameLabel.setText(eve.getNom());
            myDescEventLabel.setText(eve.getDescriptionEvent());
            myNumberLabel.setText(eve.getParticipants().size() + "/" + eve.getNombreMax());

            //Création de la gridPane
            VBox vBox1 = new VBox(myEventNameLabel, myDescEventLabel, myPlaceLabel);
            VBox vBox0 = new VBox();
            VBox vBox2 = new VBox(myLocalDateStart, myLocalDateEnd, myNumberLabel);
            HBox hbox = new HBox(vBox0, vBox1, vBox2);
            HBox hBox0 = new HBox(hbox);

            vBox0.getChildren().add(imageView);
            vBox0.setAlignment(Pos.CENTER);
            vBox0.setPadding(new Insets(10, 10, 10, 10));
            VBox.setMargin(myEventNameLabel,new Insets(25, 0, 0, 0));
//            hbox.getChildren().add(imageView);

            vBox2.setAlignment(Pos.CENTER);

            hBox0.setPrefSize(975, 720);
            hbox.setMinWidth(700);
            hbox.setMinHeight(200);
            hbox.setMaxWidth(200);
            hbox.setMaxHeight(200);
            hbox.setPrefSize(700, 200);
            hbox.setBorder(Border.stroke(Color.BLACK));
            hbox.setBackground(Background.fill(Color.WHITE));
            vBox2.setSpacing(15);

            hBox0.setStyle("-fx-background-color: #00B4D8;");

            hBox0.setAlignment(Pos.CENTER);
            HBox.setMargin(hbox, new Insets(60, 0, 20, 0));
            vBox1.setPrefSize(500, 200);
            vBox2.setPrefSize(200, 200);

            vBoxPrincipale.getChildren().add(hBox0);

            scroller.setFitToHeight(true);
            scroller.setContent(vBoxPrincipale);
            scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            vBox2.setAlignment(Pos.CENTER);
            myEventNameLabel.setStyle("-fx-font: 24 arial;");
            myEventNameLabel.setWrapText(true);
            myEventNameLabel.setPadding(new Insets(10));

            myDescEventLabel.setStyle("-fx-font: 14 arial;");
            myDescEventLabel.setWrapText(true);
            myDescEventLabel.setPadding(new Insets(10));

            myPlaceLabel.setStyle("-fx-font: 14 arial;");
            myPlaceLabel.setWrapText(true);
            myPlaceLabel.setPadding(new Insets(5));

            myNumberLabel.setStyle("-fx-font: 18 arial;");
            myNumberLabel.setWrapText(true);
            myNumberLabel.setPadding(new Insets(5));

            myLocalDateStart.setStyle("-fx-font: 18 arial;");
            myLocalDateStart.setWrapText(true);
            myLocalDateStart.setPadding(new Insets(5));

            myLocalDateEnd.setStyle("-fx-font: 18 arial;");
            myLocalDateEnd.setWrapText(true);
            myLocalDateEnd.setPadding(new Insets(5));
        }

        if (!entreeBoucle) {
            vBoxPrincipale.setPrefSize(975, 720);
            l.setText("Vous n'avez pas encore créé d'événement.");
            vBoxPrincipale.getChildren().add(l);
            l.setStyle("-fx-font: 28 arial;");
            vBoxPrincipale.setAlignment(Pos.CENTER);
            l.setPadding(new Insets(10));
            hboxEvents.getChildren().remove(scroller);
        }
    }

    /**
     * méthode d'affichage des événements auxquels l'utilisateur courant participe dans la rubrique "Participation"
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @FXML private void afficherEventsJoin() throws IOException, ClassNotFoundException {


        GenEvent data = Persisteur.lireEtat();
        boolean entreeBoucle = false;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Attending-to.fxml"));
        fxmlLoader.setController(this);
        Stage fenetre = (Stage) btnAttendingTo.getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        fenetre.setScene(scene);
        fenetre.show();
        fenetre.setResizable(false);


        VBox vBoxPrincipale = new VBox();
        VBox vBoxPrincipaleMAIN = new VBox();
        ScrollPane scroller = new ScrollPane(vBoxPrincipaleMAIN);
        vBoxPrincipaleMAIN.getChildren().add(vBoxPrincipale);
        hboxEvents.getChildren().add(vBoxPrincipale);
        hboxEvents.getChildren().add(scroller);
        int x = 0;
        Label l = new Label();



            for (Evenement eve : data.getUtilisateurCourant().getEvenementParticipe().values()) {
                entreeBoucle = true;

                if (eve.getSponsorList().contains(data.getUtilisateurCourant())) {
                    entreeBoucle = false;

                } else {
                    File file;
                    if (x == 0) {
                        file = new File("src/main/resources/fr/uga/iut2/genevent/vue/icons/mouette-bleue.png");
                        x += 1;
                    } else if (x == 1) {
                        file = new File("src/main/resources/fr/uga/iut2/genevent/vue/icons/mouette-rosée.png");
                        x += 1;
                    } else {
                        file = new File("src/main/resources/fr/uga/iut2/genevent/vue/icons/mouette-verte.png");
                        x = 0;
                    }


                    String localUrl = file.toURI().toURL().toString();
                    Image img = new Image(localUrl);
                    ImageView imageView = new ImageView(img);
                    imageView.setFitWidth(120);
                    imageView.setFitHeight(120);


                    Label myLocalDateEnd = new Label();
                    Label myEventNameLabel = new Label();
                    Label myLocalDateStart = new Label();
                    Label myPlaceLabel = new Label();
                    Label myNumberLabel = new Label();
                    Label myDescEventLabel = new Label();
                    btnSponsoEvents = new Button();
                    btnSponsoEvents.setText("Sponsoriser");
                    sommeSponso = new TextField();





                    //Assignements des labels
                    myLocalDateStart.setText(eve.getDateDebut().toString());
                    myLocalDateEnd.setText(eve.getDateFin().toString());
                    myPlaceLabel.setText(eve.getLieu());
                    myEventNameLabel.setText(eve.getNom());
                    myDescEventLabel.setText(eve.getDescriptionEvent());
                    myNumberLabel.setText(eve.getParticipants().size() + "/" + eve.getNombreMax());


                    if (sommeSponso.getText().length()==0){
                        sommeSponso.setText(" 0 ");
                    }

                       btnSponsoEvents.setOnAction(e -> {

                            data.getUtilisateurCourant().getEvenementSponsorise().put(eve.getNom(), eve);
                            eve.addSponsor(data.getUtilisateurCourant());

                            try {
                                Persisteur.sauverEtat(data);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                afficherEventsSponso();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (ClassNotFoundException ex) {
                                ex.printStackTrace();
                            }
                        });





                    //Création de la gridPane
                    VBox vBox1 = new VBox(myEventNameLabel, myDescEventLabel, myPlaceLabel, sommeSponso);
                    VBox vBox0 = new VBox();
                    VBox vBox2 = new VBox(myLocalDateStart, myLocalDateEnd, myNumberLabel, btnSponsoEvents);
                    HBox hbox = new HBox(vBox0, vBox1, vBox2);
                    HBox hBox0 = new HBox(hbox);

                    vBox0.getChildren().add(imageView);
                    vBox0.setAlignment(Pos.CENTER);
                    vBox0.setPadding(new Insets(10, 10, 10, 10));
                    VBox.setMargin(myEventNameLabel, new Insets(25, 0, 0, 0));
//            hbox.getChildren().add(imageView);

                    vBox2.setAlignment(Pos.CENTER);

                    hBox0.setPrefSize(975, 720);
                    hbox.setMinWidth(700);
                    hbox.setMinHeight(200);
                    hbox.setMaxWidth(200);
                    hbox.setMaxHeight(200);
                    hbox.setPrefSize(700, 200);
                    hbox.setBorder(Border.stroke(Color.BLACK));
                    hbox.setBackground(Background.fill(Color.WHITE));
                    vBox2.setSpacing(15);

                    hBox0.setStyle("-fx-background-color: #00B4D8;");

                    hBox0.setAlignment(Pos.CENTER);
                    HBox.setMargin(hbox, new Insets(60, 0, 20, 0));
                    vBox1.setPrefSize(500, 200);
                    vBox2.setPrefSize(200, 200);

                    vBoxPrincipale.getChildren().add(hBox0);

                scroller.setFitToHeight(true);
                scroller.setContent(vBoxPrincipale);
                scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

                    vBox2.setAlignment(Pos.CENTER);
                    myEventNameLabel.setStyle("-fx-font: 24 arial;");
                    myEventNameLabel.setWrapText(true);
                    myEventNameLabel.setPadding(new Insets(10));

                    myDescEventLabel.setStyle("-fx-font: 14 arial;");
                    myDescEventLabel.setWrapText(true);
                    myDescEventLabel.setPadding(new Insets(10));

                    myPlaceLabel.setStyle("-fx-font: 14 arial;");
                    myPlaceLabel.setWrapText(true);
                    myPlaceLabel.setPadding(new Insets(5));

                myNumberLabel.setStyle("-fx-font: 16 arial;");
                myNumberLabel.setWrapText(true);
                myNumberLabel.setPadding(new Insets(5));

                myLocalDateStart.setStyle("-fx-font: 16 arial;");
                myLocalDateStart.setWrapText(true);
                myLocalDateStart.setPadding(new Insets(5));

                myLocalDateEnd.setStyle("-fx-font: 16 arial;");
                myLocalDateEnd.setWrapText(true);
                myLocalDateEnd.setPadding(new Insets(5));


                }
            }


       if (!entreeBoucle){
            vBoxPrincipale.setPrefSize(975, 720);
            l.setText("Vous ne participez a aucun événement.");
            vBoxPrincipale.getChildren().add(l);
            l.setStyle("-fx-font: 28 arial;");
            vBoxPrincipale.setAlignment(Pos.CENTER);
            l.setPadding(new Insets(10));
            hboxEvents.getChildren().remove(scroller);
        }

        if (loginEmailtf.getLength() == 0) {
            loginConnecte.setText(newUserEmailTextField.getText());
        } else {
            loginConnecte.setText(loginEmailtf.getText());
        }
        Persisteur.sauverEtat(data);


    }


    @FXML private void afficherEventsSponso() throws IOException,ClassNotFoundException{

        GenEvent data = Persisteur.lireEtat();
        boolean entreeBoucle = false;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Sponsoring.fxml"));
        fxmlLoader.setController(this);
        Stage fenetre = (Stage) btnSponsoring.getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        fenetre.setScene(scene);
        fenetre.show();
        fenetre.setResizable(false);


        VBox vBoxPrincipale = new VBox();
        VBox vBoxPrincipaleMAIN = new VBox();
        ScrollPane scroller = new ScrollPane(vBoxPrincipaleMAIN);
        vBoxPrincipaleMAIN.getChildren().add(vBoxPrincipale);
        hboxEvents.getChildren().add(vBoxPrincipale);
        hboxEvents.getChildren().add(scroller);
        int x = 0;
        Label l = new Label();



        for (Evenement eve : data.getUtilisateurCourant().getEvenementSponsorise().values()) {
            entreeBoucle=true;




            File file;
            if (x == 0) {
                file = new File("src/main/resources/fr/uga/iut2/genevent/vue/icons/mouette-bleue.png");
                x += 1;
            } else if (x == 1) {
                file = new File("src/main/resources/fr/uga/iut2/genevent/vue/icons/mouette-rosée.png");
                x += 1;
            } else {
                file = new File("src/main/resources/fr/uga/iut2/genevent/vue/icons/mouette-verte.png");
                x = 0;
            }


            String localUrl = file.toURI().toURL().toString();
            Image img = new Image(localUrl);
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(120);
            imageView.setFitHeight(120);


            Label myLocalDateEnd = new Label();
            Label myEventNameLabel = new Label();
            Label myLocalDateStart = new Label();
            Label myPlaceLabel = new Label();
            Label myNumberLabel = new Label();
            Label myDescEventLabel = new Label();
            Label sommeAvant = new Label();


            //Assignements des labels
            myLocalDateStart.setText(eve.getDateDebut().toString());
            myLocalDateEnd.setText(eve.getDateFin().toString());
            myPlaceLabel.setText(eve.getLieu());
            myEventNameLabel.setText(eve.getNom());
            myDescEventLabel.setText(eve.getDescriptionEvent());
            myNumberLabel.setText(eve.getParticipants().size() + "/" + eve.getNombreMax());
            if (sommeSponso.getText().length()==0){
                sommeAvant.setText("Somme d'argent investi : "+sommeSponso.getText() + "€");
            }else{
                sommeAvant.setText("Somme d'argent investi : "+sommeSponso.getText() + "€");
            }

            //Création de la gridPane
            VBox vBox1 = new VBox(myEventNameLabel, myDescEventLabel, myPlaceLabel,sommeAvant);
            VBox vBox0 = new VBox();
            VBox vBox2 = new VBox(myLocalDateStart, myLocalDateEnd, myNumberLabel);
            HBox hbox = new HBox(vBox0, vBox1, vBox2);
            HBox hBox0 = new HBox(hbox);

            vBox0.getChildren().add(imageView);
            vBox0.setAlignment(Pos.CENTER);
            vBox0.setPadding(new Insets(10, 10, 10, 10));
            VBox.setMargin(myEventNameLabel,new Insets(25, 0, 0, 0));
//            hbox.getChildren().add(imageView);

            vBox2.setAlignment(Pos.CENTER);

            hBox0.setPrefSize(975, 720);
            hbox.setMinWidth(700);
            hbox.setMinHeight(200);
            hbox.setMaxWidth(200);
            hbox.setMaxHeight(200);
            hbox.setPrefSize(700, 200);
            hbox.setBorder(Border.stroke(Color.BLACK));
            hbox.setBackground(Background.fill(Color.WHITE));
            vBox2.setSpacing(15);

            hBox0.setStyle("-fx-background-color: #00B4D8;");

            hBox0.setAlignment(Pos.CENTER);
            HBox.setMargin(hbox, new Insets(60, 0, 20, 0));
            vBox1.setPrefSize(500, 200);
            vBox2.setPrefSize(200, 200);

            vBoxPrincipale.getChildren().add(hBox0);

            scroller.setFitToHeight(true);
            scroller.setContent(vBoxPrincipale);
            scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            vBox2.setAlignment(Pos.CENTER_RIGHT);
            myEventNameLabel.setStyle("-fx-font: 24 arial;");
            myEventNameLabel.setWrapText(true);
            myEventNameLabel.setPadding(new Insets(10));

            myDescEventLabel.setStyle("-fx-font: 14 arial;");
            myDescEventLabel.setWrapText(true);
            myDescEventLabel.setPadding(new Insets(10));

            myPlaceLabel.setStyle("-fx-font: 14 arial;");
            myPlaceLabel.setWrapText(true);
            myPlaceLabel.setPadding(new Insets(5));

            myNumberLabel.setStyle("-fx-font: 18 arial;");
            myNumberLabel.setWrapText(true);
            myNumberLabel.setPadding(new Insets(5));

            myLocalDateStart.setStyle("-fx-font: 18 arial;");
            myLocalDateStart.setWrapText(true);
            myLocalDateStart.setPadding(new Insets(5));

            myLocalDateEnd.setStyle("-fx-font: 18 arial;");
            myLocalDateEnd.setWrapText(true);
            myLocalDateEnd.setPadding(new Insets(5));

            sommeAvant.setStyle("-fx-font: 18 arial;");
            sommeAvant.setWrapText(true);
            sommeAvant.setPadding(new Insets(5));
        }

        if (!entreeBoucle){
            vBoxPrincipale.setPrefSize(975, 720);
            l.setText("Vous ne sponsorisez aucun événement.");
            vBoxPrincipale.getChildren().add(l);
            l.setStyle("-fx-font: 28 arial;");
            vBoxPrincipale.setAlignment(Pos.CENTER);
            l.setPadding(new Insets(10));
            hboxEvents.getChildren().remove(scroller);
        }

        if (loginEmailtf.getLength() == 0) {
            loginConnecte.setText(newUserEmailTextField.getText());
        } else {
            loginConnecte.setText(loginEmailtf.getText());
        }
        Persisteur.sauverEtat(data);

    }







    /**
     * méthode qui onvre la fenêtre des conditions d'utilisation
     *
     * @throws IOException
     */
    @FXML
    private void afficherConditionsUtilisations() throws IOException {

        FXMLLoader newUserViewLoader = new FXMLLoader(getClass().getResource("privacy.fxml"));
        newUserViewLoader.setController(this);
        Scene newUserScene = new Scene(newUserViewLoader.load());

        Stage newUserWindow = new Stage();
        newUserWindow.setTitle("Conditions d'utilisations");
        newUserWindow.initModality(Modality.APPLICATION_MODAL);
        newUserWindow.setScene(newUserScene);
        newUserWindow.show();
        newUserWindow.setFullScreen(true);          //Ne surtout pas enlever (jvous expliquerai)
        newUserWindow.setFullScreen(false);
        newUserWindow.setResizable(false);

    }

    /**
     * Méthode bouton accepter les conditions d'utilisation qui ferme la fenêtre de conditions d'utilisation
     */
    @FXML
    private void onAcceptCondition() {
        Stage stg = (Stage) btnAcceptCondition.getScene().getWindow();
        stg.close();

    }

}



