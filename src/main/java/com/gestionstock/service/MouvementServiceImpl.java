package com.gestionstock.service;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.Produit;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MouvementServiceImpl implements MouvementService {

    @Override
    public List<Mouvement> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT m FROM Mouvement m JOIN FETCH m.produit ORDER BY m.dateMouvement DESC",
                    Mouvement.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Mouvement> findByProduit(int produitId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT m FROM Mouvement m JOIN FETCH m.produit WHERE m.produit.id = :produitId ORDER BY m.dateMouvement DESC",
                            Mouvement.class)
                    .setParameter("produitId", produitId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Mouvement> findByType(TypeMouvement type) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT m FROM Mouvement m JOIN FETCH m.produit WHERE m.type = :type ORDER BY m.dateMouvement DESC",
                            Mouvement.class)
                    .setParameter("type", type)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Mouvement> findByPeriode(LocalDate debut, LocalDate fin) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            LocalDateTime debutDateTime = debut.atStartOfDay();
            LocalDateTime finDateTime = fin.atTime(23, 59, 59);

            return em.createQuery(
                    "SELECT m FROM Mouvement m JOIN FETCH m.produit " +
                            "WHERE m.dateMouvement BETWEEN :debut AND :fin ORDER BY m.dateMouvement DESC",
                            Mouvement.class)
                    .setParameter("debut", debutDateTime)
                    .setParameter("fin", finDateTime)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void addMouvement(Mouvement mouvement) {
        if (mouvement.getQuantite() <= 0) {
            throw new IllegalArgumentException("La quantité doit être strictement positive");
        }

        if (mouvement.getType() == TypeMouvement.SORTIE
                && (mouvement.getMotif() == null || mouvement.getMotif().isBlank())) {
            throw new IllegalArgumentException("Le motif est obligatoire pour une sortie");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Produit produit = em.find(Produit.class, mouvement.getProduit().getId());
            if (produit == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Produit introuvable");
            }

            if (mouvement.getType() == TypeMouvement.ENTRE) {
                produit.setQuantiteStock(produit.getQuantiteStock() + mouvement.getQuantite());
            } else {
                if (mouvement.getQuantite() > produit.getQuantiteStock()) {
                    em.getTransaction().rollback();
                    throw new IllegalStateException(
                            "Stock insuffisant : quantité demandée supérieure au stock disponible");
                }
                produit.setQuantiteStock(produit.getQuantiteStock() - mouvement.getQuantite());
            }

            mouvement.setProduit(produit);
            mouvement.setDateMouvement(LocalDateTime.now());
            mouvement.setUtilisateur(com.gestionstock.util.SessionUtilisateur.getUtilisateurConnecte());

            em.persist(mouvement);
            em.merge(produit);

            em.getTransaction().commit();
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de l'enregistrement du mouvement");
        } finally {
            em.close();
        }
    }
}