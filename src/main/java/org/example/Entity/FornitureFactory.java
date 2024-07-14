package org.example.Entity;

public class FornitureFactory extends ArticoloFactory{
    @Override
    public Articolo creaArticolo(Utente u, String nome, String inizio, String fine, double prezzo, String data) {
        return new Forniture(u,nome,inizio,fine,prezzo,data);
    }
}
