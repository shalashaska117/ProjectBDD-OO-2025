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
-- Data for Name: utente; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.utente VALUES ('admin', 'adminpass');
INSERT INTO public.utente VALUES ('pluto', 'plutopass');
INSERT INTO public.utente VALUES ('pippo', 'pippopass');
INSERT INTO public.utente VALUES ('mario', '1234');
INSERT INTO public.utente VALUES ('gennaro', '12');


--
-- Data for Name: bacheca; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.bacheca VALUES ('UNIVERSITA', 'Bacheca universitaria di admin', 'admin');
INSERT INTO public.bacheca VALUES ('LAVORO', 'Bacheca lavorativa di admin', 'admin');
INSERT INTO public.bacheca VALUES ('TEMPO_LIBERO', 'Bacheca tempo libero di admin', 'admin');
INSERT INTO public.bacheca VALUES ('UNIVERSITA', 'Bacheca universitaria di pluto', 'pluto');
INSERT INTO public.bacheca VALUES ('LAVORO', 'Bacheca lavorativa di pluto', 'pluto');
INSERT INTO public.bacheca VALUES ('TEMPO_LIBERO', 'Bacheca tempo libero di pluto', 'pluto');
INSERT INTO public.bacheca VALUES ('UNIVERSITA', 'Bacheca universitaria di pippo', 'pippo');
INSERT INTO public.bacheca VALUES ('LAVORO', 'Bacheca lavorativa di pippo', 'pippo');
INSERT INTO public.bacheca VALUES ('TEMPO_LIBERO', 'Bacheca tempo libero di pippo', 'pippo');
INSERT INTO public.bacheca VALUES ('UNIVERSITA', 'Bacheca Universit…', 'mario');
INSERT INTO public.bacheca VALUES ('LAVORO', 'Bacheca Lavoro', 'mario');
INSERT INTO public.bacheca VALUES ('TEMPO_LIBERO', 'Bacheca Tempo Libero', 'mario');
INSERT INTO public.bacheca VALUES ('UNIVERSITA', 'Bacheca Universit…', 'gennaro');
INSERT INTO public.bacheca VALUES ('LAVORO', 'Bacheca Lavoro', 'gennaro');
INSERT INTO public.bacheca VALUES ('TEMPO_LIBERO', 'Bacheca Tempo Libero', 'gennaro');


--
-- Data for Name: todo; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.todo VALUES (24, 'Preparare esame SQL', '2025-07-01', 'https://moodle.univ.it/sql', NULL, 'Ripassare trigger e funzioni', '00FF00', 1, 'NON_COMPLETATO', 'mario', 'UNIVERSITA');
INSERT INTO public.todo VALUES (3, 'Prenotare vacanza', '2025-07-10', 'ryanair.com', NULL, 'Controllare voli e hotel', '23CCFF', 8, 'COMPLETATO', 'admin', 'TEMPO_LIBERO');
INSERT INTO public.todo VALUES (25, 'Mangiare da Zio Tom', '2025-09-22', '', NULL, 'Panino Salsiccia e patatine', 'FFFFFF', 2, 'NON_COMPLETATO', 'admin', 'TEMPO_LIBERO');
INSERT INTO public.todo VALUES (27, 'andare al mcdonald', '2025-09-23', NULL, NULL, 'mangiare un big mac', 'FFFFFF', 1, 'NON_COMPLETATO', 'admin', 'TEMPO_LIBERO');
INSERT INTO public.todo VALUES (32, 'aa', '2025-05-25', NULL, NULL, 'a', 'FF00AA', 1, 'NON_COMPLETATO', 'gennaro', 'UNIVERSITA');
INSERT INTO public.todo VALUES (7, 'Tesi triennale', '2025-07-15', NULL, NULL, 'Scrivere introduzione e metodi', 'C0C0C0', 1, 'NON_COMPLETATO', 'pluto', 'UNIVERSITA');
INSERT INTO public.todo VALUES (8, 'Stage azienda', '2025-07-20', NULL, NULL, 'Contattare referente', '008000', 1, 'NON_COMPLETATO', 'pluto', 'LAVORO');
INSERT INTO public.todo VALUES (9, 'Trekking domenica', '2025-06-29', NULL, NULL, 'Preparare zaino', 'FFD700', 1, 'COMPLETATO', 'pluto', 'TEMPO_LIBERO');
INSERT INTO public.todo VALUES (12, 'Uscita in bici', '2025-06-22', '', NULL, 'Percorso lungomare', '7FFFD4', 1, 'NON_COMPLETATO', 'pippo', 'TEMPO_LIBERO');
INSERT INTO public.todo VALUES (11, 'Inviare CV', '2025-07-03', '', NULL, 'Aggiornare e inviare curriculum', 'F4D28F', 1, 'COMPLETATO', 'pippo', 'LAVORO');
INSERT INTO public.todo VALUES (2, 'Completare progetto', '2025-07-01', '', NULL, 'Finire progetto per lavoro', '00CCCC', 2, 'NON_COMPLETATO', 'admin', 'LAVORO');
INSERT INTO public.todo VALUES (10, 'Compiti programmazione', '2025-06-27', '', NULL, 'Esercizi su Array e HashMap', 'FF0000', 1, 'NON_COMPLETATO', 'pippo', 'UNIVERSITA');
INSERT INTO public.todo VALUES (1, 'Studiare SQL', '2025-06-25', '', NULL, 'Ripassare JOIN e trigger', '66FF33', 1, 'NON_COMPLETATO', 'admin', 'UNIVERSITA');


--
-- Data for Name: condivisione; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.condivisione VALUES ('admin', 11, 'ACCEPTED');
INSERT INTO public.condivisione VALUES ('admin', 24, 'ACCEPTED');
INSERT INTO public.condivisione VALUES ('admin', 12, 'PENDING');


--
-- Name: todo_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.todo_id_seq', 32, true);


--
-- PostgreSQL database dump complete
--

