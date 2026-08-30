package com.gestionstock.controller;

import com.gestionstock.model.Produit;
import com.gestionstock.service.DashboardService;
import com.gestionstock.service.DashboardServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.text.NumberFormat;
import java.util.Locale;

public class DashboardController {

    @FXML
    Label labelTotalProduits;
    @FXML
    Label labelStockBas;
    @FXML
    Label labelValeurStock;
    @FXML
    Label labelMouvementsJour;
    @FXML
    ListView<String> listeStockBas;

    private final DashboardService dashboardService = new DashboardServiceImpl();

    @FXML
    public void initialize() {
        labelTotalProduits.setText(String.valueOf(dashboardService.countTotalProduits()));
        labelStockBas.setText(String.valueOf(dashboardService.countProduitsStockBas()));

        NumberFormat formatMonnaie = NumberFormat.getNumberInstance(Locale.FRANCE);
        labelValeurStock.setText(formatMonnaie.format(dashboardService.valeurTotaleStock()) + " FCFA");

        long entrees = dashboardService.countEntreesDuJour();
        long sorties = dashboardService.countSortiesDuJour();
        labelMouvementsJour.setText(entrees + " entrées / " + sorties + " sorties");

        chargerListeStockBas();
    }

    private void chargerListeStockBas() {
        listeStockBas.setItems(FXCollections.observableArrayList());
        for (Produit produit : dashboardService.produitsEnStockBas()) {
            listeStockBas.getItems().add(
                    produit.getNom() + "  —  stock : " + produit.getQuantiteStock()
                            + " (seuil min : " + produit.getQuantiteMin() + ")"
            );
        }

        if (listeStockBas.getItems().isEmpty()) {
            listeStockBas.getItems().add("Aucun produit en stock bas actuellement");
        }
    }
}