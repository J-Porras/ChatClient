/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.presentation;

import chatclient.ServiceProxy;
import chatclient.ServiceXml;
import chatprotocol.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Porras
 */
public class Controller {
    private View view;
    private Model model;
    private ServiceProxy localService;
    private int attempts = 0;
    private Client user;
    
    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        localService = (ServiceProxy)ServiceProxy.getInstance();
        localService.setController(this);
        view.setController(this);
        view.setModel(model);
    }
    
    public void increseAttempts(){
        attempts=attempts+1;
    }
    
    public int getAttempts(){
        return this.attempts;
    }
    
    public Client getUser(){
        return this.user;
    }
    
    public void setDestino(String nickname){
        
        
        
        try {
            System.out.println("Controller: set destino");
            Client friend = model.getCurrent_user().findFriend(nickname);
            model.setCurrent_destino(friend);
            model.getCurrent_user().setDestino(friend);
        } catch (Exception e) {
            System.out.println("Error: set destino controller");
        }

    }
    
    public void login() throws Exception{
        //crea un usuario lo busca en la base de datos, null o no
        Client cl = new Client();
        cl.setId(view.getLogInID().getText());
        cl.setPassword(view.getLogInPass().getText());
        cl.setNombre(view.getLogInNombre().getText());
        cl.setNickname(view.getLogInNick().getText());
        
        

        
        Client islogged = ServiceProxy.getInstance().login(cl);
        this.user = islogged;
        this.view.setCurrent(user);
        
        System.out.println("Cliente no null dijo controller");
        System.out.println(islogged.getId());
        System.out.println(islogged.getPassword());
        
        
        model.setCurrent_user(islogged);
        if (model.getCurrent_user()!=null) {
            System.out.println(islogged.toString());
     
            ServiceXml.getInstance().setClient(islogged);
            ServiceXml.getInstance().store(islogged.getNickname());
            ServiceXml.getInstance().load(islogged.getNickname());
            model.setActivos(ServiceXml.getInstance().getData().getClient().getFriends());
        }
        
        model.commit();
    }
    
    
    
    public void logout(){
        try {
            ServiceProxy.getInstance().logout(model.getCurrent_user());
        }
        catch (Exception ex) {}
        model.setCurrent_user(null);
        model.setMessages(new ArrayList<String>());
        model.commit();
    }
    
    
    public void deliver(String message){
        
        model.getMessages().add(message);
        
        model.commit();    
    }
    
    public void deliver(Mensaje msg) throws Exception{
        model.getMessages().add(msg.getMensaje());
        ServiceXml.getInstance().addMessage(msg);
        
        model.commit();
    }
    
    public void setActivos(List<Client> clients){
        model.setActivos(clients);
        model.commit();
    }
    
    public void addtoTableActivos(Client cl){
        model.getJtableclients().addClient(cl);
    }
    
    public void updateFriends(Client c) throws Exception{
        ServiceProxy.getInstance().giveClients(c);
    }
    
    public void sendMSG() throws Exception{
        Mensaje msg = new Mensaje();
        msg.setRemitente(model.getCurrent_user());
        msg.setDestino(model.getCurrent_destino());
        msg.setMensaje(model.getCurrent_user().getNickname()+" : "+view.getPostmsg().getText());
        
        deliver(msg.getMensaje());// se envia el mensaje a si mismo y se actualiza
        
        
        
        ServiceProxy.getInstance().post(msg);
        ServiceXml.getInstance().addMessage(msg);
        model.commit();
        
        
    }
    
    public void addFriend(String id) throws Exception{
        if (!this.isDuplicated(id)) {
            Client c = new Client();
            c.setNickname(id);
            model.getActivos().add(c);
            model.getCurrent_user().getFriends().add(c);
            ServiceXml.getInstance().addFriend(c);
            model.commit();
        }

    }
    
    private boolean isDuplicated(String id){
        for (int i = 0; i < model.getActivos().size(); i++) {
            if (id == model.getActivos().get(i).getNickname()) {
                return true;
            }
        }
        return false;
    }
    
    public Client getUserController(){
        return model.getCurrent_user();
    }

    


    public void setCurrentChat(){
        
        
        try {
            
            System.out.println("Controller : set current chat"); 
            Client compa = model.getCurrent_destino();

            Chat chat = model.getCurrent_user().getChatFriend(compa.getNickname());
            System.out.println("Chat de compa encontrado");
            if (chat!=null) {
                List<String> msg = Collections.synchronizedList(new ArrayList<String>());
                
                for (int i = 0; i < chat.getChat().size(); i++) {
                    String msgaux = chat.getChat().get(i).getMensaje();
                    msg.add(msgaux);
                }
                
                System.out.println("Lista de mensajes hecha");
                model.setMessages(msg);
               
                model.commit();
            }

            
        } catch (Exception e) {
            System.out.println("Error set current chat try catch");
        }
        
    }
    
    
    
}
