package org.example.Entity;

public class ArticoloStandardFactory extends ArticoloFactory{
    @Override
    public Articolo creaArticolo(Utente u, String nome, String inizio, String fine, double prezzo, String data) {
        return new ArticoloStandard(u,nome,inizio,fine,prezzo,data);
    }
}
