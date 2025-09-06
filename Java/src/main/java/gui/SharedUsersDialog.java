package gui;

import controller.Controller;
import model.ToDo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * Finestra di dialogo modale che mostra la lista degli utenti con cui è stato condiviso un ToDo.
 * Permette di selezionare uno o più utenti e rimuovere la condivisione.
 */
public class SharedUsersDialog extends JDialog {

    /** Elenco delle checkbox associate agli utenti condivisi. */
    private final List<JCheckBox> checkBoxes = new ArrayList<>();

    /** Il controller utilizzato per gestire le azioni nella finestra. */
    private final Controller controller;

    /** Il ToDo per cui mostrare o modificare la condivisione. */
    private final ToDo todo;

    /**
     * Costruttore del dialogo.
     *
     * @param parent il frame genitore della finestra di dialogo
     * @param utentiCondivisi la lista degli username con cui è stato condiviso il ToDo
     * @param controller il controller principale per effettuare le operazioni
     * @param todo il ToDo di riferimento di cui gestire le condivisioni
     */
    public SharedUsersDialog(JFrame parent, List<String> utentiCondivisi, Controller controller, ToDo todo) {
        super(parent, "Utenti Condivisi", true);
        this.controller = controller;
        this.todo = todo;

        setSize(300, 300);
        setLayout(new BorderLayout());

        JPanel listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));

        for (String username : utentiCondivisi) {
            JCheckBox checkBox = new JCheckBox(username);
            checkBoxes.add(checkBox);
            listaPanel.add(checkBox);
        }

        JScrollPane scrollPane = new JScrollPane(listaPanel);
        add(scrollPane, BorderLayout.CENTER);

        JButton btnElimina = new JButton("Rimuovi selezionati");
        btnElimina.addActionListener((ActionEvent e) -> {
            List<String> selezionati = new ArrayList<>();
            for (JCheckBox cb : checkBoxes) {
                if (cb.isSelected()) {
                    selezionati.add(cb.getText());
                }
            }

            if (!selezionati.isEmpty()) {
                int conferma = JOptionPane.showConfirmDialog(this,
                        "Vuoi davvero rimuovere la condivisione per: " + selezionati + "?",
                        "Conferma", JOptionPane.YES_NO_OPTION);
                if (conferma == JOptionPane.YES_OPTION) {
                    controller.rimuoviCondivisioni(todo, selezionati);
                    JOptionPane.showMessageDialog(this, "Condivisioni rimosse.");
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Nessun utente selezionato.");
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnElimina);

        add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(parent);
    }
}


