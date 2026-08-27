package com.gestionstock.service;

import com.gestionstock.model.Utilisateur;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

public class UtilisateurServiceImpl implements UtilisateurService {

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.email = :email", Utilisateur.class);
            query.setParameter("email", email);
            return query.getResultStream().findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Utilisateur> authentifier(String email, String motDePasseClair) {
        Optional<Utilisateur> utilisateurOptional = findByEmail(email);

        if (utilisateurOptional.isEmpty()) {
            return Optional.empty();
        }

        Utilisateur utilisateur = utilisateurOptional.get();

        if (!utilisateur.isActif()) {
            return Optional.empty();
        }

        boolean motDePasseCorrect = BCrypt.checkpw(motDePasseClair, utilisateur.getMotDePasseHash());

        if (!motDePasseCorrect) {
            return Optional.empty();
        }

        return Optional.of(utilisateur);
    }

    @Override
    public void addUtilisateur(Utilisateur utilisateur, String motDePasseClair) {
        String hash = BCrypt.hashpw(motDePasseClair, BCrypt.gensalt());
        utilisateur.setMotDePasseHash(hash);

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(utilisateur);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la création de l'utilisateur");
        } finally {
            em.close();
        }
    }

    @Override
    public List<Utilisateur> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Utilisateur u", Utilisateur.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void setActif(Long id, boolean actif) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Utilisateur utilisateur = em.find(Utilisateur.class, id);
            if (utilisateur != null) {
                utilisateur.setActif(actif);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la mise à jour du compte");
        } finally {
            em.close();
        }
    }
}