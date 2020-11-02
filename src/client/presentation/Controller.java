/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.presentation;

import chatclient.ServiceProxy;

/**
 *
 * @author Porras
 */
public class Controller {
    View view;
    Model model;
    
    ServiceProxy localService;
    
    
    public void deliver(String message){
        model.getMessages().add(message);
        model.commit();    
    }
}
