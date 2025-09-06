package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un utente del sistema, con credenziali, bacheche personali e ToDo condivisi.
 */
public class Utente {

    private String username;
    private String password;
    private List<Bacheca> bacheche;
    private List<ToDo> toDoCondivisi;

    /**
     * Costruisce un nuovo utente con username e password specificati.
     * Inizializza le liste di bacheche e ToDo condivisi come vuote.
     *
     * @param username lo username dell'utente
     * @param password la password dell'utente
     */
    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
        this.bacheche = new ArrayList<>();
        this.toDoCondivisi = new ArrayList<>();
    }

    /**
     * Restituisce lo username dell'utente.
     *
     * @return lo username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Imposta un nuovo username per l'utente.
     *
     * @param username il nuovo username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Restituisce la password dell'utente.
     *
     * @return la password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Imposta una nuova password per l'utente.
     *
     * @param password la nuova password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Restituisce la lista delle bacheche personali dell'utente.
     *
     * @return la lista delle bacheche
     */
    public List<Bacheca> getBacheche() {
        return bacheche;
    }

    /**
     * Imposta la lista delle bacheche personali dell'utente.
     *
     * @param bacheche la nuova lista di bacheche
     */
    public void setBacheche(List<Bacheca> bacheche) {
        this.bacheche = bacheche;
    }

    /**
     * Restituisce la lista dei ToDo condivisi con l'utente.
     *
     * @return la lista dei ToDo condivisi
     */
    public List<ToDo> getToDoCondivisi() {
        return toDoCondivisi;
    }

    /**
     * Imposta la lista dei ToDo condivisi con l'utente.
     *
     * @param toDoCondivisi la nuova lista di ToDo condivisi
     */
    public void setToDoCondivisi(List<ToDo> toDoCondivisi) {
        this.toDoCondivisi = toDoCondivisi;
    }

    /**
     * Restituisce una rappresentazione testuale dell'utente,
     * comprensiva dello username e dei titoli dei ToDo condivisi.
     *
     * @return stringa descrittiva dell'utente
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Username: ").append(username).append("\n");
        sb.append("ToDo Condivisi: ");

        if (toDoCondivisi != null && !toDoCondivisi.isEmpty()) {
            for (ToDo t : toDoCondivisi) {
                sb.append("\n  - ").append(t.getTitolo());
            }
        } else {
            sb.append("Nessuno");
        }

        return sb.toString();
    }

    /**
     * Aggiunge una bacheca alla lista delle bacheche dell'utente.
     *
     * @param bacheca la bacheca da aggiungere
     */
    public void aggiungiBacheca(Bacheca bacheca) {
        this.bacheche.add(bacheca);
    }

    /**
     * Aggiunge un ToDo condiviso all'utente, se non già presente.
     *
     * @param toDo il ToDo condiviso da aggiungere
     */
    public void aggiungiToDoCondiviso(ToDo toDo) {
        if (!this.toDoCondivisi.contains(toDo)) {
            this.toDoCondivisi.add(toDo);
        }
    }

    /**
     * Rimuove la condivisione di un ToDo per l'utente.
     *
     * @param toDo il ToDo da rimuovere
     */
    public void rimuoviToDoCondiviso(ToDo toDo) {
        if (toDoCondivisi != null && toDoCondivisi.contains(toDo)) {
            toDo.rimuoviCondivisionePer(this);
        }
    }
}


