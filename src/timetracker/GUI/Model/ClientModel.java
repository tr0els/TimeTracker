/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Model;

import java.sql.SQLException;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import timetracker.BE.Client;
import timetracker.BE.Project;
import timetracker.BLL.BLLManager;
import timetracker.DAL.DALException;

/**
 *
 * @author Charlotte
 */
public class ClientModel {
    
    private ObservableList<Client> allClients;
    private static ClientModel model = null; 
    private static BLLManager bll;
    private ObservableList<Project> allProjectsbyClientID;
    
    
    
    private ClientModel() throws DALException, SQLException{
        bll = BLLManager.getInstance();
        allClients = FXCollections.observableArrayList();
        allClients.addAll(bll.getClients());
        allProjectsbyClientID = FXCollections.observableArrayList();
        
    }
    
      public static ClientModel getInstance() throws DALException, SQLException {
        if (model == null) {
            model = new ClientModel();
        }
        return model;
    }
    
      /**
     * Returnerer en liste med alle Clients.
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public ObservableList<Client> getClients() throws DALException, SQLException {
        Comparator<Client> byName = (Client cl1, Client cl2) -> cl1.getClient_name().compareTo(cl2.getClient_name());
        allClients.sort(byName);
        return allClients;
    }
    
    

      /**
     * Sender Client objekt ned til DAL laget som skal oprettes.
     *
     * @param client
     */
    public Client createClient(String navn, int timepris) {
        return bll.createClient(navn, timepris);
    }

    /**
     * Sender Client objekt ned til DAL laget som skal ændres.
     *
     * @param client
     */
    public void editClient(Client client) {
      bll.editClient(client);
      
    }

    /**
     * Sender Client objekt ned til DAL laget som skal slettes.
     * det skal vi ikke kunne 
     *
     * @param client
     */
    public void deleteClient(Client client) {
        bll.deleteClient(client);
    }
    
    public ObservableList<Project>  getClientprojcts(Client client){
        if (allProjectsbyClientID.isEmpty()){
        allProjectsbyClientID.addAll(bll.getProjekctsbyClientID(client));
        }else {
        allProjectsbyClientID.clear();
        allProjectsbyClientID.addAll(bll.getProjekctsbyClientID(client));
        }
    
        return allProjectsbyClientID;
    
    }
    

}
