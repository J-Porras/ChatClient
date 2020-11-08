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
    public static ServiceXml instance;
    
    private ServiceXml(){
        data = new Data();
    }
    
    public static ServiceXml getInstance(){
        if (instance == null) {
            instance = new ServiceXml();
        }
        return instance;
    }
    
    public void store(Client c) throws Exception{
        chatprotocol.XmlPersister.getInstance(c.getNickname()).store(data);
    }
    
    public void load(String nickname) throws Exception{
        this.data = chatprotocol.XmlPersister.getInstance(nickname).load();
    }
    

    public void addMessage(Mensaje msg) throws Exception{
        System.out.println("");
        Client c = data.getClient();
        Chat chat;
        chat = c.getChatFriend(msg.getDestino().getNickname());
        chat.addMsg(msg);
        store(data.getClient());  
    }
    
    
    public Client getFriend(Client c){
        return this.data.getClient().findFriend(c.getNickname());
    }
    
    public Chat getChatFriend(Client c){
        System.out.println("Chat protocol get chat friend");
        for (int i = 0; i < this.data.getClient().getChats().size(); i++) {
            if (this.data.getClient().getChats().get(i).getDestino().getNickname()== c.getNickname()) {
                System.out.println("OJO: chat encontrado");
                return this.data.getClient().getChats().get(i);
            }
        }
        System.out.println("OJO CHAT NO ENCONTRADO");
        return null;
    }
    
    public void addMsgtoChat(Mensaje msg){
        Client destino = msg.getDestino();
        for (int i = 0; i < this.data.getClient().getChats().size(); i++) {
            if (this.data.getClient().getChats().get(i).getDestino().getNickname() == destino.getNickname()) {
                this.data.getClient().getChats().get(i).getChat().add(msg);
                break;
            }
            
        }
    }
    
    public void addFriend(Client c){
        this.data.getClient().addFriend(c);
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
