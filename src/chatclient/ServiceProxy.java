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
import java.util.List;
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
    private boolean continuar = true;
    
    
    public static IService getInstance(){
        if (singleton == null) {
            singleton = new ServiceProxy();     
        }
        return singleton;
    }
    
    
    ServiceProxy(){}
    
    
    public void setController(Controller controller) {
        this.controller = controller;
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
                            String message=(String)in.readObject();
                            deliver(message);
                        } 
                        catch (ClassNotFoundException ex) 
                        {
                        }
                        
                    break;   
                }//fin switch
                
                out.flush();
                requestClients();
                updateTable();
            } 
            catch (IOException  ex) {
                continuar = false;
            }                        
        }//fin while(continuar)
    }

    
    private void updateTable(){
       int method;
       continuar = true;
        while (continuar) {
            try 
            {
                method = in.readInt();
                
                switch(method)
                {
                    case Protocol.ON_USERS:
                        try {
                            List<Client> active_clients = (List<Client>) in.readObject();
                            this.controller.setActivos(active_clients);
                        } 
                        catch (ClassNotFoundException ex) 
                        {
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
    
    
    private void requestClients(){
        try {
            out.writeInt(Protocol.REQ_USERS);
            out.flush();
        }
        catch (Exception e) {
        }
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
    
    private void deliver( final String  message ){
      SwingUtilities.invokeLater(new Runnable(){
            public void run(){
               controller.deliver(message);
            }
         }
      );
   }

    
    
    @Override
    public Client login(Client client) throws Exception {
       this.connect();
        try {
            out.writeInt(Protocol.LOGIN);
            out.writeObject(client);
            out.flush();
            int response = in.readInt();
            if(response == Protocol.ERROR_NO_ERROR){
                Client clienteIn =  (Client) in.readObject();
                this.start();
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
    public void logout(Client client) throws Exception {
        out.writeInt(Protocol.LOGOUT);
        out.writeObject(client);
        out.flush();
        this.stop();
        this.disconnect();
    }
    
    
    public void stop(){
        continuar=false;
    }
    

    @Override
    public void signin(Client client) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void post_msg(String string, Client client) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void giveClients() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
