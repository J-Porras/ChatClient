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
import java.io.PrintWriter;
import java.io.StringWriter;
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
                            System.out.println("\nProtocolo deliver\n");
                            Mensaje msg = (Mensaje)in.readObject();
                            System.out.println("\nMENSAJE PARSEADO DE VUELTA\n");
                            deliver(msg);
                            
                        } 
                        catch (ClassNotFoundException ex) 
                        {
                        }
                    break;   
                    
                    case Protocol.ON_USERS:
                        try {
                            System.out.println("Devuelve Users");
                            List<Client> onUsers = (List<Client>) in.readObject();
                            controller.setStateFriends(onUsers);
                            
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
    

    private void deliver(final Mensaje msg){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                try {
                    System.out.println("\ntrying to deliver to controller\n");
                    controller.deliver(msg);
                } catch (Exception ex) {
                    Logger.getLogger(ServiceProxy.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
         }
      );
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
             // data.setClient(client);
                //chatprotocol.XmlPersister.getInstance(client.getId());
                System.out.println("Cliente encontrado desde database");
                clienteIn.setIsonline(true);
                
                //ResponseOnlineUsers();
                
                
                
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
    
    public void ResponseOnlineUsers() throws Exception{
        out.writeInt(Protocol.IM_ONLINE);
        //out.writeObject(client);
        System.out.println("-------------------------------------");
        System.out.println("-online user get");
        out.flush();
         
                
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
            System.out.println(msg.getMensaje());
            out.writeInt(Protocol.MSG);
            out.writeObject(msg);
            
            System.out.println("Objeto Mensaje escrito");
            System.out.println("****************************************");
            out.flush();
        } catch (Exception e) {
            System.out.println("*****************************************");
            System.out.println("Error post Serviceproxy");
            System.out.println("****************************************");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionDetails = sw.toString();
            System.out.println(exceptionDetails);
        }
    }

    @Override
    public List<Client> OnlineUsers(List<Client> list) throws Exception {
        List<Client> friends = Collections.synchronizedList(new ArrayList<Client>());
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j <ServiceXml.getInstance().getData().getClient().getFriends().size() ; j++) {
                if (list.get(i).getNickname().equals(ServiceXml.getInstance().getData().getClient().getFriends().get(j).getNickname())) 
                {
                    friends.add(list.get(i));
                    
                }
            }
            
        }
        return friends;
    }

    
}
