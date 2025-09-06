package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe di utilità per gestire la connessione al database PostgreSQL.
 * Implementa il pattern Singleton per garantire un'unica connessione condivisa.
 */
public class ConnessioneDatabase {

    /** URL di connessione al database */
    private static final String URL = "jdbc:postgresql://localhost:5432/ProgettoToDo";

    /** Nome utente del database */
    private static final String USER = "postgres";

    /** Password dell'utente del database */
    private static final String PASSWORD = "postgres";

    /** Connessione condivisa */
    private static Connection connection;

    /**
     * Costruttore privato per impedire l'istanziazione della classe.
     */
    private ConnessioneDatabase() {
        // Prevent instantiation
    }

    /**
     * Restituisce la connessione al database. Se la connessione è chiusa o nulla, ne crea una nuova.
     *
     * @return oggetto {@link Connection} attivo al database
     * @throws SQLException se la connessione non può essere stabilita
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}

