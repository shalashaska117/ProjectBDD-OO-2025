package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un'attività (ToDo) associata a una bacheca, con informazioni su contenuto,
 * stato, scadenza, immagine, condivisione e proprietà.
 */
public class ToDo {

    private int id;
    private String titolo;
    private String dataDiScadenza;
    private String url;
    private byte[] immagine;
    private String descrizione;
    private String colore;
    private int posizione;
    private StatoToDo stato;
    private String proprietario;
    private List<Utente> utentiCondivisi;
    private TipoBacheca tipoBacheca;

    /**
     * Costruttore completo per creare un nuovo ToDo.
     *
     * @param titolo         il titolo del ToDo
     * @param dataDiScadenza la data di scadenza
     * @param url            un eventuale URL collegato
     * @param immagine       l'immagine associata al ToDo (in formato byte array)
     * @param descrizione    la descrizione del contenuto
     * @param colore         il colore scelto per il ToDo (es. "FFFFFF")
     */
    public ToDo(String titolo, String dataDiScadenza, String url, byte[] immagine,
                String descrizione, String colore) {
        this.titolo = titolo;
        this.dataDiScadenza = dataDiScadenza;
        this.url = url;
        this.immagine = immagine;
        this.descrizione = descrizione;
        this.colore = colore;
        this.stato = StatoToDo.NON_COMPLETATO;
        this.utentiCondivisi = new ArrayList<>();
        this.posizione = 0;
        this.id = -1;
        this.proprietario = null;
    }

    /**
     * Costruttore semplificato, inizializza solo con il titolo.
     *
     * @param titolo il titolo del ToDo
     */
    public ToDo(String titolo) {
        this(titolo, "", "", null, "", "FFFFFF");
    }

    // Getter e setter con Javadoc

    /**
     * Restituisce l'identificativo numerico del ToDo.
     *
     * @return l'ID del ToDo
     */
    public int getId() { return id; }

    /**
     * Imposta l'identificativo numerico del ToDo.
     *
     * @param id l'ID da assegnare al ToDo
     */
    public void setId(int id) { this.id = id; }

    /**
     * Restituisce il titolo assegnato al ToDo.
     *
     * @return il titolo del ToDo
     */
    public String getTitolo() { return titolo; }

    /**
     * Imposta un nuovo titolo per il ToDo.
     *
     * @param titolo il titolo da assegnare
     */
    public void setTitolo(String titolo) { this.titolo = titolo; }

    /**
     * Restituisce la data di scadenza assegnata al ToDo.
     *
     * @return la data di scadenza
     */
    public String getDataDiScadenza() { return dataDiScadenza; }

    /**
     * Imposta la data di scadenza per il ToDo.
     *
     * @param dataDiScadenza la data di scadenza da assegnare
     */
    public void setDataDiScadenza(String dataDiScadenza) { this.dataDiScadenza = dataDiScadenza; }

    /**
     * Restituisce l'URL collegato al ToDo.
     *
     * @return l'URL collegato al ToDo
     */
    public String getUrl() { return url; }

    /**
     * Imposta l'URL collegato al ToDo.
     *
     * @param url l'URL da assegnare
     */
    public void setUrl(String url) { this.url = url; }

    /**
     * Restituisce l'immagine associata al ToDo.
     *
     * @return l'immagine associata al ToDo
     */
    public byte[] getImmagine() { return immagine; }

    /**
     * Imposta l'immagine associata al ToDo.
     *
     * @param immagine l'immagine da assegnare (byte[])
     */
    public void setImmagine(byte[] immagine) { this.immagine = immagine; }

    /**
     * Restituisce la descrizione testuale del ToDo.
     *
     * @return la descrizione testuale
     */
    public String getDescrizione() { return descrizione; }

    /**
     * Imposta la descrizione testuale del ToDo.
     *
     * @param descrizione la descrizione da assegnare
     */
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    /**
     * Restituisce il colore assegnato al ToDo.
     *
     * @return il colore del ToDo
     */
    public String getColore() { return colore; }

    /**
     * Imposta il colore associato al ToDo.
     *
     * @param colore il colore da assegnare (es. "FFFFFF")
     */
    public void setColore(String colore) { this.colore = colore; }

    /**
     * Restituisce la posizione ordinata del ToDo nella bacheca.
     *
     * @return la posizione del ToDo
     */
    public int getPosizione() { return posizione; }

    /**
     * Imposta la posizione ordinata del ToDo nella bacheca.
     *
     * @param posizione la nuova posizione
     */
    public void setPosizione(int posizione) { this.posizione = posizione; }

    /**
     * Restituisce lo stato attuale del ToDo.
     *
     * @return lo stato attuale del ToDo
     */
    public StatoToDo getStato() { return stato; }

    /**
     * Imposta lo stato attuale del ToDo.
     *
     * @param stato lo stato da assegnare (COMPLETATO / NON_COMPLETATO)
     */
    public void setStato(StatoToDo stato) { this.stato = stato; }

    /**
     * Restituisce il nome utente del proprietario del ToDo.
     *
     * @return il nome utente del proprietario
     */
    public String getProprietario() { return proprietario; }

    /**
     * Imposta il nome utente del proprietario del ToDo.
     *
     * @param proprietario il nome utente del proprietario
     */
    public void setProprietario(String proprietario) { this.proprietario = proprietario; }

    /**
     * Restituisce la lista di utenti con cui il ToDo è condiviso.
     *
     * @return la lista di utenti con cui è condiviso
     */
    public List<Utente> getUtentiCondivisi() { return utentiCondivisi; }

    /**
     * Imposta la lista di utenti con cui il ToDo è condiviso.
     *
     * @param utentiCondivisi la lista degli utenti con cui condividere
     */
    public void setUtentiCondivisi(List<Utente> utentiCondivisi) { this.utentiCondivisi = utentiCondivisi; }

    /**
     * Restituisce il tipo di bacheca a cui appartiene il ToDo.
     *
     * @return il tipo di bacheca
     */
    public TipoBacheca getTipoBacheca() { return tipoBacheca; }

    /**
     * Imposta il tipo di bacheca a cui appartiene il ToDo.
     *
     * @param tipoBacheca il tipo di bacheca da assegnare
     */
    public void setTipoBacheca(TipoBacheca tipoBacheca) { this.tipoBacheca = tipoBacheca; }

    // Metodi funzionali

    /**
     * Condivide il ToDo con un utente, aggiornando anche il suo elenco di ToDo condivisi.
     *
     * @param utente l'utente con cui condividere
     */
    public void condividiCon(Utente utente) {
        if (this.utentiCondivisi == null) this.utentiCondivisi = new ArrayList<>();
        if (!this.utentiCondivisi.contains(utente)) this.utentiCondivisi.add(utente);

        if (utente.getToDoCondivisi() == null) utente.setToDoCondivisi(new ArrayList<>());
        if (!utente.getToDoCondivisi().contains(this)) utente.getToDoCondivisi().add(this);
    }

    /**
     * Rimuove la condivisione di questo ToDo per un utente specifico.
     *
     * @param utente l'utente da rimuovere dalla condivisione
     */
    public void rimuoviCondivisionePer(Utente utente) {
        if (utentiCondivisi != null) utentiCondivisi.remove(utente);
        if (utente.getToDoCondivisi() != null) utente.getToDoCondivisi().remove(this);
    }
}





