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
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
    
    private Client selectedClient;
    private Client newclient = new Client();
    private static ClientModel model;
    private static TaskModel taskmodel;

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
    /**
     * sætter alle klienterne ind i klient listviewet 
     * @throws DALException
     * @throws SQLException 
     */
    private void populateClientList() throws DALException, SQLException {
 
       listviewfx.setItems(model.getClients());

    }

    /**
     * håndtere den valgte klient fra klient listviwet, og tranportere dens info til relevandte lables, og arktivere "Ret klient knappen" mm. 
     * @param event
     * @throws DALException
     * @throws SQLException 
     */
    @FXML
    private void getSelectedClient(MouseEvent event) throws DALException, SQLException {

        selectedClient = listviewfx.getSelectionModel().getSelectedItem();
       
        klientNavnlbl.setText(selectedClient.getClient_name());
        timeprislbl.setText(selectedClient.getDefault_rate() + " DKK");
        
        //gør ret klient btb og retvalgteklient btb synlige
        retklientbtb.setVisible(true);
        retvalgteklientnbtb.setVisible(true);
        
        //tilføjer projeketer til listviewet for den valgte klient
        addprojektstolistview(selectedClient);
        
        //resetter projekt info lablsne
        lblprojektnavn.setText("");
        lblprojekttimepris.setText("");
    }
    
    /**
     * finder ud af hvilket tooltip der skal vises 
     * @return 
     */
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
        
   /**
    * hjælpe metode til at finde ud af hvad der er i projekt listviewet, og retunere en int
    * som bruges i tooltipforprojektview() for at sætte den korrekte tooltip. 
    * @return 
    */     
    public int istheresomthinginprojectview(){
        
        if ( selectedClient == null) {
            return 1;}
        if(Bindings.isEmpty(listviewprojekts.getItems()).get() && selectedClient != null )
        {return 2;}
      
       return 0; 
    }

      
    /**
     * tilføjer den valgte klients projekter til listviewet
     * @param client
     * @throws DALException
     * @throws SQLException 
     */
    private void addprojektstolistview(Client client) throws DALException, SQLException {
        listviewprojekts.setItems(FXCollections.observableArrayList(model.getClientprojcts(client)));
    }
    
    /**
     * Tooltip eventhandler, tjekker om musen hover over projektlistview for at sætte det rigtige tooltip
     * @param event 
     */
    @FXML
    private void tooltip(MouseEvent event) {

        listviewprojekts.setTooltip(tooltipforprojektlist());
    }
    /**
     * opret ny klient knap, som åbner draweren med txtinput felter til at oprette en klient
     * @param event 
     */
    @FXML
    private void showpanewithnewklient(ActionEvent event) {
        skuffe.setSidePane(opretnyklientpane);
        skuffe.open();
        skuffe.toFront();
    }
    /**
     * knap til at håndtere oprettelsen af en ny klien, knappen ligger i anchorpane, som ikke kan ses når draweren ikke er åben.
     * @param event
     * @throws DALException
     * @throws SQLException 
     */
    @FXML
    private void handleopretklient(ActionEvent event) throws DALException, SQLException {
 
       Client newklient = new Client();
       
        String nytnavn = Nyklientnavn.getText().trim();
        int timepris = Integer.parseInt(nydefaulttimepris.getText().trim());
       
        newklient = model.createClient(nytnavn, timepris);
       
        model.getClients().add(newklient);

        listviewfx.refresh();
        skuffe.close();

    }
    /**
     * håndtere retklient, knappen ligger i anchorpane som kun kan ses når draweren med retklien er åben. 
     * @param event
     * @throws DALException
     * @throws SQLException 
     */
    @FXML
    private void handleretklient(ActionEvent event) throws DALException, SQLException {
       
        String retnavn = txtretnavn.getText().trim();
        int rettimepris = Integer.parseInt(txtrettimepris.getText().trim().replace(" DKK", " ").trim());

        selectedClient.setClient_name(retnavn);
        selectedClient.setDefault_rate(rettimepris);
        model.editClient(selectedClient);
        timeprislbl.setText(rettimepris+" DKK");
        klientNavnlbl.setText(retnavn);
        listviewfx.refresh();
        skuffe.close();
       

    }
    /**
     * åbner draweren med ret klien txtinputfelter
     * @param event 
     */
    @FXML
    private void openskuffemenretklient(ActionEvent event) {
        skuffe.setSidePane(retklientpane);
        skuffe.open();
        txtretnavn.setText(selectedClient.getClient_name());
        txtrettimepris.setText(selectedClient.getDefault_rate() + " DKK");

    }
    /**
     * sætter info lablesne men info om det valgte projekt. 
     * @param event 
     */
    @FXML
    private void getSelectedProjekt(MouseEvent event) {
       Project valgteprojekt = listviewprojekts.getSelectionModel().getSelectedItem();
       
       
       lblprojektnavn.setText(valgteprojekt.getProject_name());
       lblprojekttimepris.setText(valgteprojekt.getProject_rate()+"DKK");
       
       
    }

}
