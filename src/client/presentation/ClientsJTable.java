/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.presentation;

import chatprotocol.Client;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

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
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return activos.get(rowIndex).getNickname();
        }
        if(columnIndex == 1){
            if(activos.get(rowIndex).getIsonline()){
                return "Activo";
            }
            return "Inactivo";
        }
        return 0;
    }
    
    public String getColumnName(int col) {
       if(col == 0)
            return "Usuarios";
       if(col == 1)
           return "Estado";
        return null;
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
