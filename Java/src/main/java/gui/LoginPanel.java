package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import controller.*;

/**
 * Pannello per la schermata di login dell'applicazione.
 * Consente l'inserimento delle credenziali (username e password)
 * e permette l'accesso o la registrazione di un nuovo utente.
 */
public class LoginPanel extends JPanel {

    /** Campo di input per l'username */
    private final JTextField userField = new JTextField(15);

    /** Campo di input per la password */
    private final JPasswordField passField = new JPasswordField(15);

    /** Controller usato per gestire login e registrazione */
    private final Controller controller = new Controller(null, null, null); // Solo per login/registrazione

    /**
     * Costruttore del pannello di login.
     *
     * @param parent il frame principale dell'applicazione, usato per la navigazione tra i pannelli
     */
    public LoginPanel(MainFrame parent) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(89, 25));

        // Listener per il bottone di login
        loginButton.addActionListener((ActionEvent e) -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            controller.login(user, pass, parent);
        });

        // Posizionamento componenti
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
        add(loginButton, gbc);

        // Bottone per la registrazione
        JButton registerButton = new JButton("Registrati");
        registerButton.addActionListener(e -> new RegistrationDialog(parent, controller));

        gbc.gridy = 3;
        add(registerButton, gbc);
    }

    /**
     * Pulisce i campi di testo per username e password.
     * Utile quando si ritorna alla schermata di login dopo un logout.
     */
    public void clearFields() {
        userField.setText("");
        passField.setText("");
    }
}



