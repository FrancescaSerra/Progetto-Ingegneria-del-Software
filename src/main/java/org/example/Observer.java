package org.example;

public interface Observer {

    public boolean Registracliente(String nome,String cognome);

    public boolean mettiArticolo(Articolo a );

    public boolean piazzaOfferta(String nomeArt,String nome,String cognome,double nuovaOfferta);


    public void notificami();
}
