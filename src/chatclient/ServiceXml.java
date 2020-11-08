/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import chatprotocol.Chat;
import chatprotocol.Client;
import chatprotocol.Data;
import chatprotocol.Mensaje;

/**
 *
 * @author Porras
 */
public class ServiceXml {
    private Data data;
    
    private ServiceXml(){
        data = new Data();
    }
    
    public static ServiceXml singleton;
    
    public static ServiceXml getInstance(){
        if (singleton == null) {
            singleton = new ServiceXml();
        }
        return singleton;
    }
    
    public void store(String nickname) throws Exception{
        chatprotocol.XmlPersister.getInstance(nickname).store(data);
    }
    
    public void load(String nickname) throws Exception{
        this.data = chatprotocol.XmlPersister.getInstance(nickname).load();
    }
    
    public void addFriend(Client c) throws Exception{
        store(data.getClient().getNickname());
    }
    
    public void addMessage(Mensaje msg) throws Exception{
        Client c = data.getClient();
        Chat chat;
        chat = c.getChatFriend(msg.getDestino().getNickname());
        chat.addMsg(msg);
        store(msg.getRemitente().getId());

        
    }
    
    public void setData(Data data){
        this.data = data;
    }
    
    public Data getData(){
        return this.data;
    }
    
    public void setClient(Client c){
        data.setClient(c);
    }
            
}
