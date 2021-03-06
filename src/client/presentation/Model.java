 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.presentation;

import chatprotocol.Client;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Porras
 */

public class Model extends java.util.Observable {
    private Client current_user;
    private List<String> messages;
    private List<Client> activos;//TODOS NO SOLO ACTIVOS
    private List<Client> allFriends;
    private ClientsJTable jtableclients;
    private String current_destino;
    
    
    public Model() {
       current_user = null;
       messages=new ArrayList<>();
       this.activos = Collections.synchronizedList(new ArrayList<Client>());
       jtableclients  = new ClientsJTable();
       
    }

    public List<Client> getAllFriends() {
        return allFriends;
    }

    public void setAllFriends(List<Client> allFriends) {
        System.out.println("MODEL SET ALL FRIENDS STATE");
        this.allFriends = allFriends;
    }
    
    
    

    public String getCurrent_destino() {
        return current_destino;
    }

    public void setCurrent_destino(String current_destino) {
        this.current_destino = current_destino;
    }
    
    
    

    public Client getCurrent_user() {
        return current_user;
    }

    public void setCurrent_user(Client current_user) {
        this.current_user = current_user;

    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<Client> getActivos() {
        return activos;
    }

    public void setActivos(List<Client> activos) {
        this.activos = activos;
        for (int i = 0; i < activos.size(); i++) {
            jtableclients.addClient(activos.get(i));
        }
    }

    public ClientsJTable getJtableclients() {
        return jtableclients;
    }

    public void setJtableclients(ClientsJTable jtableclients) {
        this.jtableclients = jtableclients;
    }

    
    
    
    
    //----------------Patron Observer------------
    
    public void addObserver(java.util.Observer o) {
        super.addObserver(o);
        this.commit();
    }
    
    public void commit(){
        this.setChanged();
        this.notifyObservers();        
    } 
    
    public void fixList(){
        for (int j = 0; j < this.activos.size(); j++) {
            activos.get(j).setIsonline(false);
        }
        for (int j = 0; j < this.activos.size(); j++) {
                for (int i = 0; i < allFriends.size(); i++) {
                    if (activos.get(j).getNickname().equals(allFriends.get(i).getNickname())) {
                        activos.get(j).setIsonline(true);
                        break;
                    }
                    
                }
        }
        
    }
    
    
    
    
    
}
