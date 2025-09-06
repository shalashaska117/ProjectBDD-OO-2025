package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Finestra principale dell'applicazione. Gestisce la dashboard e il login.
 */
public class MainFrame extends JFrame {

    /** Layout a schede per cambiare facilmente pannelli all'interno del frame. */
    private CardLayout cardLayout = new CardLayout();

    /** Pannello principale che contiene i vari pannelli della GUI, gestito tramite CardLayout. */
    private JPanel mainPanel = new JPanel(cardLayout);

    /** Pannello per la schermata di login. */
    private LoginPanel loginPanel;

    /** Pannello per la dashboard principale dell’utente autenticato. */
    private DashboardPanel dashboardPanel;

    /** Crea il frame principale dell'applicazione. */
    public MainFrame() {
        setTitle("ToDo Manager");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null); // Centra la finestra

        // Inizializza i pannelli
        loginPanel = new LoginPanel(this);
        dashboardPanel = new DashboardPanel(this);

        // Aggiungi le viste al CardLayout
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(dashboardPanel, "Dashboard");

        add(mainPanel);
        cardLayout.show(mainPanel, "Login"); // Mostra login all'avvio

        setVisible(true);
    }

    /**
     * Mostra il pannello della dashboard per l'utente specificato.
     * @param username Nome utente dell'utente loggato
     */
    public void showDashboard(String username) {
        SwingUtilities.invokeLater(() -> dashboardPanel.loadUser(username));
        cardLayout.show(mainPanel, "Dashboard");
    }

    /**
     * Torna alla schermata di login (es. dopo logout)
     */
    public void showLoginPanel() {
        loginPanel.clearFields();
        cardLayout.show(mainPanel, "Login");
    }

}
