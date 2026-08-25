# Tests du frontend
## General
### Tests manuels
Tester la persistance du compte -> Si je suis connecté, que je ferme l'application et la relance, je devrais toujours être connecté
Tester la mémoire lors du changement de page -> Par exemple, quand je suis sur la carte à un endroit en particulier, je veux que, si je passe sur la liste puis reviens sur la carte, on se trouve au même endroit

## Page de connexion
### Tests unitaires
1. l'email entré doit être valide, c'est à dire de la forme aaa@bbb.ccc
2. Le bouton "connexion avec google" ouvre l
3. Les champs vides sont détecté et empêchent de passer à l'étape suivante (Un utilisateur doit inscrire son nom)
4. La date de naissance ne peut se trouver que dans le passé
5. L'âge de l'utilisateur doit être d'au moins 18 ans (vérification avec la date courante et la date de naissance. Cette vérification devrait également faite pour la base de donnée, mais le faire dans le frontend permet de ne pas tenter d'envoyer des données erronées)
### Tests d'intégration
1. Se connecter à l'aide d'un email envoie automatiquement un mail à l'email entré (si il est valide)
2. Le comportement est différent selon si le mode de connexion est déjà existant ou si l'utilisateur se connecte pour la première fois
3. Le bouton de connexion avec google ouvre le widget de connexion dédié
4. Le bouton de sélection de date ouvre une vue calendrier (normalement c'est une fonctionnalité native qu'il doit être possible de réaliser avec Kotlin)
### Tests manuels
Il faut ici vérifier les aspects UI et UX :
- Est ce qu'on voit bien tout les boutons?
- Est ce qu'on arrive bien à lire toutes les questions? sont elles claires?
- Est ce que sélectionner plusieurs activités est possible (centres d'intérêts)
- Est ce que ne sélectionner aucune activité est possible (Si un utilisateur n'a pas de sport qui l'intéresse en particulier, il peut ne pas en sélectionner)

## Page "Liste d'activitées"
### Tests unitaires
Aucun, je vois pas lesquelles faire

### Tests d'intégration
1. Activer un filtre retire de la vue toutes les activité ne respectant pas le filtre
2. Activer plusieurs filtre met à jour correctement la liste des activités visible
3. Une activité pleine ne peut pas être rejoint
4. Le bouton rejoindre d'une activité envoi une requête de connection
5. Si un utilisateur a pu rejoindre une activité, celle-ci disparaît de la vue des activités
6. Un utilisateur qui rejoint une activité quand un autre utilisateur a dans ça vue cette activité doit être mis à jour
7. deux utilisateurs qui rejoignent une activité avec une seul place disponible ne devrait pas arriver : l'un des deux ne sera pas mis dans celle-ci (attention à la concurence)

### Tests manuels
Vérifier que les activités ne se chevauchent pas, Vérifier le bon fonctionnement du scroll (on ne scroll que sur les activités), vérifier les boutons des activités (info et rejoindre)

## Page Carte



# Test du backend
## Connection
### Tests unitaire
1. On peut envoyer une demande de création à la base de donnée
2. Si le compte existe déjà, alors l'utilisateur est connecté à son compte, sinon, une deuxième requête sera envoyé avec les champs nécessaires
3. Tout les champs nécessaire sont dans la demande, sans quoi celle-ci est refusé
4. Il doit être possible de faire une demande de suppression de compte

### Tests manuels
- Tenter de se connecter, avec un mail existant, puis un nouveau
- Tenter de se connecter, avec un compte google ayant un compte associé, puis un nouveau

## Création d'évènements
### Tests unitaires
1. On peut envoyer une demande de création à la base de donnée
2. deux activités avec toutes les informations identiques doit être défini comme 2 activités distinctes.
  -> On peut créer deux activités à la suite
3. Tenter de créer une activité sans préciser son nom, la date ou l'heure ainsi que la durée doit renvoyer un code d'erreur
4. Il doit être possible de créer une activité avec une description vide

### Tests manuels
