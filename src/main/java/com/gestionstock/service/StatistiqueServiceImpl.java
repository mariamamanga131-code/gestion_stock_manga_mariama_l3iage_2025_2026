package com.gestionstock.service;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.Produit;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class StatistiqueServiceImpl implements StatistiqueService {

    private static final DateTimeFormatter FORMAT_MOIS = DateTimeFormatter.ofPattern("MM/yyyy");

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
    public String produitLePlusMouvemente(LocalDate debut, LocalDate fin) {
        List<Mouvement> mouvements = mouvementsDeLaPeriode(debut, fin);

        Map<String, Integer> totalParProduit = new HashMap<>();
        for (Mouvement m : mouvements) {
            String nom = m.getProduit().getNom();
            totalParProduit.merge(nom, m.getQuantite(), Integer::sum);
        }

        return totalParProduit.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entree -> entree.getKey() + " (" + entree.getValue() + " unités)")
                .orElse("Aucun mouvement sur la période");
    }

    @Override
    public String categoriePlusForteValeur() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Produit> produits = em.createQuery("SELECT p FROM Produit p", Produit.class).getResultList();

            Map<String, Double> valeurParCategorie = new HashMap<>();
            for (Produit p : produits) {
                if (p.getCategorie() != null) {
                    String nomCategorie = p.getCategorie().getNom();
                    double valeur = p.getQuantiteStock() * p.getPrix();
                    valeurParCategorie.merge(nomCategorie, valeur, Double::sum);
                }
            }

            return valeurParCategorie.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("Aucune donnée");
        } finally {
            em.close();
        }
    }

    @Override
    public String fournisseurPlusDeProduits() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Object[]> resultats = em.createQuery(
                    "SELECT f.nom, COUNT(p) FROM Produit p JOIN p.fournisseur f GROUP BY f.nom", Object[].class
            ).getResultList();

            return resultats.stream()
                    .max(Comparator.comparingLong(ligne -> (Long) ligne[1]))
                    .map(ligne -> ligne[0] + " (" + ligne[1] + " produits)")
                    .orElse("Aucune donnée");
        } finally {
            em.close();
        }
    }

    @Override
    public long nombreSortiesVersRupture(LocalDate debut, LocalDate fin) {
        List<Mouvement> mouvements = mouvementsDeLaPeriode(debut, fin);

        return mouvements.stream()
                .filter(m -> m.getType() == TypeMouvement.SORTIE)
                .filter(m -> m.getProduit().getQuantiteStock() <= m.getProduit().getQuantiteMin())
                .count();
    }

    @Override
    public Map<String, Integer> entreesParMois(LocalDate debut, LocalDate fin) {
        return quantiteParMois(debut, fin, TypeMouvement.ENTRE);
    }

    @Override
    public Map<String, Integer> sortiesParMois(LocalDate debut, LocalDate fin) {
        return quantiteParMois(debut, fin, TypeMouvement.SORTIE);
    }

    private Map<String, Integer> quantiteParMois(LocalDate debut, LocalDate fin, TypeMouvement type) {
        List<Mouvement> mouvements = mouvementsDeLaPeriode(debut, fin);

        Map<String, Integer> resultat = new TreeMap<>();
        for (Mouvement m : mouvements) {
            if (m.getType() == type) {
                String cleMois = m.getDateMouvement().format(FORMAT_MOIS);
                resultat.merge(cleMois, m.getQuantite(), Integer::sum);
            }
        }
        return resultat;
    }

    @Override
    public Map<String, Double> valeurStockParCategorie() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Produit> produits = em.createQuery("SELECT p FROM Produit p", Produit.class).getResultList();

            Map<String, Double> resultat = new LinkedHashMap<>();
            for (Produit p : produits) {
                if (p.getCategorie() != null) {
                    String nomCategorie = p.getCategorie().getNom();
                    double valeur = p.getQuantiteStock() * p.getPrix();
                    resultat.merge(nomCategorie, valeur, Double::sum);
                }
            }
            return resultat;
        } finally {
            em.close();
        }
    }

    private List<Mouvement> mouvementsDeLaPeriode(LocalDate debut, LocalDate fin) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            LocalDateTime debutDateTime = debut.atStartOfDay();
            LocalDateTime finDateTime = fin.atTime(23, 59, 59);

            return em.createQuery(
                            "SELECT m FROM Mouvement m JOIN FETCH m.produit p JOIN FETCH p.categorie " +
                                    "WHERE m.dateMouvement BETWEEN :debut AND :fin", Mouvement.class)
                    .setParameter("debut", debutDateTime)
                    .setParameter("fin", finDateTime)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}