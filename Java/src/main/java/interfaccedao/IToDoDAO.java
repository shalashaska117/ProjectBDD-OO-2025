package interfaccedao;

import model.ToDo;
import model.TipoBacheca;

import java.util.List;

/**
 * Interfaccia per la gestione dei ToDo nel database.
 * Definisce le operazioni di salvataggio, aggiornamento, recupero e eliminazione.
 */
public interface IToDoDAO {

    /**
     * Salva un nuovo ToDo nel database e aggiorna il suo ID generato automaticamente.
     *
     * @param todo il ToDo da salvare
     * @param proprietario lo username del proprietario del ToDo
     * @param tipoBacheca il tipo di bacheca a cui appartiene il ToDo
     * @return true se l'inserimento ha avuto successo, false altrimenti
     */
    boolean salva(ToDo todo, String proprietario, TipoBacheca tipoBacheca);

    /**
     * Restituisce tutti i ToDo associati a una determinata bacheca.
     *
     * @param proprietario lo username del proprietario della bacheca
     * @param tipoBacheca il tipo di bacheca
     * @return lista di ToDo appartenenti a quella bacheca
     */
    List<ToDo> trovaPerBacheca(String proprietario, TipoBacheca tipoBacheca);

    /**
     * Aggiorna i dati di un ToDo nel database usando il suo ID.
     *
     * @param todo il ToDo aggiornato
     * @param proprietario lo username del proprietario
     * @param tipoBacheca il tipo di bacheca a cui appartiene
     * @return true se l'aggiornamento ha avuto successo, false altrimenti
     */
    boolean aggiorna(ToDo todo, String proprietario, TipoBacheca tipoBacheca);

    /**
     * Elimina un ToDo dal database tramite il suo ID.
     *
     * @param id l'ID del ToDo da eliminare
     * @return true se l'eliminazione ha avuto successo, false altrimenti
     */
    boolean elimina(int id);

    /**
     * Restituisce la lista dei ToDo condivisi con uno specifico utente.
     *
     * @param username lo username dell'utente con cui sono condivisi i ToDo
     * @return lista dei ToDo condivisi
     */
    List<ToDo> getToDoCondivisiCon(String username);
}
