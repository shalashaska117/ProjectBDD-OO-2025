package gui;

import model.*;
import controller.*;

import javax.swing.*;
import java.awt.*;

/**
 * Pannello grafico che rappresenta una bacheca (Università, LAVORO, Tempo Libero),
 * mostrando i ToDo associati e permettendo l'aggiunta e gestione degli stessi.
 */
public class BoardPanel extends JPanel {

    /** Nome visualizzato della bacheca */
    private String boardName;

    /** Bacheca logica associata al pannello */
    private Bacheca bacheca;

    /** Pannello contenitore dei ToDoCardPanel */
    private JPanel toDoListPanel;

    /** Controller per gestire la logica dell’applicazione */
    private Controller controller;

    /** Pulsante per aggiungere un nuovo ToDo */
    private JButton addButton;

    /** Utente attualmente loggato */
    private Utente utenteCorrente;

    /**
     * Costruttore del pannello della bacheca.
     *
     * @param boardName nome visualizzato della bacheca
     * @param bacheca oggetto logico della bacheca
     * @param controller controller MVC
     * @param utenteCorrente utente attualmente loggato
     */
    public BoardPanel(String boardName, Bacheca bacheca, Controller controller, Utente utenteCorrente) {
        this.boardName = boardName;
        this.bacheca = bacheca;
        this.controller = controller;
        this.utenteCorrente = utenteCorrente;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(boardName));

        toDoListPanel = new JPanel();
        toDoListPanel.setLayout(new BoxLayout(toDoListPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(toDoListPanel), BorderLayout.CENTER);

        addButton = new JButton("+ Aggiungi ToDo");
        add(addButton, BorderLayout.SOUTH);

        // Registra listener solo se il controller è già presente
        if (controller != null) {
            addButton.addActionListener(e -> controller.addNewToDo(this));
        }
    }

    /**
     * Imposta l’utente corrente.
     *
     * @param utente nuovo utente corrente
     */
    public void setUtenteCorrente(Utente utente) {
        this.utenteCorrente = utente;
    }

    /**
     * Imposta il controller e aggiorna il listener del pulsante.
     *
     * @param controller nuovo controller
     */
    public void setController(Controller controller) {
        this.controller = controller;

        // Rimuove eventuali listener precedenti
        for (var al : addButton.getActionListeners()) {
            addButton.removeActionListener(al);
        }

        if (controller != null) {
            addButton.addActionListener(e -> controller.addNewToDo(this));
        }
    }

    /**
     * Imposta la bacheca logica associata al pannello.
     *
     * @param bacheca oggetto bacheca da associare
     */
    public void setBacheca(Bacheca bacheca) {
        this.bacheca = bacheca;
    }

    /**
     * Richiama il refresh grafico della bacheca.
     */
    public void aggiornaBoard() {
        refresh();
    }

    /**
     * Aggiunge graficamente un ToDo alla lista.
     *
     * @param todo oggetto ToDo da aggiungere
     */
    public void addToDo(ToDo todo) {
        ToDoCardPanel card = new ToDoCardPanel(todo, this, controller, utenteCorrente);
        toDoListPanel.add(card);
        revalidate();
        repaint();
    }

    /**
     * Rimuove un ToDo dalla bacheca attraverso il controller.
     *
     * @param todo oggetto ToDo da rimuovere
     */
    public void removeToDo(ToDo todo) {
        controller.removeToDo(this, todo);
    }

    /**
     * Rimuove tutti i ToDoCardPanel dalla board (grafica).
     */
    public void clearToDos() {
        toDoListPanel.removeAll();
        revalidate();
        repaint();
    }

    /**
     * Ricarica i ToDo dalla bacheca e da quelli condivisi.
     */
    public void refresh() {
        clearToDos();

        if (bacheca != null) {
            for (ToDo todo : bacheca.getToDoList()) {
                addToDo(todo);
            }
        }

        if (utenteCorrente != null && bacheca != null) {
            for (ToDo todo : utenteCorrente.getToDoCondivisi()) {
                if (todo.getTipoBacheca() == bacheca.getTipo() &&
                        !bacheca.getToDoList().contains(todo)) {
                    addToDo(todo);
                }
            }
        }
    }

    /**
     * Restituisce l'oggetto {@link Bacheca} associato al pannello.
     *
     * @return oggetto Bacheca associato al pannello
     */
    public Bacheca getBacheca() {
        return bacheca;
    }

    /**
     * Restituisce il nome della bacheca rappresentata dal pannello.
     *
     * @return nome della bacheca
     */
    public String getBoardName() {
        return boardName;
    }

}
