package com.gestionstock.service;

import com.gestionstock.model.Fournisseur;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class FournisseurServiceImpl implements FournisseurService {

    @Override
    public List<Fournisseur> findAllFournisseurs() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT f FROM Fournisseur f", Fournisseur.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Fournisseur> findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Fournisseur.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public void addFournisseur(Fournisseur fournisseur) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(fournisseur);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de l'ajout du fournisseur");
        } finally {
            em.close();
        }
    }

    @Override
    public void updateFournisseur(Fournisseur fournisseur) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(fournisseur);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la modification du fournisseur");
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteFournisseur(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Fournisseur fournisseur = em.find(Fournisseur.class, id);
            if (fournisseur == null) {
                em.getTransaction().rollback();
                return;
            }

            long nbProduits = em.createQuery(
                            "SELECT COUNT(p) FROM Produit p WHERE p.fournisseur.id = :id", Long.class)
                    .setParameter("id", id)
                    .getSingleResult();

            if (nbProduits > 0) {
                em.getTransaction().rollback();
                throw new IllegalStateException(
                        "Impossible de supprimer ce fournisseur : des produits y sont encore rattachés");
            }

            em.remove(fournisseur);
            em.getTransaction().commit();
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la suppression du fournisseur");
        } finally {
            em.close();
        }
    }

    @Override
    public long countProduitsByFournisseur(int fournisseurId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(p) FROM Produit p WHERE p.fournisseur.id = :id", Long.class)
                    .setParameter("id", fournisseurId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}