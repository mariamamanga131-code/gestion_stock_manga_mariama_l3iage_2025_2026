package com.gestionstock.service;

import com.gestionstock.model.Produit;

import java.util.List;

public interface DashboardService {
    long countTotalProduits();
    long countProduitsStockBas();
    double valeurTotaleStock();
    List<Produit> produitsEnStockBas();
    long countEntreesDuJour();
    long countSortiesDuJour();
}