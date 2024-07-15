package org.example;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import org.example.Entity.Articolo;
import org.example.Entity.ArticoloFactory;
import org.example.Entity.ArticoloStandardFactory;
import org.example.Entity.Utente;
import org.example.Service.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


public class ClassTest {


    private Server server;
    private ManagedChannel channel;
    private Client client;

    private Client client2;
    private static final String SERVER_NAME = InProcessServerBuilder.generateName();

    @Before
    public void setUp() throws Exception {
        // Avvia il server in-process
        server = InProcessServerBuilder.forName(SERVER_NAME)
                .addService(new org.example.Service.Server.ServerImple())
                .directExecutor() // Esegue chiamate sullo stesso thread
                .build()
                .start();

        // Crea un canale in-process e il client
        channel = InProcessChannelBuilder.forName(SERVER_NAME)
                .directExecutor()
                .build();
        client = new Client(channel);// Modificato per accettare un ManagedChannel
        client2 = new Client(channel);
    }

    @After
    public void tearDown() throws Exception {
        if (channel != null) {
            channel.shutdown();
        }
        if (server != null) {
            server.shutdown();
        }
    }

    @Test
    public void testRegistracliente() {
        boolean result = client.Registracliente("Francesca", "Serra");
        assertTrue(result);
        boolean result1 = client.Registracliente("Francesca","Serra");
        assertFalse(result1);
    }

    @Test
    public void testMettiArticolo() {
        Utente utente = new Utente("Francesca", "Serra");
        ArticoloFactory a = new ArticoloStandardFactory();
        Articolo articolo = a.creaArticolo(utente, "TestArticolo", "10:06", "12:00", 100.0,"2024-07-12");
        boolean result = client.mettiArticolo(articolo,"Standard");
        assertTrue(result);
        Utente utente1 = new Utente("francesca", "serra");
        Articolo articolo1 = a.creaArticolo(utente1, "TestArticolo", "10:06", "12:00", 100.0,"2024-07-12");
        boolean result1 = client.mettiArticolo(articolo1,"Standard");
        assertFalse(result1);

    }

    @Test
    public void testPiazzaOfferta() {
        Utente utente = new Utente("Francesca", "Serra");
        ArticoloFactory a = new ArticoloStandardFactory();
        Articolo articolo = a.creaArticolo(utente, "TestArticolo", "20:26", "20:30", 100.0,"2024-07-13");
        client.mettiArticolo(articolo,"Standard");
        boolean result = client.piazzaOfferta("TestArticolo", "Fra", "se", 200.0);
        boolean result1 = client.piazzaOfferta("articoloInesistente", "Fra", "se", 200.0);
        assertTrue(result);
        assertFalse(result1);
    }


    @Test
    public void test() throws InterruptedException {
        Utente utente = new Utente("Francesca", "Serra");
        Utente utente2 = new Utente("Utente", "Prova");
        client.Registracliente("Utente","Prova");
        client2.Registracliente("Francesca","Serra");
        client.notificami();
        ArticoloFactory a = new ArticoloStandardFactory();
        Articolo articolo = a.creaArticolo(utente, "TestArticolo", "10:58", "12:00", 100.0,"2024-07-15");
        client2.mettiArticolo(articolo,"Standard");
        client.piazzaOfferta("TestArticolo", "Utente", "Prova", 200.0);
        TimeUnit.SECONDS.sleep(2);
        List<Articolo> not = client.getNot();
        Articolo articolo1 = not.get(0);
        assertEquals(articolo,articolo1);
        assertEquals(articolo1.getUtente(),utente2);
    }
}

