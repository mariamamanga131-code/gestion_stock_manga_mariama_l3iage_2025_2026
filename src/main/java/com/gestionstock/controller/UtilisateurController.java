package com.gestionstock.controller;

import com.gestionstock.model.Utilisateur;
import com.gestionstock.service.UtilisateurService;
import com.gestionstock.service.UtilisateurServiceImpl;
import com.gestionstock.util.SessionUtilisateur;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class UtilisateurController {

    @FXML
    TableView<Utilisateur> tableUtilisateurs;
    @FXML
    TableColumn<Utilisateur, String> colonneNom;
    @FXML
    TableColumn<Utilisateur, String> colonneEmail;
    @FXML
    TableColumn<Utilisateur, String> colonneRole;
    @FXML
    TableColumn<Utilisateur, String> colonneStatut;

    private final UtilisateurService utilisateurService = new UtilisateurServiceImpl();

    @FXML
    public void initialize() {
        if (!SessionUtilisateur.estAdmin()) {
            Alert alerte = new Alert(Alert.AlertType.ERROR);
            alerte.setTitle("Accès refusé");
            alerte.setContentText("Seul un administrateur peut accéder à la gestion des comptes.");
            alerte.showAndWait();
            return;
        }

        colonneNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colonneEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colonneRole.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRole().toString())
        );
        colonneStatut.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().isActif() ? "Actif" : "Inactif")
        );

        chargerDonnees();
    }

    private void chargerDonnees() {
        tableUtilisateurs.setItems(FXCollections.observableArrayList(utilisateurService.findAll()));
    }

    @FXML
    void handleActiver() {
        changerStatut(true);
    }

    @FXML
    void handleDesactiver() {
        changerStatut(false);
    }

    private void changerStatut(boolean actif) {
        Utilisateur selection = tableUtilisateurs.getSelectionModel().getSelectedItem();

        if (selection == null) {
            Alert alerte = new Alert(Alert.AlertType.WARNING);
            alerte.setTitle("Aucune sélection");
            alerte.setContentText("Sélectionnez un utilisateur.");
            alerte.showAndWait();
            return;
        }

        utilisateurService.setActif(selection.getId(), actif);
        chargerDonnees();
    }
}