package controller;

import gui.*;
import model.*;
import dao.*;
import interfaccedao.*;

import javax.swing.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller principale che gestisce la logica dell'applicazione ToDo.
 * Si occupa delle operazioni CRUD sui ToDo, del caricamento utente e della gestione delle bacheche.
 */
public class Controller {

    /** Pannello per la bacheca "Università". */
    private final BoardPanel universitaBoard;

    /** Pannello per la bacheca "Lavoro". */
    private final BoardPanel lavoroBoard;

    /** Pannello per la bacheca "Tempo Libero". */
    private final BoardPanel tempoLiberoBoard;

    /** DAO per la gestione degli utenti. */
    private final IUtenteDAO utenteDAO;

    /** DAO per la gestione delle bacheche. */
    private final IBachecaDAO bachecaDAO;

    /** DAO per la gestione dei ToDo. */
    private final IToDoDAO toDoDAO;

    /** DAO per la gestione delle condivisioni. */
    private final ICondivisioneDAO condivisioneDAO = new CondivisioneDAO();

    /** Utente attualmente autenticato nel sistema. */
    private Utente utenteCorrente;


    /**
     * Costruttore del Controller.
     * Inizializza i pannelli bacheca e i DAO per l'accesso ai dati.
     *
     * @param universitaBoard pannello per la bacheca Università
     * @param lavoroBoard pannello per la bacheca LAVORO
     * @param tempoLiberoBoard pannello per la bacheca Tempo Libero
     */
    public Controller(BoardPanel universitaBoard, BoardPanel lavoroBoard, BoardPanel tempoLiberoBoard) {
        this.universitaBoard = universitaBoard;
        this.lavoroBoard = lavoroBoard;
        this.tempoLiberoBoard = tempoLiberoBoard;

        this.utenteDAO = new UtenteDAO();
        this.bachecaDAO = new BachecaDAO();
        this.toDoDAO = new ToDoDAO();
    }

