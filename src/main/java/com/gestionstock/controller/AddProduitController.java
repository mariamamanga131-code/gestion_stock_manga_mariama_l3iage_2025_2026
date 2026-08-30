package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.model.Fournisseur;
import com.gestionstock.model.Produit;
import com.gestionstock.service.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddProduitController {

    @FXML
    Label labelTitre;
    @FXML
    TextField champNom;
    @FXML
    ComboBox<Categorie> comboCategorie;
    @FXML
    ComboBox<Fournisseur> comboFournisseur;
    @FXML
    TextField champPrix;
    @FXML
    TextField champPrixPromo;
    @FXML
    TextField champQuantiteStock;
    @FXML
    TextField champQuantiteMin;
    @FXML
    Label labelErreur;

    private final ProduitService produitService = new ProduitServiceImpl();
    private final CategorieService categorieService = new CategorieServiceImpl();
    private final FournisseurService fournisseurService = new FournisseurServiceImpl();

    private Runnable surProduitEnregistre;
    private Produit produitEnEdition;

    public void setSurProduitEnregistre(Runnable callback) {
        this.surProduitEnregistre = callback;
    }

    public void preparerPourModification(Produit produit) {
        this.produitEnEdition = produit;
        labelTitre.setText("Modifier le produit");

        champNom.setText(produit.getNom());
        comboCategorie.setValue(produit.getCategorie());
        comboFournisseur.setValue(produit.getFournisseur());
        champPrix.setText(String.valueOf(produit.getPrix()));
        champPrixPromo.setText(produit.getPrixPromo() != null ? String.valueOf(produit.getPrixPromo()) : "");
        champQuantiteStock.setText(String.valueOf(produit.getQuantiteStock()));
        champQuantiteMin.setText(String.valueOf(produit.getQuantiteMin()));
    }

    @FXML
    public void initialize() {
        comboCategorie.setItems(FXCollections.observableArrayList(categorieService.findAllCategories()));
        comboCategorie.setCellFactory(liste -> new ListCell<>() {
            @Override
            protected void updateItem(Categorie c, boolean vide) {
                super.updateItem(c, vide);
                setText(vide || c == null ? "" : c.getNom());
            }
        });
        comboCategorie.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Categorie c, boolean vide) {
                super.updateItem(c, vide);
                setText(vide || c == null ? "" : c.getNom());
            }
        });

        comboFournisseur.setItems(FXCollections.observableArrayList(fournisseurService.findAllFournisseurs()));
        comboFournisseur.setCellFactory(liste -> new ListCell<>() {
            @Override
            protected void updateItem(Fournisseur f, boolean vide) {
                super.updateItem(f, vide);
                setText(vide || f == null ? "" : f.getNom());
            }
        });
        comboFournisseur.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Fournisseur f, boolean vide) {
                super.updateItem(f, vide);
                setText(vide || f == null ? "" : f.getNom());
            }
        });
    }

    @FXML
    void handleEnregistrer() {
        String nom = champNom.getText();
        if (nom == null || nom.trim().length() < 2) {
            afficherErreur("Le nom doit contenir au moins 2 caractères");
            return;
        }

        double prix;
        try {
            prix = Double.parseDouble(champPrix.getText());
            if (prix <= 0) {
                afficherErreur("Le prix doit être strictement positif");
                return;
            }
        } catch (NumberFormatException e) {
            afficherErreur("Le prix doit être un nombre valide");
            return;
        }

        Double prixPromo = null;
        String texteProxPromo = champPrixPromo.getText();
        if (texteProxPromo != null && !texteProxPromo.isBlank()) {
            try {
                prixPromo = Double.parseDouble(texteProxPromo);
                if (prixPromo <= 0 || prixPromo >= prix) {
                    afficherErreur("Le prix promo doit être positif et strictement inférieur au prix normal");
                    return;
                }
            } catch (NumberFormatException e) {
                afficherErreur("Le prix promo doit être un nombre valide");
                return;
            }
        }

        int quantiteStock;
        int quantiteMin;
        try {
            quantiteStock = Integer.parseInt(champQuantiteStock.getText());
            quantiteMin = Integer.parseInt(champQuantiteMin.getText());
            if (quantiteStock < 0 || quantiteMin < 0) {
                afficherErreur("Les quantités doivent être des entiers positifs ou nuls");
                return;
            }
        } catch (NumberFormatException e) {
            afficherErreur("Les quantités doivent être des nombres entiers valides");
            return;
        }

        Categorie categorie = comboCategorie.getValue();
        Fournisseur fournisseur = comboFournisseur.getValue();
        if (categorie == null || fournisseur == null) {
            afficherErreur("Veuillez sélectionner une catégorie et un fournisseur");
            return;
        }

        try {
            if (produitEnEdition == null) {
                Produit nouveauProduit = new Produit(nom.trim(), quantiteStock, quantiteMin, prix, categorie, fournisseur);
                nouveauProduit.setPrixPromo(prixPromo);
                produitService.addProduit(nouveauProduit);
            } else {
                produitEnEdition.setNom(nom.trim());
                produitEnEdition.setPrix(prix);
                produitEnEdition.setPrixPromo(prixPromo);
                produitEnEdition.setQuantiteStock(quantiteStock);
                produitEnEdition.setQuantiteMin(quantiteMin);
                produitEnEdition.setCategorie(categorie);
                produitEnEdition.setFournisseur(fournisseur);
                produitService.updateProduit(produitEnEdition);
            }

            if (surProduitEnregistre != null) {
                surProduitEnregistre.run();
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