package gui;

import model.ToDo;
import model.Utente;
import model.StatoToDo;
import controller.Controller;
import dao.CondivisioneDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Componente grafico che rappresenta un singolo ToDo all'interno di una bacheca.
 * Gestisce l'interazione per modificarlo, completarlo, rimuoverlo o condividerlo.
 */
public class ToDoCardPanel extends JPanel {

    /** Il ToDo visualizzato nel pannello. */
    private ToDo todo;

    /** Il pannello bacheca a cui appartiene il ToDo. */
    private BoardPanel boardPanel;

    /** Il controller utilizzato per gestire le azioni utente. */
    private Controller controller;

    /** L'utente attualmente loggato. */
    private Utente utenteCorrente;

    /**
     * Costruisce un pannello per un ToDo specifico.
     *
     * @param todo il ToDo da rappresentare
     * @param boardPanel il pannello bacheca genitore
     * @param controller il controller per gestire le azioni
     * @param utenteCorrente l'utente attualmente loggato
     */
    public ToDoCardPanel(ToDo todo, BoardPanel boardPanel, Controller controller, Utente utenteCorrente) {
        this.todo = todo;
        this.boardPanel = boardPanel;
        this.controller = controller;
        this.utenteCorrente = utenteCorrente;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        try {
            setBackground(Color.decode("#" + todo.getColore()));
        } catch (Exception e) {
            setBackground(Color.WHITE);
        }

        setMaximumSize(new Dimension(300, 100));
        setPreferredSize(new Dimension(300, 100));

        boolean isCondiviso = utenteCorrente.getToDoCondivisi().contains(todo);

        JLabel titolo = new JLabel("<html><div style='text-align: center;'><b>" + todo.getTitolo() +
                "</b><br/>Scadenza: " + todo.getDataDiScadenza() +
                (utenteCorrente.getToDoCondivisi().contains(todo) ? "<br/><i>di " + todo.getProprietario() + "</i>" : "") +
                "</div></html>");
        titolo.setHorizontalAlignment(SwingConstants.CENTER);
        titolo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(titolo, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JCheckBox checkCompletato = new JCheckBox("COMPLETATO");
        checkCompletato.setSelected(todo.getStato() == StatoToDo.COMPLETATO);
        checkCompletato.setEnabled(!isCondiviso);
        if (isCondiviso) {
            checkCompletato.setToolTipText("Non puoi modificare un ToDo condiviso");
        }
        checkCompletato.addActionListener(e -> {
            if (!isCondiviso) {
                controller.toggleCompletamento(this.boardPanel, todo);
            }
        });

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(checkCompletato);

        if (!isCondiviso) {
            JButton btnEdit = new JButton("Modifica");
            btnEdit.addActionListener(e -> openEditDialog(true));
            buttonPanel.add(btnEdit);
        }

        JButton btnRemove = new JButton(isCondiviso ? "Rimuovi Condivisione" : "Rimuovi");
        btnRemove.addActionListener(e -> {
            if (isCondiviso) {
                int conferma = JOptionPane.showConfirmDialog(
                        this,
                        "Sei sicuro di voler rimuovere questa condivisione?",
                        "Conferma",
                        JOptionPane.YES_NO_OPTION
                );

                if (conferma == JOptionPane.YES_OPTION) {
                    CondivisioneDAO dao = new CondivisioneDAO();
                    boolean success = dao.rimuoviCondivisione(
                            utenteCorrente.getUsername(),
                            todo.getProprietario(),
                            todo.getTipoBacheca().name(),
                            todo.getTitolo()
                    );

                    if (success) {
                        utenteCorrente.rimuoviToDoCondiviso(todo);
                        if (this.boardPanel != null) {
                            this.boardPanel.refresh();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Errore durante la rimozione della condivisione dal database", "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                removeToDo();
            }

        });
        buttonPanel.add(btnRemove);

        add(buttonPanel, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ToDoFormDialog dialog = new ToDoFormDialog(
                            todo,
                            false,
                            controller,
                            utenteCorrente,
                            todo.getTipoBacheca()
                    );
                    dialog.setVisible(true);
                }
            }
        });
    }

    /**
     * Apre il dialogo di modifica per il ToDo.
     *
     * @param isEditable true se il dialogo deve essere in modalità modifica
     */
    private void openEditDialog(boolean isEditable) {
        controller.editToDo(boardPanel, todo, isEditable, this::updateCardContent);
    }

    /**
     * Aggiorna i contenuti del pannello in base allo stato attuale del ToDo.
     * Viene chiamato dopo modifiche.
     */
    private void updateCardContent() {
        boolean isCondiviso = utenteCorrente.getToDoCondivisi().contains(todo);

        try {
            setBackground(Color.decode("#" + todo.getColore()));
        } catch (Exception e) {
            setBackground(Color.WHITE);
        }

        JLabel titolo = new JLabel("<html><div style='text-align: center;'><b>" + todo.getTitolo() +
                "</b><br/>Scadenza: " + todo.getDataDiScadenza() + "</div></html>");
        titolo.setHorizontalAlignment(SwingConstants.CENTER);
        titolo.setFont(new Font("SansSerif", Font.PLAIN, 14));

        removeAll();
        setLayout(new BorderLayout());
        add(titolo, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JCheckBox checkCompletato = new JCheckBox("COMPLETATO");
        checkCompletato.setSelected(todo.getStato() == StatoToDo.COMPLETATO);
        checkCompletato.setEnabled(!isCondiviso);
        if (isCondiviso) {
            checkCompletato.setToolTipText("Non puoi modificare un ToDo condiviso");
        }
        checkCompletato.addActionListener(e -> {
            if (!isCondiviso) {
                controller.toggleCompletamento(boardPanel, todo);
            }
        });

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(checkCompletato);

        if (!isCondiviso) {
            JButton btnEdit = new JButton("Modifica");
            btnEdit.addActionListener(e -> openEditDialog(true));
            buttonPanel.add(btnEdit);
        }

        JButton btnRemove = new JButton(isCondiviso ? "Rimuovi Condivisione" : "Rimuovi");
        btnRemove.addActionListener(e -> {
            if (isCondiviso) {
                utenteCorrente.rimuoviToDoCondiviso(todo);
                if (boardPanel != null) {
                    boardPanel.refresh();
                }
            } else {
                removeToDo();
            }
        });
        buttonPanel.add(btnRemove);

        add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    /**
     * Rimuove il ToDo dal pannello genitore.
     */
    private void removeToDo() {
        if (boardPanel != null) {
            boardPanel.removeToDo(todo);
        }
    }
}

