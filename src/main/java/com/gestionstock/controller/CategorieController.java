package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.service.CategorieService;
import com.gestionstock.service.CategorieServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CategorieController {

    @FXML
    TableView<Categorie> tableCategories;
    @FXML
    TableColumn<Categorie, String> colonneNom;
    @FXML
    TableColumn<Categorie, String> colonneDescription;
    @FXML
    TableColumn<Categorie, Integer> colonneNbProduits;
    @FXML
    TextField champRecherche;

    private final CategorieService categorieService = new CategorieServiceImpl();
    private ObservableList<Categorie> listeCategories;

    @FXML
    public void initialize() {
        colonneNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colonneDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colonneNbProduits.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(
                        cellData.getValue().getProduits().size()
                ).asObject()
        );

        chargerDonnees();
    }

    private void chargerDonnees() {
        listeCategories = FXCollections.observableArrayList(categorieService.findAllCategories());
        tableCategories.setItems(listeCategories);
    }

    @FXML
    void rechercherCategories() {
        String texte = champRecherche.getText().toLowerCase();
        ObservableList<Categorie> filtrees = FXCollections.observableArrayList();
        for (Categorie c : categorieService.findAllCategories()) {
            if (c.getNom().toLowerCase().contains(texte)) {
                filtrees.add(c);
            }
        }
        tableCategories.setItems(filtrees);
    }

    @FXML
    void handleAjouter(ActionEvent event) {
        System.out.println("Ajouter catégorie - formulaire à brancher plus tard");
    }

    @FXML
    void handleModifier(ActionEvent event) {
        System.out.println("Modifier catégorie - formulaire à brancher plus tard");
    }

    @FXML
    void supprimerCategorie(ActionEvent event) {
        Categorie selection = tableCategories.getSelectionModel().getSelectedItem();

        if (selection == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez une catégorie à supprimer.");
            return;
        }

        try {
            categorieService.deleteCategorie(selection.getId());
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