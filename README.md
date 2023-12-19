# Homework 2 del corso di Ingegneria dei Dati (2023/2024)

## Descrizione del progetto
Il progetto consiste in quanto segue:
* Indicizzazione di file TXT contenuti in una directory locale;
* Interrogazione dell'indice costruito.

## Struttura del progetto
Il progetto include due classi Java i cui rispettivi metodi `main` possono essere
eseguiti, con dei parametri, da riga di comando:
* **IndexFiles.java** contiene il codice che indicizza i file .TXT contenuti in una directory locale specificata
  dall’utente (e quelli contenuti nel sottoalbero della directory);
* **SearchFiles.java** contiene il codice che interroga l’indice con una query immessa dall’utente. È possibile
  immettere query secondo la [sintassi interpretata dal Query Parser classico di Apache Lucene](
  https://lucene.apache.org/core/9_8_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html). Vengono
  restituiti i primi dieci documenti del ranking predefinito.

## Comandi
* `java com.github.lorenzopollastrini.IndexFiles [-index INDEX_PATH] [-docs DOCS_PATH] [-update]`: indicizza i file
  .TXT contenuti nella directory al percorso `DOCS_PATH` (e quelli contenuti nel sottoalbero della directory), salvando
  l'indice al percorso `INDEX_PATH`. Se si specifica `-update`, ad ogni esecuzione del comando l'indice non verrà
  cancellato e ricostruito, bensì aggiornato.
* `java com.github.lorenzopollastrini.SearchFiles [-index INDEX_PATH] [-query QUERY]`: interroga l'indice al percorso
  `INDEX_PATH` con la `QUERY` specificata, la quale può essere immessa nella [sintassi interpretata dal Query Parser
  classico di Apache Lucene](
  https://lucene.apache.org/core/9_8_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html). Va
  effettuato l'escaping di alcuni caratteri speciali interpretati da tale sintassi (ad esempio, va effettuato l'escaping
  del carattere `"` con `\"`).
