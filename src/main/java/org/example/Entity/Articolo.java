package org.example.Entity;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Articolo {


    private Utente utente ;

    private String nome;

    private Utente venditore;
    private String data;

    private String inizio;

    private String fine;

    private  double prezzo;

    private AtomicInteger contatore = new AtomicInteger(0);

    public Articolo(Utente u,String n,String inizio,String fine,double prezzo,String data){
        this.utente = u;
        this.nome = n;
        this.inizio= inizio;
        this.fine = fine;
        this.prezzo = prezzo;
        this.data = data;


    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public void setVenditore(Utente venditore) {
        this.venditore = venditore;
    }

    public Utente getVenditore() {
        return venditore;
    }

    public double getPrezzo() {
        return prezzo;
    }
    public String getNome() {
        return nome;
    }

    public Utente getUtente() {
        return utente;
    }

    public String getFine() {
        return fine;
    }

    public String getInizio() {
        return inizio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Articolo articolo = (Articolo) o;
        return  this.nome.equals( articolo.nome) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(utente, nome, inizio, fine, prezzo);
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public void incrementa(){
        contatore.incrementAndGet();
    }
    public int decrementa(){
        return contatore.decrementAndGet();
    }
    public abstract String getTipoArticolo();


}
