--
-- PostgreSQL database dump
--

-- Dumped from database version 17.0
-- Dumped by pg_dump version 17.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: aggiorna_stato_condivisione(text, integer, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.aggiorna_stato_condivisione(p_destinatario text, p_id_todo integer, p_nuovo_stato text) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF UPPER(p_nuovo_stato) = 'ACCEPTED' THEN
        UPDATE condivisione
        SET stato = 'ACCEPTED'
        WHERE username_utente = p_destinatario
          AND id_todo = p_id_todo;

        IF NOT FOUND THEN
            RAISE EXCEPTION 'Nessuna condivisione trovata da aggiornare';
        END IF;

    ELSIF UPPER(p_nuovo_stato) = 'REJECTED' THEN
        DELETE FROM condivisione
        WHERE username_utente = p_destinatario
          AND id_todo = p_id_todo
          AND stato = 'PENDING'; -- sicurezza: rimuove solo richieste non ancora accettate

        IF NOT FOUND THEN
            RAISE EXCEPTION 'Nessuna richiesta PENDING trovata da rimuovere';
        END IF;

    ELSE
        RAISE EXCEPTION 'Stato non valido: usa solo ACCEPTED o REJECTED';
    END IF;
END;
$$;


ALTER FUNCTION public.aggiorna_stato_condivisione(p_destinatario text, p_id_todo integer, p_nuovo_stato text) OWNER TO postgres;

--
-- Name: aggiorna_todo(integer, text, text, text, text, text, text, bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.aggiorna_todo(p_id integer, p_titolo text, p_descrizione text, p_data_scadenza text, p_colore text, p_stato text, p_url text, p_immagine bytea) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
  UPDATE todo
  SET titolo = p_titolo,
      descrizione = p_descrizione,
      data_scadenza = p_data_scadenza,
      colore = p_colore,
      stato = p_stato,
      url = p_url,
      immagine = p_immagine
  WHERE id = p_id;

  RETURN FOUND;
END;
$$;


ALTER FUNCTION public.aggiorna_todo(p_id integer, p_titolo text, p_descrizione text, p_data_scadenza text, p_colore text, p_stato text, p_url text, p_immagine bytea) OWNER TO postgres;

--
-- Name: aggiorna_todo_parziale(integer, text, text, text, text, text, text, bytea); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.aggiorna_todo_parziale(p_id integer, p_titolo text, p_descrizione text, p_data_scadenza text, p_colore text, p_stato text, p_url text, p_immagine bytea) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_sql TEXT := 'UPDATE todo SET ';
    v_sep TEXT := '';
BEGIN
    IF p_titolo IS NOT NULL THEN
        v_sql := v_sql || v_sep || 'titolo = ' || quote_literal(p_titolo);
        v_sep := ', ';
    END IF;

    IF p_descrizione IS NOT NULL THEN
        v_sql := v_sql || v_sep || 'descrizione = ' || quote_literal(p_descrizione);
        v_sep := ', ';
    END IF;

    IF p_data_scadenza IS NOT NULL THEN
        v_sql := v_sql || v_sep || 'data_scadenza = ' || quote_literal(p_data_scadenza);
        v_sep := ', ';
    END IF;

    IF p_colore IS NOT NULL THEN
        v_sql := v_sql || v_sep || 'colore = ' || quote_literal(p_colore);
        v_sep := ', ';
    END IF;

    IF p_stato IS NOT NULL THEN
        v_sql := v_sql || v_sep || 'stato = ' || quote_literal(p_stato);
        v_sep := ', ';
    END IF;

    IF p_url IS NOT NULL THEN
        v_sql := v_sql || v_sep || 'url = ' || quote_literal(p_url);
        v_sep := ', ';
    END IF;

    IF p_immagine IS NOT NULL THEN
        v_sql := v_sql || v_sep || 'immagine = ' || quote_literal(encode(p_immagine, 'hex')::bytea);
        v_sep := ', ';
    END IF;

    IF v_sep = '' THEN
        RETURN FALSE; -- Nessun campo aggiornato
    END IF;

    v_sql := v_sql || ' WHERE id = ' || p_id;

    EXECUTE v_sql;
    RETURN FOUND;
END;
$$;


ALTER FUNCTION public.aggiorna_todo_parziale(p_id integer, p_titolo text, p_descrizione text, p_data_scadenza text, p_colore text, p_stato text, p_url text, p_immagine bytea) OWNER TO postgres;

--
-- Name: cancella_immagine_todo(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.cancella_immagine_todo(p_id integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
  v_check BYTEA;
BEGIN
  -- Controlla se il ToDo esiste e ha immagine
  SELECT immagine INTO v_check
  FROM todo
  WHERE id = p_id;

  IF NOT FOUND OR v_check IS NULL THEN
    RETURN FALSE;
  END IF;

  -- Cancella immagine
  UPDATE todo
  SET immagine = NULL
  WHERE id = p_id;

  RETURN TRUE;
END;
$$;


ALTER FUNCTION public.cancella_immagine_todo(p_id integer) OWNER TO postgres;

--
-- Name: check_bacheche_standard(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.check_bacheche_standard() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    n_bacheche INTEGER;
BEGIN
    -- Confronta direttamente con gli ENUM Java usati in tipobacheca
    IF NEW.tipo NOT IN ('UNIVERSITA', 'LAVORO', 'TEMPO_LIBERO') THEN
        RAISE EXCEPTION 'Tipo bacheca non valido: % (consentiti: UNIVERSITA, LAVORO, TEMPO_LIBERO)', NEW.tipo;
    END IF;

    -- Controlla se l'utente ha gi… questo tipo
    SELECT COUNT(*) INTO n_bacheche
    FROM bacheca
    WHERE proprietario = NEW.proprietario AND tipo = NEW.tipo;

    IF n_bacheche > 0 THEN
        RAISE EXCEPTION 'L''utente % ha gi… una bacheca di tipo %', NEW.proprietario, NEW.tipo;
    END IF;

    RETURN NEW;
END;
$$;


ALTER FUNCTION public.check_bacheche_standard() OWNER TO postgres;

--
-- Name: check_colore_todo(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.check_colore_todo() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
BEGIN
    -- Non deve iniziare con #
    IF LEFT(NEW.colore, 1) = '#' THEN
        RAISE EXCEPTION 'Il colore non pu• iniziare con #. Valore ricevuto: %', NEW.colore;
    END IF;

    -- Deve essere lungo 6 caratteri ed esadecimale
    IF NEW.colore !~ '^[0-9A-Fa-f]{6}$' THEN
        RAISE EXCEPTION 'Colore non valido: deve essere un codice esadecimale di 6 cifre. Valore ricevuto: %', NEW.colore;
    END IF;

    RETURN NEW;
END;
$_$;


ALTER FUNCTION public.check_colore_todo() OWNER TO postgres;

--
-- Name: condividi_todo(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.condividi_todo(p_destinatario text, p_id_todo integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
  v_proprietario text;
BEGIN
  SELECT proprietario INTO v_proprietario
  FROM todo
  WHERE id = p_id_todo;

  IF NOT FOUND THEN
    RETURN FALSE;
  END IF;

  IF v_proprietario = p_destinatario THEN
    RETURN FALSE; -- blocca condivisione con s‚ stessi
  END IF;

  INSERT INTO condivisione (username_utente, id_todo, stato)
  VALUES (p_destinatario, p_id_todo, 'PENDING');

  RETURN TRUE;

EXCEPTION WHEN OTHERS THEN
  RETURN FALSE;
END;
$$;


ALTER FUNCTION public.condividi_todo(p_destinatario text, p_id_todo integer) OWNER TO postgres;

--
-- Name: crea_bacheche_standard(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.crea_bacheche_standard() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO bacheca (tipo, descrizione, proprietario)
    VALUES
        ('UNIVERSITA', 'Bacheca Universit…', NEW.username),
        ('LAVORO', 'Bacheca Lavoro', NEW.username),
        ('TEMPO_LIBERO', 'Bacheca Tempo Libero', NEW.username);
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.crea_bacheche_standard() OWNER TO postgres;

--
-- Name: default_stato_todo(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.default_stato_todo() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NEW.stato IS NULL THEN
        NEW.stato := 'NON_COMPLETATO';
    END IF;
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.default_stato_todo() OWNER TO postgres;

--
-- Name: elimina_condivisioni_collegate(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.elimina_condivisioni_collegate(p_id_todo integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM condivisione WHERE id_todo = p_id_todo;
    RETURN TRUE;
END;
$$;


ALTER FUNCTION public.elimina_condivisioni_collegate(p_id_todo integer) OWNER TO postgres;

--
-- Name: elimina_todo(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.elimina_todo(p_id integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM todo WHERE id = p_id;
    RETURN FOUND;
END;
$$;


ALTER FUNCTION public.elimina_todo(p_id integer) OWNER TO postgres;

--
-- Name: elimina_utente(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.elimina_utente(p_username text) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
    id_todo INTEGER;
BEGIN
    -- 1. Elimina condivisioni ricevute
    DELETE FROM condivisione WHERE username_utente = p_username;

    -- 2. Elimina condivisioni fatte (dopo recupero id)
    FOR id_todo IN SELECT id FROM todo WHERE proprietario = p_username LOOP
        DELETE FROM condivisione WHERE id_todo = id_todo;
    END LOOP;

    -- 3. Elimina ToDo
    DELETE FROM todo WHERE proprietario = p_username;

    -- 4. Elimina bacheche
    DELETE FROM bacheca WHERE proprietario = p_username;

    -- 5. Elimina utente
    DELETE FROM utente WHERE username = p_username;

    RETURN TRUE;
EXCEPTION WHEN OTHERS THEN
    RETURN FALSE;
END;
$$;


ALTER FUNCTION public.elimina_utente(p_username text) OWNER TO postgres;

--
-- Name: esiste_condivisione(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.esiste_condivisione(p_destinatario text, p_id_todo integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
    exists_bool boolean;
BEGIN
    SELECT EXISTS (
        SELECT 1 FROM condivisione
        WHERE username_utente = p_destinatario
          AND id_todo = p_id_todo
    ) INTO exists_bool;

    RETURN exists_bool;
END;
$$;


ALTER FUNCTION public.esiste_condivisione(p_destinatario text, p_id_todo integer) OWNER TO postgres;

--
-- Name: get_todo_completati(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_todo_completati(p_username text) RETURNS TABLE(id integer, titolo text, descrizione text, data_scadenza text, colore text, stato text, url text, posizione integer, proprietario text, tipo_bacheca text)
    LANGUAGE plpgsql
    AS $$
BEGIN
  RETURN QUERY
  SELECT
    v.id,
    v.titolo::text,
    v.descrizione::text,
    v.data_scadenza::text,
    v.colore::text,
    v.stato::text,
    v.url::text,
    v.posizione,
    v.proprietario::text,
    v.tipo_bacheca::text
  FROM vista_todo_senza_immagine v
  WHERE v.stato = 'COMPLETATO'
    AND v.proprietario = p_username;
END;
$$;


ALTER FUNCTION public.get_todo_completati(p_username text) OWNER TO postgres;

--
-- Name: get_todo_scaduti(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_todo_scaduti(p_username text) RETURNS TABLE(id integer, titolo text, descrizione text, data_scadenza text, colore text, stato text, url text, posizione integer, proprietario text, tipo_bacheca text)
    LANGUAGE plpgsql
    AS $$
BEGIN
  RETURN QUERY
  SELECT
    v.id,
    v.titolo::text,
    v.descrizione::text,
    v.data_scadenza::text,
    v.colore::text,
    v.stato::text,
    v.url::text,
    v.posizione,
    v.proprietario::text,
    v.tipo_bacheca::text
  FROM vista_todo_senza_immagine v
  WHERE v.data_scadenza::date < CURRENT_DATE
    AND v.stato != 'COMPLETATO'
    AND v.proprietario = p_username;
END;
$$;


ALTER FUNCTION public.get_todo_scaduti(p_username text) OWNER TO postgres;

--
-- Name: mostra_funzioni(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.mostra_funzioni() RETURNS TABLE(nome text, firma text)
    LANGUAGE sql
    AS $$
  SELECT p.proname::text AS nome,
         pg_get_function_identity_arguments(p.oid) AS firma
  FROM pg_proc p
  JOIN pg_namespace n ON p.pronamespace = n.oid
  WHERE n.nspname = 'public'
    AND pg_function_is_visible(p.oid);
$$;


ALTER FUNCTION public.mostra_funzioni() OWNER TO postgres;

--
-- Name: mostra_view(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.mostra_view() RETURNS TABLE(nome_view text, definizione text)
    LANGUAGE plpgsql
    AS $$
BEGIN
  RETURN QUERY
  SELECT
    viewname::TEXT,
    definition::TEXT
  FROM pg_views
  WHERE schemaname = 'public';
END;
$$;


ALTER FUNCTION public.mostra_view() OWNER TO postgres;

--
-- Name: richieste_pendenti_per_utente(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.richieste_pendenti_per_utente(p_username text) RETURNS TABLE(richiedente text, tipo_bacheca text, titolo text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        t.proprietario::text, 
        t.tipo_bacheca::text, 
        t.titolo::text
    FROM condivisione c
    JOIN todo t ON c.id_todo = t.id
    WHERE c.username_utente = p_username AND c.stato = 'PENDING';
END;
$$;


ALTER FUNCTION public.richieste_pendenti_per_utente(p_username text) OWNER TO postgres;

--
-- Name: rimuovi_condivisione(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.rimuovi_condivisione(p_destinatario text, p_id_todo integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM condivisione
    WHERE username_utente = p_destinatario
      AND id_todo = p_id_todo;
END;
$$;


ALTER FUNCTION public.rimuovi_condivisione(p_destinatario text, p_id_todo integer) OWNER TO postgres;

--
-- Name: salva_todo(text, text, text, text, text, bytea, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.salva_todo(p_titolo text, p_descrizione text, p_data_scadenza text, p_colore text, p_url text, p_immagine bytea, p_proprietario text, p_tipo_bacheca text) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    new_id INTEGER;
    colore_finale text;
BEGIN
    -- Colore default a 'FFFFFF' (senza cancelletto)
    colore_finale := COALESCE(p_colore, 'FFFFFF');

    -- Shift posizioni
    UPDATE todo
    SET posizione = posizione + 1
    WHERE proprietario = p_proprietario AND tipo_bacheca = p_tipo_bacheca;

    -- Inserimento
    INSERT INTO todo (
        titolo, descrizione, data_scadenza, colore, stato,
        url, immagine, posizione, proprietario, tipo_bacheca
    )
    VALUES (
        p_titolo, p_descrizione, p_data_scadenza, colore_finale, 'NON_COMPLETATO',
        p_url, p_immagine, 1, p_proprietario, p_tipo_bacheca
    )
    RETURNING id INTO new_id;

    RETURN new_id;
END;
$$;


ALTER FUNCTION public.salva_todo(p_titolo text, p_descrizione text, p_data_scadenza text, p_colore text, p_url text, p_immagine bytea, p_proprietario text, p_tipo_bacheca text) OWNER TO postgres;

--
-- Name: salva_utente(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.salva_utente(p_username text, p_password text) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO utente (username, password)
    VALUES (LOWER(TRIM(p_username)), p_password);
    RETURN TRUE;
EXCEPTION
    WHEN unique_violation THEN
        RAISE NOTICE 'Utente gi… esistente: %', LOWER(TRIM(p_username));
        RETURN FALSE;
    WHEN OTHERS THEN
        RAISE NOTICE 'Errore generico durante salva_utente: %', SQLERRM;
        RETURN FALSE;
END;
$$;


ALTER FUNCTION public.salva_utente(p_username text, p_password text) OWNER TO postgres;

--
-- Name: trova_todo_per_bacheca(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.trova_todo_per_bacheca(p_proprietario text, p_tipo_bacheca text) RETURNS TABLE(id integer, titolo text, descrizione text, data_scadenza text, colore text, stato text, url text, immagine bytea, posizione integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT id, titolo, descrizione, data_scadenza, colore, stato, url, immagine, posizione
    FROM todo
    WHERE proprietario = p_proprietario AND tipo_bacheca = p_tipo_bacheca
    ORDER BY posizione ASC;
END;
$$;


ALTER FUNCTION public.trova_todo_per_bacheca(p_proprietario text, p_tipo_bacheca text) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: bacheca; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bacheca (
    tipo character varying(30) NOT NULL,
    descrizione text,
    proprietario character varying(100) NOT NULL
);


ALTER TABLE public.bacheca OWNER TO postgres;

--
-- Name: condivisione; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.condivisione (
    username_utente character varying(100) NOT NULL,
    id_todo integer NOT NULL,
    stato character varying(20) DEFAULT 'PENDING'::character varying NOT NULL,
    CONSTRAINT check_stato_condivisione CHECK (((stato)::text = ANY ((ARRAY['PENDING'::character varying, 'ACCEPTED'::character varying])::text[]))),
    CONSTRAINT condivisione_stato_check CHECK (((stato)::text = ANY ((ARRAY['PENDING'::character varying, 'ACCEPTED'::character varying])::text[])))
);


ALTER TABLE public.condivisione OWNER TO postgres;

--
-- Name: todo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.todo (
    id integer NOT NULL,
    titolo character varying(100) NOT NULL,
    data_scadenza character varying(20),
    url text,
    immagine bytea,
    descrizione text,
    colore character varying(7),
    posizione integer,
    stato character varying(30) NOT NULL,
    proprietario character varying(100) NOT NULL,
    tipo_bacheca character varying(30) NOT NULL,
    CONSTRAINT stato_todo_check CHECK (((stato)::text = ANY ((ARRAY['COMPLETATO'::character varying, 'NON_COMPLETATO'::character varying])::text[])))
);


ALTER TABLE public.todo OWNER TO postgres;

--
-- Name: todo_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.todo_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.todo_id_seq OWNER TO postgres;

--
-- Name: todo_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.todo_id_seq OWNED BY public.todo.id;


--
-- Name: utente; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.utente (
    username character varying(100) NOT NULL,
    password character varying(100) NOT NULL
);


ALTER TABLE public.utente OWNER TO postgres;

--
-- Name: vista_todo_senza_immagine; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.vista_todo_senza_immagine AS
 SELECT id,
    titolo,
    descrizione,
    data_scadenza,
    colore,
    stato,
    url,
    posizione,
    proprietario,
    tipo_bacheca
   FROM public.todo;


ALTER VIEW public.vista_todo_senza_immagine OWNER TO postgres;

--
-- Name: todo id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.todo ALTER COLUMN id SET DEFAULT nextval('public.todo_id_seq'::regclass);


--
-- Name: bacheca bacheca_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bacheca
    ADD CONSTRAINT bacheca_pkey PRIMARY KEY (proprietario, tipo);


--
-- Name: condivisione condivisione_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.condivisione
    ADD CONSTRAINT condivisione_pkey PRIMARY KEY (username_utente, id_todo);


--
-- Name: todo todo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.todo
    ADD CONSTRAINT todo_pkey PRIMARY KEY (id);


--
-- Name: utente utente_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utente
    ADD CONSTRAINT utente_pkey PRIMARY KEY (username);


--
-- Name: bacheca trg_check_bacheche; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_check_bacheche BEFORE INSERT ON public.bacheca FOR EACH ROW EXECUTE FUNCTION public.check_bacheche_standard();


--
-- Name: utente trg_crea_bacheche; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_crea_bacheche AFTER INSERT ON public.utente FOR EACH ROW EXECUTE FUNCTION public.crea_bacheche_standard();


--
-- Name: todo trg_default_stato; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_default_stato BEFORE INSERT ON public.todo FOR EACH ROW EXECUTE FUNCTION public.default_stato_todo();


--
-- Name: todo trigger_check_colore_todo; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_check_colore_todo BEFORE INSERT OR UPDATE ON public.todo FOR EACH ROW EXECUTE FUNCTION public.check_colore_todo();


--
-- Name: bacheca bacheca_proprietario_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bacheca
    ADD CONSTRAINT bacheca_proprietario_fkey FOREIGN KEY (proprietario) REFERENCES public.utente(username);


--
-- Name: condivisione condivisione_id_todo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.condivisione
    ADD CONSTRAINT condivisione_id_todo_fkey FOREIGN KEY (id_todo) REFERENCES public.todo(id) ON DELETE CASCADE;


--
-- Name: condivisione condivisione_username_utente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.condivisione
    ADD CONSTRAINT condivisione_username_utente_fkey FOREIGN KEY (username_utente) REFERENCES public.utente(username);


--
-- Name: todo todo_proprietario_tipo_bacheca_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.todo
    ADD CONSTRAINT todo_proprietario_tipo_bacheca_fkey FOREIGN KEY (proprietario, tipo_bacheca) REFERENCES public.bacheca(proprietario, tipo);


--
-- PostgreSQL database dump complete
--

