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



# Définition de la problématique et des utilisateurs cibles : 

## À qui s’adresse FitMeet ?
FitMeet s’adresse aux personnes qui souhaitent pratiquer un sport avec d’autres, mais qui ne disposent pas forcément du bon partenaire, du bon groupe ou du bon club au moment où elles sont disponibles. L’application vise également les clubs et associations qui cherchent à rendre leurs activités plus visibles et à attirer de nouveaux participants.

Le problème principal n’est donc pas l’absence d’activités sportives, mais la difficulté à faire correspondre simplement **un sport, un lieu, un niveau et une disponibilité**.

## Le problème aujourd’hui

Trouver quelqu’un avec qui faire du sport localement reste étonnamment fragmenté. Selon le sport et la ville, les pratiquants utilisent leur cercle d’amis, des groupes WhatsApp ou Facebook, des forums, des clubs ou plusieurs applications différentes. On trouve par exemple encore des communautés sportives locales organisées essentiellement autour de groupes WhatsApp, notamment à Neuchâtel ou dans certains clubs de padel. 

Ces outils fonctionnent lorsqu’on connaît déjà la communauté, mais deviennent moins adaptés lorsqu’il faut trouver rapidement une personne correspondant à plusieurs critères : proximité, niveau, sport et disponibilité. Des acteurs spécialisés dans le padel soulignent notamment les limites de WhatsApp pour filtrer les joueurs par niveau ou intégrer facilement de nouveaux pratiquants. 

Le besoin de solutions spécialisées est confirmé par l’existence de nombreuses applications comme Smatch, PoteSport, Sports-Connect ou SportWithYou, qui proposent toutes de trouver des sportifs, événements ou clubs à proximité.  Même Strava a récemment développé une fonctionnalité permettant de rechercher des événements locaux selon le lieu et le type de sport.  Cela montre que le besoin existe, mais également que le marché reste fragmenté entre applications sociales, communautés sportives, clubs et outils spécialisés.

Enfin, la dimension sociale peut directement influencer la pratique. Les études françaises identifient notamment le manque de temps et le manque de motivation comme deux freins importants à l'activité physique. 

## Personas

**Lucas - 24 ans, sportif occasionnel**

Lucas vient d'arriver dans une nouvelle ville et aimerait jouer au badminton ou courir plusieurs fois par mois.
Besoin : trouver rapidement quelqu'un de son niveau, disponible près de chez lui.
Frustration : il ne connaît personne localement et ne veut pas rejoindre plusieurs groupes Facebook ou WhatsApp avant de pouvoir organiser une simple séance.

**Sophie - 32 ans, sportive régulière**

Sophie pratique le tennis et le running, mais ses partenaires habituels ne sont pas toujours disponibles.
Besoin : trouver une activité correspondant précisément à son emploi du temps, son niveau et sa localisation.
Frustration : organiser une séance nécessite souvent plusieurs messages et dépend fortement de son cercle social existant.

**Marc - 40 ans, responsable d'un club local**

Marc organise les activités d'un petit club de volley.
Besoin : rendre les entraînements ouverts visibles, remplir les places disponibles et toucher des personnes extérieures au club.
Frustration : les informations sont dispersées entre le site du club, Instagram, WhatsApp et le bouche-à-oreille.

## Éléments de validation

L'existence de services comme Smatch est particulièrement intéressante : l'entreprise indique déjà plusieurs milliers de partenaires potentiels à Lausanne et plusieurs centaines à Genève ou Zurich. 

Parallèlement, des applications généralistes comme Frimake ou Knockk incluent le sport parmi leurs activités, mais leur positionnement reste centré sur les rencontres et sorties sociales plutôt que sur l'organisation structurée de pratiques sportives. 

## Problem statement final

Les personnes souhaitant pratiquer un sport à plusieurs rencontrent aujourd'hui des difficultés à trouver, au bon moment et près de chez elles, des partenaires ou événements correspondant à leur sport, leur niveau et leurs disponibilités. Les solutions existantes sont souvent dispersées entre réseaux sociaux, groupes privés, clubs et applications spécialisées. FitMeet vise à centraliser cette découverte afin de rendre l'organisation et la pratique sportive locale plus simple, accessible et spontanée.

# Requirements

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







----------------
----------------
----------------


## Architecture préliminaire

FitMeet repose sur une architecture relativement simple, adaptée à un MVP mobile :

```text
### Application FitMeet :
- Kotlin Multiplatform
- Compose Multiplatform

### Supabase :
- Auth
- PostgreSQL
- Rest API
- Stockage
- Realtime API

(événements / profiles, coordionnées/images)


### Cartographie :
- OpenStreetMap + MapLibre

### Git repo
- CI/CD
- Build / Tests
- Android / iOS
```

