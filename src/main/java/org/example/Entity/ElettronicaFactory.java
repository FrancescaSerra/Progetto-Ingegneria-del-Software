package org.example.Entity;

public class ElettronicaFactory extends ArticoloFactory{
    @Override
    public Articolo creaArticolo(Utente u, String nome, String inizio, String fine, double prezzo, String data) {
        return new Elettronica(u,nome,inizio,fine,prezzo,data);
    }
}
