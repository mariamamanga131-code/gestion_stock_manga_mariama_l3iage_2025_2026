package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.model.Fournisseur;
import com.gestionstock.model.Produit;
import com.gestionstock.service.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ProduitController {
    @FXML
    TableView<Produit> tableProduits;
    @FXML
    TableColumn<Produit, Integer> colonneNom;
    @FXML
    TableColumn<Produit, Double> colonnePrix;
    @FXML
    TableColumn<Produit, String> colonnePrixPromo;
    @FXML
    TableColumn<Produit, Integer> colonneStock;
    @FXML
    TableColumn<Produit, Integer> colonneStockMin;
    @FXML
    TableColumn<Produit, String> colonneCategorie;
    @FXML
    TableColumn<Produit, String> colonneFournisseur;
    @FXML
    TextField champRecherche;
    @FXML
    ComboBox<Categorie> comboFiltreCategorie;
    @FXML
    ComboBox<Fournisseur> comboFiltreFournisseur;
    @FXML
    CheckBox checkStockBas;
    @FXML
    Button boutonSupprimer;


    private final ProduitService produitService = new ProduitServiceImpl();
    private final CategorieService categorieService = new CategorieServiceImpl();
    private final FournisseurService fournisseurService = new FournisseurServiceImpl();

    // Liste complète chargée depuis la base, utilisée comme référence pour les filtres
    private ObservableList<Produit> listeProduits;

    @FXML
    public void initialize() {
        if (!com.gestionstock.util.SessionUtilisateur.estAdmin()) {
            boutonSupprimer.setVisible(false);
            boutonSupprimer.setManaged(false);
        }

        configurerColones();
        configurerFiltres();
        chargerDonnees();
    }

    private void configurerColones() {
        colonneNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colonnePrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colonnePrixPromo.setCellValueFactory(data -> {
            Double prixPromo = data.getValue().getPrixPromo();
            return new SimpleStringProperty(prixPromo != null ? String.valueOf(prixPromo) : "");
        });
        colonneStock.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colonneStockMin.setCellValueFactory(new PropertyValueFactory<>("quantiteMin"));
        colonneCategorie.setCellValueFactory(data -> {
            Categorie cat = data.getValue().getCategorie();
            return new SimpleStringProperty(cat != null ? cat.getNom() : "");
        });
        colonneFournisseur.setCellValueFactory(data -> {
            Fournisseur fournisseur = data.getValue().getFournisseur();
            return new SimpleStringProperty(fournisseur != null ? fournisseur.getNom() : "");
        });
    }

    private void configurerFiltres() {
        comboFiltreCategorie.setItems(FXCollections.observableArrayList(categorieService.findAllCategories()));
        comboFiltreCategorie.setCellFactory(liste -> new ListCell<>() {
            @Override
            protected void updateItem(Categorie c, boolean vide) {
                super.updateItem(c, vide);
                setText(vide || c == null ? "" : c.getNom());
            }
        });
        comboFiltreCategorie.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Categorie c, boolean vide) {
                super.updateItem(c, vide);
                setText(vide || c == null ? "" : c.getNom());
            }
        });

        comboFiltreFournisseur.setItems(FXCollections.observableArrayList(fournisseurService.findAllFournisseurs()));
        comboFiltreFournisseur.setCellFactory(liste -> new ListCell<>() {
            @Override
            protected void updateItem(Fournisseur f, boolean vide) {
                super.updateItem(f, vide);
                setText(vide || f == null ? "" : f.getNom());
            }
        });
        comboFiltreFournisseur.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Fournisseur f, boolean vide) {
                super.updateItem(f, vide);
                setText(vide || f == null ? "" : f.getNom());
            }
        });
    }

    private void chargerDonnees() {
        List<Produit> produits = produitService.findAllProduits();
        listeProduits = FXCollections.observableArrayList(produits);
        appliquerFiltres();
    }

    @FXML
    void appliquerFiltres() {
        String recherche = champRecherche.getText();
        String rechercheMinuscule = (recherche == null) ? "" : recherche.trim().toLowerCase();

        Categorie categorieFiltre = comboFiltreCategorie.getValue();
        Fournisseur fournisseurFiltre = comboFiltreFournisseur.getValue();
        boolean stockBasUniquement = checkStockBas.isSelected();

        ObservableList<Produit> resultats = listeProduits.filtered(produit -> {
            boolean correspondNom = rechercheMinuscule.isEmpty()
                    || (produit.getNom() != null && produit.getNom().toLowerCase().contains(rechercheMinuscule));

            boolean correspondCategorie = categorieFiltre == null
                    || (produit.getCategorie() != null && produit.getCategorie().getId() == categorieFiltre.getId());

            boolean correspondFournisseur = fournisseurFiltre == null
                    || (produit.getFournisseur() != null && produit.getFournisseur().getId() == fournisseurFiltre.getId());

            boolean correspondStockBas = !stockBasUniquement
                    || produit.getQuantiteStock() <= produit.getQuantiteMin();

            return correspondNom && correspondCategorie && correspondFournisseur && correspondStockBas;
        });

        tableProduits.setItems(resultats);
    }

    @FXML
    private void supprimerProduit() {
        if (!com.gestionstock.util.SessionUtilisateur.estAdmin()) {
            Alert alerteAcces = new Alert(Alert.AlertType.ERROR);
            alerteAcces.setContentText("Seul un administrateur peut supprimer un produit.");
            alerteAcces.showAndWait();
            return;
        }

        Produit produitSelectionne = tableProduits.getSelectionModel().getSelectedItem();

        Alert alerteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        alerteConfirmation.setTitle("Confirmation de suppression");
        alerteConfirmation.setHeaderText(null);
        alerteConfirmation.setContentText("Voulez-vous vraiment supprimer le produit \"" + produitSelectionne.getNom() + "\" ?");

        Optional<ButtonType> reponse = alerteConfirmation.showAndWait();

        if (reponse.isPresent() && reponse.get() == ButtonType.OK) {
            produitService.deleteProduit(produitSelectionne.getId());
            chargerDonnees();
        }
    }

    @FXML
    void handleAjouter() {
        ouvrirFormulaireProduit(null);
    }

    @FXML
    void handleModifier() {
        Produit selection = tableProduits.getSelectionModel().getSelectedItem();

        if (selection == null) {
            Alert alerte = new Alert(Alert.AlertType.WARNING);
            alerte.setTitle("Aucune sélection");
            alerte.setContentText("Sélectionnez un produit à modifier.");
            alerte.showAndWait();
            return;
        }

        ouvrirFormulaireProduit(selection);
    }

    private void ouvrirFormulaireProduit(Produit produitAModifier) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestionstock/AddProduitDialog.fxml")
            );
            Parent root = loader.load();

            AddProduitController controleurDialogue = loader.getController();
            controleurDialogue.setSurProduitEnregistre(this::chargerDonnees);

            if (produitAModifier != null) {
                controleurDialogue.preparerPourModification(produitAModifier);
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(produitAModifier == null ? "Nouveau produit" : "Modifier le produit");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}