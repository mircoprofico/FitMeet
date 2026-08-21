# FitMeet — Processus de développement

Ce document décrit comment on travaille. Il sert de base à la partie
« Processus » de la présentation de phase 1.

Équipe : [@mircoprofico](https://github.com/mircoprofico) ·
[@calystoxi](https://github.com/calystoxi) ·
[@fr2c](https://github.com/fr2c) ·
[@IbuprofenLover](https://github.com/IbuprofenLover)

---

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
