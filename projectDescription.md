# Description du projet

L'application FitMeet est une application mobile cross-platform qui permet de mettre en contact des personnes autour du sport.

Des utilisateurs, qu'elles soient des particuliers ou des clubs/associations, créent des activités sportives, avec un sport donné, une heure, une date, un lieu, un niveau, une durée, etc.

Les autres utilisateurs peuvent alors rejoindre ces événements.

Les événements disponibles sont visibles sur une carte, ou bien sous forme de liste, avec plusieurs filtres applicables.

## Objectifs

- **Faciliter la pratique sportive collective** : permettre à toute personne de trouver facilement un partenaire ou un groupe pour pratiquer une activité sportive, quel que soit son niveau ou sa disponibilité.
- **Fédérer une communauté locale** : créer du lien social autour du sport en connectant particuliers, clubs et associations d'un même bassin géographique.
- **Donner de la visibilité aux clubs et associations** : offrir aux structures sportives un canal simple pour promouvoir leurs séances, recruter de nouveaux membres et remplir leurs créneaux.
- **Simplifier l'organisation d'événements sportifs** : réduire la friction liée à la création, la gestion et la recherche d'activités (plus besoin de groupes WhatsApp ou d'affichages papier).
- **Encourager la régularité de la pratique sportive** : via la découverte facile d'événements récurrents et la mise en relation avec des pratiquants partageant les mêmes créneaux/niveaux.
- **Garantir une expérience de découverte fluide** : proposer une recherche intuitive combinant carte interactive, liste filtrable et recommandations pertinentes.

## Requirements fonctionnels

### Gestion des comptes utilisateurs
| ID | Exigence |
|----|----------|
| RF-01 | L'utilisateur peut créer un compte en tant que particulier ou en tant que club/association |
| RF-02 | L'utilisateur peut s'authentifier (email/mot de passe, et/ou connexion via un compte tiers) |
| RF-03 | L'utilisateur peut compléter et modifier son profil (photo, bio, sports pratiqués, niveau, localisation) |
| RF-04 | Un compte club/association dispose de champs additionnels (nom de la structure, description, logo, site web, réseaux sociaux) |
| RF-05 | L'utilisateur peut réinitialiser son mot de passe et supprimer son compte |

### Gestion des événements
| ID | Exigence |
|----|----------|
| RF-06 | Un utilisateur peut créer un événement sportif en renseignant : sport, date, heure, lieu, niveau requis, durée, nombre de places, description, prix éventuel |
| RF-07 | Un utilisateur peut modifier ou annuler un événement qu'il a créé |
| RF-08 | Un utilisateur peut créer un événement récurrent (hebdomadaire, mensuel, etc.) |
| RF-09 | Un utilisateur peut rejoindre un événement disponible, dans la limite des places restantes |
| RF-10 | Un utilisateur peut se désinscrire d'un événement auquel il s'est inscrit |
| RF-11 | Le créateur d'un événement peut consulter et gérer la liste des participants (accepter/refuser si validation manuelle) |
| RF-12 | Le système notifie automatiquement les participants en cas de modification ou d'annulation d'un événement |

### Recherche et découverte
| ID | Exigence |
|----|----------|
| RF-13 | L'utilisateur peut visualiser les événements disponibles sur une carte interactive |
| RF-14 | L'utilisateur peut visualiser les événements disponibles sous forme de liste |
| RF-15 | L'utilisateur peut filtrer les événements par sport, date, niveau, distance, prix, disponibilité de places et type d'organisateur (particulier/club) |
| RF-16 | L'utilisateur peut rechercher un événement par mot-clé ou par lieu |
| RF-17 | L'utilisateur peut consulter le détail complet d'un événement avant de le rejoindre |

### Interaction sociale
| ID | Exigence |
|----|----------|
| RF-18 | Les participants d'un événement peuvent échanger via une messagerie/chat de groupe dédié à l'événement |
| RF-19 | L'utilisateur peut noter et laisser un avis après avoir participé à un événement |
| RF-20 | L'utilisateur peut suivre d'autres utilisateurs, clubs ou associations pour être informé de leurs prochains événements |
| RF-21 | L'utilisateur peut consulter l'historique de ses événements passés et à venir |

### Notifications
| ID | Exigence |
|----|----------|
| RF-22 | L'utilisateur reçoit une notification push lors de l'inscription/désinscription d'un participant à son événement |
| RF-23 | L'utilisateur reçoit un rappel avant le début d'un événement auquel il est inscrit |
| RF-24 | L'utilisateur peut configurer ses préférences de notifications |

## Requirements non-fonctionnels

### Performance
| ID | Exigence |
|----|----------|
| RNF-01 | L'affichage de la carte et des résultats de recherche doit s'effectuer en moins de 2 secondes dans des conditions réseau normales |
| RNF-02 | L'application doit supporter un affichage fluide même avec plusieurs centaines d'événements chargés simultanément sur une même zone géographique |

### Scalabilité et fiabilité
| ID | Exigence |
|----|----------|
| RNF-03 | L'architecture backend doit pouvoir supporter une croissance du nombre d'utilisateurs sans dégradation notable des performances |
| RNF-04 | Le service doit garantir une disponibilité minimale de 99,5 % (hors maintenance planifiée) |
| RNF-05 | Les données doivent être sauvegardées régulièrement pour éviter toute perte en cas d'incident |

### Sécurité et confidentialité
| ID | Exigence |
|----|----------|
| RNF-06 | Les données personnelles doivent être traitées conformément au RGPD (consentement, droit à l'oubli, portabilité des données) |
| RNF-07 | Les mots de passe doivent être stockés de façon sécurisée (hachage) |
| RNF-08 | Les échanges entre l'application et le serveur doivent être chiffrés (HTTPS/TLS) |
| RNF-09 | La géolocalisation précise de l'utilisateur ne doit être partagée qu'avec son consentement explicite |

### Utilisabilité / Expérience utilisateur
| ID | Exigence |
|----|----------|
| RNF-10 | L'interface doit être intuitive et accessible à des utilisateurs non technophiles |
| RNF-11 | L'application doit respecter les standards d'accessibilité mobile de base (contraste, taille de police, navigation au clavier/lecteur d'écran) |
| RNF-12 | L'application doit être disponible en plusieurs langues (au minimum français et anglais) |

### Compatibilité
| ID | Exigence |
|----|----------|
| RNF-13 | L'application doit fonctionner sur les principales versions récentes d'iOS et d'Android (approche cross-platform) |
| RNF-14 | L'application doit s'adapter à différentes tailles d'écran (smartphones et tablettes) |

### Maintenabilité
| ID | Exigence |
|----|----------|
| RNF-15 | Le code doit être structuré de manière modulaire pour faciliter l'ajout de nouvelles fonctionnalités |
| RNF-16 | L'application doit intégrer un système de suivi des erreurs (monitoring/logging) pour faciliter le diagnostic des incidents en production |
