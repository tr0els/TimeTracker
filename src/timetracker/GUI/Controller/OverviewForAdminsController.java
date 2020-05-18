/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDrawer;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import timetracker.BE.Project;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import timetracker.BE.Client;
import timetracker.DAL.DALException;
import timetracker.BE.User;
import timetracker.GUI.Model.BrugerModel;
import timetracker.GUI.Model.ClientModel;
import timetracker.GUI.Model.ProjektModel;

/**
 * FXML Controller class
 *
 * @author Charlotte
 */
public class OverviewForAdminsController implements Initializable {

    @FXML
    private JFXDrawer filterskuffe;
    @FXML
    private AnchorPane searchAnchorpane;
    @FXML
    private JFXComboBox<YearMonth> comboPerioder;
    @FXML
    private JFXDatePicker fradato;
    @FXML
    private JFXDatePicker tildato;
    @FXML
    private JFXComboBox<User> ComboMedarbejder;
    @FXML
    private JFXComboBox<Client> comboKlienter;
    @FXML
    private TableView<Project> tableview;
    @FXML
    private TableColumn<Project, String> colprojekts;
    @FXML
    private TableColumn<Project, String> coltotaltid;
    @FXML
    private PieChart piechart;
    @FXML
    private BarChart<?, ?> barchart;
    @FXML
    private JFXButton Filterkanp;
    @FXML
    private TableColumn<Project, String> colKlient;
    @FXML
    private TableColumn<Project, String> colBillable;
    @FXML
    private JFXButton seekbtb;
    @FXML
    private JFXButton clearFilterbtb;
    private ProjektModel pModel;
    private BrugerModel bModel;
    private ClientModel cModel;
    
    private ObservableList<Project> listeAfProjekter;
    private ObservableList<String> listOfMonths;

    public OverviewForAdminsController() throws DALException, SQLException {
        pModel = ProjektModel.getInstance();
        bModel = BrugerModel.getInstance();
        cModel = ClientModel.getInstance();

    }


    /**
     * Initializes the controller class.
     *
     * @param url
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            filterskuffe.setSidePane(searchAnchorpane);
            filterskuffe.toFront();
            filterskuffe.close();
            populatetable();
            populatecombobox();
        } catch (DALException ex) {
            Logger.getLogger(OverviewForAdminsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(OverviewForAdminsController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void handleFilteropen(ActionEvent event) {

        if (filterskuffe.isOpened()) {
            filterskuffe.close();
        } else {
            filterskuffe.open();
        }
    }

    public void populatetable() throws DALException, SQLException {

        listeAfProjekter = pModel.getProjectsWithExtraData();
        
        colprojekts.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProject_name()));
        coltotaltid.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTotal_tid()));
        colKlient.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClientName()));
        colBillable.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBillableTime()));
        tableview.setItems(listeAfProjekter);

    }

    private void populatecombobox() throws DALException, SQLException {

        ComboMedarbejder.setItems(bModel.getUsers());
        comboKlienter.setItems(cModel.getClients());
        comboPerioder.setItems(bModel.getListOfPeriods());
      


    }

    public void getProjectsForfilter() throws DALException, SQLException {

        String europeanDatePattern = "dd-MM-yyyy";

        User comboUser = null;
        Client comboKlient = null;
        String fradatoSelected = null;
        String tildatoSelected = null;
        String MonthStart = null;
        String MonthEnd = null;
        
   

        if (fradato.getValue() != null) {
            fradatoSelected = fradato.getValue().format(DateTimeFormatter.ofPattern(europeanDatePattern));
        }
        System.out.println(fradatoSelected);

        if (tildato.getValue() != null) {
            tildatoSelected = tildato.getValue().format(DateTimeFormatter.ofPattern(europeanDatePattern));
        }
        System.out.println(tildatoSelected);

        if (comboKlienter.getValue() != null) {
            comboKlient = (Client) comboKlienter.getValue();
        }
        if (ComboMedarbejder.getValue() != null) {
            comboUser = (User) ComboMedarbejder.getValue();
        }
        if(comboPerioder.getValue() != null ){
            YearMonth getmonth =  comboPerioder.getValue();
            MonthEnd = getmonth.atEndOfMonth().format(DateTimeFormatter.ofPattern(europeanDatePattern));
            int lengthOfMonth =  getmonth.lengthOfMonth();
            MonthStart = getmonth.atEndOfMonth().minusDays(lengthOfMonth-1).format(DateTimeFormatter.ofPattern(europeanDatePattern));
            System.out.println(MonthStart);
            System.out.println(MonthEnd);
        }
        
        checkFilter();
         
        listeAfProjekter = pModel.getProjectsToFilter(comboUser, comboKlient, fradatoSelected, tildatoSelected, MonthStart, MonthEnd);

        // tableview.getItems().clear();
    }

    @FXML
    private void handleSeekPressed(ActionEvent event) throws DALException, SQLException {
        getProjectsForfilter();
    }

    @FXML
    private void handleClearFilter(ActionEvent event) throws DALException, SQLException {
        ComboMedarbejder.getSelectionModel().clearSelection();
        comboKlienter.getSelectionModel().clearSelection();
        comboPerioder.getSelectionModel().clearSelection();
        tildato.setValue(null);
        fradato.setValue(null);
        tildato.getEditor().clear();
        fradato.getEditor().clear();

        populatetable();

    }
    /**
     * tjekker Filter vaglene, 
     */
    public void checkFilter() throws DALException, SQLException{
        
         if (fradato.getValue() != null && tildato.getValue() != null && tildato.getValue().isBefore(fradato.getValue()) && fradato.getValue().isAfter(tildato.getValue())) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("Fejl i datoer");
            alert.setTitle("Fejl i valg af til- og fradato");
            alert.setContentText("fradato må ikke ligge efter tildato \n og tildato må ikke ligge før fradatoen");
            alert.showAndWait();
            tildato.setValue(null);
            fradato.setValue(null);
            tildato.getEditor().clear();
            fradato.getEditor().clear();
        }

        
       if (fradato.getValue() != null && comboPerioder !=null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("Fejl i filtrering");
            alert.setTitle("Fejl i valg af filtering");
            alert.setContentText("Du kan ikke søge på fra- og tildatoer\nog på perioder, på sammetid");
            alert.showAndWait();
            tildato.setValue(null);
            fradato.setValue(null);
            tildato.getEditor().clear();
            fradato.getEditor().clear();
            comboPerioder.getSelectionModel().clearSelection();
            populatetable();
        }
       
           
       if (tildato.getValue() != null && comboPerioder !=null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("Fejl i filtrering");
            alert.setTitle("Fejl i valg af filtering");
            alert.setContentText("Du kan ikke søge på fra- og tildatoer\nog på perioder, på sammetid");
            alert.showAndWait();
            tildato.setValue(null);
            fradato.setValue(null);
            tildato.getEditor().clear();
            fradato.getEditor().clear();
            comboPerioder.getSelectionModel().clearSelection();
            populatetable();
       }
       
       
    }

}
