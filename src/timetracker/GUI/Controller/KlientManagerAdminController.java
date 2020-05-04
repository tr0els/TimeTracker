/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.Duration;
import timetracker.BE.Client;
import timetracker.BE.Project;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.ClientModel;
import timetracker.GUI.Model.TaskModel;

/**
 * FXML Controller class
 *
 * @author Charlotte
 */
public class KlientManagerAdminController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private JFXListView<Client> listviewfx;
    @FXML
    private Label klientNavnlbl;
    @FXML
    private Label timeprislbl;
    @FXML
    private JFXListView<Project> listviewprojekts;
    @FXML
    private JFXTextField Nyklientnavn;
    @FXML
    private JFXTextField nydefaulttimepris;
    @FXML
    private JFXDrawer skuffe;
    @FXML
    private JFXButton btbnyklient;

    private Client selectedClient;
    private Client newclient = new Client();
    private static ClientModel model;
    private static TaskModel taskmodel;
    @FXML
    private AnchorPane opretnyklientpane;
    @FXML
    private JFXButton btbopretklient;
    @FXML
    private JFXTextField txtretnavn;
    @FXML
    private JFXButton retklientbtb;
    @FXML
    private AnchorPane retklientpane;
    @FXML
    private JFXButton retvalgteklientnbtb;
    @FXML
    private JFXTextField txtrettimepris;
    @FXML
    private Label lblprojektnavn;
    @FXML
    private Label lblprojekttimepris;

    public KlientManagerAdminController() throws DALException, SQLException {
        model = ClientModel.getInstance();
        taskmodel = TaskModel.getInstance();
    }
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            
            populateClientList();
        
            skuffe.close();
            retklientbtb.setVisible(false);
            retvalgteklientnbtb.setVisible(false);
            listviewprojekts.setTooltip(tooltipforprojektlist());
          

        

        } catch (DALException ex) {
            Logger.getLogger(KlientManagerAdminController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(KlientManagerAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void populateClientList() throws DALException, SQLException {
 
       listviewfx.setItems(model.getClients());

    }


    @FXML
    private void getSelectedClient(MouseEvent event) throws DALException, SQLException {

        selectedClient = listviewfx.getSelectionModel().getSelectedItem();
       
        klientNavnlbl.setText(selectedClient.getClient_name());
        timeprislbl.setText(selectedClient.getDefault_rate() + " DKK");
        retklientbtb.setVisible(true);
        retvalgteklientnbtb.setVisible(true);
        addprojektstolistview(selectedClient);
        lblprojektnavn.setText("");
        lblprojekttimepris.setText("");
    }

    private Tooltip tooltipforprojektlist() {
            Tooltip tip = new Tooltip();

        if (istheresomthinginprojectview()== 1) {
            tip.setText("Vælg en klient for at få vist deres projekter");
            return tip;}
        else if(istheresomthinginprojectview() == 2) {
            tip.setText("Klienten har ingen projekter");
            return tip;
    }
    return null;
    }
        
        
    public int istheresomthinginprojectview(){
        
        if ( selectedClient == null) {
            return 1;}
        if(Bindings.isEmpty(listviewprojekts.getItems()).get() && selectedClient != null )
        {return 2;}
      
       return 0; 
    }

      

    private void addprojektstolistview(Client client) throws DALException, SQLException {

        //selectedClient = listviewfx.getSelectionModel().getSelectedItem();

        listviewprojekts.setItems(FXCollections.observableArrayList(model.getClientprojcts(client)));
        //listviewprojekts.setItems(FXCollections.observableArrayList(taskmodel.getProjects()));
    }

    @FXML
    private void tooltip(MouseEvent event) {

        listviewprojekts.setTooltip(tooltipforprojektlist());
    }

    @FXML
    private void showpanewithnewklient(ActionEvent event) {
        skuffe.setSidePane(opretnyklientpane);
        skuffe.open();
        skuffe.toFront();
    }

    @FXML
    private void handleopretklient(ActionEvent event) throws DALException, SQLException {
         for (int i = 0; i < model.getClients().size(); i++) {
            System.out.println(i + "before");
        }
        
         
         
        Client mockclient = new Client(5,"EASV",1234);
        
        
        model.getClients().add(mockclient);
        
        for (int i = 0; i < model.getClients().size(); i++) {
            System.out.println(i +"after");
        }

        
        listviewfx.refresh();
        skuffe.close();

    }

    @FXML
    private void handleretklient(ActionEvent event) throws DALException, SQLException {
       
        String retnavn = txtretnavn.getText().trim();
        int rettimepris = Integer.parseInt(txtrettimepris.getText().trim());

        selectedClient.setClient_name(retnavn);
        selectedClient.setDefault_rate(rettimepris);
        model.editClient(selectedClient);
        timeprislbl.setText(rettimepris+" DKK");
        klientNavnlbl.setText(retnavn);
        listviewfx.refresh();
        skuffe.close();

    }

    @FXML
    private void openskuffemenretklient(ActionEvent event) {
        skuffe.setSidePane(retklientpane);
        skuffe.open();
        txtretnavn.setText(selectedClient.getClient_name());
        txtrettimepris.setText(selectedClient.getDefault_rate() + "");

    }

    @FXML
    private void getSelectedProjekt(MouseEvent event) {
       Project valgteprojekt = listviewprojekts.getSelectionModel().getSelectedItem();
       
       lblprojektnavn.setText(valgteprojekt.getProject_name());
       lblprojekttimepris.setText(valgteprojekt.getProject_rate()+"DKK");
       
    }

}
