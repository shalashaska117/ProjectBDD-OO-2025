package dao;

import model.Utente;
import interfaccedao.IUtenteDAO;
import database.ConnessioneDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione dell'interfaccia {@link IUtenteDAO} per la gestione della tabella "utente".
 * Esegue le operazioni CRUD di base sul database PostgreSQL.
 */
public class UtenteDAO implements IUtenteDAO {

    /**
     * Costruttore di default per la classe UtenteDAO.
     * Crea un nuovo oggetto per accedere alla tabella degli utenti nel database.
     */
    public UtenteDAO() {
        // Nessuna inizializzazione necessaria
    }

    /**
     * Salva un nuovo utente nel database.
     *
     * @param utente L'oggetto {@link Utente} da inserire
     * @return true se l'inserimento ha avuto successo, false altrimenti
     */
    @Override
    public boolean salvaUtente(Utente utente) {
        String sql = "INSERT INTO utente (username, password) VALUES (?, ?)";

        utente.setUsername(utente.getUsername().trim().toLowerCase());

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, utente.getUsername());
            stmt.setString(2, utente.getPassword());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cerca un utente nel database usando username e password.
     *
     * @param username L'username dell'utente
     * @param password La password dell'utente
     * @return L'oggetto {@link Utente} trovato o null se non esiste
     */
    @Override
    public Utente findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT username, password FROM utente WHERE username = ? AND password = ?";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Utente(rs.getString("username"), rs.getString("password"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cerca un utente nel database solo tramite username.
     *
     * @param username L'username da cercare
     * @return L'oggetto {@link Utente} trovato o null se non esiste
     */
    @Override
    public Utente findByUsername(String username) {
        String sql = "SELECT username, password FROM utente WHERE username = ?";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Utente(rs.getString("username"), rs.getString("password"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Elimina un utente dal database in base allo username.
     *
     * @param username Lo username dell'utente da eliminare
     * @return true se l'eliminazione ha avuto successo, false altrimenti
     */
    @Override
    public boolean eliminaUtente(String username) {
        try (Connection conn = ConnessioneDatabase.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Elimina condivisioni ricevute
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM condivisione WHERE username_utente = ?")) {
                stmt.setString(1, username);
                stmt.executeUpdate();
            }

            // 2. Ottieni tutti gli ID dei ToDo dell’utente
            List<Integer> idToDoUtente = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id FROM todo WHERE proprietario = ?")) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        idToDoUtente.add(rs.getInt("id"));
                    }
                }
            }

            // 3. Elimina condivisioni fatte
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM condivisione WHERE id_todo = ?")) {
                for (Integer id : idToDoUtente) {
                    stmt.setInt(1, id);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }


            // 4. Elimina i ToDo
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM todo WHERE proprietario = ?")) {
                stmt.setString(1, username);
                stmt.executeUpdate();
            }

            // 5. Elimina le bacheche
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM bacheca WHERE proprietario = ?")) {
                stmt.setString(1, username);
                stmt.executeUpdate();
            }

            // 6. Elimina l’utente
            boolean result;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM utente WHERE username = ?")) {
                stmt.setString(1, username);
                result = stmt.executeUpdate() > 0;
            }

            conn.commit();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * Verifica se un utente esiste già nel database.
     *
     * @param username Lo username da verificare
     * @return true se l'utente esiste, false altrimenti
     */
    public boolean esisteUtente(String username) {
        String sql = "SELECT 1 FROM utente WHERE username = ?";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true se almeno una riga è presente
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}



