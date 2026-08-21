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

### Comptes utilisateurs
- RF-01 : Créer un compte en tant que particulier ou club/association, et s'authentifier
- RF-02 : Compléter/modifier son profil (photo, sports pratiqués, niveau, localisation)

### Gestion des événements
- RF-03 : Créer un événement (sport, date, heure, lieu, niveau, durée, nombre de places, prix éventuel)
- RF-04 : Modifier ou annuler un événement créé
- RF-05 : Rejoindre un événement disponible, dans la limite des places restantes
- RF-06 : Se désinscrire d'un événement
- RF-07 : Le créateur peut consulter la liste des participants

### Recherche et découverte
- RF-08 : Visualiser les événements sur une carte interactive
- RF-09 : Visualiser les événements sous forme de liste
- RF-10 : Filtrer les événements (sport, date, niveau, distance, disponibilité de places)
- RF-11 : Consulter le détail d'un événement avant de le rejoindre

### Notifications
- RF-12 : Recevoir une notification lors d'un changement sur un événement rejoint (modification, annulation, rappel avant le début)

> Fonctionnalités envisagées mais non confirmées pour une première version : messagerie entre participants, avis/notation, suivi d'organisateurs.

## Requirements non-fonctionnels

- **Performance** : les recherches et l'affichage de la carte doivent rester fluides (< 2 secondes en usage normal)
- **Fiabilité** : le service doit rester disponible de façon quasi continue, avec sauvegarde régulière des données
- **Sécurité et confidentialité** : conformité RGPD, mots de passe stockés de façon sécurisée, échanges chiffrés (HTTPS)
- **Utilisabilité** : interface simple et intuitive, accessible à des utilisateurs non technophiles
- **Compatibilité** : l'application doit fonctionner sur iOS et Android, et s'adapter à différentes tailles d'écran
- **Maintenabilité** : code structuré de manière modulaire pour faciliter l'évolution du produit

## Différence par rapport aux solutions existantes

A la différence de Smatch, ce ne sont pas des personnes qui matchent, mais des sessions. A la place de "Je cherche quelqu'un avec qui faire du sport", l'utilisateur demanderait plutôt "Je veux faire du [sport] jeudi à 18h, qui veut venir ?".

L’objectif est ainsi de passer d’une logique de rencontre entre individus à une logique de création et de participation à des activités sportives concrètes. L’utilisateur peut créer ou rejoindre une session en fonction du sport, de la date, de l’heure, du lieu et du niveau recherché.

Cette approche permet également de réduire le risque de « match sans suite » : le but n’est plus simplement de trouver une personne compatible, mais de réunir rapidement les bonnes personnes autour d’une activité précise.

