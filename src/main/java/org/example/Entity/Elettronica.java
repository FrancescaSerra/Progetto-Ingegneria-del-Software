package org.example.Entity;

public class Elettronica extends Articolo{
    public Elettronica(Utente u, String n, String inizio, String fine, double prezzo, String data) {
        super(u, n, inizio, fine, prezzo, data);
    }



    @Override
    public String getTipoArticolo() {

        return "Elettronica";
    }

    @Override
    public void setVenditore(Utente venditore) {
        super.setVenditore(venditore);
    }

    @Override
    public void setData(String data) {
        super.setData(data);
    }

    @Override
    public void setUtente(Utente utente) {
        super.setUtente(utente);
    }

    @Override
    public void setPrezzo(double prezzo) {
        super.setPrezzo(prezzo);
    }
}
