package main;

import gui.MainFrame;

import javax.swing.*;

/**
 * Classe principale del progetto.
 * Avvia l'inizializzazione dei dati statici nel database e l'interfaccia grafica Swing.
 */
public class Main {

    /** Costruttore privato per evitare l'istanziazione della classe utility. */
    private Main() {
        // Classe non instanziabile
    }

    /**
     * Metodo principale di avvio dell'applicazione.
     *
     * Inizializza i tipi di bacheca e gli stati dei ToDo se non sono già presenti nel database,
     * poi avvia l'interfaccia grafica principale (MainFrame) usando il thread dell'Event Dispatch Thread di Swing.
     *
     * @param args eventuali argomenti da linea di comando (non usati)
     */
    public static void main(String[] args) {
        // InitDAO.inserisciTipiEStati(); non mi serve più

        // Avvia l'interfaccia grafica sul thread corretto di Swing
        SwingUtilities.invokeLater(MainFrame::new);
    }
}

