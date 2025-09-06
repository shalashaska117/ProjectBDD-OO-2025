package dao;

import model.Bacheca;
import model.TipoBacheca;
import database.ConnessioneDatabase;
import interfaccedao.IBachecaDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione dell'interfaccia {@link IBachecaDAO}.
 * Gestisce le operazioni CRUD relative alla tabella "bacheca" del database.
 * Fornisce metodi per salvare, cercare per utente e per tipo di bacheca.
 */
public class BachecaDAO implements IBachecaDAO {

    /**
     * Costruttore di default per BachecaDAO.
     * Inizializza l'oggetto per gestire le operazioni sulla tabella "bacheca".
     */
    public BachecaDAO() {
        // Nessuna inizializzazione specifica richiesta
    }

    /**
     * Salva una nuova bacheca per un dato utente.
     *
     * @param bacheca     L'oggetto {@link Bacheca} da salvare.
     * @param proprietario Lo username del proprietario della bacheca.
     * @return true se l'inserimento è avvenuto con successo, false altrimenti.
     */
    @Override
    public boolean salvaBacheca(Bacheca bacheca, String proprietario) {
        String sql = "INSERT INTO bacheca (tipo, descrizione, proprietario) VALUES (?, ?, ?)";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bacheca.getTipo().name());
            stmt.setString(2, bacheca.getDescrizione());
            stmt.setString(3, proprietario);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Trova tutte le bacheche associate a un determinato utente.
     *
     * @param username Lo username dell’utente.
     * @return Una lista di {@link Bacheca} appartenenti all’utente.
     */
    @Override
    public List<Bacheca> findByUtente(String username) {
        List<Bacheca> lista = new ArrayList<>();
        String sql = "SELECT proprietario, tipo, descrizione FROM bacheca WHERE proprietario = ?";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Bacheca b = new Bacheca(
                        TipoBacheca.valueOf(rs.getString("tipo")),
                        rs.getString("descrizione")
                );
                lista.add(b);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Trova una bacheca di un determinato tipo per un utente.
     *
     * @param username Lo username dell’utente.
     * @param tipo     Il tipo di bacheca ({@link TipoBacheca}).
     * @return L’oggetto {@link Bacheca} se trovato, altrimenti null.
     */
    @Override
    public Bacheca findByTipo(String username, TipoBacheca tipo) {
        String sql = "SELECT proprietario, tipo, descrizione FROM bacheca WHERE proprietario = ? AND tipo = ?";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, tipo.name());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Bacheca(
                        TipoBacheca.valueOf(rs.getString("tipo")),
                        rs.getString("descrizione")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

