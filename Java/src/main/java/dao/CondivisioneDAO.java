package dao;

import interfaccedao.ICondivisioneDAO;
import database.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione dell'interfaccia ICondivisioneDAO.
 * Gestisce tutte le operazioni CRUD legate alla tabella "condivisione",
 * come la condivisione, la rimozione, la verifica e l'accettazione/rifiuto
 * delle richieste di condivisione dei ToDo.
 */
public class CondivisioneDAO implements ICondivisioneDAO {

    /**
     * Costruttore di default per CondivisioneDAO.
     * Inizializza l'oggetto per la gestione delle condivisioni.
     */
    public CondivisioneDAO() {
        // Nessuna inizializzazione specifica
    }

    private static final String QUERY_TODO_ID =
            "SELECT id FROM todo WHERE proprietario = ? AND tipo_bacheca = ? AND titolo = ?";


    /**
     * Inserisce una richiesta di condivisione di un ToDo con uno specifico utente.
     * Se il ToDo viene trovato, la condivisione viene registrata con stato 'PENDING'.
     *
     * @param username Lo username dell'utente destinatario della condivisione
     * @param prop Lo username del proprietario del ToDo
     * @param tipo Il tipo di bacheca a cui appartiene il ToDo (es. UNIVERSITA, LAVORO, TEMPO_LIBERO)
     * @param titolo Il titolo del ToDo da condividere
     * @return true se la condivisione è stata inserita correttamente, false altrimenti
     */
    @Override
    public boolean condividi(String username, String prop, String tipo, String titolo) {
        String queryId = QUERY_TODO_ID;
        String insertCondivisione = "INSERT INTO condivisione (username_utente, id_todo, stato) VALUES (?, ?, 'PENDING')";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmtSelect = conn.prepareStatement(queryId)) {

            stmtSelect.setString(1, prop);
            stmtSelect.setString(2, tipo);
            stmtSelect.setString(3, titolo);

            ResultSet rs = stmtSelect.executeQuery();
            if (rs.next()) {
                int idToDo = rs.getInt("id");

                try (PreparedStatement stmtInsert = conn.prepareStatement(insertCondivisione)) {
                    stmtInsert.setString(1, username);
                    stmtInsert.setInt(2, idToDo);
                    return stmtInsert.executeUpdate() > 0;
                }

            } else {
                System.err.println("ToDo non trovato per prop=" + prop + ", tipo=" + tipo + ", titolo=" + titolo);
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Rimuove la condivisione di un ToDo per un determinato utente destinatario.
     * La condivisione viene identificata tramite il proprietario, tipo di bacheca e titolo del ToDo.
     *
     * @param username Lo username del destinatario della condivisione
     * @param prop Lo username del proprietario del ToDo
     * @param tipo Il tipo di bacheca (es. UNIVERSITA, LAVORO, TEMPO_LIBERO)
     * @param titolo Il titolo del ToDo condiviso
     * @return true se almeno una riga è stata eliminata, false altrimenti
     */
    @Override
    public boolean rimuoviCondivisione(String username, String prop, String tipo, String titolo) {
        String sql = """
            DELETE FROM condivisione 
            WHERE username_utente = ? 
              AND id_todo = (
                SELECT id FROM todo WHERE proprietario = ? AND tipo_bacheca = ? AND titolo = ?
              )
        """;
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, prop);
            stmt.setString(3, tipo);
            stmt.setString(4, titolo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica se esiste già una condivisione tra il proprietario e il destinatario per uno specifico ToDo.
     * Il ToDo è identificato tramite proprietario, tipo di bacheca e titolo.
     *
     * @param username Lo username del destinatario
     * @param prop Lo username del proprietario del ToDo
     * @param tipo Il tipo di bacheca del ToDo (es. UNIVERSITA, LAVORO, TEMPO_LIBERO)
     * @param titolo Il titolo del ToDo
     * @return true se la condivisione è già presente nel database, false altrimenti
     */
    @Override
    public boolean esisteCondivisione(String username, String prop, String tipo, String titolo) {
        String sql = """
            SELECT 1 FROM condivisione 
            WHERE username_utente = ? 
              AND id_todo = (
                SELECT id FROM todo WHERE proprietario = ? AND tipo_bacheca = ? AND titolo = ?
              )
        """;
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, prop);
            stmt.setString(3, tipo);
            stmt.setString(4, titolo);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Restituisce la lista delle richieste di condivisione pendenti (stato 'PENDING') per uno specifico utente.
     * Ogni richiesta è rappresentata da un array di 3 elementi: richiedente, tipo bacheca e titolo ToDo.
     *
     * @param usernameUtente Lo username dell’utente destinatario delle richieste
     * @return Una lista di richieste di condivisione pendenti, ciascuna rappresentata come array {richiedente, tipoBacheca, titolo}
     */
    public List<String[]> getRichiestePendentiPerUtente(String usernameUtente) {
        List<String[]> richieste = new ArrayList<>();
        String sql = """
            SELECT t.proprietario AS richiedente, t.tipo_bacheca, t.titolo
            FROM condivisione c
            JOIN todo t ON c.id_todo = t.id
            WHERE c.username_utente = ? AND c.stato = 'PENDING'
        """;
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usernameUtente);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String richiedente = rs.getString("richiedente");
                String tipoBacheca = rs.getString("tipo_bacheca");
                String titolo = rs.getString("titolo");
                richieste.add(new String[] { richiedente, tipoBacheca, titolo });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return richieste;
    }

    /**
     * Aggiorna lo stato di una richiesta di condivisione per un determinato ToDo.
     * Se lo stato è "ACCEPTED", la richiesta viene aggiornata a tale stato.
     * Se lo stato è "REJECTED", la richiesta viene eliminata completamente.
     *
     * @param username Lo username del destinatario della richiesta
     * @param proprietario Lo username del proprietario del ToDo
     * @param tipo Il tipo di bacheca del ToDo (es. UNIVERSITA, LAVORO, TEMPO_LIBERO)
     * @param titolo Il titolo del ToDo condiviso
     * @param nuovoStato Il nuovo stato da assegnare alla richiesta ("ACCEPTED" o "REJECTED")
     * @return true se l’operazione ha avuto successo, false altrimenti
     */
    public boolean aggiornaStatoRichiesta(String username, String proprietario, String tipo, String titolo, String nuovoStato) {
        String queryId = QUERY_TODO_ID;
        String updateSql = "UPDATE condivisione SET stato = ? WHERE username_utente = ? AND id_todo = ?";
        String deleteSql = "DELETE FROM condivisione WHERE username_utente = ? AND id_todo = ? AND stato = 'PENDING'";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmtId = conn.prepareStatement(queryId)) {

            stmtId.setString(1, proprietario);
            stmtId.setString(2, tipo);
            stmtId.setString(3, titolo);

            ResultSet rs = stmtId.executeQuery();
            if (rs.next()) {
                int idTodo = rs.getInt("id");

                if (nuovoStato.equalsIgnoreCase("ACCEPTED")) {
                    try (PreparedStatement stmtUpdate = conn.prepareStatement(updateSql)) {
                        stmtUpdate.setString(1, nuovoStato);
                        stmtUpdate.setString(2, username);
                        stmtUpdate.setInt(3, idTodo);
                        return stmtUpdate.executeUpdate() > 0;
                    }
                } else if (nuovoStato.equalsIgnoreCase("REJECTED")) {
                    try (PreparedStatement stmtDelete = conn.prepareStatement(deleteSql)) {
                        stmtDelete.setString(1, username);
                        stmtDelete.setInt(2, idTodo);
                        return stmtDelete.executeUpdate() > 0;
                    }
                } else {
                    System.err.println("[ERRORE] Stato non valido: " + nuovoStato);
                    return false;
                }

            } else {
                System.err.println("ToDo non trovato per aggiornamento stato richiesta");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Rimuove una richiesta di condivisione in stato 'PENDING' per uno specifico ToDo.
     * Il ToDo è identificato tramite proprietario, tipo di bacheca e titolo.
     *
     * @param username Lo username dell'utente destinatario della richiesta
     * @param proprietario Lo username del proprietario del ToDo
     * @param tipo Il tipo di bacheca del ToDo (es. UNIVERSITA, LAVORO, TEMPO_LIBERO)
     * @param titolo Il titolo del ToDo
     * @return true se l'eliminazione è avvenuta con successo, false altrimenti
     */
    public boolean rimuoviRichiesta(String username, String proprietario, String tipo, String titolo) {
        String queryId =  QUERY_TODO_ID;
        String deleteSql = "DELETE FROM condivisione WHERE username_utente = ? AND id_todo = ? AND stato = 'PENDING'";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmtId = conn.prepareStatement(queryId)) {

            stmtId.setString(1, proprietario);
            stmtId.setString(2, tipo);
            stmtId.setString(3, titolo);

            ResultSet rs = stmtId.executeQuery();
            if (rs.next()) {
                int idTodo = rs.getInt("id");

                try (PreparedStatement stmtDelete = conn.prepareStatement(deleteSql)) {
                    stmtDelete.setString(1, username);
                    stmtDelete.setInt(2, idTodo);
                    return stmtDelete.executeUpdate() > 0;
                }
            } else {
                System.err.println("ToDo non trovato per rimuovi richiesta");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina tutte le condivisioni associate a un determinato ToDo.
     * Utilizzato ad esempio quando un ToDo viene eliminato dal proprietario.
     *
     * @param idToDo L'ID del ToDo per cui si vogliono rimuovere tutte le condivisioni
     * @return true se le condivisioni sono state eliminate correttamente, false in caso di errore
     */
    public boolean eliminaCondivisioniCollegate(int idToDo) {
        String sql = "DELETE FROM condivisione WHERE id_todo = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idToDo);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera gli username degli utenti con cui è stato condiviso un determinato ToDo.
     *
     * @param proprietario Lo username del proprietario del ToDo
     * @param tipoBacheca Il tipo di bacheca a cui appartiene il ToDo
     * @param titolo Il titolo del ToDo
     * @return Una lista di username dei destinatari della condivisione
     */
    @Override
    public List<String> getUtentiCondivisi(String proprietario, String tipoBacheca, String titolo) {
        List<String> utenti = new ArrayList<>();

        String sql = """
        SELECT c.username_utente AS destinatario
        FROM condivisione c
        JOIN todo t ON c.id_todo = t.id
        WHERE t.proprietario = ? AND t.tipo_bacheca = ? AND t.titolo = ?
        """;

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, proprietario);
            stmt.setString(2, tipoBacheca);
            stmt.setString(3, titolo);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                utenti.add(rs.getString("destinatario"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return utenti;
    }

}

