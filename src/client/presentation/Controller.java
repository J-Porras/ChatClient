/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.presentation;

import chatclient.ServiceProxy;
import chatprotocol.*;
import java.util.ArrayList;
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
    
    public void login() throws Exception{
        //crea un usuario lo busca en la base de datos, null o no
        Client cl = new Client();
        cl.setId(view.getLogInID().getText());
        cl.setPassword(view.getLogInPass().getText());
        cl.setNombre(view.getLogInNombre().getText());
        cl.setNickname(view.getLogInNick().getText());
        
        
        System.out.print("\n" + view.getLogInID().getText());//probando datos
          
        System.out.print("\n" + view.getLogInPass().getText());
        
        
        Client islogged = ServiceProxy.getInstance().login(cl);
        
        
        
        
        model.setCurrent_user(islogged);
       
        System.out.print("\nController fin, usuario asignado a model"+ model.getCurrent_user().getNickname());
        
        System.out.print("\nNickname: "+model.getCurrent_user().getNickname()+"\n");
      
        
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
}
