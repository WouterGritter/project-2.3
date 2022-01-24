![Het winnende bod en het scorebord. Top 4 is C4, A5, A1 en D3](https://i.imgur.com/uT0vMv9.png)
_Het winnende potje en het scorebord_

# Project 2.3

Door groep C4: [Marijn Kok](https://github.com/2zqa), [Naomi Verkade](https://github.com/pluficorn), [Kevin Poelstra](https://github.com/graatje), [Wouter Gritter](https://github.com/WouterGritter) en Mike Land

Dit project is een java-applicatie geschreven om bordspellen te spelen. Het kent twee spellen: Othello en boter kaas en eieren. Het project bevat een framework voor bordspellen, ondersteuning voor offline en online wedstrijden, een minimax AI en een JavaFX frontend. Online wedstrijden gaan via Hanze's eigen multiplayer serversoftware.

Op het Othellotournooi van 2021 op de HBO-ICT studie van de Hanzehogeschool Groningen heeft dit project tegen 17 andere AIs de eerste plek gehaald.

## Lokaal draaien

Om het programma lokaal te draaien zal enkel maven geïnstalleerd moeten zijn. Wanneer maven geinstalleerd is, kan het programma uitgevoerd worden met het commando `mvn clean javafx:run`.

Om de GPUOthelloAI klasse te laten werken, moet het commando `make` uitgevoerd worden binnen de `gpu-othello` submodule folder binnen de juiste build environment. Zie `gpu-othello/README.md`.

## Toelichting AI implementatie

Onze AI wijkt waarschijnlijk veel af van andere groepen. De “core” van de AI is nog steeds minimax met bepaalde bewezen heuristische waarden voor elke board piece:

![Heuristische waardes van de plekken van het othellobord](https://i.imgur.com/55Op3h1.png)
 
_Deze heuristische waarden komen uit het artikel [“An Analysis of Heuristic in Othello”](https://github.com/Jules-Lion/kurwa/blob/master/Dokumentation/An%20Analysis%20of%20Heuristics%20in%20Othello.pdf)_
 
Wij dachten zelf dat dit alleen niet goed genoeg was om het tournament te winnen, dus we hebben twee foefjes bedacht.De eerste is vrij simpel, en dit is gebruik maken van multithreading. Minimax is erg moeilijk om multithreaded werkende te krijgen, en dit is ons ook niet volledig gelukt, maar toch kwamen we een heel eind. Omdat het ons niet lukte om de implementatie van Minimax zelf multithreaded te krijgen, hebben we gekeken naar wat wel multithreaded uitgevoerd kon worden. We kwamen er achter dat de AI elke valide zet moet uitvoeren (simuleren), en dat de eerste zetten (diepte 1 dus) zeker wel in meerdere threads uitgevoerd kunnen worden. Dit komt omdat ze allemaal dezelfde beginwaarde hanteren, namelijk het actuele bord. Onze AI maakt dus 1 thread per valide zet, en omdat iemand in onze projectgroep beschikking had tot een ryzen 7 5800x konden wij maximaal 16 threads tegelijk runnen op 8 cores (met gebruik van hyperthreading).

Het tweede foefje is de maximale bedenktijd van het tournament te nuttigen (10 seconden). Hoe lang het duurt om een bepaalde diepte af te lopen in de minimax boom verschilt veel. Dit hangt niet alleen af van de beginsituatie, maar ook de situaties verder in de boom. Daarom lopen wij constant de minimax-boom (multithreaded) af, tot de tijd om is. Soms komt de boom niet verder dan de begin-diepte van 5, maar vaak komt de AI ook tot diepte 8-9. Richting het eind, wanneer er steeds minder valide zetten mogelijk zijn, is het mogelijk om met onze AI de boom tot diepte 12-14 af te lopen, zodat wij de laatste zetten zetten met een volledige blik op de toekomst en eindsituatie. Dit is waarschijnlijk hoe wij in het eindtoernooi op het allerlaatst een grote comeback konden krijgen.

![log tijdens het spelen van het winnende potje. Er zijn twee regels tekst geselecteerd waar staat dat we het potje met zekerheid zullen winnen, ongeacht wat de tegenstander doet](https://i.imgur.com/RhmzVrT.png)
_Log tijdens het spelen van het winnende potje_
