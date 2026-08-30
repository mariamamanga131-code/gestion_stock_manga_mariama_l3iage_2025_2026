package com.gestionstock.service;

import java.time.LocalDate;
import java.util.Map;

public interface StatistiqueService {
    double valeurTotaleStock();
    String produitLePlusMouvemente(LocalDate debut, LocalDate fin);
    String categoriePlusForteValeur();
    String fournisseurPlusDeProduits();
    long nombreSortiesVersRupture(LocalDate debut, LocalDate fin);
    Map<String, Integer> entreesParMois(LocalDate debut, LocalDate fin);
    Map<String, Integer> sortiesParMois(LocalDate debut, LocalDate fin);
    Map<String, Double> valeurStockParCategorie();
}