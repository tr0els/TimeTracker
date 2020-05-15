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
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
import timetracker.BE.Task.Log;
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
    private JFXComboBox<?> comboProjekter;
    @FXML
    private JFXComboBox<?> comboPerioder;
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
    
     private ProjektModel pModel;
     private BrugerModel bModel;
     private ClientModel cModel;
    @FXML
    private JFXButton seekbtb;
    @FXML
    private JFXButton clearFilterbtb;
         
    
    public OverviewForAdminsController() throws DALException, SQLException{
    pModel = ProjektModel.getInstance();
    bModel = BrugerModel.getInstance();
    cModel = ClientModel.getInstance();
        
    }

    
    private ObservableList<Project> listeAfProjekter;
    FilteredList<Project> filteredItems;

    /**
     * Initializes the controller class.
     * @param url
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
                try {
       filterskuffe.setSidePane(searchAnchorpane);
       filterskuffe.toFront();
       filterskuffe.close();
       filteredItems = new FilteredList<>(FXCollections.observableList(pModel.getProjectsWithExtraData()));

            populatetable();
            //addFilter();
            populatecombobox();
        } catch (DALException ex) {
            Logger.getLogger(OverviewForAdminsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(OverviewForAdminsController.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }    
    
    @FXML
    private void handleFilteropen(ActionEvent event) {
        
          if(filterskuffe.isOpened())
        {filterskuffe.close();}
        else { filterskuffe.open(); }
    }
    
    
    public void populatetable() throws DALException, SQLException{
    
       listeAfProjekter = pModel.getProjectsWithExtraData();
         //List<Task.Log> logList = new ArrayList<>();
         
         //logList = taskmodel.getTaskLogListById(1);
         
     
      colprojekts.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProject_name()));
      coltotaltid.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTotal_tid()));
      colKlient.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClientName()));
      colBillable.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBillableTime()) );
      tableview.setItems(listeAfProjekter);
   
    }
    
    private void populatecombobox() throws DALException, SQLException{
    
        
        //comboPerioder
        //comboProjekter
        
        ComboMedarbejder.setItems(bModel.getUsers());
        comboKlienter.setItems(cModel.getClients());
        
          
    }
    
    
//    public void combofilter(ActionEvent event){
//       
//          FilteredList<Project> filteredItems = new FilteredList<Project>(listeAfProjekter, p -> true);
//
//        
//        int val = comboKlienter.getValue().getClient_id();
//        
//       
//    
//    }
    
    
//    public void addFilter() throws DALException{
//        ObjectProperty<Predicate<Project>> klientFilter = new SimpleObjectProperty<>();
//        //ObjectProperty<Predicate<Project>> medarbejderFilter = new SimpleObjectProperty<>();
//        
//        klientFilter.bind(Bindings.createObjectBinding(() -> 
//            project -> comboKlienter.getValue() == null || comboKlienter.getValue().getClient_id() == project.getClient_id(), 
//            comboKlienter.valueProperty()));    
//        
//         tableview.setItems(filteredItems);
//         
//          filteredItems.predicateProperty().bind(Bindings.createObjectBinding(
//                () -> klientFilter.get(), 
//                klientFilter));
//    }
//    
    public void getProjectsForfilter() throws DALException{
        
        String europeanDatePattern = "dd-MM-yyyy";
        
        User comboUser = null;
        Client comboKlient = null;
        String fradatoSelected = null;
        String tildatoSelected = null;
        
        
       
        
        if(fradato.getValue() != null)
            fradatoSelected =  fradato.getValue().format(DateTimeFormatter.ofPattern(europeanDatePattern));
               System.out.println(fradatoSelected);
        
        if(tildato.getValue() != null )
            tildatoSelected =  tildato.getValue().format(DateTimeFormatter.ofPattern(europeanDatePattern));
               System.out.println(tildatoSelected);
               
        if (comboKlienter.getValue() != null)
            comboKlient = (Client) comboKlienter.getValue();
        if(ComboMedarbejder.getValue() != null)
            comboUser = (User) ComboMedarbejder.getValue();
        
        if( fradato.getValue() !=null && tildato.getValue() != null && tildato.getValue().isBefore(fradato.getValue()) && fradato.getValue().isAfter(tildato.getValue()))
        {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("Fejl i datoer");
            alert.setTitle("Fejl i valg af til- og fradatoer");
            alert.setContentText("fradato må ikke ligge efter tildato \n og tildato må ikke ligge før fradatoen");
            alert.showAndWait();
           
        }
 
            
        listeAfProjekter = pModel.getProjectsToFilter(comboUser, comboKlient, fradatoSelected, tildatoSelected);
      
       // tableview.getItems().clear();
        tableview.setItems(listeAfProjekter);
        
        
    
    }

 
    @FXML
    private void handleSeekPressed(ActionEvent event) throws DALException {
        getProjectsForfilter();
    }

    @FXML
    private void handleClearFilter(ActionEvent event) throws DALException, SQLException {
        ComboMedarbejder.getSelectionModel().clearSelection();
        comboKlienter.getSelectionModel().clearSelection();
       tildato.getEditor().clear();
       fradato.getEditor().clear();
        
       populatetable();
        
    }

    
}
