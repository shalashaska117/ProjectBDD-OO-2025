package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una bacheca che contiene una lista di ToDo.
 * Ogni bacheca ha un tipo e una descrizione.
 */
public class Bacheca {

    private TipoBacheca tipo;
    private String descrizione;
    private List<ToDo> toDoList;

    /**
     * Costruisce una nuova bacheca con tipo e descrizione specificati.
     * Inizializza la lista dei ToDo come vuota.
     *
     * @param tipo il tipo di bacheca (es. UNIVERSITA, LAVORO, TEMPO_LIBERO)
     * @param descrizione la descrizione della bacheca
     */
    public Bacheca(TipoBacheca tipo, String descrizione) {
        this.tipo = tipo;
        this.descrizione = descrizione;
        this.toDoList = new ArrayList<>();
    }

    /**
     * Restituisce il tipo della bacheca.
     *
     * @return il tipo della bacheca
     */
    public TipoBacheca getTipo() {
        return tipo;
    }

    /**
     * Imposta il tipo della bacheca.
     *
     * @param tipo il nuovo tipo della bacheca
     */
    public void setTipo(TipoBacheca tipo) {
        this.tipo = tipo;
    }

    /**
     * Restituisce la descrizione della bacheca.
     *
     * @return la descrizione della bacheca
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Imposta la descrizione della bacheca.
     *
     * @param descrizione la nuova descrizione della bacheca
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * Restituisce la lista dei ToDo contenuti nella bacheca.
     *
     * @return la lista dei ToDo
     */
    public List<ToDo> getToDoList() {
        return toDoList;
    }

    /**
     * Imposta la lista dei ToDo della bacheca.
     *
     * @param toDoList la nuova lista di ToDo
     */
    public void setToDoList(List<ToDo> toDoList) {
        this.toDoList = toDoList;
    }
}


