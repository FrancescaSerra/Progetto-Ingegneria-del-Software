package org.example.Service;

import io.grpc.ServerBuilder;

import io.grpc.stub.StreamObserver;
import org.example.*;
import org.example.Entity.Articolo;
import org.example.Entity.Utente;
import org.example.Gui.GuiServer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class Server {

    private static GuiServer miaGui;

   private  static CopyOnWriteArrayList<Utente> ListadiUtenti = new CopyOnWriteArrayList<>();
    private static CopyOnWriteArrayList<Articolo> listaArticoli = new CopyOnWriteArrayList<>();
    private static ConcurrentHashMap<StreamObserver<OffertaNotificaResponse>,ArrayList<String>> offertepiazzate = new ConcurrentHashMap<>();
    private static CopyOnWriteArrayList<Articolo> ArticoloInAttesa = new CopyOnWriteArrayList<>();


    public Server(){
         miaGui = new GuiServer(this);
         RimuoviArticoli r = new RimuoviArticoli();
         r.start();

    }

    public  CopyOnWriteArrayList<Articolo> getListaArticoli() {
        return listaArticoli;
    }

    public  CopyOnWriteArrayList<Utente> getListadiUtenti() {
        return ListadiUtenti;
    }

    public CopyOnWriteArrayList<Articolo> getArticoloInAttesa() {
        return ArticoloInAttesa;
    }

    class RimuoviArticoli extends Thread{
        @Override
        public void run() {
            while(true){
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(!listaArticoli.isEmpty()){
                    for (Articolo a : listaArticoli){
                        LocalTime l = LocalTime.parse(a.getFine());
                        if (LocalTime.now().isAfter(l) || LocalTime.now().equals(l)) {
                            ServerImple.removeArticolo(a);
                            miaGui.aggiornaArticolo();

                        }
                    }
                }
            }
        }
    }


   public static class ServerImple extends RegistrazioneServiceGrpc.RegistrazioneServiceImplBase {
        private static synchronized void aggiungiArticolo(Articolo a){
            listaArticoli.add(a);

        }
        private static synchronized void removeArticolo(Articolo a){
            listaArticoli.remove(a);

        }


        @Override
        public synchronized void registrazione(RegistrazioneRequest request, StreamObserver<RegistrazioneResponse> responseObserver) {
            String nome = request.getNome();
            String cognome = request.getCognome();
            Utente u = new Utente(nome, cognome);
            boolean inserito = true;
            for (Utente utente : ListadiUtenti) {
                if (u.getNome().equals(utente.getNome()))
                    inserito = false;
            }
            if (inserito) {
                ListadiUtenti.add(u);
                 miaGui.aggionaU();

            }
            RegistrazioneResponse risp = RegistrazioneResponse.newBuilder().setRisposta(inserito).build();
            responseObserver.onNext(risp);
            responseObserver.onCompleted();
        }

        @Override
        public synchronized void mettiArticolo(MettiArticoloRequest request, StreamObserver<MettiArticoloResponse> responseObserver) {
            String nomeUtente = request.getNomeUtente();
            String nomeArticolo = request.getNomearticolo();
            String inizio = request.getOrarioinizio();
            String fine = request.getOrariofine();
            String cognome = request.getCognome();
            double prezzo = request.getPrezzo();
            String data = request.getData();
            boolean inserito = true;
            for (Articolo a : listaArticoli) {
                if (a.getNome().equals(nomeArticolo))
                    inserito = false;
            }
            Articolo f = new Articolo(new Utente(nomeUtente, cognome), nomeArticolo, inizio, fine, prezzo,data);
            f.setVenditore(new Utente(nomeUtente,cognome));
            LocalTime l = LocalTime.parse(inizio);
            LocalDate d = LocalDate.parse(data);
            if (inserito ){
                if((LocalTime.now().minusMinutes(5).isBefore(l)|| LocalTime.now().equals(l)) && LocalDate.now().isEqual(d))
                    aggiungiArticolo(f);
                else{
                    ArticoloInAttesa.add(f);
                }
                miaGui.aggiornaArticolo();
            }
            MettiArticoloResponse risp = MettiArticoloResponse.newBuilder().setRisposta(inserito).build();
            responseObserver.onNext(risp);
            responseObserver.onCompleted();
        }

        @Override
        public synchronized void notificaOfferta(OffertaNotificaRequest request, StreamObserver<OffertaNotificaResponse> responseObserver) {
            String nomearticolo = request.getNomeArticolo();
            if(!offertepiazzate.containsKey(responseObserver)){
                ArrayList<String> a = new ArrayList<>();
                a.add(nomearticolo);
                offertepiazzate.put(responseObserver,a);
                NotificaOfferta n = new NotificaOfferta(responseObserver);
                n.start();
            }
            else{
                offertepiazzate.get(responseObserver).add(nomearticolo);
            }
        }

        class NotificaOfferta extends Thread {


            private StreamObserver<OffertaNotificaResponse> client;

            public NotificaOfferta(StreamObserver<OffertaNotificaResponse> r) {
                this.client = r;

            }

            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (!listaArticoli.isEmpty()) {
                        for (Articolo a : listaArticoli) {
                            if (offertepiazzate.get(client).contains(a.getNome())) {
                                LocalTime l = LocalTime.parse(a.getFine());
                                if (LocalTime.now().isAfter(l) || LocalTime.now().equals(l)) {
                                    offertepiazzate.get(client).remove(a.getNome());
                                    OffertaNotificaResponse f = OffertaNotificaResponse.newBuilder().setNomearticolo(a.getNome()).setNomeUtente(a.getUtente().getNome()).setCognome(a.getUtente().getCognome()).setPrezzo(a.getPrezzo()).setFineAsta(true).build();
                                    client.onNext(f);
                                    int count = a.decrementa();
                                    if(count == 0){
                                        removeArticolo(a);
                                        miaGui.aggiornaArticolo();
                                    }
                                }
                            }
                        }



                    }

                }
            }
        }


        @Override
        public  void notifica(NotificaRequest request, StreamObserver<NotificaResponse> responseObserver) {
            NotificaThread n = new NotificaThread(responseObserver);
            n.start();
        }

        class NotificaThread extends Thread {

            private ArrayList<Articolo> giavisit = new ArrayList<>();

            private StreamObserver<NotificaResponse> client;

            public NotificaThread(StreamObserver<NotificaResponse> r) {
                this.client = r;

            }

            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Iterator<Articolo> iteratore = giavisit.iterator();
                    while (iteratore.hasNext()) {
                        Articolo a = iteratore.next();
                        if (!listaArticoli.contains(a)) {
                            iteratore.remove();
                            NotificaResponse n = NotificaResponse.newBuilder().setNomeUtente(a.getUtente().getNome()).setCognome(a.getUtente().getCognome())
                                    .setNomearticolo(a.getNome()).setOrarioinizio(a.getInizio()).setOrariofine(a.getFine()).setPrezzo(a.getPrezzo())
                                    .setAggiungi(false).setModifica(false).build();
                            client.onNext(n);
                        }
                    }

                        for (Articolo a : listaArticoli) {
                            if (!giavisit.contains(a)) {
                                Articolo b = new Articolo(a.getUtente(),a.getNome(),a.getInizio(),a.getFine(),a.getPrezzo(),a.getData());
                                giavisit.add(b);
                                NotificaResponse n = NotificaResponse.newBuilder().setNomeUtente(a.getUtente().getNome()).setCognome(a.getUtente().getCognome())
                                        .setNomearticolo(a.getNome()).setOrarioinizio(a.getInizio()).setOrariofine(a.getFine()).setPrezzo(a.getPrezzo())
                                        .setAggiungi(true).setModifica(false).build();
                                client.onNext(n);
                            }
                        }
                        for (Articolo a : ArticoloInAttesa){
                            LocalTime l = LocalTime.parse(a.getInizio());
                            LocalDate d = LocalDate.parse(a.getData());
                            if ((LocalTime.now().minusMinutes(5).isBefore(l)  || LocalTime.now().equals(l))&& LocalDate.now().isEqual(d) ){
                                if(!listaArticoli.contains(a)) {
                                    aggiungiArticolo(a);
                                    ArticoloInAttesa.remove(a);
                                }
                            }

                        }
                        for (Articolo a : listaArticoli) {
                            for (Articolo k : giavisit) {
                                if (a.getNome().equals(k.getNome()) && a.getPrezzo() != k.getPrezzo()) {
                                    k.setPrezzo(a.getPrezzo());
                                    k.setUtente(new Utente(a.getUtente().getCognome(),a.getUtente().getCognome()));
                                    NotificaResponse n = NotificaResponse.newBuilder().setNomeUtente(a.getUtente().getNome()).setCognome(a.getUtente().getCognome())
                                            .setNomearticolo(a.getNome()).setOrarioinizio(a.getInizio()).setOrariofine(a.getFine()).setPrezzo(a.getPrezzo())
                                            .setAggiungi(true).setModifica(true).build();
                                    client.onNext(n);

                                }
                            }

                        }
                }
            }
        }

        @Override
        public synchronized void piazzaOfferta(OffertaRequest request, StreamObserver<OffertaResponse> responseObserver) {
            double prezzo = request.getNuovaOfferta();
            String nomeArticolo = request.getNomeArticolo();
            String nome = request.getNomeUtente();
            String cognome = request.getCognome();
            Utente u = new Utente(nome,cognome);
            boolean inserito = false;
            for (Articolo a : listaArticoli) {
                if (a.getNome().equals(nomeArticolo) && a.getPrezzo() < prezzo && !a.getVenditore().equals(u)) {
                    inserito = true;
                    a.setPrezzo(prezzo);
                    a.setUtente(u);
                    a.incrementa();
                    notificaOsservatori(a);
                    miaGui.aggiornaArticolo();
                }
            }
            OffertaResponse o = OffertaResponse.newBuilder().setRiposta(inserito).build();
            responseObserver.onNext(o);
            responseObserver.onCompleted();
        }

        private void notificaOsservatori(Articolo a) {
            for(StreamObserver<OffertaNotificaResponse> observer: offertepiazzate.keySet() ){
                if(offertepiazzate.get(observer).contains(a.getNome())){
                    OffertaNotificaResponse o = OffertaNotificaResponse.newBuilder()
                            .setPrezzo(a.getPrezzo()).setNomeUtente(a.getUtente().getNome())
                            .setCognome(a.getUtente().getCognome()).setNomearticolo(a.getNome()).
                            setFineAsta(false).build();
                    observer.onNext(o);
                }
            }
        }

    }
       public static void main(String[] args) throws IOException, InterruptedException {
        io.grpc.Server server  = ServerBuilder.forPort(10000).addService(new ServerImple()).build().start();
        Server s = new Server();
        System.out.println("il server è attivo");
        server.awaitTermination();

    }

}
