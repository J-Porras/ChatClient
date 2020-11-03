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
    
    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        localService = (ServiceProxy)ServiceProxy.getInstance();
        localService.setController(this);
        view.setController(this);
        view.setModel(model);
    }
    
    public void login() throws Exception{
        //crea un usuario lo busca en la base de datos, null o no
        Client cl = new Client();
        cl.setId(view.getLogInID().getText());
        cl.setPassword(view.getLogInPass().getText());
        Client islogged = ServiceProxy.getInstance().login(cl);
        
        
        model.setCurrent_user(islogged);
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
}
