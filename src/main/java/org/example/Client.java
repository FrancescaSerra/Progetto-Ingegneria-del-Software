package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;



public class Client implements Observer{

    private String nome;
    private String Cognome;

    private GuiClient gui;

    private final RegistrazioneServiceGrpc.RegistrazioneServiceBlockingStub blockingStub;

    private final RegistrazioneServiceGrpc.RegistrazioneServiceStub stub;



    public Client(String host,int port) {
        gui = new GuiClient(this);

        ManagedChannel canale = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        blockingStub = RegistrazioneServiceGrpc.newBlockingStub(canale);
        stub = RegistrazioneServiceGrpc.newStub(canale);


    }

    public Client(ManagedChannel channel) {
        gui = new GuiClient(this);

        blockingStub = RegistrazioneServiceGrpc.newBlockingStub(channel);
        stub = RegistrazioneServiceGrpc.newStub(channel);

    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        Cognome = cognome;
    }

    public String getCognome() {
        return Cognome;
    }

    public String getNome() {
        return nome;
    }

    public boolean Registracliente(String nome , String cognome){
        RegistrazioneRequest r = RegistrazioneRequest.newBuilder()
                .setNome(nome).setCognome(cognome).build();
        RegistrazioneResponse f = blockingStub.registrazione(r);
        notificami();
        return f.getRisposta();
    }

    public  boolean mettiArticolo(Articolo a){
        MettiArticoloRequest m = MettiArticoloRequest.newBuilder().setCognome(a.getUtente().getCognome())
                .setNomearticolo(a.getNome()).setNomeUtente(a.getUtente().getNome())
                .setOrariofine(a.getFine()).setOrarioinizio(a.getInizio()).setPrezzo(a.getPrezzo()).build();
        MettiArticoloResponse f = blockingStub.mettiArticolo(m);
        return f.getRisposta();
    }

    public boolean piazzaOfferta(String nomeArt,String nome,String cognome,double nuovaOfferta){
        OffertaRequest o = OffertaRequest.newBuilder()
                .setNomeArticolo(nomeArt)
                .setNomeUtente(nome).
                setCognome(cognome)
                .setNuovaOfferta(nuovaOfferta).build();
        OffertaResponse f = blockingStub.piazzaOfferta(o);
        new notificaAsta(nomeArt).start();
        return f.getRiposta();

    }


    class notificaAsta extends Thread {
        private String nomeArticolo;

        public notificaAsta(String a){
            this.nomeArticolo = a;
        }

        public void run () {
                StreamObserver<OffertaNotificaResponse> c = new StreamObserver<OffertaNotificaResponse>() {
                    @Override
                    public void onNext(OffertaNotificaResponse offertaNotificaResponse) {
                        String nomeUtente = offertaNotificaResponse.getNomeUtente();
                        String nomeArticolo = offertaNotificaResponse.getNomearticolo();
                        String cognome = offertaNotificaResponse.getCognome();
                        double prezzo = offertaNotificaResponse.getPrezzo();
                        boolean fine = offertaNotificaResponse.getFineAsta();
                        gui.mandaNotifiche(nomeUtente, nomeArticolo, cognome, prezzo, fine);
                    }
                    @Override
                    public void onError(Throwable throwable) {
                    }
                    @Override
                    public void onCompleted() {
                    }
                };
                OffertaNotificaRequest f = OffertaNotificaRequest.newBuilder().setNomeArticolo(nomeArticolo).build();
                stub.notificaOfferta(f, c);
            }

    }



        public void notificami() {
            StreamObserver<NotificaResponse> c = new StreamObserver<NotificaResponse>() {
                @Override
                public void onNext(NotificaResponse notificaResponse) {
                    String nome = notificaResponse.getNomeUtente();
                    String cognome = notificaResponse.getCognome();
                    Utente u = new Utente(nome, cognome);
                    String inizio = notificaResponse.getOrarioinizio();
                    String fine = notificaResponse.getOrariofine();
                    double prezzo = notificaResponse.getPrezzo();
                    boolean aggiungi = notificaResponse.getAggiungi();
                    String nomeArticolo = notificaResponse.getNomearticolo();
                    Articolo articolo = new Articolo(u, nomeArticolo, inizio, fine, prezzo);
                    boolean modifica = notificaResponse.getModifica();
                    gui.aggiornaGui(articolo, aggiungi, modifica);
                }
                @Override
                public void onError(Throwable throwable) {
                }
                @Override
                public void onCompleted() {
                }
            };
            NotificaRequest g = NotificaRequest.newBuilder().build();
            stub.notifica(g, c);
        }


    public static void main(String[] args) {
        Client c = new Client("localhost",10000);

    }



}
