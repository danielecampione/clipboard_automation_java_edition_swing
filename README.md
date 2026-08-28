# Clipboard Automation - Clipboard History Automation on Microsoft Windows 11 and 10

## Description
This Java 1.8 project with Swing automates the use of multiple clipboard items available in Microsoft Windows 11 and 10. The program:

1. Automatically opens Notepad
2. Uses Win+V to trigger the "Clipboard History" feature of Windows 11 or 10
3. Pastes clipboard items in reverse order, for the number of items specified by the user
4. Optionally adds line breaks at the end of each pasted item

## Prerequisites
- Microsoft Windows 11 or 10 with Clipboard History feature already enabled
- Java 1.8
- Clipboard items already copied in the system clipboard, matching the number of items specified by the user in the application

## How to Use
1. Make sure the desired number of clipboard items are copied before starting the application
2. Run the program
3. Click on "Start Automation" in the graphical interface
4. The program will launch Notepad and automatically paste the items

## Technical Notes
- Uses Java’s Robot class for key automation
- Introduces appropriate delays to ensure smooth rendering
- Clean separation between business logic and GUI
- Compatible with Java 1.8

## Timing and Delays
- Waits a few seconds after pressing Win+V to allow the Clipboard History UI to render
- Includes short pauses between each paste operation

---

# Clipboard Automation - Automazione appunti della "Cronologia Appunti" di Microsoft Windows 11 e 10

## Descrizione
Questo progetto Java 1.8 con Swing automatizza l'utilizzo degli appunti multipli di Microsoft Windows 11 e 10. Il programma:

1. Apre automaticamente Notepad
2. Utilizza Win+V per aprire la "Cronologia Appunti", ovvero gli appunti multipli, di Windows 11 o 10
3. Incolla gli elementi degli appunti nell'ordine inverso, per il numero di elementi definito dall'utente
4. Aggiunge automaticamente interruzioni di riga al termine, se richiesto dall'utente

## Prerequisiti
- Sistema operativo Microsoft Windows 11 o 10 con la funzionalità "Cronologia Appunti" già attiva
- Java 1.8
- Elementi già copiati negli appunti di sistema prima dell'esecuzione del processo da parte del programma, nella stessa quantità indicata dall'utente nell'applicativo

## Come Utilizzare
1. Assicurarsi di aver copiato gli elementi negli appunti di Windows 11 o 10, nella stessa quantità indicata dall'utente nell'applicativo
2. Eseguire il programma
3. Cliccare su "Avvia Automazione" nell'interfaccia grafica
4. Il programma aprirà Notepad e incollerà automaticamente gli elementi

## Note Tecniche
- Utilizza la classe Robot di Java per l'automazione dei tasti
- Implementa pause appropriate per evitare problemi di rendering
- Separazione netta tra logica di business e interfaccia grafica
- Compatibile con Java 1.8

## Timing e Pause
- alcuni secondi di attesa dopo Win+V per il rendering della "Cronologia Appunti" e tra ogni operazione di incolla

---