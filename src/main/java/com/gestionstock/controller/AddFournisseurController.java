package com.gestionstock.controller;

import com.gestionstock.model.Fournisseur;
import com.gestionstock.service.FournisseurService;
import com.gestionstock.service.FournisseurServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class AddFournisseurController {

    @FXML
    Label labelTitre;
    @FXML
    TextField champNom;
    @FXML
    TextField champEmail;
    @FXML
    TextField champTel;
    @FXML
    Label labelErreur;

    private final FournisseurService fournisseurService = new FournisseurServiceImpl();

    private Runnable surFournisseurEnregistre;
    private Fournisseur fournisseurEnEdition;

    private static final Pattern PATTERN_EMAIL =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PATTERN_TEL =
            Pattern.compile("^(77|78|75|76|70)\\d{7}$");

    public void setSurFournisseurEnregistre(Runnable callback) {
        this.surFournisseurEnregistre = callback;
    }

    public void preparerPourModification(Fournisseur fournisseur) {
        this.fournisseurEnEdition = fournisseur;
        labelTitre.setText("Modifier le fournisseur");
        champNom.setText(fournisseur.getNom());
        champEmail.setText(fournisseur.getEmail());
        champTel.setText(fournisseur.getTel());
    }

    @FXML
    void handleEnregistrer() {
        String nom = champNom.getText();
        if (nom == null || nom.trim().length() < 2) {
            afficherErreur("Le nom doit contenir au moins 2 caractères");
            return;
        }

        String email = champEmail.getText();
        if (email != null && !email.isBlank() && !PATTERN_EMAIL.matcher(email).matches()) {
            afficherErreur("Le format de l'email n'est pas valide");
            return;
        }

        String tel = champTel.getText();
        if (tel != null && !tel.isBlank() && !PATTERN_TEL.matcher(tel).matches()) {
            afficherErreur("Le téléphone doit contenir 9 chiffres et commencer par 77, 78, 75, 76 ou 70");
            return;
        }

        try {
            if (fournisseurEnEdition == null) {
                Fournisseur nouveau = new Fournisseur(nom.trim(), email, tel);
                fournisseurService.addFournisseur(nouveau);
            } else {
                fournisseurEnEdition.setNom(nom.trim());
                fournisseurEnEdition.setEmail(email);
                fournisseurEnEdition.setTel(tel);
                fournisseurService.updateFournisseur(fournisseurEnEdition);
            }

            if (surFournisseurEnregistre != null) {
                surFournisseurEnregistre.run();
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