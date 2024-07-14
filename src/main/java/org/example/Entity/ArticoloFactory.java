package org.example.Entity;

public abstract class ArticoloFactory {

    public abstract Articolo creaArticolo(Utente u,String nome, String inizio, String fine, double prezzo,String data);
}

