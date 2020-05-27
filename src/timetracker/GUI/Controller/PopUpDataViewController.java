/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXButton;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import timetracker.BE.Project;
import timetracker.BE.TaskForDataView;
import timetracker.BE.User;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.TaskModel;

/**
 * FXML Controller class
 *
 * @author Charlotte
 */
public class PopUpDataViewController implements Initializable
{
    
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
    private ObservableList<TaskForDataView> listeAfTask;
    private Project choosenProject = null;
    private User userFromOVerview = null;
    private String fradato = null;
    private String tildato = null;
    private String monthStart = null;
    private String monthEnd = null;
    
    String europeanDatePattern = "dd-MM-yyyy HH:mm";
    @FXML
    private TableColumn<TaskForDataView, String> colhhmm;
    
    public PopUpDataViewController() throws DALException, SQLException
    {
        tModel = TaskModel.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        //populateTable();
    }
    
    public void populateTable() throws DALException
    {
        listeAfTask = tModel.getListOfTaskForDataView(choosenProject, userFromOVerview, fradato, tildato, monthStart,monthEnd);
        
        colOpgave.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colStart.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart().format(DateTimeFormatter.ofPattern(europeanDatePattern))));
        colEnd.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnd().format(DateTimeFormatter.ofPattern(europeanDatePattern))));
        colhhmm.setCellValueFactory(cellData ->    
        {   String hhMM = cellData.getValue().getTime();
            String[] timesplitup = hhMM.split(":");
            
            int hhTotal = Integer.parseInt(timesplitup[0]);
            int mmTotal = Integer.parseInt(timesplitup[1]);
            int secTotal = Integer.parseInt(timesplitup[2]);
            String totaltimeString;
            if ( secTotal >= 30)
            {
                mmTotal +=1;
            } 
            totaltimeString = hhTotal + ":" + String.format( "%02d", mmTotal);
            return new SimpleStringProperty(totaltimeString);
        });
            
        colBillable.setCellValueFactory(cellData ->
        {
            boolean billable = cellData.getValue().isBillable();
            String billableAsString;
            if (billable == true)
            {
                billableAsString = "Ja";
            } else
            {
                billableAsString = "Nej";
            }
            return new SimpleStringProperty(billableAsString);
        });
        colMedarebjder.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedarbejder()));
        
        tableView.setItems(listeAfTask);
        tableView.getSelectionModel().setCellSelectionEnabled(false);
        
    }
    
    public void TransferInfoForPopup(Project project, User user , String fradato, String tildato, String monthStart, String monthEnd ) throws DALException
    {
        
        this.choosenProject = project;
        this.userFromOVerview = user;
        this.tildato = tildato;
        this.fradato = fradato;
        this.monthStart = monthStart;
        this.monthEnd = monthEnd;
        
        populateTable();
        lblProjektnavn.setText("Dette er er overblik over opgaverne for dit valgte projekt. \n" +"På denne sider er det muligt for dig at eksportere opgaverne, \n"+
                 "til en CSV fil. \n"+ "\nOBS, det er ikke muligt at redigere i opgaverne på denne side!");
        
       
     
                
        
    }
    
    @FXML
    private void handleExportCSV(ActionEvent event) throws Exception
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV file", "csv"));
        
        fileChooser.setDialogTitle("Save file");
        int userSelection = fileChooser.showSaveDialog(fileChooser);
        if (userSelection == JFileChooser.APPROVE_OPTION)
        {
            File fileToSave = new File(fileChooser.getSelectedFile()+".csv");
            try
            {
                FileWriter fw = new FileWriter(fileToSave);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(colOpgave.getText() + ";" + colStart.getText() + ";" + colEnd.getText() + ";" + colBillable.getText() + ";" + colMedarebjder.getText());
                bw.newLine();
                for (TaskForDataView tfdv : listeAfTask) {
                    {   
                        bw.write(tfdv.getName() + ";" + tfdv.getStart().format(DateTimeFormatter.ofPattern(europeanDatePattern))+ ";" + tfdv.getEnd().format(DateTimeFormatter.ofPattern(europeanDatePattern)) +";"+ tfdv.isBillable() + ";" + tfdv.getMedarbejder());
                    }
                    bw.newLine();
                  
                }
                bw.newLine();
                bw.write("Projekt navn : "+choosenProject.getProject_name() +";"+"timepris : " +choosenProject.getProject_rate()+" DKK");
                bw.close();
                fw.close();
            } catch (IOException ex)
            {
               
            }
        }
    }
}
