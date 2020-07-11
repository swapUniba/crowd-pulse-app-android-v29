# Applicazione Android per l'estrazione ed invio di dati al server Myrror
Il seguente framework è formato da un insieme di package che collaborano tra loro
per estrarre i dati relativi al GPS , agli Accounts associati allo smartphone , alle attività svolte dall'utente , ai
dati statistici di rete , delle app installate sul dispositivo e del display. In seguito i dati vengono inviati al server sotto formato di oggetti Json.

## Elenco dei package:
* business_object: contiene le classi rappresentati i dati.
Ognuna delle classi presenti in questo package verrà mappata all'interno del DB. Ogni dato verrà inviato  al server sotto formato Json , per questo ogni classe in questo package estende AbstractData la quale implementa 3 metodi per la gestione e trasformazione degli oggetti in Json.

* handlers: sono presenti le classi di servizio (una per ogni business_object) che a partire dai dati presenti nel dispositivo li convertono nelle classi indicate nel package business_object, così da poterle utilizzare o inviare, in più per ogni singolo dato possono essere chiamate le funzioni di  salvataggio o aggiornamento del dato nel DB interno l’applicazione e le apposite funzioni per verificare se è possibile rilevare il dato o vi è necessità di aspettare.

* comunication: Questo package contiene tutte le classi necessarie per la comunicazione con l’esterno, come l’istanziazione della socket per la comunicazione con il server e la funzione di passaggio dei dati al server.

* config: Questo package contiene le costanti utilizzate da tutte le classi, le SharedPreferences di Andorid relative i dati da memorizzare e quelli da non memorizzare e infine contiene l'activity (Pannello di Controllo) che si occupa di gestire se condividere o meno un dato con il server.

* database : contiene tutte le classi utilizzate per lo storage dei dati e degli oggetti all’interno del DB SQLLite di Andorid.

* main: contiene le activity dell'applicazione (a parte il pannello di controllo presente in config) e le classi che si occupano di estrarre i dati (BackgroundService) e inviarli al server (SendDataWorker).

* reactive: contiene le classi che si occupano della riattivazione dei servizi di estrazione ed invio al server dei dati.

* utility: contiene due classi contenenti metodi utilizzati nella maggior parte delle classi per gestire e ottenere le date.

* debug: contiene le classi per svolgere le attività di debug sul codice.


## Installazione dell'Applicazione
L'installazione può avvenire principalmente in due modi:
1. Installando il solo comando ADB (https://www.softstore.it/guida-come-installare-adb-su-windows/) ed eseguendo il comando: ```adb install com.swapuniba.crowdpulse su terminale``` .
2. Eseguendo il progetto con Android Sudio 3.2 o versioni successive.

## Configurazione delle principali funzionalità

### Estrazione dei dati:
L'estrazione dei dati avviene attraverso la creazione di un allarme attivato dopo essere trascorso il minore intervallo definito nel Pannello di Controllo.
Dopo l'attivazione, la classe BackgroundService si occuperà di ricevere l'attivazione ed estrarre e salvare i dati.
L'estrazione può essere effettuata in un minimo di 30 minuti e un massimo di 12 ore. Da precisare che negli orari notturni l'allarme viene attivato con un intervallo maggiore : 00:00 , 03:00 e 06:00.
* Per modificare i tempi di estrazione dei dati bisogna modificare opportunamente il metodo **reactive()** della classe BackgroundService



### Invio dei dati al server:
L'invio dei dati al server avviene attraverso la creazione di un lavoro eseguibile dopo 5m dalla prima esecuzione dell'applicazione e in seguito dopo ogni 6 ore. In entrambi i casi è necessario che il telefono sia collegato al cavo di ricarica.
* Per modificare i tempi di invio dei dati al server bisogna modificare la variabile **send_hours** presente nella classe Constants.


### Controllo giornaliero
Il controllo giornaliero è un lavoro che ha il compito di controllare se l'allarme e il lavoro di invio al server sono attivi.
L'esecuzione di questo lavoro viene effettuata in due tempi: 
1. Quando l'applicazione viene aperta ;
2. Ogni 24 ore se il telefono è in caria;
* Per modificare i tempi di esecuzione del lavoro di controllo modificare la variabile **checkDilay_hours** presente nella classe Constants.


### Debug:
Le attività di debug possono essere attivate modificando in true la variabile booleana **SHOW_DEBUG**. Questo darà la possibilità a chi sta testando l'applicazione di visualizzare un ulteriore bottone in Pannello di Controllo per raggiungere le schermate di testing.
Alcuni comandi utili da eseguire sul terminale sono i seguenti:
* Per verificare l'effettiva esistenza dell'allarme aprire il terminale ed eseguire questa riga di codice: ```adb shell dumpsys alarm com.swapuniba.crowdpulse``` (per comprendere la tabella visualizzata  visita: https://stackoverflow.com/questions/28742884/how-to-read-adb-shell-dumpsys-alarm-output/31600886)
* Per verificare l'effettiva esistenza del lavoro di invio al server o del lavoro per il controllo giornaliero aprire il terminale ed eseguire questa riga di codice: ```adb shell dumpsys jobscheduler com.swapuniba.crowdpulse```.


##Bug
E' possibile che ci sia un bug nell'applicazione riguardo agli intervalli. Può accadere che i tempi sul sito vengano settati a dei valori minori di 30 minuti per le attività e il GPS, per questo è necessario controllare sul sito queste situazioni.
