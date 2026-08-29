package com.gestionstock.controller;

import com.gestionstock.model.Utilisateur;
import com.gestionstock.service.UtilisateurService;
import com.gestionstock.service.UtilisateurServiceImpl;
import com.gestionstock.util.SessionUtilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML
    TextField champEmail;

    @FXML
    PasswordField champMotDePasse;

    @FXML
    Label labelErreur;

    private final UtilisateurService utilisateurService = new UtilisateurServiceImpl();

    @FXML
    void handleConnexion(ActionEvent event) {
        String email = champEmail.getText();
        String motDePasse = champMotDePasse.getText();

        if (email.isBlank() || motDePasse.isBlank()) {
            afficherErreur("Veuillez remplir tous les champs");
            return;
        }

        Optional<Utilisateur> utilisateurOptional = utilisateurService.authentifier(email, motDePasse);

        if (utilisateurOptional.isEmpty()) {
            afficherErreur("Email ou mot de passe incorrect, ou compte désactivé");
            return;
        }

        SessionUtilisateur.connecter(utilisateurOptional.get());
        ouvrirMenuPrincipal(event);
    }

    private void afficherErreur(String message) {
        labelErreur.setText(message);
        labelErreur.setVisible(true);
    }

    private void ouvrirMenuPrincipal(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestionstock/main.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/com/gestionstock/style.css").toExternalForm()
            );
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur lors du chargement du menu principal");
        }
    }
}