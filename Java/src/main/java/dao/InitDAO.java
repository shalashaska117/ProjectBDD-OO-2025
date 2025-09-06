package dao;

import database.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Classe di utilità per l'inizializzazione del database.
 * Inserisce i valori predefiniti per le tabelle `tipobacheca` e `statotodo`,
 * se non già presenti.
 */
public class InitDAO {

    /**
     * Costruttore privato per impedire l'istanziazione della classe.
     * La classe contiene solo metodi statici di utilità.
     */
    private InitDAO() {
        // Classe di utility: non istanziabile
    }

    /**
     * Inserisce i tipi di bacheca e gli stati dei ToDo nel database,
     * solo se non sono già presenti.
     *
     * Utilizza la clausola `ON CONFLICT DO NOTHING` per evitare duplicati.
     */
    public static void inserisciTipiEStati() {
        try (Connection conn = ConnessioneDatabase.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                INSERT INTO tipobacheca (nome) VALUES 
                ('Universita'), 
                ('Lavoro'), 
                ('TempoLibero')
                ON CONFLICT DO NOTHING
            """);

            stmt.executeUpdate("""
                INSERT INTO statotodo (nome) VALUES 
                ('Completato'), 
                ('NonCompletato')
                ON CONFLICT DO NOTHING
            """);

        } catch (SQLException e) {
            System.err.println("Errore durante l'inizializzazione:");
            e.printStackTrace();
        }
    }
}



