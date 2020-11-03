/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import client.presentation.Controller;
import client.presentation.Model;
import client.presentation.View;

/**
 *
 * @author Porras
 */
public class Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Model m= new Model();
        View v = new View();
        Controller c= new Controller(v,m);
        v.setVisible(true);
    }
    
}
