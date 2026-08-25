Ces tests sont sujets à des changements et sont surtout là pour avoir une ligne de direction sur les tests à réaliser. Pendant la création des tests, il y aura potentiellement certain tests unitaires non réalisable, mais également certains tests non mentionnés ici qui seront ajouté

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

## Page "Liste d'activités"
### Tests unitaires
Aucun, je vois pas lesquelles faire

### Tests d'intégration
1. Activer un filtre retire de la vue toutes les activité ne respectant pas le filtre
2. Activer plusieurs filtre met à jour correctement la liste des activités visible
3. Une activité pleine ne peut pas être rejoint
4. Le bouton rejoindre d'une activité envoi une requête de connexion
5. Si un utilisateur a pu rejoindre une activité, celle-ci disparaît de la vue des activités
6. Un utilisateur qui rejoint une activité quand un autre utilisateur a dans ça vue cette activité doit être mis à jour
7. deux utilisateurs qui rejoignent une activité avec une seul place disponible ne devrait pas arriver : l'un des deux ne sera pas mis dans celle-ci (attention à la concurence)

### Tests manuels
Vérifier que les activités ne se chevauchent pas, Vérifier le bon fonctionnement du scroll (on ne scroll que sur les activités), vérifier les boutons des activités (info et rejoindre)

## Page Carte
### Tests unitaires
1. Les informations de la carte, permettant de l'afficher, sont obtenu via appel api
2. La position de l'utilisateur est demandé -> demande système
3. Il est possible de placer un pin sur la carte à une position donnée
4. Le pin reste sur la position relative à la carte et non à l'écran
### Tests d'intégration
1. Il est possible de se déplacer sur la carte
2. Un pin se trouve sur la carte pour chaque activité existante
3. Les activités sur la carte sont les mêmes que celles dans la page liste d'activité
4. Les filtres sont fonctionnel
### Tests manuels
Il faudra ici vérifier que l'intégralité des activités se retrouvent sur la carte, et que la position indiqué dans les informations de l'activité corresponde à la position réelle

## Page Messagerie
### Tests unitaires
1. le nombre de groupe est équivalent au nombre d'activité dont l'utilisateur est actuellement inscrit
2. Chaque groupe est une instance différente, avec ses propres messages
3. Une demande de poste de message peut être fait. Si le message est nulle, on retourne une erreur
4. Les messages doivent être mis à jour dès qu'il y a modification 
### Tests d'intégration
1. L'ordre des messages est le même sur tout appareil
2. L'ouverture d'un groupe charge les messages, il est capable de différentier chaque message selon l'utilisateur en question
3. La vue de chaque utilisateur est mise à jour quand l'un d'eux envoi un message dans un groupe
4. Rejoindre une activité fait automatiquement rejoindre le groupe textuel correspondant
### Tests manuels
Créer des activités, les rejoindre et vérifier qu'on se trouve bien sur le groupe -> la création et la gestion d'activité doivent donc être fonctionnelles pour ces tests
Vérifier la mise à jour lors d'un envoi de message (avec 2 appareils)

## Page Profil
### Tests unitaires
1. Le nom ainsi que la photo de profil apparaissent bien
2. Les statistiques peuvent être mise à jour en direct
### Tests d'intégration
1. Mise à jour de statistique au moment ou une activité se termine
2. Moyen de déconnection de compte
### Tests manuels
Simplement tester la page de profil, voir que tout y est bien affiché

## Page Création d'évènement
### Tests unitaires
1. Les champs vides nécessaires empêchent de passer à la page suivante
2. Les champs vides optionnels ne bloquent pas le passage à la page suivante (uniquement description)
3. La date ne peut être que après le jour courant ou le jour courant lui même
4. un seul sport possible pour une activité
5. La page de création d'activité n'est quitté que si la base de donnée a accepté de créer l'activité 

### Tests d'intégration
1. La date et l'heure précisent un moment dans le futur, et non dans le passé. Il faut ici vérifier l'heure suivant si on est le jour J ou non

### Tests manuels
Créer des activités, de différent types, de différentes difficulté, à des temps différent. Vérifier que l'utilisateur n'ai pas la possibilité de rentrer des dates futures



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
5. Il doit être possible d'effacer une activité

### Tests manuels
- tenter de créer un évènement 2 fois d'affilé avec les mêmes informations, et s'assurer qu'il s'agisse bien de 2 évènements distincts
- Vérifier qu'on peut bien rejoindre un évenement
- Vérifier qu'en cas de suppression d'un évènement par le créateur, toutes les personnes inscrites reçoivent une notification
- Vérifier que seul le créateur d'une activité peut la supprimer
## Messagerie
### Tests unitaires
1. Un message vide n'est pas accepté
2. Un message reçu notifie toutes les personnes qui peuvent le voir
3. Un utilisateur qui rejoint une activité reçoit directement tout les messages
4. les messages sont stocké chez l'utilisateur
### Tests manuels
Envoyer des messages, vérifier qu'ils sont reçu dans un temps négligeable, vérifier que sans nouveau message, cliquer sur le groupe ne fait que charger les messages enregistré (cache)
## Profil
### Tests unitaires
1. les Statistiques peuvent être mise à jour
2. Il est possible de supprimer entièrement un compte
3. Un compte a toujours un nom d'utilisateur et une date de naissance. Il a également un mode de connexion
### Tests manuel

Créer un nouveau compte, supprimer le compte, se connecter sur un autre appareil avec un compte existant


