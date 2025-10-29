package org.example.supplychainx.common.security;


public enum Role {
    // Administration
    ADMIN,

    // Approvisionnement
    GESTIONNAIRE_APPROVISIONNEMENT,
    RESPONSABLE_ACHATS,
    SUPERVISEUR_LOGISTIQUE,

    // Production
    CHEF_PRODUCTION,
    PLANIFICATEUR,
    SUPERVISEUR_PRODUCTION,

    // Livraison
    GESTIONNAIRE_COMMERCIAL,
    RESPONSABLE_LOGISTIQUE,
    SUPERVISEUR_LIVRAISONS,

    // generic/read-only
    VIEWER
}