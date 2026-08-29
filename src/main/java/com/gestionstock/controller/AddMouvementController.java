package com.gestionstock.controller;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.Produit;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.service.MouvementService;
import com.gestionstock.service.MouvementServiceImpl;
import com.gestionstock.service.ProduitService;
import com.gestionstock.service.ProduitServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddMouvementController {

    @FXML
    ComboBox<Produit> comboProduit;
    @FXML
    RadioButton radioEntree;
    @FXML
    RadioButton radioSortie;
    @FXML
    TextField champQuantite;
    @FXML
    TextField champMotif;
    @FXML
    Label labelApercu;
    @FXML
    Label labelErreur;

    private final ProduitService produitService = new ProduitServiceImpl();
    private final MouvementService mouvementService = new MouvementServiceImpl();

    private Runnable surMouvementAjoute;

    public void setSurMouvementAjoute(Runnable callback) {
        this.surMouvementAjoute = callback;
    }

    @FXML
    public void initialize() {
        comboProduit.setItems(FXCollections.observableArrayList(produitService.findAllProduits()));

        comboProduit.setCellFactory(liste -> new ListCell<>() {
            @Override
            protected void updateItem(Produit produit, boolean vide) {
                super.updateItem(produit, vide);
                setText(vide || produit == null ? "" : produit.getNom());
            }
        });
        comboProduit.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Produit produit, boolean vide) {
                super.updateItem(produit, vide);
                setText(vide || produit == null ? "" : produit.getNom());
            }
        });

        radioEntree.setSelected(true);
    }

    @FXML
    void mettreAJourApercu() {
        Produit produit = comboProduit.getValue();
        String texteQuantite = champQuantite.getText();

        if (produit == null || texteQuantite.isBlank()) {
            labelApercu.setText("");
            return;
        }

        try {
            int quantite = Integer.parseInt(texteQuantite);
            int stockActuel = produit.getQuantiteStock();
            int stockResultant = radioEntree.isSelected()
                    ? stockActuel + quantite
                    : stockActuel - quantite;

            labelApercu.setText("Stock actuel : " + stockActuel + "  →  Stock après opération : " + stockResultant);
        } catch (NumberFormatException e) {
            labelApercu.setText("");
        }
    }

    @FXML
    void handleEnregistrer() {
        Produit produit = comboProduit.getValue();

        if (produit == null) {
            afficherErreur("Veuillez sélectionner un produit");
            return;
        }

        int quantite;
        try {
            quantite = Integer.parseInt(champQuantite.getText());
        } catch (NumberFormatException e) {
            afficherErreur("La quantité doit être un nombre entier");
            return;
        }

        TypeMouvement type = radioEntree.isSelected() ? TypeMouvement.ENTRE : TypeMouvement.SORTIE;
        String motif = champMotif.getText();

        Mouvement mouvement = new Mouvement(produit, type, quantite, motif, null);

        try {
            mouvementService.addMouvement(mouvement);

            if (surMouvementAjoute != null) {
                surMouvementAjoute.run();
            }

            fermerFenetre();
        } catch (IllegalArgumentException | IllegalStateException e) {
            afficherErreur(e.getMessage());
        } catch (Exception e) {
            afficherErreur("Une erreur est survenue lors de l'enregistrement");
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
        Stage stage = (Stage) champQuantite.getScene().getWindow();
        stage.close();
    }
}