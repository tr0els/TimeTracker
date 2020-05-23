/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import timetracker.BE.Project;
import timetracker.BE.Task;
import timetracker.BE.TaskForDataView;
import timetracker.BE.User;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.BrugerModel;
import timetracker.GUI.Model.ClientModel;
import timetracker.GUI.Model.ProjektModel;
import timetracker.GUI.Model.TaskModel;

/**
 * FXML Controller class
 *
 * @author Charlotte
 */
public class PopUpDataViewController implements Initializable {

    @FXML
    private TableColumn<TaskForDataView, String> colOpgave;
    @FXML
    private TableColumn<TaskForDataView, String> colStart;
    @FXML
    private TableColumn<TaskForDataView, String> colEnd;
    @FXML
    private TableColumn<TaskForDataView, String> colMedarebjder;
    @FXML
    private JFXButton btbExport;
    @FXML
    private Label lblProjektnavn;
    @FXML
    private TableView<TaskForDataView> tableView;
    @FXML
    private TableColumn<TaskForDataView, String> colBillable; 
    

    private final TaskModel tModel;
    private  ObservableList<TaskForDataView> listeAfTask;
    private Project choosenProject = null;
    private User userFromOVerview = null;
 
   
     String europeanDatePattern = "dd-MM-yyyy HH:mm";
    
    public PopUpDataViewController() throws DALException, SQLException{
    
   
       tModel = TaskModel.getInstance();
    
    }
    
     
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //populateTable();
         
    }    
 
    
    public void populateTable() throws DALException{
        
        listeAfTask =  tModel.getListOfTaskForDataView(choosenProject, userFromOVerview);
        
        
        colOpgave.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colStart.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart().format(DateTimeFormatter.ofPattern(europeanDatePattern))));
        colEnd.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnd().format(DateTimeFormatter.ofPattern(europeanDatePattern))));
        colBillable.setCellValueFactory(cellData -> {
        boolean billable = cellData.getValue().isBillable();
        String billableAsString;
        if(billable == true)
        { billableAsString = "Ja" ; }
        else {  billableAsString = "Nej";}
       return new SimpleStringProperty(billableAsString); });
        colMedarebjder.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedarbejder()));
    
        tableView.setItems(listeAfTask);
        tableView.getSelectionModel().setCellSelectionEnabled(false);
       
        
    }
    
    public void TransferProjektID(Project project, User user) throws DALException{
    
    this.choosenProject = project;
    this.userFromOVerview = user;
    populateTable(); 

    }
    
    
    
    public Label getLabel(){
    return lblProjektnavn;
    }
    
}
