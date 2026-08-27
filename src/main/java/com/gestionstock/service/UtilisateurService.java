package com.gestionstock.service;

import com.gestionstock.model.Utilisateur;

import java.util.List;
import java.util.Optional;

public interface UtilisateurService {
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> authentifier(String email, String motDePasseClair);
    void addUtilisateur(Utilisateur utilisateur, String motDePasseClair);
    List<Utilisateur> findAll();
    void setActif(Long id, boolean actif);
}