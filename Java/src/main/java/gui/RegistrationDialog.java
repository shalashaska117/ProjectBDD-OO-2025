package gui;

import javax.swing.*;
import java.awt.*;
import controller.Controller;

/**
 * Finestra di dialogo per la registrazione di un nuovo utente.
 * Utilizza un layout GridBag per disporre i componenti in modo ordinato.
 */
public class RegistrationDialog extends JDialog {

    /**
     * Costruttore della finestra di registrazione.
     *
     * @param parent     Finestra principale (frame genitore)
     * @param controller Controller per gestire la logica di registrazione
     */
    public RegistrationDialog(JFrame parent, Controller controller) {
        super(parent, "Registrazione", true); // Finestra modale
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Componenti interfaccia
        JLabel userLabel = new JLabel("Nuovo Username:");
        JTextField userField = new JTextField(15);
        JLabel passLabel = new JLabel("Nuova Password:");
        JPasswordField passField = new JPasswordField(15);
        JButton registerButton = new JButton("Registrati");
        registerButton.setPreferredSize(new Dimension(120, 25));

        // Azione del pulsante di registrazione
        registerButton.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            boolean success = controller.register(user, pass);
            if (success) {
                JOptionPane.showMessageDialog(this, "Registrazione avvenuta con successo!");
                dispose(); // Chiudi finestra
            } else {
                JOptionPane.showMessageDialog(this, "Errore: Username già esistente.");
            }
        });

        // Layout con GridBag
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        add(userLabel, gbc);

        gbc.gridx = 1;
        add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(passLabel, gbc);

        gbc.gridx = 1;
        add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(registerButton, gbc);

        // Configurazione finestra
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}