    /**
     * Aggiunge un nuovo ToDo alla bacheca selezionata.
     *
     * @param board pannello della bacheca dove aggiungere il ToDo
     */
    public void addNewToDo(BoardPanel board) {
        Bacheca bacheca = board.getBacheca();

        ToDo nuovoToDo = new ToDo("", "", "", null, "", "FFFFFF");

        ToDoFormDialog dialog = new ToDoFormDialog(nuovoToDo, true, this, getUtenteCorrente(), bacheca.getTipo());
        dialog.setModal(true);
        dialog.pack();
        dialog.setLocationRelativeTo(board);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            if (!nuovoToDo.getTitolo().isEmpty()) {
                toDoDAO.salva(nuovoToDo, utenteCorrente.getUsername(), bacheca.getTipo());
                board.clearToDos();
                List<ToDo> todos = toDoDAO.trovaPerBacheca(utenteCorrente.getUsername(), bacheca.getTipo());
                bacheca.setToDoList(todos);
                todos.forEach(board::addToDo);
                board.aggiornaBoard();
            } else {
                JOptionPane.showMessageDialog(board, "Il titolo non può essere vuoto.");
            }
        }
    }

    /**
     * Rimuove un ToDo dalla bacheca. Se l'utente è il proprietario viene eliminato completamente,
     * altrimenti viene rimossa solo la condivisione.
     *
     * @param board pannello della bacheca
     * @param todo ToDo da rimuovere
     */
    public void removeToDo(BoardPanel board, ToDo todo) {
        Bacheca bacheca = board.getBacheca();

        int option = JOptionPane.showConfirmDialog(board,
                "Sei sicuro di voler rimuovere questo ToDo?",
                "Conferma Rimozione",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (option == JOptionPane.YES_OPTION && bacheca != null) {
            boolean success = false;
            String prop = todo.getProprietario();

            if (prop != null && prop.equals(utenteCorrente.getUsername())) {
                boolean condivisioniEliminate = condivisioneDAO.eliminaCondivisioniCollegate(todo.getId());
                success = toDoDAO.elimina(todo.getId());
            } else if (prop != null) {
                success = condivisioneDAO.rimuoviCondivisione(
                        utenteCorrente.getUsername(),
                        prop,
                        bacheca.getTipo().name(),
                        todo.getTitolo()
                );

                if (success) {
                    utenteCorrente.rimuoviToDoCondiviso(todo);
                }
            }
            if (success) {
                List<ToDo> todos = toDoDAO.trovaPerBacheca(utenteCorrente.getUsername(), bacheca.getTipo());
                bacheca.setToDoList(todos);
                board.clearToDos();
                board.refresh();
            } else {
                JOptionPane.showMessageDialog(board, "Errore durante l'eliminazione dal database.");
            }
        }
    }

    /**
     * Carica l'utente corrente e tutte le sue bacheche e ToDo.
     * Mostra avvisi per eventuali ToDo scaduti non completati.
     *
     * @param username nome utente da caricare
     */
    public void loadUser(String username) {
        utenteCorrente = utenteDAO.findByUsername(username);
        if (utenteCorrente == null) {
            JOptionPane.showMessageDialog(null, "Utente non trovato.");
            return;
        }

        universitaBoard.setBacheca(null);
        lavoroBoard.setBacheca(null);
        tempoLiberoBoard.setBacheca(null);

        LocalDate oggi = LocalDate.now();

        for (TipoBacheca tipo : TipoBacheca.values()) {
            Bacheca b = bachecaDAO.findByTipo(username, tipo);
            if (b == null) continue;

            List<ToDo> todos = toDoDAO.trovaPerBacheca(username, tipo);

            StringBuilder avvisi = new StringBuilder();

            for (ToDo todo : todos) {
                if (todo.getDataDiScadenza() != null && !todo.getDataDiScadenza().isEmpty()) {
                    try {
                        LocalDate dataScadenza = LocalDate.parse(todo.getDataDiScadenza());
                        if (!dataScadenza.isAfter(oggi)) {
                            if (todo.getStato() == StatoToDo.COMPLETATO) {
                                todo.setStato(StatoToDo.NON_COMPLETATO);
                                toDoDAO.aggiorna(todo, username, tipo);
                            }
                            if (todo.getStato() == StatoToDo.NON_COMPLETATO) {
                                avvisi.append("- ").append(todo.getTitolo()).append("\n");
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Errore parsing data: " + todo.getDataDiScadenza());
                    }
                }
            }

            b.setToDoList(todos);

            BoardPanel target = switch (tipo) {
                case UNIVERSITA -> universitaBoard;
                case LAVORO -> lavoroBoard;
                case TEMPO_LIBERO -> tempoLiberoBoard;
            };

            target.setBacheca(b);

            if (!avvisi.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Attenzione! I seguenti ToDo nella bacheca \"" + tipo.name() + "\" sono scaduti e non completati:\n\n" + avvisi,
                        "ToDo Scaduti",
                        JOptionPane.WARNING_MESSAGE);
            }
        }

        List<ToDo> condivisi = toDoDAO.getToDoCondivisiCon(username);
        utenteCorrente.setToDoCondivisi(condivisi);
    }

    /**
     * Effettua il login dell'utente verificando username e password.
     *
     * @param username nome utente
     * @param password password utente
     * @param parent riferimento alla finestra principale
     * @return true se il login ha successo, false altrimenti
     */
    public boolean login(String username, String password, MainFrame parent) {
        Utente utente = utenteDAO.findByUsernameAndPassword(username, password);
        if (utente != null) {
            utenteCorrente = utente;
            parent.showDashboard(username);
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Credenziali errate");
            return false;
        }
    }

    /**
     * Modifica un ToDo già esistente.
     *
     * @param board pannello della bacheca
     * @param todo ToDo da modificare
     * @param isEditable true se il ToDo è modificabile
     * @param updateView funzione da eseguire per aggiornare la GUI
     */
    public void editToDo(BoardPanel board, ToDo todo, boolean isEditable, Runnable updateView) {
        ToDoFormDialog dialog = new ToDoFormDialog(todo, isEditable, this, getUtenteCorrente(), board.getBacheca().getTipo());
        dialog.setModal(true);
        dialog.pack();
        dialog.setLocationRelativeTo(board);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Bacheca bacheca = board.getBacheca();
            toDoDAO.aggiorna(todo, utenteCorrente.getUsername(), bacheca.getTipo());
            List<ToDo> todos = toDoDAO.trovaPerBacheca(utenteCorrente.getUsername(), bacheca.getTipo());
            bacheca.setToDoList(todos);
            board.setBacheca(bacheca);
            updateView.run();
        }
    }
    /**
     * Condivide un ToDo con un altro utente.
     *
     * @param destinatario username del destinatario
     * @param todo ToDo da condividere
     * @param prop proprietario del ToDo
     * @param tipo tipo della bacheca
     * @return true se la condivisione è andata a buon fine, false altrimenti
     */
    public boolean condividiToDo(String destinatario, ToDo todo, String prop, TipoBacheca tipo) {
        String usernameMittente = utenteCorrente.getUsername();
        String tipoString = tipo.name();
        String titolo = todo.getTitolo();

        if (destinatario.equalsIgnoreCase(usernameMittente)) {
            JOptionPane.showMessageDialog(null, "Non puoi condividere un ToDo con te stesso.");
            return false;
        }

        UtenteDAO utenteDAO = new UtenteDAO();
        if (!utenteDAO.esisteUtente(destinatario)) {
            JOptionPane.showMessageDialog(null, "L'utente destinatario non esiste.");
            return false;
        }

        if (condivisioneDAO.esisteCondivisione(destinatario, prop, tipoString, titolo)) {
            JOptionPane.showMessageDialog(null, "Hai già condiviso questo ToDo con questo utente.");
            return false;
        }

        boolean success = condivisioneDAO.condividi(destinatario, prop, tipoString, titolo);
        if (success) {
            JOptionPane.showMessageDialog(null, "ToDo condiviso con successo.");
        } else {
            JOptionPane.showMessageDialog(null, "Errore durante la condivisione.");
        }

        return success;
    }

    /**
     * Verifica se un ToDo è stato condiviso con un utente.
     *
     * @param utente destinatario
     * @param todo ToDo in questione
     * @param prop proprietario
     * @param tipo tipo di bacheca
     * @return true se esiste la condivisione
     */
    public boolean isToDoCondiviso(String utente, ToDo todo, String prop, TipoBacheca tipo) {
        return condivisioneDAO.esisteCondivisione(utente, prop, tipo.name(), todo.getTitolo());
    }

    /**
     * Rimuove una condivisione esistente per un utente.
     *
     * @param utente destinatario
     * @param todo ToDo condiviso
     * @param prop proprietario
     * @param tipo tipo di bacheca
     * @return true se la rimozione è andata a buon fine
     */
    public boolean rimuoviCondivisione(String utente, ToDo todo, String prop, TipoBacheca tipo) {
        return condivisioneDAO.rimuoviCondivisione(utente, prop, tipo.name(), todo.getTitolo());
    }

    /**
     * Registra un nuovo utente nel sistema.
     *
     * @param username nome utente
     * @param password password
     * @return true se la registrazione ha successo, false se l'utente esiste già
     */
    public boolean register(String username, String password) {
        username = username.trim();

        if (utenteDAO.findByUsername(username) != null) {
            return false;
        } else {
            Utente nuovoUtente = new Utente(username, password);
            boolean result = utenteDAO.salvaUtente(nuovoUtente);

            if (result) {
                bachecaDAO.salvaBacheca(new Bacheca(TipoBacheca.UNIVERSITA, "Bacheca Università"), username);
                bachecaDAO.salvaBacheca(new Bacheca(TipoBacheca.LAVORO, "Bacheca LAVORO"), username);
                bachecaDAO.salvaBacheca(new Bacheca(TipoBacheca.TEMPO_LIBERO, "Bacheca Tempo Libero"), username);
            }

            return result;
        }
    }

    /**
     * Restituisce l'utente attualmente loggato.
     *
     * @return utente corrente
     */
    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    /**
     * Esegue il logout dell'utente corrente e resetta le bacheche.
     */
    public void logout() {
        utenteCorrente = null;

        universitaBoard.setBacheca(null);
        universitaBoard.clearToDos();

        lavoroBoard.setBacheca(null);
        lavoroBoard.clearToDos();

        tempoLiberoBoard.setBacheca(null);
        tempoLiberoBoard.clearToDos();
    }

    /**
     * Inverte lo stato di completamento di un ToDo e aggiorna il database e la GUI.
     *
     * @param board pannello contenente il ToDo
     * @param todo ToDo da modificare
     */
    public void toggleCompletamento(BoardPanel board, ToDo todo) {
        if (todo.getStato() == StatoToDo.COMPLETATO) {
            todo.setStato(StatoToDo.NON_COMPLETATO);
        } else {
            todo.setStato(StatoToDo.COMPLETATO);
        }

        Bacheca bacheca = board.getBacheca();
        toDoDAO.aggiorna(todo, utenteCorrente.getUsername(), bacheca.getTipo());

        List<ToDo> todos = toDoDAO.trovaPerBacheca(utenteCorrente.getUsername(), bacheca.getTipo());
        bacheca.setToDoList(todos);
        board.setBacheca(bacheca);
    }

    /**
     * Restituisce l'elenco delle richieste pendenti per l'utente corrente.
     *
     * @return lista di array contenenti i dati delle richieste
     */
    public List<String[]> getRichiestePendenti() {
        if (utenteCorrente == null) return new ArrayList<>();
        return ((CondivisioneDAO) condivisioneDAO).getRichiestePendentiPerUtente(utenteCorrente.getUsername());
    }

    /**
     * Accetta una richiesta di condivisione, aggiornando il suo stato a "ACCEPTED".
     *
     * @param destinatario destinatario della condivisione
     * @param proprietario proprietario del ToDo
     * @param tipoBacheca tipo della bacheca
     * @param titoloToDo titolo del ToDo condiviso
     * @return true se l'aggiornamento ha avuto successo
     */
    public boolean accettaRichiesta(String destinatario, String proprietario, String tipoBacheca, String titoloToDo) {
        return ((CondivisioneDAO) condivisioneDAO).aggiornaStatoRichiesta(destinatario, proprietario, tipoBacheca, titoloToDo, "ACCEPTED");
    }

    /**
     * Rifiuta una richiesta di condivisione, aggiornando il suo stato a "REJECTED".
     *
     * @param destinatario destinatario della condivisione
     * @param proprietario proprietario del ToDo
     * @param tipoBacheca tipo della bacheca
     * @param titoloToDo titolo del ToDo condiviso
     * @return true se l'aggiornamento ha avuto successo
     */
    public boolean rifiutaRichiesta(String destinatario, String proprietario, String tipoBacheca, String titoloToDo) {
        return ((CondivisioneDAO) condivisioneDAO).aggiornaStatoRichiesta(destinatario, proprietario, tipoBacheca, titoloToDo, "REJECTED");
    }

    /**
     * Carica la bacheca di un dato tipo per l'utente corrente, inclusi i ToDo.
     *
     * @param tipo tipo di bacheca
     * @return bacheca caricata, oppure null se non trovata
     */
    public Bacheca getBachecaPerTipo(TipoBacheca tipo) {
        if (utenteCorrente == null) return null;

        Bacheca b = bachecaDAO.findByTipo(utenteCorrente.getUsername(), tipo);
        if (b != null) {
            List<ToDo> todos = toDoDAO.trovaPerBacheca(utenteCorrente.getUsername(), tipo);
            b.setToDoList(todos);
        }
        return b;
    }

    /**
     * Restituisce la lista dei ToDo condivisi con l'utente specificato.
     *
     * @param username nome dell'utente
     * @return lista di ToDo condivisi
     */
    public List<ToDo> getToDoCondivisi(String username) {
        return ((ToDoDAO) toDoDAO).getToDoCondivisiCon(username);
    }

    /**
     * Restituisce la lista degli username degli utenti con cui il ToDo è stato condiviso.
     *
     * @param todo Il ToDo per cui recuperare gli utenti condivisi
     * @return Lista di username degli utenti destinatari della condivisione
     */
    public List<String> getUtentiCondivisi(ToDo todo) {
        return condivisioneDAO.getUtentiCondivisi(
                todo.getProprietario(),
                todo.getTipoBacheca().name(),
                todo.getTitolo()
        );
    }

    /**
     * Rimuove la condivisione del ToDo per un determinato destinatario.
     *
     * @param destinatario Lo username dell’utente da rimuovere dalla condivisione
     * @param todo Il ToDo da cui rimuovere la condivisione
     * @return true se la rimozione ha avuto successo, false altrimenti
     */
    public boolean rimuoviCondivisione(String destinatario, ToDo todo) {
        return condivisioneDAO.rimuoviCondivisione(
                destinatario,
                todo.getProprietario(),
                todo.getTipoBacheca().name(),
                todo.getTitolo()
        );
    }

    /**
     * Rimuove la condivisione del ToDo per tutti gli utenti specificati.
     *
     * @param todo Il ToDo da cui rimuovere le condivisioni
     * @param utentiDaRimuovere Lista di username degli utenti da rimuovere dalla condivisione
     */
    public void rimuoviCondivisioni(ToDo todo, List<String> utentiDaRimuovere) {
        for (String destinatario : utentiDaRimuovere) {
            condivisioneDAO.rimuoviCondivisione(
                    destinatario,
                    todo.getProprietario(),
                    todo.getTipoBacheca().name(),
                    todo.getTitolo()
            );
        }
    }
    

    /**
    * Elimina l'utente attualmente autenticato dal sistema utilizzando UtenteDAO.
    * Questo metodo cancella l'utente dal database e resetta lo stato dell'utente corrente.
    */
    public void eliminaUtente() {
       if (utenteCorrente != null) {
           boolean successo = utenteDAO.eliminaUtente(utenteCorrente.getUsername());
           if (successo) {
              utenteCorrente = null;
          } else {
              JOptionPane.showMessageDialog(null, "Errore durante l'eliminazione dell'utente.");
         }
      }
   }
}