### Flux de données

Quand l'utilisateur ouvre la carte, l'application demande au backend les événements correspondant à la zone géographique affichée.

Supabase interroge PostgreSQL avec **PostGIS**, qui permet de stocker des coordonnées géographiques et d'effectuer efficacement des recherches spatiales. 

Les événements sont retournés à l'application puis positionnés sur la carte MapLibre.

Lorsqu'un utilisateur crée un événement, le chemin inverse est utilisé :

**App → Supabase → PostgreSQL/PostGIS → App → affichage sur la carte.**

Supabase gère également l'authentification l'api rest et le stockage des fichiers, par exemple les photos de profil ou les logos des clubs. 
Le chat utilisera l'api temps réel de Supabase.

---

## Pourquoi Kotlin Multiplatform ?

Nous avons choisi **Kotlin Multiplatform avec Compose Multiplatform** pour développer Android et iOS avec une base de code largement commune. Compose permet notamment de partager directement l'interface utilisateur entre les plateformes tout en conservant la possibilité d'utiliser des API natives lorsqu'une fonctionnalité l'exige. 

Face aux principales alternatives :

**Deux applications natives Android/iOS** offriraient le meilleur contrôle plateforme par plateforme, mais impliqueraient davantage de code dupliqué et de maintenance.

Nous avons préféré Kotlin multiplatforme face à Flutter et React Native principalement car cela nous permet de découvrir Kotlin en avance du cours de développement d'application android du 5ème semestre.

Kotlin Multiplatform nous permet donc de conserver **Kotlin comme langage principal, partager la logique et l'UI, tout en gardant un accès aux API natives lorsque nécessaire**.

---

## Pourquoi Supabase ?

Pour le MVP, nous avons choisi **Supabase plutôt qu'un backend développé entièrement par nos soins**.

FitMeet nécessite déjà une base de données, de l'authentification, du stockage de fichiers et des fonctionnalités géographiques. Supabase fournit directement PostgreSQL, Auth, Storage ainsi que la possibilité d'activer PostGIS. 

Développer un backend spécifique nous donnerait davantage de contrôle, mais nous obligerait aussi à développer, déployer et maintenir tous ces composants.

Pour un MVP, Supabase permet donc de **réduire fortement la complexité backend et de concentrer le développement sur les fonctionnalités propres à FitMeet**.

Cela ne bloque pas nécessairement une évolution future : les données restent basées sur PostgreSQL, ce qui limite la dépendance à une technologie propriétaire.

---

## Pourquoi MapLibre + OpenStreetMap ?

Nous avons choisi **MapLibre** comme moteur d'affichage cartographique et les données OpenStreetMap plutôt qu'une solution directement dépendante de Google Maps ou Mapbox.

MapLibre est un projet cartographique open source et fournit des moteurs natifs pour Android et iOS. 

Cela nous apporte principalement :

- plus de liberté sur l'affichage et la source des cartes ;
- moins de dépendance envers un fournisseur unique ;

### Risque identifié : intégration KMP

Le principal risque technique de ce choix concerne Kotlin Multiplatform.

Il existe désormais **MapLibre Compose**, qui fournit un wrapper Compose Multiplatform autour des SDK MapLibre.  Cependant, cette couche est plus récente que les SDK Android et iOS natifs.

Nous considérons donc ce wrapper comme un **point de risque de l'architecture** : certaines fonctionnalités ou certains comportements pourraient nécessiter du code spécifique à Android ou iOS.

L'avantage de Kotlin Multiplatform est justement de nous permettre de conserver le reste de l'application partagé tout en implémentant cette partie nativement si nécessaire. 

### Résumé

Notre architecture privilégie donc un MVP rapidement réalisable :

**Kotlin Multiplatform** pour partager Android et iOS,  
**Supabase** pour éviter de reconstruire toute l'infrastructure backend,  
**PostgreSQL + PostGIS** pour les recherches géographiques,  
et **MapLibre + OpenStreetMap** pour garder une solution cartographique ouverte et flexible.

Le principal risque technique identifié est aujourd'hui l'intégration de MapLibre avec Compose Multiplatform, que nous prévoyons d'isoler afin de pouvoir revenir aux SDK natifs si nécessaire.









---------------------------
---------------------------
---------------------------

# FitMeet — Processus de développement

## 1. Kanban

Tableau : **https://github.com/users/mircoprofico/projects/2**

### Colonnes

| Colonne | Signification |
|---|---|
| **Phase 1** | Livrables notés le **24.08.2026**. Pas encore commencés. |
| **Phase 2** | Livrables finaux, notés le **04.09.2026**. Pas encore commencés. |
| **In progress** | Quelqu'un travaille dessus. |
| **In review** | Pull request ouverte, on attend un relecteur. |
| **Done** | Fusionné dans `main` et l'issue est fermée. |

