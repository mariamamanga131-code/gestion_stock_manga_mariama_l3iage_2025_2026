package com.gestionstock.controller;

import com.gestionstock.service.StatistiqueService;
import com.gestionstock.service.StatistiqueServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class StatistiqueController {

    @FXML
    DatePicker dateDebut;
    @FXML
    DatePicker dateFin;
    @FXML
    Label labelValeurStock;
    @FXML
    Label labelProduitMouvemente;
    @FXML
    Label labelCategorieForte;
    @FXML
    Label labelFournisseurTop;
    @FXML
    Label labelSortiesRupture;
    @FXML
    BarChart<String, Number> graphiqueBarres;
    @FXML
    CategoryAxis axeXMois;
    @FXML
    PieChart graphiqueCamembert;

    private final StatistiqueService statistiqueService = new StatistiqueServiceImpl();

    @FXML
    public void initialize() {
        dateDebut.setValue(LocalDate.now().minusMonths(3));
        dateFin.setValue(LocalDate.now());
        actualiser();
    }

    @FXML
    void actualiser() {
        LocalDate debut = dateDebut.getValue();
        LocalDate fin = dateFin.getValue();

        if (debut == null || fin == null || debut.isAfter(fin)) {
            return;
        }

        NumberFormat formatMonnaie = NumberFormat.getNumberInstance(Locale.FRANCE);

        labelValeurStock.setText(formatMonnaie.format(statistiqueService.valeurTotaleStock()) + " FCFA");
        labelProduitMouvemente.setText(statistiqueService.produitLePlusMouvemente(debut, fin));
        labelCategorieForte.setText(statistiqueService.categoriePlusForteValeur());
        labelFournisseurTop.setText(statistiqueService.fournisseurPlusDeProduits());
        labelSortiesRupture.setText(String.valueOf(statistiqueService.nombreSortiesVersRupture(debut, fin)));

        mettreAJourGraphiqueBarres(debut, fin);
        mettreAJourCamembert();
    }

    private void mettreAJourGraphiqueBarres(LocalDate debut, LocalDate fin) {
        Map<String, Integer> entrees = statistiqueService.entreesParMois(debut, fin);
        Map<String, Integer> sorties = statistiqueService.sortiesParMois(debut, fin);

        Set<String> tousLesMois = new TreeSet<>();
        tousLesMois.addAll(entrees.keySet());
        tousLesMois.addAll(sorties.keySet());

        XYChart.Series<String, Number> serieEntrees = new XYChart.Series<>();
        serieEntrees.setName("Entrées");
        XYChart.Series<String, Number> serieSorties = new XYChart.Series<>();
        serieSorties.setName("Sorties");

        for (String mois : tousLesMois) {
            serieEntrees.getData().add(new XYChart.Data<>(mois, entrees.getOrDefault(mois, 0)));
            serieSorties.getData().add(new XYChart.Data<>(mois, sorties.getOrDefault(mois, 0)));
        }

        graphiqueBarres.getData().clear();
        graphiqueBarres.getData().addAll(serieEntrees, serieSorties);
    }

    private void mettreAJourCamembert() {
        Map<String, Double> valeurParCategorie = statistiqueService.valeurStockParCategorie();

        graphiqueCamembert.getData().clear();
        for (Map.Entry<String, Double> entree : valeurParCategorie.entrySet()) {
            graphiqueCamembert.getData().add(new PieChart.Data(entree.getKey(), entree.getValue()));
        }
    }
}