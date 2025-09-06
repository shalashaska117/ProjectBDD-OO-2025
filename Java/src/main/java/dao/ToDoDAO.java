package dao;

import model.ToDo;
import model.StatoToDo;
import model.TipoBacheca;
import interfaccedao.IToDoDAO;
import database.ConnessioneDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione di {@link IToDoDAO} per la gestione dei ToDo nel database PostgreSQL.
 * Fornisce operazioni CRUD e metodi di supporto per la gestione di ToDo e condivisioni.
 */
public class ToDoDAO implements IToDoDAO {

    /**
     * Costruttore di default per la classe ToDoDAO.
     * Inizializza un oggetto per gestire le operazioni sulla tabella ToDo.
     */
    public ToDoDAO() {
        // Nessuna inizializzazione specifica richiesta
    }

    /**
     * Salva un nuovo ToDo nel database, aggiornando le posizioni esistenti e assegnando un nuovo ID.
     *
     * @param todo L'oggetto {@link ToDo} da salvare
     * @param proprietario Il proprietario del ToDo
     * @param tipoBacheca Il tipo di bacheca associato
     * @return true se il salvataggio è avvenuto correttamente, false altrimenti
     */
    @Override
    public boolean salva(ToDo todo, String proprietario, TipoBacheca tipoBacheca) {
        String aggiornaPosizioni = "UPDATE todo SET posizione = posizione + 1 WHERE proprietario = ? AND tipo_bacheca = ?";
        String insert = "INSERT INTO todo (titolo, descrizione, data_scadenza, colore, stato, url, immagine, posizione, proprietario, tipo_bacheca) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConnessioneDatabase.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtUpdate = conn.prepareStatement(aggiornaPosizioni)) {
                stmtUpdate.setString(1, proprietario);
                stmtUpdate.setString(2, tipoBacheca.name());
                stmtUpdate.executeUpdate();
            }

            try (PreparedStatement stmtInsert = conn.prepareStatement(insert)) {
                stmtInsert.setString(1, todo.getTitolo());
                stmtInsert.setString(2, todo.getDescrizione());
                stmtInsert.setString(3, todo.getDataDiScadenza());
                stmtInsert.setString(4, todo.getColore());
                stmtInsert.setString(5, todo.getStato().name());
                stmtInsert.setString(6, todo.getUrl());
                stmtInsert.setBytes(7, todo.getImmagine());
                stmtInsert.setInt(8, 1); // sempre prima posizione
                stmtInsert.setString(9, proprietario);
                stmtInsert.setString(10, tipoBacheca.name());

                ResultSet rs = stmtInsert.executeQuery();
                if (rs.next()) {
                    todo.setId(rs.getInt("id"));
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Restituisce tutti i ToDo di una determinata bacheca, ordinati per posizione.
     *
     * @param proprietario Username del proprietario
     * @param tipoBacheca Tipo di bacheca
     * @return Lista di ToDo ordinati per posizione
     */
    @Override
    public List<ToDo> trovaPerBacheca(String proprietario, TipoBacheca tipoBacheca) {
        List<ToDo> lista = new ArrayList<>();
        String sql = "SELECT * FROM todo WHERE proprietario = ? AND tipo_bacheca = ? ORDER BY posizione ASC";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, proprietario);
            stmt.setString(2, tipoBacheca.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ToDo todo = new ToDo(
                        rs.getString("titolo"),
                        rs.getString("data_scadenza"),
                        rs.getString("url"),
                        rs.getBytes("immagine"),
                        rs.getString("descrizione"),
                        rs.getString("colore")
                );
                todo.setId(rs.getInt("id"));
                todo.setStato(StatoToDo.valueOf(rs.getString("stato")));
                todo.setPosizione(rs.getInt("posizione"));
                todo.setProprietario(proprietario);
                todo.setTipoBacheca(tipoBacheca);
                lista.add(todo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Aggiorna un ToDo esistente nel database.
     *
     * @param todo Il ToDo aggiornato
     * @param proprietario Il proprietario del ToDo
     * @param tipoBacheca La bacheca a cui appartiene
     * @return true se l'aggiornamento ha avuto successo, false altrimenti
     */
    @Override
    public boolean aggiorna(ToDo todo, String proprietario, TipoBacheca tipoBacheca) {
        String sql = "UPDATE todo SET titolo = ?, descrizione = ?, data_scadenza = ?, colore = ?, stato = ?, " +
                "url = ?, immagine = ?, posizione = ? WHERE id = ?";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, todo.getTitolo());
            stmt.setString(2, todo.getDescrizione());
            stmt.setString(3, todo.getDataDiScadenza());
            stmt.setString(4, todo.getColore());
            stmt.setString(5, todo.getStato().name());
            stmt.setString(6, todo.getUrl());

            if (todo.getImmagine() != null) {
                stmt.setBytes(7, todo.getImmagine());
            } else {
                stmt.setNull(7, java.sql.Types.BINARY);
            }

            stmt.setInt(8, todo.getPosizione());
            stmt.setInt(9, todo.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un ToDo dal database in base al suo ID.
     *
     * @param id L'identificatore del ToDo
     * @return true se l'eliminazione ha avuto successo, false altrimenti
     */
    @Override
    public boolean elimina(int id) {
        String sql = "DELETE FROM todo WHERE id = ?";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Restituisce l'elenco dei ToDo condivisi con un utente e accettati.
     *
     * @param username L'username dell'utente a cui sono stati condivisi i ToDo
     * @return Lista di {@link ToDo} condivisi accettati
     */
    @Override
    public List<ToDo> getToDoCondivisiCon(String username) {
        List<ToDo> lista = new ArrayList<>();
        String sql = """
            SELECT t.id, t.titolo, t.data_scadenza, t.url, t.immagine, t.descrizione,
                   t.colore, t.posizione, t.stato AS stato_todo, t.proprietario, t.tipo_bacheca
            FROM condivisione c
            JOIN todo t ON c.id_todo = t.id
            WHERE c.username_utente = ? AND c.stato = 'ACCEPTED'
            ORDER BY t.tipo_bacheca, t.posizione
        """;

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ToDo todo = new ToDo(
                        rs.getString("titolo"),
                        rs.getString("data_scadenza"),
                        rs.getString("url"),
                        rs.getBytes("immagine"),
                        rs.getString("descrizione"),
                        rs.getString("colore")
                );
                todo.setId(rs.getInt("id"));
                todo.setPosizione(rs.getInt("posizione"));
                todo.setStato(StatoToDo.valueOf(rs.getString("stato_todo")));
                todo.setProprietario(rs.getString("proprietario"));
                todo.setTipoBacheca(TipoBacheca.valueOf(rs.getString("tipo_bacheca")));
                lista.add(todo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}



