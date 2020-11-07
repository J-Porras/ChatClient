/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;
import chatprotocol.*;
import client.presentation.Controller;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author Porras
 */
public class ServiceProxy  implements IService{
    
    private static IService singleton;
    
    
    private Socket skt;    
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Controller controller;
    private Data data;
    private boolean continuar = true;
    
    
    public static IService getInstance(){
        if (singleton == null) {
            singleton = new ServiceProxy();     
        }
        return singleton;
    }
    
    
    ServiceProxy(){
        data = new Data();
    }
    
    
    public void setController(Controller controller) {
        this.controller = controller;
    }
    
    public void stop(){
        continuar=false;
    }
    
    public void start(){
        //inicia el thread que recibe las peticiones del server
        Thread t = new Thread(new Runnable(){
            public void run(){
                listen();
                

            }
        });
        continuar = true;
        t.start();
    }
    
    public void listen(){
        //dependiendo de la entrada realiza X accion
        int method;
        while (continuar) {
            try 
            {
                method = in.readInt();
                
                switch(method)
                {
                    case Protocol.DELIVER:
                        try {
                            Mensaje msg = (Mensaje)in.readObject();
                            
                            deliver(msg);
                            
                        } 
                        catch (ClassNotFoundException ex) 
                        {
                        }
                    break;   
                    
                    case Protocol.ON_USERS:
                        try {
                            List<Client> friends = Collections.synchronizedList(new ArrayList<Client>());
                            friends = (List<Client>) in.readObject();
                            deliverFriends(friends);
                            
                        }
                        catch (Exception e) {
                        }  
                    break;
                    
                    
                    
                }//fin switch
                
                out.flush();
            } 
            catch (IOException  ex) {
                continuar = false;
            }                        
        }//fin while(continuar)
    }

    
    
    
    private void connect() throws Exception{
        skt = new Socket(Protocol.SERVER,Protocol.PORT);
        out = new ObjectOutputStream(skt.getOutputStream() );
        in = new ObjectInputStream(skt.getInputStream());    
    }
    
    
    private void disconnect() throws Exception{
        skt.shutdownOutput();
        skt.close();
    }
    
    private void deliver( final String  message){
      SwingUtilities.invokeLater(new Runnable(){
            public void run(){
               controller.deliver(message);
            }
         }
      );
    }
    
    private void deliver(final Mensaje msg){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                try {
                    controller.deliver(msg);
                } catch (Exception ex) {
                    Logger.getLogger(ServiceProxy.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
         }
      );
    }
    
    private void deliverFriends(List<Client> friends){
        controller.setActivos(friends);
    }


    @Override
    public void logout(Client client) throws Exception {
        out.writeInt(Protocol.LOGOUT);
        out.writeObject(client);
        out.flush();
        this.stop();
        this.disconnect();
    }
    
   

    @Override
    public void giveClients(Client c) throws Exception {
        
        try {

            out.writeInt(Protocol.REQ_USERS);
            out.writeObject(c);
            out.flush();
        }
        catch (Exception e) {
        }
        
    }

    @Override
    public Client login(Client client) throws Exception {
        connect();

        try {

            out.writeInt(Protocol.LOGIN);
            out.writeObject(client);
            out.flush();
  
            
            int response = in.readInt();
            
            if(response == Protocol.ERROR_NO_ERROR){
                Client clienteIn =  (Client) in.readObject();
                this.start();
                data.setClient(client);
                chatprotocol.XmlPersister.getInstance(client.getId());
                return clienteIn;
                
            }
            else{
                disconnect();
                throw new Exception("No se ha encontrado el usuario");
            } 
            
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Client addFriend(Client c) throws Exception {
        
        try {
            out.writeInt(Protocol.ADD_USER);
            out.writeObject(c);
            out.flush(); 
            
            
            int response = in.readInt();
            
            
            if(response == Protocol.ERROR_NO_ERROR){
                Client clienteIn =  (Client) in.readObject();
                
                return clienteIn;             
            }
            else{
                throw new Exception("No se ha encontrado el usuario para agregar");
            } 
            
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void post(Mensaje msg) throws Exception {
        try {
            out.writeInt(Protocol.MSG);
            out.writeObject(msg);
            out.flush();
        } catch (Exception e) {
        }
    }

    
}
