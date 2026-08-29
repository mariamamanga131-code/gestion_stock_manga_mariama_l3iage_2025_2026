package com.gestionstock.service;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.enums.TypeMouvement;

import java.time.LocalDate;
import java.util.List;

public interface MouvementService {
    List<Mouvement> findAll();
    List<Mouvement> findByProduit(int produitId);
    List<Mouvement> findByType(TypeMouvement type);
    List<Mouvement> findByPeriode(LocalDate debut, LocalDate fin);
    void addMouvement(Mouvement mouvement);
}