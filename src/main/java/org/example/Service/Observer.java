package org.example.Service;

import org.example.Entity.Articolo;

public interface Observer {

    public boolean Registracliente(String nome,String cognome);

    public boolean mettiArticolo(Articolo a,String tipoarticolo );

    public boolean piazzaOfferta(String nomeArt,String nome,String cognome,double nuovaOfferta);


    public void notificami();
}
