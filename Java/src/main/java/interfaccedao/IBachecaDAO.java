package interfaccedao;

import model.Bacheca;
import model.TipoBacheca;

import java.util.List;

/**
 * Interfaccia per la gestione delle bacheche nel database.
 * Fornisce operazioni per salvare e recuperare bacheche associate a un utente.
 */
public interface IBachecaDAO {

    /**
     * Salva una nuova bacheca associata a un utente nel database.
     *
     * @param bacheca la bacheca da salvare
     * @param proprietario lo username del proprietario della bacheca
     * @return true se il salvataggio ha avuto successo, false altrimenti
     */
    boolean salvaBacheca(Bacheca bacheca, String proprietario);

    /**
     * Restituisce tutte le bacheche associate a uno specifico utente.
     *
     * @param username lo username dell'utente
     * @return lista di bacheche appartenenti all'utente
     */
    List<Bacheca> findByUtente(String username);

    /**
     * Cerca una bacheca di un determinato tipo per uno specifico utente.
     *
     * @param username lo username dell'utente
     * @param tipo il tipo di bacheca da cercare
     * @return la bacheca trovata, oppure null se non esiste
     */
    Bacheca findByTipo(String username, TipoBacheca tipo);
}