On n'a pas un backlog unique : les cartes sont rangées par date de rendu. Une
carte avance de `Phase N` vers `In progress`, puis `In review`, puis `Done`.

### Charge de travail

On ne met pas de limite au nombre de cartes en cours. On est quatre sur trois
semaines et chacun a son domaine, donc ça n'apporterait rien.

Ce qui nous protège, c'est qu'il y a toujours au moins deux personnes par issue,
et que le point quotidien fait remonter ce qui traîne.

### Définition de « terminé »

Une carte passe en *Done* quand :

1. Toutes les cases de l'issue sont cochées.
2. `./gradlew :androidApp:assembleDebug` passe en local.
3. La pull request a été relue et approuvée par quelqu'un d'autre.
4. La branche est fusionnée dans `main`.
5. L'issue s'est fermée toute seule à la fusion, pas à la main.

### Labels

Chaque issue a un label de phase (`01-product` à `14-presentation`) et un label
de priorité :

| Label | Signification |
|---|---|
| `P1` | Livrable du 24.08.2026 |
| `P2` | Livrable final du 04.09.2026 |

### Jalons

| Jalon | Échéance |
|---|---|
| `Semaine 1 - 24.08.2026` | lundi 24.08.2026 |
| `Livrables finaux - 04.09.2026` | vendredi 04.09.2026 |

---

## 2. Workflow Git

On suit **GitHub Flow** : une branche principale, `main`, et une branche courte
par issue.

```
issue  →  branche  →  commits  →  pull request  →  relecture  →  fusion dans main
```

Les règles :

1. Une branche par issue.
2. On ne travaille jamais directement sur `main`, elle est protégée.
3. On crée la branche depuis l'issue, avec le bouton *Create a branch* dans la
   section *Development*, et on garde le nom proposé.

On n'a pas de convention de nommage, ni pour les branches, ni pour les messages
de commit. Comme le nom de branche vient de GitHub, il contient déjà le numéro
et le titre de l'issue.

Créer la branche depuis l'issue la relie aussi à celle-ci : quand la pull
request est fusionnée, GitHub ferme l'issue et déplace la carte en *Done* sans
qu'on ait à le faire.

---

## 3. Pull requests

### `main` est protégée

Un ruleset `protect-main` est actif sur la branche principale :

| Règle | Effet |
|---|---|
| `pull_request` | Push direct refusé. **1 approbation requise.** |
| `non_fast_forward` | Force push refusé. |
| `deletion` | `main` ne peut pas être supprimée. |

La règle vaut pour tout le monde, y compris le propriétaire du dépôt. Personne
ne peut approuver sa propre pull request.

### Règles

- **Qui relit** : n'importe qui sauf l'auteur, de préférence l'autre personne
  assignée à l'issue.
- **Combien d'approbations** : 1.
- **Ce qui bloque une fusion** : approbation manquante, commentaire non résolu,
  ou build rouge une fois le pipeline en place (issue #86).
- **Méthode de fusion** : `squash` ou `merge`, au choix.
- **Délai de relecture** : le jour même.

---

## 4. Responsabilités

Chaque phase a un responsable. Il n'est pas seul dessus, mais c'est lui qui
suit son avancement et qui répond aux questions du jury sur cette partie.

| Phase | Responsable |
|---|---|
| 01 — Produit et exigences | @calystoxi |
| 02 — Processus de projet | @mircoprofico |
| 03 — Design UX / UI | @IbuprofenLover |
| 04 — Landing page | @IbuprofenLover |
| 05 — Supabase | @fr2c |
| 06 — Fondations de l'application mobile | @mircoprofico |
| 07 — Authentification et profils | @fr2c |
| 08 — Événements | @calystoxi |
| 09 — Carte et géolocalisation | @calystoxi |
| 10 — Recherche et filtres | @calystoxi |
| 11 — Tests | @IbuprofenLover |
| 12 — DevOps / CI-CD | @mircoprofico |
| 13 — Documentation | @fr2c |
| 14 — Présentation | Tous le monde |

### Assignation

Il y a au moins deux personnes sur chaque issue, comme ça une absence ne bloque
pas un livrable. La première porte l'issue, l'autre contribue et relit.

La répartition de départ n'est pas figée. Chacun peut se réassigner une issue,
ou la passer à quelqu'un d'autre s'il est chargé. On le dit au point quotidien.

### Rythme quotidien

Un point par jour, comme demandé dans le cours : ce qui a avancé, ce qui bloque,
et ce que chacun prend ensuite.
