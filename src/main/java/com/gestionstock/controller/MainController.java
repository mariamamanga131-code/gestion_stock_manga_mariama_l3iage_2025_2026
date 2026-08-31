package com.gestionstock.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import com.gestionstock.util.SessionUtilisateur;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
/*
    -@FXML: Annotation qui connecte un attribut Java à un composant déclaré dans le fichier XML via son fx:id
    -initialize(): méthode spéciale appelée automatiquement par JavaFx après le chargement du FXML
 */
public class MainController {
    @FXML
    private StackPane contenuPrincipale;

    @FXML
    private javafx.scene.control.Button btnUtilisateurs;
    @FXML
    private Label labelUtilisateurConnecte;


    @FXML
    public void initialize() {
        com.gestionstock.model.Utilisateur utilisateur = SessionUtilisateur.getUtilisateurConnecte();
        if (utilisateur != null) {
            labelUtilisateurConnecte.setText("Connecté : " + utilisateur.getNom() + " (" + utilisateur.getRole() + ")");
        }

        if (!SessionUtilisateur.estAdmin()) {
            btnUtilisateurs.setVisible(false);
            btnUtilisateurs.setManaged(false);
        }

        afficherDashboard();
    }

    @FXML
    private void handleDeconnexion(javafx.event.ActionEvent event) {
        SessionUtilisateur.deconnecter();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionstock/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/gestionstock/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void afficherDashboard() {
        chargerVue("/com/gestionstock/dashboard.fxml");
    }

    @FXML
    private void afficherProduits() {
        chargerVue("/com/gestionstock/produits.fxml");
    }

    @FXML
    private void afficherCategories() {
        chargerVue("/com/gestionstock/categories.fxml");
    }

    @FXML
    private void afficherFournisseurs() {
        chargerVue("/com/gestionstock/fournisseurs.fxml");
    }

    @FXML
    private void afficherMouvements() { chargerVue("/com/gestionstock/mouvements.fxml");
    }
    @FXML
    private void afficherUtilisateurs() {chargerVue("/com/gestionstock/utilisateurs.fxml");
    }
    @FXML
    private void afficherStatistiques() {
        chargerVue("/com/gestionstock/statistiques.fxml");
    }


    private void chargerVue(String cheminFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(cheminFxml)
            );
            Node vue = loader.load();
            contenuPrincipale.getChildren().clear();
            contenuPrincipale.getChildren().add(vue);
        } catch (Exception e) {
            Alert alerte = new Alert(Alert.AlertType.ERROR);
            alerte.setTitle("Erreur de navigation");
            alerte.setHeaderText("Impossible de charger cet écran");
            alerte.setContentText("Détail : " + e.getMessage());
            alerte.showAndWait();
        }
    }
}
