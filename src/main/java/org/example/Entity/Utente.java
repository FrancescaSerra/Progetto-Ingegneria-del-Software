package org.example.Entity;

import java.util.Objects;

public class Utente {


    private String nome;

    private String cognome;



    public Utente(String n,String c){
        this.nome = n;
        this.cognome=c;

    }

    public String getNome() {

        return nome;
    }

    public String getCognome() {
        return cognome;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utente utente = (Utente) o;
        return nome.equals( utente.nome) && cognome.equals( utente.cognome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, cognome);
    }
}
