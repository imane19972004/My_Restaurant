# SopiaTech Eats – Team I-1

## Équipe projet
- Fares Kobbi – Software Architect (SA)
- Youssef Ben Mzoughia – Software Architect (SA)
- Younes Hammoud – Operations (OPS)
- Rajaa Tchani – Product Owner (PO)
- Imane Amraoui – Quality Assurance (QA)

## Installer et lancer le projet
1. **Prérequis**
    - JDK 17
    - Maven 3.9+
2. **Cloner le dépôt**
   ```bash
   git clone https://github.com/PNS-Conception/ste-25-26-team-i-1.git
   cd ste-25-26-team-i-1
   ```
3. **Construire et exécuter les tests**
   ```bash
   mvn clean package
   mvn test
   ```
4. **Lancer l'application**
    - Ce dépôt propose pour l'instant l'ossature du projet backend. Les prochaines itérations introduiront les modules applicatifs.

## Structure du projet
```
.
├── doc/           # Documentation fonctionnelle et technique
├── src/
│   ├── main/      # Code applicatif (à compléter au fil des sprints)
│   └── test/      # Jeux de tests JUnit & Cucumber
├── pom.xml        # Configuration Maven (JDK 17, Cucumber 7, JUnit 5)
└── README.md      # Présentation du projet
```

Cette structure standard permet de séparer clairement le code de production et les tests. Les dépendances (Cucumber, JUnit, etc.) sont centralisées dans le `pom.xml`.

## Tableau Kanban de l'équipe
[Github – Kanban SopiaTech Eats Team I-1](https://github.com/orgs/PNS-Conception/projects/91)