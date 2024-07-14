package org.example;
/*
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.example.Entity.Articolo;
import org.example.Entity.Utente;
import org.example.Service.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class ClassTest {


    private Server server;
    private ManagedChannel channel;
    private Client client;
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
        client = new Client(channel); // Modificato per accettare un ManagedChannel
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
        Articolo articolo = new Articolo(utente, "TestArticolo", "10:06", "12:00", 100.0,"2024-07-12");
     //   boolean result = client.mettiArticolo(articolo);
      //  assertTrue(result);
        Utente utente1 = new Utente("francesca", "serra");
        Articolo articolo1 = new Articolo(utente1, "TestArticolo", "10:06", "12:00", 100.0,"2024-07-12");
     //   boolean result1 = client.mettiArticolo(articolo1);
      //  assertFalse(result1);

    }

    @Test
    public void testPiazzaOfferta() {
        Utente utente = new Utente("Francesca", "Serra");
        Articolo articolo = new Articolo(utente, "TestArticolo", "20:26", "20:30", 100.0,"2024-07-13");
      //  client.mettiArticolo(articolo);
        boolean result = client.piazzaOfferta("TestArticolo", "Fra", "se", 200.0);
        boolean result1 = client.piazzaOfferta("articoloInesistente", "Fra", "se", 200.0);
        assertTrue(result);
        assertFalse(result1);
    }
}

 */