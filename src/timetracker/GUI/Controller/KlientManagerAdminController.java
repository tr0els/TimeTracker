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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import timetracker.BE.Client;
import timetracker.BE.Project;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.ClientModel;
import timetracker.GUI.Model.ProjektModel;
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


    private Client selectedClient;
    private Client newclient = new Client();
    private static ClientModel model;
    private static ProjektModel pModel;
   
    private JFXButton btbGåtilprojekter;
    @FXML
    private JFXButton btbcancelnyklient;
    @FXML
    private JFXButton btbcloseret;

    public KlientManagerAdminController() throws DALException, SQLException {
        model = ClientModel.getInstance();
        pModel = ProjektModel.getInstance();
   
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        nydefaulttimepris.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    nydefaulttimepris.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        txtrettimepris.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtrettimepris.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        try {

            populateClientList();
            skuffe.close();
            retklientbtb.setVisible(false);
            retvalgteklientnbtb.setVisible(false);
            listviewprojekts.setFocusTraversable(false);
            listviewprojekts.setTooltip(tooltipforprojektlist());

        } catch (DALException ex) {
            Logger.getLogger(KlientManagerAdminController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(KlientManagerAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * sætter alle klienterne ind i klient listviewet
     *
     * @throws DALException
     * @throws SQLException
     */
    private void populateClientList() throws DALException, SQLException {

        listviewfx.setItems(model.getClients());

    }

    /**
     * håndtere den valgte klient fra klient listviwet, og tranportere dens info
     * til relevandte lables, og arktivere "Ret klient knappen" mm.
     *
     * @param event
     * @throws DALException
     * @throws SQLException
     */
    @FXML
    private void getSelectedClient(MouseEvent event) throws DALException, SQLException {

        selectedClient = listviewfx.getSelectionModel().getSelectedItem();

        klientNavnlbl.setText(selectedClient.getClientName());
        timeprislbl.setText(selectedClient.getDefaultRate()+ " DKK");

        //gør ret klient btb og retvalgteklient btb synlige
        retklientbtb.setVisible(true);
        retvalgteklientnbtb.setVisible(true);

        //tilføjer projeketer til listviewet for den valgte klient
        
        addprojektstolistview(selectedClient);

  

    }

    /**
     * finder ud af hvilket tooltip der skal vises
     *
     * @return
     */
    private Tooltip tooltipforprojektlist() {
        Tooltip tip = new Tooltip();

        if (istheresomthinginprojectview() == 1) {
            tip.setText("Vælg en klient for at få vist deres projekter");
            return tip;
        } else if (istheresomthinginprojectview() == 2) {
            tip.setText("Klienten har ingen projekter");
            return tip;
        }
        return null;
    }

    /**
     * hjælpe metode til at finde ud af hvad der er i projekt listviewet, og
     * retunere en int som bruges i tooltipforprojektview() for at sætte den
     * korrekte tooltip.
     *
     * @return
     */
    public int istheresomthinginprojectview() {

        if (selectedClient == null) {
            return 1;
        }
        if (Bindings.isEmpty(listviewprojekts.getItems()).get() && selectedClient != null) {
            return 2;
        }

        return 0;
    }

    /**
     * tilføjer den valgte klients projekter til listviewet
     *
     * @param client
     * @throws DALException
     * @throws SQLException
     */
    private void addprojektstolistview(Client client) throws DALException, SQLException {
    
            
            
        listviewprojekts.setCellFactory(param -> new ListCell<Project>() {
            @Override
            protected void updateItem(Project item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null && empty) {
                    setText(null);
                    setGraphic(null);
         
           
                } else {
                    setText(item.getProjectName() + " - " + item.getProjectRate() + " DKK");
                }
            }

        });
            
        listviewprojekts.setItems(model.getClientprojcts(client));
    
    }

    /**
     * Tooltip eventhandler, tjekker om musen hover over projektlistview for at
     * sætte det rigtige tooltip
     *
     * @param event
     */
    @FXML
    private void tooltip(MouseEvent event) {

        listviewprojekts.setTooltip(tooltipforprojektlist());
    }

    /**
     * opret ny klient knap, som åbner draweren med txtinput felter til at
     * oprette en klient
     *
     * @param event
     */
    @FXML
    private void showpanewithnewklient(ActionEvent event) {
        skuffe.setSidePane(opretnyklientpane);
        skuffe.open();
        skuffe.toFront();
    }

    /**
     * knap til at håndtere oprettelsen af en ny klien, knappen ligger i
     * anchorpane, som ikke kan ses når draweren ikke er åben.
     *
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
     * håndtere retklient, knappen ligger i anchorpane som kun kan ses når
     * draweren med retklien er åben.
     *
     * @param event
     * @throws DALException
     * @throws SQLException
     */
    @FXML
    private void handleretklient(ActionEvent event) throws DALException, SQLException {

        String retnavn = txtretnavn.getText().trim();
        int rettimepris = Integer.parseInt(txtrettimepris.getText().trim().replace(" DKK", " ").trim());

        selectedClient.setClientName(retnavn);
        selectedClient.setDefaultRate(rettimepris);
        model.editClient(selectedClient);
        timeprislbl.setText(rettimepris + " DKK");
        klientNavnlbl.setText(retnavn);
        listviewfx.refresh();
        skuffe.close();

    }

    /**
     * åbner draweren med ret klien txtinputfelter
     *
     * @param event
     */
    @FXML
    private void openskuffemenretklient(ActionEvent event) {
        skuffe.setSidePane(retklientpane);
        skuffe.open();
        txtretnavn.setText(selectedClient.getClientName());
        txtrettimepris.setText(selectedClient.getDefaultRate() + " DKK");

    }

 

    @FXML
    private void handleCancelRetklient(ActionEvent event) {

        skuffe.close();
    }

    @FXML
    private void handleCancelnyklient(ActionEvent event) {
        nydefaulttimepris.clear();
        Nyklientnavn.clear();
        skuffe.close();
    }

}
