package com.gestionstock.controller;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.service.MouvementService;
import com.gestionstock.service.MouvementServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class MouvementController {

    @FXML
    TableView<Mouvement> tableMouvements;
    @FXML
    TableColumn<Mouvement, String> colonneDate;
    @FXML
    TableColumn<Mouvement, String> colonneProduit;
    @FXML
    TableColumn<Mouvement, TypeMouvement> colonneType;
    @FXML
    TableColumn<Mouvement, Integer> colonneQuantite;
    @FXML
    TableColumn<Mouvement, String> colonneMotif;
    @FXML
    ComboBox<String> comboType;
    @FXML
    DatePicker dateDebut;
    @FXML
    DatePicker dateFin;
    @FXML
    TableColumn<Mouvement, String> colonneUtilisateur;

    private final MouvementService mouvementService = new MouvementServiceImpl();
    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        comboType.setItems(FXCollections.observableArrayList("Toutes", "ENTRE", "SORTIE"));
        comboType.setValue("Toutes");

        colonneDate.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDateMouvement().format(FORMAT_DATE)
                )
        );
        colonneProduit.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getProduit().getNom()
                )
        );
        colonneType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colonneQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colonneMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colonneUtilisateur.setCellValueFactory(cellData -> {
            com.gestionstock.model.Utilisateur utilisateur = cellData.getValue().getUtilisateur();
            return new javafx.beans.property.SimpleStringProperty(utilisateur != null ? utilisateur.getNom() : "—");
        });

        chargerDonnees();
    }

    private void chargerDonnees() {
        List<Mouvement> mouvements = mouvementService.findAll();
        tableMouvements.setItems(FXCollections.observableArrayList(mouvements));
    }

    @FXML
    void appliquerFiltres() {
        List<Mouvement> resultats;

        String type = comboType.getValue();
        LocalDate debut = dateDebut.getValue();
        LocalDate fin = dateFin.getValue();

        if (debut != null && fin != null) {
            resultats = mouvementService.findByPeriode(debut, fin);
        } else if (type != null && type.equals("ENTRE")) {
            resultats = mouvementService.findByType(TypeMouvement.ENTRE);
        } else if (type != null && type.equals("SORTIE")) {
            resultats = mouvementService.findByType(TypeMouvement.SORTIE);
        } else {
            resultats = mouvementService.findAll();
        }

        tableMouvements.setItems(FXCollections.observableArrayList(resultats));
    }

    @FXML
    void handleAjouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestionstock/AddMouvementDialog.fxml")
            );
            Parent root = loader.load();

            AddMouvementController controleurDialogue = loader.getController();
            controleurDialogue.setSurMouvementAjoute(this::chargerDonnees);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nouveau mouvement");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}