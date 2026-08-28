package com.gestionstock.controller;

import com.gestionstock.model.Fournisseur;
import com.gestionstock.service.FournisseurService;
import com.gestionstock.service.FournisseurServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class FournisseurController {

    @FXML
    TableView<Fournisseur> tableFournisseurs;
    @FXML
    TableColumn<Fournisseur, String> colonneNom;
    @FXML
    TableColumn<Fournisseur, String> colonneEmail;
    @FXML
    TableColumn<Fournisseur, String> colonneTel;
    @FXML
    TableColumn<Fournisseur, Long> colonneNbProduits;
    @FXML
    TextField champRecherche;

    private final FournisseurService fournisseurService = new FournisseurServiceImpl();
    private ObservableList<Fournisseur> listeFournisseurs;

    @FXML
    public void initialize() {
        colonneNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colonneEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colonneTel.setCellValueFactory(new PropertyValueFactory<>("tel"));
        colonneNbProduits.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        fournisseurService.countProduitsByFournisseur(cellData.getValue().getId())
                )
        );

        chargerDonnees();
    }

    private void chargerDonnees() {
        listeFournisseurs = FXCollections.observableArrayList(fournisseurService.findAllFournisseurs());
        tableFournisseurs.setItems(listeFournisseurs);
    }

    @FXML
    void rechercherFournisseurs() {
        String texte = champRecherche.getText().toLowerCase();
        ObservableList<Fournisseur> filtres = FXCollections.observableArrayList();
        for (Fournisseur f : fournisseurService.findAllFournisseurs()) {
            if (f.getNom().toLowerCase().contains(texte)) {
                filtres.add(f);
            }
        }
        tableFournisseurs.setItems(filtres);
    }

    @FXML
    void handleAjouter(ActionEvent event) {
        System.out.println("Ajouter fournisseur - formulaire à brancher plus tard");
    }

    @FXML
    void handleModifier(ActionEvent event) {
        System.out.println("Modifier fournisseur - formulaire à brancher plus tard");
    }

    @FXML
    void supprimerFournisseur(ActionEvent event) {
        Fournisseur selection = tableFournisseurs.getSelectionModel().getSelectedItem();

        if (selection == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez un fournisseur à supprimer.");
            return;
        }

        try {
            fournisseurService.deleteFournisseur(selection.getId());
            chargerDonnees();
        } catch (IllegalStateException e) {
            afficherAlerte(Alert.AlertType.WARNING, "Suppression impossible", e.getMessage());
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la suppression.");
        }
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}