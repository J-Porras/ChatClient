/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.presentation;

import chatprotocol.Client;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Porras
 */
public class ClientsJTable extends AbstractTableModel {
    
    List<Client> activos;
    
    public ClientsJTable(){ 
        this.activos = Collections.synchronizedList(new ArrayList<Client>());
    }
    
    public ClientsJTable(List<Client> usuarios){
        this.activos = usuarios;
        
    }
    
   
    @Override
    public int getRowCount() {
        return activos.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return activos.get(rowIndex).getNickname();
        }
        return 0;
    }
    
    public String getColumnName(int col) {
      return "Usuarios";
    }   
    
    private void insertNewRow(Client cl) {
        activos.add(cl);
        int rowIndex = activos.size();
        fireTableRowsInserted(rowIndex, rowIndex);
    }   
    
    
    //esto deberia agregar por medio de threads usuarios a la table
    //https://stackoverflow.com/questions/13407418/threads-and-jtable
    public void addClient(final Client cl) {
    SwingUtilities.invokeLater(
            new Runnable() {
                @Override
                public void run() {
                    insertNewRow(cl);
                }
            }
        );//fin invokeLater
    }
    
    
    
}
