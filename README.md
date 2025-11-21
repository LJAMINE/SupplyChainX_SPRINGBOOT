# SupplyChainX_SPRINGBOOT

Sécurisation et tests de l'API SupplyChainX

Ce README explique le fonctionnement de l’authentification dans le projet SupplyChainX, décrit le rôle des composants Spring Security mis en place, et donne des instructions pas-à-pas pour tester l’API via Postman (ou curl). La deuxième partie documente les endpoints sécurisés, les rôles autorisés et fournit des exemples de requêtes authentifiées.

---

## Sommaire
- Contexte et résumé rapide
- Flux d’authentification
- Composants Spring Security et responsabilités
- Règles d’autorisation (méthode + URL)
- Tester avec Postman (étapes détaillées)
- Exemples de requêtes (curl + Postman)
- Erreurs fréquentes & dépannage
- Notes opérationnelles et recommandations futures (ex. migration vers JWT)

---

## Contexte et résumé rapide
- Authentification actuelle : HTTP Basic Auth.
- Stockage des mots de passe : BCrypt (algorithme sécurisé).
- Autorisation : annotations `@PreAuthorize` (méthode/service) + règles URL dans `SecurityConfig`.
- Principal personnalisé : `UserPrincipal` implémente `UserDetails` et expose `id`, `email`, `role`.
- OpenAPI/Swagger : configuré pour montrer le schéma `basicAuth` dans l’UI.

Vous pouvez tester immédiatement via Postman avec les comptes présents dans la base de données (ex. `GESTIONNAIRE_APPROVISIONNEMENT@gmail.com` / mot de passe `secret`) après la migration Bcrypt.

---

## Flux d’authentification (concret)
1. Le client envoie une requête HTTP avec l’en-tête `Authorization: Basic <base64(email:password)>`.
2. Spring Security intercepte la requête (filtre HTTP Basic).
3. `JpaUserDetailsService.loadUserByUsername(String username)` est appelé avec la "username" fournie (ici l’email).
4. Le service récupère l’entité `User` depuis la base (via `UserRepository`).
5. Spring compare le mot de passe fourni (en clair) avec le `passwordHash` stocké dans la DB en utilisant `PasswordEncoder` (BCrypt).
6. Si authentifié, Spring crée un `Authentication` contenant le `UserPrincipal` (UserDetails).
7. Les annotations `@PreAuthorize` (ou règles URL) déterminent si l’accès à la ressource est autorisé (en se basant sur les authorities `ROLE_<NAME>`).

---

## Composants Spring Security et responsabilités

- `SecurityConfig`
    - Configure `SecurityFilterChain` :
        - `httpBasic()` (HTTP Basic Auth)
        - `csrf` désactivé pour API REST (adapter si vos clients utilisent des formulaires/cookies)
        - Règles URL (ex. actuator/swagger/public endpoints)
    - Expose un `PasswordEncoder` (BCryptPasswordEncoder).

- `JpaUserDetailsService`
    - Implémente `UserDetailsService`.
    - Recherche l’utilisateur par email et renvoie un `UserDetails` (ici `UserPrincipal`).
    - Mappe le rôle de l’entité `User` en `GrantedAuthority`, p.ex. `ROLE_GESTIONNAIRE_APPROVISIONNEMENT`.

- `UserPrincipal` (implémente `UserDetails`)
    - Contient `id`, `email`, `passwordHash`, `role`.
    - Méthode `getAuthorities()` renvoie `ROLE_<role>` ce qui permet d’utiliser `hasRole(...)` / `hasAnyRole(...)` dans `@PreAuthorize`.

- Méthode-level security
    - Activée par `@EnableMethodSecurity`.
    - Utilisez `@PreAuthorize(...)` sur les méthodes de service (recommandé) ou sur les contrôleurs.

- OpenAPI / Swagger config
    - Ajoute un `SecurityScheme` de type HTTP basic pour que Swagger UI propose l’authentification.

---

## Règles d’accès recommandées (extraits et mapping)

