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
    private View view;
    private Model model;
    private ServiceProxy localService;
    
    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        localService = (ServiceProxy)ServiceProxy.instance();
        localService.setController(this);
        //view.setController(this);
        //view.setModel(model);
    }
    
    
    public void deliver(String message){
        model.getMessages().add(message);
        model.commit();    
    }
}
