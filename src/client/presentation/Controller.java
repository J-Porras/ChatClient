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
            model.setCurrent_destino(friend.getNickname());
          //  model.getCurrent_user().setDestino(friend);
        } catch (Exception e) {
            System.out.println("Error: set destino controller");
        }

    }
    
    public void login() throws Exception{
        //crea un usuario lo busca en la base de datos, null o no
        Client islogged = new Client(view.getLogInID().getText(),view.getLogInPass().getText(),view.getLogInNombre().getText(),view.getLogInNick().getText());
        islogged.setIsonline(true);
        
        

        
        islogged = ServiceProxy.getInstance().login(islogged);
        this.user = islogged;
        this.view.setCurrent(user);
        
        
        System.out.println(islogged.getId());
        System.out.println(islogged.getPassword());
        
        
        model.setCurrent_user(islogged);
        if (model.getCurrent_user()!=null) {
            System.out.println("Cliente no null dijo controller");
            ServiceXml.getInstance().setData(XmlPersister.getInstance(model.getCurrent_user().getId()).load()); 
            model.setCurrent_user(ServiceXml.getInstance().getData().getClient());
            if("N/A".equals(model.getCurrent_user().getId())){
                model.setCurrent_user(islogged);
            }
            ServiceXml.getInstance().setClient(model.getCurrent_user()); 
            model.setActivos(model.getCurrent_user().getFriends());
            System.out.println("Cantidad de Chats:");
            System.out.println(model.getCurrent_user().getChats().size());
            System.out.println("Cantidad de Amigos:");
            System.out.println(model.getCurrent_user().getFriends().size());
            System.out.println(islogged.toString());
            ServiceProxy.getInstance().ResponseOnlineUsers();
            
            
            model.commit();

     
            System.out.println("compa");
    
        }
        else
            System.out.println("Cliente  null dijo controller");
        
        model.commit();
    }
    
    
    
    public void logout(){
        this.attempts = 1;
        try {
            ServiceXml.getInstance().setClient(model.getCurrent_user());
            XmlPersister.getInstance(model.getCurrent_user().getId()).store(ServiceXml.getInstance().getData());
            ServiceProxy.getInstance().logout(model.getCurrent_user());
        }
        catch (Exception ex) {}
       
        model.setCurrent_user(null);
        model.setMessages(new ArrayList<String>());
        
        model.commit();
    }
    //AFK  comiendo, haga pull
    
    public void deliver(String message){
        System.out.println("******CONTROLLER DELIVER");
        model.getMessages().add(message);
        
        model.commit();    
    }
    
    public void deliver(Mensaje msg) throws Exception{
        System.out.println("\nController: deliver mensaje\n");
        model.getMessages().add(msg.getMensaje());
        Client actual = model.getCurrent_user();
        Chat c = actual.getChatFriend(model.getCurrent_destino());
        c.addMsg(msg);
          model.commit();
        //ServiceXml.getInstance().addMessage(msg);
        System.out.println("\n*********************Controller:  mensaje añadido a la lista total de msgs, proximo a commit\n");
      
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
        msg.setRemitente(model.getCurrent_user().getNickname());
       msg.setDestino(model.getCurrent_destino());
        msg.setMensaje(model.getCurrent_user().getNickname()+" : "+view.getPostmsg().getText());
        System.out.println("**********Mensaje creado");
        
        deliver(msg.getMensaje());// se envia el mensaje a si mismo y se actualiza
        
        Client actual = model.getCurrent_user();
        Chat c = actual.getChatFriend(model.getCurrent_destino());
        c.addMsg(msg);
        
        ServiceProxy.getInstance().post(msg);
        //ServiceXml.getInstance().addMessage(msg);
        model.commit();
        
        
    }
    
    public void addFriend(String id) throws Exception{
        
        Client c = new Client();
        c.setNickname(id);
        model.getCurrent_user().addFriend(c);
        //ServiceXml.getInstance().store(model.getCurrent_user());
      //  model.getActivos().add(c);
        XmlPersister.getInstance(model.getCurrent_user().getId()).store(ServiceXml.getInstance().getData());
        this.model.fixList();
        model.commit();
    }
    

    public Client getUserController(){
        return model.getCurrent_user();
    }
    
    public void setStateFriends(List<Client> friends){
        System.out.println("CONTROLLER SETSTATE FRIENDS----------");
        model.setAllFriends(friends);
        this.model.fixList();
        model.commit();
    }
    


    public void setCurrentChat(String nickname){
        
        
        try {
            
            System.out.println("Controller : set current chat y cantidad"); 
            //Client compa = model.getCurrent_destino();
            System.out.println(model.getCurrent_user().getChats().size()); 
            Chat chat = model.getCurrent_user().getChatFriend(nickname);
            
            if (chat!=null) {//
                System.out.println("Chat de compa encontrado");
                List<String> msg = Collections.synchronizedList(new ArrayList<String>());
                
                for (int i = 0; i < chat.getChat().size(); i++) {
                    String msgaux = chat.getChat().get(i).getMensaje();
                    msg.add(msgaux);
                }
                
                System.out.println("Lista de mensajes hecha");
                model.setMessages(msg);
               
                
            }
            model.commit();
            
        } catch (Exception e) {
            System.out.println("Error set current chat try catch");
        }
        
    }
    
    
    
    
    
}