Remarque : dans Spring Security `hasRole("X")` correspond à une authority `ROLE_X`.

Exemples de mappings (adaptez selon vos règles métier) :

- /api/raw-materials
    - GET /api/raw-materials — roles autorisés : RESPONSABLE_ACHATS, GESTIONNAIRE_APPROVISIONNEMENT
    - GET /api/raw-materials/{id} — roles autorisés : RESPONSABLE_ACHATS, SUPERVISEUR_LOGISTIQUE
    - POST /api/raw-materials — roles autorisés : GESTIONNAIRE_APPROVISIONNEMENT, RESPONSABLE_ACHATS (selon besoin)
    - PUT /api/raw-materials/{id} — roles autorisés : GESTIONNAIRE_APPROVISIONNEMENT
    - DELETE /api/raw-materials/{id} — roles autorisés : GESTIONNAIRE_APPROVISIONNEMENT
- Ces règles peuvent être appliquées soit :
- via `SecurityConfig` (coarse-grained URL rules), soit
- via `@PreAuthorize` sur les méthodes de service (recommandé pour la sécurité métier).

---

## Documentation des endpoints sécurisés (exemples)

1) POST Create Raw Material
- Route : POST /api/raw-materials
- Rôles autorisés : RESPONSABLE_ACHATS, GESTIONNAIRE_APPROVISIONNEMENT
- Body (JSON) :
```json
{
  "name": "steel",
  "unit": "kg",
  "pricePerUnit": 12.5
}
```
- Header : `Content-Type: application/json`, `Authorization: Basic <base64(email:password)>`
- Exemples :
    - curl :
      ```
      curl -v -u "GESTIONNAIRE_APPROVISIONNEMENT@gmail.com:secret" \
        -X POST "http://localhost:8080/api/raw-materials" \
        -H "Content-Type: application/json" \
        -d '{"name":"steel","unit":"kg","pricePerUnit":12.5}'
      ```
    - Postman :
        - Authorization → Type: Basic Auth → Username: GESTIONNAIRE_APPROVISIONNEMENT@gmail.com, Password: secret
        - Body → raw → JSON → paste payload → Send

2) GET List Raw Materials
- Route : GET /api/raw-materials?s=&page=0&size=20
- Rôles autorisés : RESPONSABLE_ACHATS, GESTIONNAIRE_APPROVISIONNEMENT
- Exemples :
    - curl :
      ```
      curl -v -u "RESPONSABLE_ACHATS@gmail.com:secret" \
        "http://localhost:8080/api/raw-materials?page=0&size=20"
      ```

3) GET Current Authenticated User Info
- Route : GET /api/raw-materials/me
- Rôles autorisés : tout utilisateur authentifié (global `.anyRequest().authenticated()` suffit)
- Retour : JSON contenant id, email, role (obtenu via `@AuthenticationPrincipal UserPrincipal`).
- Exemple Postman :
    - Basic Auth: user email / password
    - GET http://localhost:8080/api/raw-materials/me

4) GET Raw Material by id
- Route : GET /api/raw-materials/{id}
- Rôles autorisés : RESPONSABLE_ACHATS, SUPERVISEUR_LOGISTIQUE
- Exemple :
  ```
  curl -v -u "RESPONSABLE_ACHATS@gmail.com:secret" \
    "http://localhost:8080/api/raw-materials/1"
  ```


## Commandes utiles

- Vérifier que tous les `password_hash` sont bien bcrypt :
  ```sql
  SELECT email, password_hash FROM users WHERE password_hash NOT LIKE '$2%';
  ```
  Si la requête renvoie des lignes, ces mots de passe ne sont pas hashés avec BCrypt.

- Générer un hash BCrypt (exemples rapides) :
    - Java quick main:
      ```java
      System.out.println(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("secret"));
      ```
    - `htpasswd` (linux) :
      ```
      htpasswd -nbB user secret
      ```

- Docker compose rebuild with profile:
  ```
  docker compose down
  docker compose up --build -d
  ```

---
