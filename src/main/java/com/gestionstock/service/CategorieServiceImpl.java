package com.gestionstock.service;

import com.gestionstock.model.Categorie;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class CategorieServiceImpl implements CategorieService {

    @Override
    public List<Categorie> findAllCategories() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Categorie c", Categorie.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Categorie> findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Categorie categorie = em.find(Categorie.class, id);
            return Optional.ofNullable(categorie);
        } finally {
            em.close();
        }
    }

    @Override
    public void addCategorie(Categorie categorie) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(categorie);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de l'ajout de la catégorie");
        } finally {
            em.close();
        }
    }

    @Override
    public void updateCategorie(Categorie categorie) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(categorie);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la modification de la catégorie");
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteCategorie(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Categorie categorie = em.find(Categorie.class, id);

            if (categorie == null) {
                em.getTransaction().rollback();
                return;
            }

            if (!categorie.getProduits().isEmpty()) {
                em.getTransaction().rollback();
                throw new IllegalStateException(
                        "Impossible de supprimer cette catégorie : des produits y sont encore rattachés");
            }

            em.remove(categorie);
            em.getTransaction().commit();
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la suppression de la catégorie");
        } finally {
            em.close();
        }
    }
}