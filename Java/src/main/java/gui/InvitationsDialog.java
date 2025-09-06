package gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

import dao.CondivisioneDAO;
import model.Utente;

/**
 * Finestra di dialogo per gestire le richieste di partecipazione
 * (condivisione di ToDo) ricevute dall'utente corrente.
 *
 * Permette di visualizzare le richieste pendenti e accettarle o rifiutarle.
 */
public class InvitationsDialog extends JDialog {

    /** Utente corrente loggato */
    private Utente currentUser;

    /** DAO per gestire le condivisioni */
    private CondivisioneDAO condivisioneDAO;

    /** Tabella che mostra le richieste */
    private JTable table;

    /** Modello della tabella */
    private DefaultTableModel tableModel;

    /** Dashboard da aggiornare in caso di modifiche */
    private DashboardPanel dashboard;

    /**
     * Costruttore della finestra di dialogo.
     *
     * @param parent       finestra principale (JFrame) da cui è invocata
     * @param currentUser  utente attualmente loggato
     * @param dashboard    riferimento alla dashboard da aggiornare dopo modifiche
     */
    public InvitationsDialog(JFrame parent, Utente currentUser, DashboardPanel dashboard) {
        super(parent, "Richieste di Partecipazione", true);
        this.currentUser = currentUser;
        this.condivisioneDAO = new CondivisioneDAO();
        this.dashboard = dashboard;

        setSize(500, 300);
        setLocationRelativeTo(parent);

        initUI();
        loadRequests();
    }

    /**
     * Inizializza l'interfaccia grafica con tabella e pulsanti.
     */
    private void initUI() {
        String[] columns = {"Richiedente", "Tipo Bacheca", "Titolo ToDo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton acceptButton = new JButton("Accetta");
        JButton rejectButton = new JButton("Rifiuta");

        acceptButton.addActionListener(e -> handleRequest("ACCEPTED"));
        rejectButton.addActionListener(e -> handleRequest("REJECTED"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Carica tutte le richieste pendenti dell'utente e aggiorna la tabella.
     */
    private void loadRequests() {
        tableModel.setRowCount(0);
        List<String[]> richieste = condivisioneDAO.getRichiestePendentiPerUtente(currentUser.getUsername());

        for (String[] r : richieste) {
            tableModel.addRow(r);
        }
    }

    /**
     * Gestisce l'accettazione o il rifiuto della richiesta selezionata.
     *
     * @param newStatus lo stato da impostare ("ACCEPTED" o "REJECTED")
     */
    private void handleRequest(String newStatus) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona una richiesta dalla tabella.", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String proprietario = (String) tableModel.getValueAt(selectedRow, 0);
        String tipoBacheca = (String) tableModel.getValueAt(selectedRow, 1);
        String titolo = (String) tableModel.getValueAt(selectedRow, 2);

        boolean success = condivisioneDAO.aggiornaStatoRichiesta(
                currentUser.getUsername(), proprietario, tipoBacheca, titolo, newStatus);

        if (success) {
            JOptionPane.showMessageDialog(this, "Richiesta " +
                    (newStatus.equals("ACCEPTED") ? "accettata" : "rifiutata") + " con successo.");

            if (dashboard != null) {
                dashboard.loadUser(currentUser.getUsername());
            }

            loadRequests();
        } else {
            JOptionPane.showMessageDialog(this, "Errore durante l'aggiornamento della richiesta.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}
