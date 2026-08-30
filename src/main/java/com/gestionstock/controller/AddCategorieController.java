package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.service.CategorieService;
import com.gestionstock.service.CategorieServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddCategorieController {

    @FXML
    Label labelTitre;
    @FXML
    TextField champNom;
    @FXML
    TextField champDescription;
    @FXML
    Label labelErreur;

    private final CategorieService categorieService = new CategorieServiceImpl();

    private Runnable surCategorieEnregistree;
    private Categorie categorieEnEdition;

    public void setSurCategorieEnregistree(Runnable callback) {
        this.surCategorieEnregistree = callback;
    }

    public void preparerPourModification(Categorie categorie) {
        this.categorieEnEdition = categorie;
        labelTitre.setText("Modifier la catégorie");
        champNom.setText(categorie.getNom());
        champDescription.setText(categorie.getDescription());
    }

    @FXML
    void handleEnregistrer() {
        String nom = champNom.getText();
        if (nom == null || nom.trim().length() < 2) {
            afficherErreur("Le nom doit contenir au moins 2 caractères");
            return;
        }

        try {
            if (categorieEnEdition == null) {
                Categorie nouvelle = new Categorie(champDescription.getText(), nom.trim());
                categorieService.addCategorie(nouvelle);
            } else {
                categorieEnEdition.setNom(nom.trim());
                categorieEnEdition.setDescription(champDescription.getText());
                categorieService.updateCategorie(categorieEnEdition);
            }

            if (surCategorieEnregistree != null) {
                surCategorieEnregistree.run();
            }

            fermerFenetre();
        } catch (Exception e) {
            afficherErreur("Erreur lors de l'enregistrement : " + e.getMessage());
        }
    }

    @FXML
    void handleAnnuler() {
        fermerFenetre();
    }

    private void afficherErreur(String message) {
        labelErreur.setText(message);
        labelErreur.setVisible(true);
    }

    private void fermerFenetre() {
        Stage stage = (Stage) champNom.getScene().getWindow();
        stage.close();
    }
}