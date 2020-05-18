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
import java.text.DecimalFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
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
    
    private final ProjektModel pModel;
    private final BrugerModel bModel;
    private final ClientModel cModel;
    private double totalhouersForPiechart;
    private double billaableHouersForPiechart;
    private ObservableList<Project> listeAfProjekter;
  //  private ObservableList<String> listOfMonths;
    @FXML
    private Label lblforPiechart;
    final Label caption = new Label("");
    private Tooltip tipholder = new Tooltip();


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
            handlePieChart();
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
        
        //totalhouersForPiechart;

    }
    
    public void populatepieChart(){
        
        int getmetimeHHBill;
        int getmetimeMMBill;
        double getmetotalHHMMBill = 0.0;
        int getmetimeHHTotal;
        int getmetimeMMTotal;
        double getmetotalHHMMTotal = 0.0;
        
        for (Project project : listeAfProjekter) {
            //henter billable timer ud fra projekt listen 
            getmetimeHHBill = (Integer.parseUnsignedInt(project.getBillableTime().substring(0, project.getBillableTime().length()-6)))*3600;
            //System.out.println(getmetimeHHBill +" HH");
            getmetimeMMBill = (Integer.parseUnsignedInt(project.getBillableTime().substring(howLongIsTheString(project.getBillableTime()), project.getBillableTime().length()-3)))*60;
            //System.out.println(getmetimeMMBill+ " MM");
            
            //henter total tidud fra projekt listen
            getmetimeHHTotal = (Integer.parseUnsignedInt(project.getTotal_tid().substring(0, project.getTotal_tid().length()-6)))*3600;
            //System.out.println(getmetimeHHBill +" HH");
            getmetimeMMTotal = (Integer.parseUnsignedInt(project.getTotal_tid().substring(howLongIsTheString(project.getTotal_tid()), project.getTotal_tid().length()-3)))*60;
            //System.out.println(getmetimeMMBill+ " MM");
            
                      
            getmetotalHHMMBill += getmetimeHHBill+getmetimeMMBill;
            getmetotalHHMMTotal += getmetimeHHTotal+getmetimeMMTotal;
        }
        billaableHouersForPiechart = (getmetotalHHMMBill/3600);
        totalhouersForPiechart = (getmetotalHHMMTotal/3600);
        
     
       
    
    }
    
       
    
    public int howLongIsTheString(String timeStirng) {
        
        //fra SQL ved vi at denne string kan max være 9 lang
        if(timeStirng.length() == 9 )
            return 4;
            else 
            return 3;
            
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
        //System.out.println(fradatoSelected);

        if (tildato.getValue() != null) {
            tildatoSelected = tildato.getValue().format(DateTimeFormatter.ofPattern(europeanDatePattern));
        }
       // System.out.println(tildatoSelected);

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
           // System.out.println(MonthStart);
           // System.out.println(MonthEnd);
        }
        
        checkFilter();
         
        listeAfProjekter = pModel.getProjectsToFilter(comboUser, comboKlient, fradatoSelected, tildatoSelected, MonthStart, MonthEnd);
        handlePieChart();
        // tableview.getItems().clear();
    }

    @FXML
    private void handleSeekPressed(ActionEvent event) throws DALException, SQLException {
        getProjectsForfilter();
        //filterskuffe.toBack();
        filterskuffe.close();
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
        handlePieChart();

    }
    
    
        public void handlePieChart()
    {   populatepieChart();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Non Billable", totalhouersForPiechart - billaableHouersForPiechart),
                new PieChart.Data("Billable", billaableHouersForPiechart)
        );
     
        piechart.setData(pieChartData);
        piechart.setClockwise(true);
        piechart.setLabelLineLength(5);
        piechart.setLegendVisible(false);
        piechart.setLegendSide(Side.LEFT);
        piechart.setStartAngle(90);
        
        DecimalFormat df = new DecimalFormat("#.##");
        
        lblforPiechart.setText("non Billabale timer brugt " + (df.format(totalhouersForPiechart - billaableHouersForPiechart)) + "\n" 
                + "Billable timer brugt " + df.format(billaableHouersForPiechart) + "\n" 
                + "I alt Timer brugt " + df.format(totalhouersForPiechart));
        
      
        
    }
    
    
    /**
     * tjekker Filter vaglene, 
     * @throws timetracker.DAL.DALException
     * @throws java.sql.SQLException
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
            populatetable();
            handlePieChart();
        }

        
       if (fradato.getValue() != null && comboPerioder.getValue() !=null) {
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
            handlePieChart();
        }
       
           
       if (tildato.getValue() != null && comboPerioder.getValue() !=null) {
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
            handlePieChart();
       }
       
       
    }

}
