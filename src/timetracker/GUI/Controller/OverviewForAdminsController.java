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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import timetracker.BE.Client;
import timetracker.BE.Task;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.TaskModel;
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
    private TableColumn<Log, String> coltotaltid;
    @FXML
    private TableColumn<Project, String> colsidstarbpå;
    @FXML
    private PieChart piechart;
    @FXML
    private BarChart<?, ?> barchart;
    @FXML
    private JFXButton Filterkanp;
    @FXML
    private TableColumn<Project, String> colKlient;
    
     private ProjektModel pModel;
     private BrugerModel bModel;
     private ClientModel cModel;
    
    public OverviewForAdminsController() throws DALException, SQLException{
    pModel = ProjektModel.getInstance();
    bModel = BrugerModel.getInstance();
    cModel = ClientModel.getInstance();
        
    }

    
    private ObservableList<Project> listeAfProjekter;
    

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
        
          if(filterskuffe.isOpened())
        {filterskuffe.close();}
        else { filterskuffe.open(); }
    }
    
    
    public void populatetable() throws DALException, SQLException{
    
       listeAfProjekter = pModel.getProjects();
         //List<Task.Log> logList = new ArrayList<>();
         
         //logList = taskmodel.getTaskLogListById(1);
         
    //    logList 
      //  String startTid = getStart_time().format(DateTimeFormatter.ofPattern("HH:mm"));
       
       colprojekts.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProject_name()));
       
       //coltotaltid.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTotal_tid().toString()));
       
       tableview.setItems(listeAfProjekter);
   
    }
    
    private void populatecombobox() throws DALException, SQLException{
    
        //ComboMedarbejder
        //comboKlienter
        //comboPerioder
        //comboProjekter
        
        ComboMedarbejder.setItems(bModel.getUsers());
        comboKlienter.setItems(cModel.getClients());
        
        
    
    }
    
    
}
