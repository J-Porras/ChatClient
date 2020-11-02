 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.presentation;

import chatprotocol.Client;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Porras
 */
public class Model extends java.util.Observable {
    private Client current_user;
    List<String> messages;
    
    public Model() {
       current_user = null;
       messages=new ArrayList<>();
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
    
    //----------------Patron Observer------------
    
    public void addObserver(java.util.Observer o) {
        super.addObserver(o);
        this.commit();
    }
    
    public void commit(){
        this.setChanged();
        this.notifyObservers();        
    } 
    
    
}
