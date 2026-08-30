package com.gestionstock.service;

import com.gestionstock.model.Produit;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

public class DashboardServiceImpl implements DashboardService {

    @Override
    public long countTotalProduits() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(p) FROM Produit p", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public long countProduitsStockBas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(p) FROM Produit p WHERE p.quantiteStock <= p.quantiteMin", Long.class
            ).getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public double valeurTotaleStock() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Double resultat = em.createQuery(
                    "SELECT SUM(p.quantiteStock * p.prix) FROM Produit p", Double.class
            ).getSingleResult();
            return resultat != null ? resultat : 0.0;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Produit> produitsEnStockBas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Produit p WHERE p.quantiteStock <= p.quantiteMin ORDER BY p.nom",
                    Produit.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public long countEntreesDuJour() {
        return countMouvementsDuJour(TypeMouvement.ENTRE);
    }

    @Override
    public long countSortiesDuJour() {
        return countMouvementsDuJour(TypeMouvement.SORTIE);
    }

    private long countMouvementsDuJour(TypeMouvement type) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            LocalDateTime debutJournee = java.time.LocalDate.now().atStartOfDay();
            LocalDateTime finJournee = java.time.LocalDate.now().atTime(23, 59, 59);

            return em.createQuery(
                            "SELECT COUNT(m) FROM Mouvement m WHERE m.type = :type " +
                                    "AND m.dateMouvement BETWEEN :debut AND :fin", Long.class)
                    .setParameter("type", type)
                    .setParameter("debut", debutJournee)
                    .setParameter("fin", finJournee)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}