package interfaccedao;

import java.util.List;

/**
 * Interfaccia per la gestione delle condivisioni dei ToDo tra utenti.
 * Include operazioni per aggiungere, rimuovere, controllare e gestire richieste di condivisione.
 */
public interface ICondivisioneDAO {

    /**
     * Inserisce una richiesta di condivisione di un ToDo con uno specifico utente.
     * La richiesta viene salvata con stato iniziale 'PENDING'.
     *
     * @param username Lo username dell'utente destinatario della condivisione
     * @param proprietario Lo username del proprietario del ToDo
     * @param tipoBacheca Il tipo di bacheca a cui appartiene il ToDo (es. UNIVERSITA, LAVORO, TEMPO_LIBERO)
     * @param titoloToDo Il titolo del ToDo da condividere
     * @return true se la condivisione è stata inserita correttamente, false altrimenti
     */
    boolean condividi(String username, String proprietario, String tipoBacheca, String titoloToDo);

    /**
     * Rimuove una condivisione esistente tra due utenti per un determinato ToDo.
     *
     * @param username lo username dell'utente destinatario
     * @param proprietario lo username del proprietario del ToDo
     * @param tipoBacheca il tipo di bacheca del ToDo
     * @param titoloToDo il titolo del ToDo condiviso
     * @return true se la rimozione è avvenuta correttamente, false altrimenti
     */
    boolean rimuoviCondivisione(String username, String proprietario, String tipoBacheca, String titoloToDo);

    /**
     * Verifica se esiste già una condivisione per un determinato ToDo tra due utenti.
     *
     * @param username lo username del destinatario
     * @param proprietario lo username del proprietario del ToDo
     * @param tipoBacheca il tipo di bacheca del ToDo
     * @param titoloToDo il titolo del ToDo condiviso
     * @return true se la condivisione esiste, false altrimenti
     */
    boolean esisteCondivisione(String username, String proprietario, String tipoBacheca, String titoloToDo);

    /**
     * Restituisce le richieste di condivisione pendenti per un determinato utente.
     *
     * @param proprietario lo username del proprietario che ha ricevuto le richieste
     * @return lista di array contenenti informazioni sulle richieste pendenti
     */
    List<String[]> getRichiestePendentiPerUtente(String proprietario);

    /**
     * Aggiorna lo stato di una richiesta di condivisione (es. da pendente ad accettata).
     *
     * @param username lo username del destinatario della richiesta
     * @param proprietario lo username del proprietario del ToDo
     * @param tipoBacheca il tipo della bacheca
     * @param titoloToDo il titolo del ToDo
     * @param nuovoStato il nuovo stato della richiesta (es. "accettata", "rifiutata")
     * @return true se l'aggiornamento ha avuto successo, false altrimenti
     */
    boolean aggiornaStatoRichiesta(String username, String proprietario, String tipoBacheca, String titoloToDo, String nuovoStato);

    /**
     * Rimuove una richiesta di condivisione, indipendentemente dal suo stato.
     *
     * @param username lo username del destinatario della richiesta
     * @param proprietario lo username del proprietario del ToDo
     * @param tipoBacheca il tipo della bacheca
     * @param titoloToDo il titolo del ToDo
     * @return true se la richiesta è stata rimossa, false altrimenti
     */
    boolean rimuoviRichiesta(String username, String proprietario, String tipoBacheca, String titoloToDo);

    /**
     * Elimina tutte le condivisioni associate a un ToDo dato il suo ID.
     * Utile quando il proprietario elimina definitivamente il ToDo.
     *
     * @param id l'identificatore del ToDo
     * @return true se l'eliminazione delle condivisioni collegate ha avuto successo, false altrimenti
     */
    boolean eliminaCondivisioniCollegate(int id);

    /**
     * Restituisce la lista di username con cui è stato condiviso un determinato ToDo.
     *
     * @param proprietario Lo username del proprietario del ToDo
     * @param tipoBacheca Il tipo di bacheca (es. UNIVERSITA, LAVORO, TEMPO_LIBERO)
     * @param titolo Il titolo del ToDo condiviso
     * @return Lista di username dei destinatari
     */
    List<String> getUtentiCondivisi(String proprietario, String tipoBacheca, String titolo);
}

