package interfaccedao;

import model.Utente;

/**
 * Interfaccia per la gestione dei dati persistenti relativi agli utenti.
 * Definisce le operazioni CRUD essenziali per l'entità Utente.
 */
public interface IUtenteDAO {

    /**
     * Salva un nuovo utente nel database.
     *
     * @param utente l'utente da salvare
     * @return true se l'operazione ha avuto successo, false altrimenti
     */
    boolean salvaUtente(Utente utente);

    /**
     * Trova un utente in base a username e password.
     * Usato principalmente per il login.
     *
     * @param username lo username dell'utente
     * @param password la password dell'utente
     * @return l'utente trovato o null se non esiste
     */
    Utente findByUsernameAndPassword(String username, String password);

    /**
     * Trova un utente in base allo username.
     *
     * @param username lo username da cercare
     * @return l'utente corrispondente o null se non trovato
     */
    Utente findByUsername(String username);

    /**
     * Elimina un utente in base allo username.
     *
     * @param username lo username dell'utente da eliminare
     * @return true se l'eliminazione ha avuto successo, false altrimenti
     */
    boolean eliminaUtente(String username);
}


