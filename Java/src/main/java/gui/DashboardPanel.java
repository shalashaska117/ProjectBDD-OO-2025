package gui;

import javax.swing.*;
import java.awt.*;

import model.Utente;
import model.TipoBacheca;
import controller.*;

/**
 * Pannello principale della dashboard utente, contenente le tre bacheche
 * (Università, LAVORO, Tempo Libero) e i pulsanti di controllo (logout, inviti).
 */
public class DashboardPanel extends JPanel {

    /** Riferimento alla finestra principale (MainFrame) */
    private MainFrame frame;

    /** Pannello che rappresenta la bacheca "Università" dell'utente */
    private BoardPanel universitaBoard;

    /** Pannello che rappresenta la bacheca "Lavoro" dell'utente */
    private BoardPanel lavoroBoard;

    /** Pannello che rappresenta la bacheca "Tempo Libero" dell'utente */
    private BoardPanel tempoLiberoBoard;

    /** Controller MVC per gestire la logica dell'app */
    private Controller controller;

    /** Label per il messaggio di benvenuto */
    private JLabel welcomeLabel;

    /**
     * Costruttore del pannello dashboard.
     *
     * @param frame riferimento al MainFrame principale
     */
    public DashboardPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        // Pannello superiore con messaggio di benvenuto e pulsanti
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        welcomeLabel = new JLabel("Benvenuto:");
        leftPanel.add(welcomeLabel);
        
        /**
        * Bottone per eliminare l'utente corrente.
        * Alla pressione, chiede conferma all'utente tramite JOptionPane.
        * Se confermato, invoca il metodo controller.eliminaUtente()
        * e ritorna alla schermata di login.
        */
        JButton eliminaUtenteButton = new JButton("Elimina utente");
        eliminaUtenteButton.addActionListener(e -> {
             int conferma = JOptionPane.showConfirmDialog(
                this,
                "Sei sicuro di voler eliminare l'utente?",
                "Conferma eliminazione",
                JOptionPane.YES_NO_OPTION
           );

          if (conferma == JOptionPane.YES_OPTION) {
            controller.eliminaUtente();  // Metodo da implementare nel Controller
            frame.showLoginPanel();     // Torna alla schermata di login
          }
        });

        leftPanel.add(eliminaUtenteButton);
        topPanel.add(leftPanel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> frame.showLoginPanel());

        JButton invitiButton = new JButton("Inviti");
        invitiButton.addActionListener(e -> {
            JDialog dialog = new InvitationsDialog(frame, controller.getUtenteCorrente(), this);
            dialog.setVisible(true);
        });

        JPanel rightButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtonsPanel.add(invitiButton);
        rightButtonsPanel.add(logoutButton);
        topPanel.add(rightButtonsPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Pannello centrale con le tre bacheche
        JPanel boards = new JPanel(new GridLayout(1, 3, 10, 10));

        universitaBoard = new BoardPanel("Università", null, null, null);
        lavoroBoard = new BoardPanel("Lavoro", null, null, null);
        tempoLiberoBoard = new BoardPanel("Tempo Libero", null, null, null);

        controller = new Controller(universitaBoard, lavoroBoard, tempoLiberoBoard);

        universitaBoard.setController(controller);
        lavoroBoard.setController(controller);
        tempoLiberoBoard.setController(controller);

        boards.add(universitaBoard);
        boards.add(lavoroBoard);
        boards.add(tempoLiberoBoard);

        add(boards, BorderLayout.CENTER);
    }

    /**
     * Aggiorna il messaggio di benvenuto nella parte superiore della dashboard.
     *
     * @param username nome dell'utente corrente
     */
    public void setWelcomeUser(String username) {
        welcomeLabel.setText("Benvenuto: " + username);
    }

    /**
     * Carica l'utente corrente e aggiorna le tre bacheche con i relativi ToDo.
     *
     * @param username username dell’utente da caricare
     */
    public void loadUser(String username) {
        controller.loadUser(username);

        Utente utente = controller.getUtenteCorrente();

        if (utente != null) {
            setWelcomeUser(utente.getUsername());

            universitaBoard.setUtenteCorrente(utente);
            universitaBoard.setBacheca(controller.getBachecaPerTipo(TipoBacheca.UNIVERSITA));
            universitaBoard.aggiornaBoard();

            lavoroBoard.setUtenteCorrente(utente);
            lavoroBoard.setBacheca(controller.getBachecaPerTipo(TipoBacheca.LAVORO));
            lavoroBoard.aggiornaBoard();

            tempoLiberoBoard.setUtenteCorrente(utente);
            tempoLiberoBoard.setBacheca(controller.getBachecaPerTipo(TipoBacheca.TEMPO_LIBERO));
            tempoLiberoBoard.aggiornaBoard();
        } else {
            setWelcomeUser("ospite");
        }
    }

}

