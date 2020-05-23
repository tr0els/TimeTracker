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
import java.io.IOException;
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
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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
    private  JFXComboBox<User> ComboMedarbejder;
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
    @FXML
    private JFXButton btbPopupData;
    @FXML
    private Label lblforPiechart;
    
    private final ProjektModel pModel;
    private  BrugerModel bModel;
    private final ClientModel cModel;
    private double totalhouersForPiechart;
    private double billaableHouersForPiechart;
    private ObservableList<Project> listeAfProjekter;
 
    
    final Label caption = new Label("");
    private Tooltip tipholder = new Tooltip();
    private User UserLoggedInForMinTid= null;

 



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
            //hvis userloggedin er null skal vi initalisere tabelleren og piechart i denne metode
            //hvis der er en userloggetin så bliver det initaliseret i metoden getCurrentUserForMinTidView
            if (UserLoggedInForMinTid == null){
            populatetable();
            handlePieChart();
            populatecombobox();
            
        }
        
    }   catch (DALException ex) {
            Logger.getLogger(OverviewForAdminsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(OverviewForAdminsController.class.getName()).log(Level.SEVERE, null, ex);
        } }
    /**
     * håndtere Filterknappen som åbner skuffen med filter mulighederne
     * @param event 
     */
    @FXML
    private void handleFilteropen(ActionEvent event) {

        if (filterskuffe.isOpened()) {
            filterskuffe.close();
        } else {
            filterskuffe.open();
        }
    }
    /*
    metoden her gøre vores tableview klar til at kunne holde vores værider fra listeAfProjekter 
    til slut fylder vi tableViewet op med opjekter fra listeAfProjekter
    */
    public void populatetable() throws DALException, SQLException {
        if(UserLoggedInForMinTid == null )
        listeAfProjekter = pModel.getProjectsWithExtraData();
        else {
        listeAfProjekter = pModel.getProjectsToFilter(UserLoggedInForMinTid, null , null, null, null, null);
          
        
        }
        colprojekts.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProject_name()));
        coltotaltid.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTotal_tid()));
        colKlient.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClientName()));
        colBillable.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBillableTime()));
        tableview.setItems(listeAfProjekter);
        
    }
    /**
     * denne metode bruges til at beregne tiden der er brugt på et projekt, i billable og total værdier som vi allere har i vores listeAfPorjekter
     * vi renger dem om til sekunder, for at converetere dem til timer i double så 1.5 timer er 90 minutter som er 5400 sekunder. 
     */
    public void populatepieChart(){
        
        int getmetimeHHBill;
        int getmetimeMMBill;
        double getmetotalHHMMBill = 0.0;
        int getmetimeHHTotal;
        int getmetimeMMTotal;
        double getmetotalHHMMTotal = 0.0;
        
        for (Project project : listeAfProjekter) {
            //henter billable timer ud fra projekt listen
            //vi * med 3600, for det er skeunder der er i en time, og vi * med 60 for det er det antal sekunder der er i et minut
            getmetimeHHBill = (Integer.parseUnsignedInt(project.getBillableTime().substring(0, project.getBillableTime().length()-6)))*3600;
            getmetimeMMBill = (Integer.parseUnsignedInt(project.getBillableTime().substring(howLongIsTheString(project.getBillableTime()), project.getBillableTime().length()-3)))*60;
     
            
            //henter total tidud fra projekt listen
            //vi * med 3600, for det er skeunder der er i en time, og vi * med 60 for det er det antal sekunder der er i et minut
            getmetimeHHTotal = (Integer.parseUnsignedInt(project.getTotal_tid().substring(0, project.getTotal_tid().length()-6)))*3600;
            getmetimeMMTotal = (Integer.parseUnsignedInt(project.getTotal_tid().substring(howLongIsTheString(project.getTotal_tid()), project.getTotal_tid().length()-3)))*60;
       
            
            //her tager vi bill HH og MM som er konverteret til sekunder og lægger over i en variabel  for at give os et total for projektet.        
            getmetotalHHMMBill += getmetimeHHBill+getmetimeMMBill;
            getmetotalHHMMTotal += getmetimeHHTotal+getmetimeMMTotal;
        }
        //når vi er færdig med loopet, dividere vi med 3600, for at få konvereteret vore sekunder til timer i decimaltal
        billaableHouersForPiechart = (getmetotalHHMMBill/3600);
        totalhouersForPiechart = (getmetotalHHMMTotal/3600);
           
    }
    
       
    /**
     * denne metode bruges i populatePiechart(), og er en hjælpe medtode til at finde ud af hvor lang en String er om den er 8 eller 9 værdier lang 
     * for at finde ud af hvor minutterne står i Stringen. 
     * @param timeStirng
     * @return 
     */
    public int howLongIsTheString(String timeStirng) {
        
        //fra SQL ved vi at denne string kan max være 9 lang
        if(timeStirng.length() == 9 )
            //hvis den er 9 lang ved vi minutterne starter på position 4 000:00:00 HHH/MM/SS
            return 4;
            else 
            //hvis ikke den er 9 lang ved vi at minutterne starter på position 3 00:00:00 HH/MM/SS
            return 3;
            
    }
    
/**
 * gøre Comboboxene klar med lister
 * @throws DALException
 * @throws SQLException 
 */
    private void populatecombobox() throws DALException, SQLException {

        ComboMedarbejder.setItems(bModel.getUsers());
        comboKlienter.setItems(cModel.getClients());
        comboPerioder.setItems(bModel.getListOfPeriods());
      
       

    }
    /**
     * håndtere de valgte søge kritier, og sender værdierne ind i ProjektModellen og opdatere ListeafPRojekter med filteret
     * @throws DALException
     * @throws SQLException 
     */
    public void getProjectsForfilter() throws DALException, SQLException {

        String europeanDatePattern = "dd-MM-yyyy";
      
        User comboUser = null;
        Client comboKlient = null;
        String fradatoSelected = null;
        String tildatoSelected = null;
        String MonthStart = null;
        String MonthEnd = null;
        
        if(UserLoggedInForMinTid != null)
              comboUser = UserLoggedInForMinTid;
        
        if (fradato.getValue() != null) {
            fradatoSelected = fradato.getValue().format(DateTimeFormatter.ofPattern(europeanDatePattern));
        }
       

        if (tildato.getValue() != null) {
            tildatoSelected = tildato.getValue().format(DateTimeFormatter.ofPattern(europeanDatePattern));
        }
       

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
          
        }
        
        checkFilter();
         
        listeAfProjekter = pModel.getProjectsToFilter(comboUser, comboKlient, fradatoSelected, tildatoSelected, MonthStart, MonthEnd);
        handlePieChart();
       
    }
/**
 * Håndtere søgningen, og lukker draweren 
 * @param event
 * @throws DALException
 * @throws SQLException 
 */
    @FXML
    private void handleSeekPressed(ActionEvent event) throws DALException, SQLException {
        getProjectsForfilter();
        filterskuffe.close();
    }
    /**
     * Nul stiller filter, og tableview samt piechart
     * @param event
     * @throws DALException
     * @throws SQLException 
     */
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
    
    /**
     * Håndere PieChart og fremviser det med data
     */
        public void handlePieChart()
    {   populatepieChart();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Non Billable", totalhouersForPiechart - billaableHouersForPiechart),
                new PieChart.Data("Billable", billaableHouersForPiechart)
        );
       DecimalFormat df = new DecimalFormat("#.##");
        piechart.setData(pieChartData);
        pieChartData.forEach(data -> data.nameProperty().bind(Bindings.concat(data.getName(), " ", df.format(data.getPieValue()), " Timer")));
        piechart.setStartAngle(90);
        piechart.setLegendSide(Side.RIGHT);
        piechart.setLabelsVisible(false);
        
    
        
        
        
        //lblforPiechart.setText("non Billabale timer brugt " + (df.format(totalhouersForPiechart - billaableHouersForPiechart)) + "\n" 
          //      + "Billable timer brugt " + df.format(billaableHouersForPiechart) + "\n" 
            //    + "I alt Timer brugt " + df.format(totalhouersForPiechart));
        
      
        
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
    /**
     * metoden bruges i menubarcontroller, når vi klikker ind på min tid, på den måde får vi sat den nuværende bruger som er logget ind. 
     * @param currentUser
     * @throws DALException
     * @throws SQLException 
     */
  public void getCurrentUserForMinTidView(User currentUser)  throws DALException, SQLException{
            UserLoggedInForMinTid = currentUser;
            initalizePopulatethings();
            

  }
  /**
   * bruges i menubarcontroller til at disable medarbejdercomboboxen i min tid. 
   * @return 
   */
    public JFXComboBox<User> getComboMedarbejder() {
        return ComboMedarbejder;
    }
    /**
     * initalisere min tid viewet, hvis der er en user som er logget ind bruges i getcurrentuserformintidview 
     * @throws DALException
     * @throws SQLException 
     */
  public void initalizePopulatethings()  throws DALException, SQLException{
      if (UserLoggedInForMinTid != null){
            populatetable();
            handlePieChart();
            populatecombobox();
      } 
  
  } 
  /**
   * Håndtere kanppen til at åbne et overbilk over taks i et projekt. 
   * @param event
   * @throws IOException
   * @throws DALException 
   */
    @FXML
    private void handelPopupDataView(ActionEvent event) throws IOException, DALException {
        
        User transferUser = null;     
        Project selectedProject= null;
        
        //vi tjekker om vores tableview er tomt
        first:
       if( !listeAfProjekter.isEmpty())
       {    //hvis den ikke er tom tjekker vi om der er valgt et projekt i tabellen, hvis der er det ligger vi projekt objektet 
           //over i vores selectedProjekt
           second: 
          if(tableview.getSelectionModel().getSelectedItem() != null )
               selectedProject = tableview.getSelectionModel().getSelectedItem();
           //hvis der ikke er valgt et projekt i tabellen, åbner vi en alert, og stopper 'if kørslen', så vi ikke  åbner et tomt dataview
           third:
          if(tableview.getSelectionModel().getSelectedItem() == null ){
               Alert alert = new Alert(AlertType.ERROR);
               alert.setHeaderText("Fejl i Åbning af Data Overblik");
               alert.setTitle("Fejl");
               alert.setContentText("Du skal vælge et projekt for at få \n"
                       + "vist Data");
               alert.showAndWait();
               break first;
           }
            
           //hvis der er valgt et projekt, tjekker vi om det er brugeren som er logget ind eller om det et en bruger fra comboboxen vi skal bruge i dataoverblikker 
           //hvis ikke det er nogen af dem er det en admin som er logget ind og har ikke brugt comboboxen til at filtere på.
           //hvis det er tilfældet sætter vi tranferuser til null, på den mådet kommer alle bruger med ud
           if(UserLoggedInForMinTid != null)
           {transferUser = UserLoggedInForMinTid;}
           else if(ComboMedarbejder.getValue() != null  )
           {transferUser = ComboMedarbejder.getValue();}
           else transferUser = null;
           
           FXMLLoader loader = new FXMLLoader();
           loader.setLocation(getClass().getResource("/timetracker/GUI/View/popUpDataView.fxml"));
           loader.load();
           Parent root = loader.getRoot();
           
           PopUpDataViewController controller = loader.getController();
           controller.TransferProjektID(selectedProject, transferUser );
           
           Scene scene = new Scene(root);
           Stage stage = new Stage();
           stage.setTitle("Overblik over tiden brugt på et Projekt");
           stage.getIcons().add(new Image("/timetracker/GUI/Icons/grumsen.png"));
           stage.setScene(scene);
           stage.setResizable(false);
           stage.show();
           
       }
       
       else {
           Alert alert = new Alert(AlertType.ERROR);
           alert.setHeaderText("Fejl i Åbning af  Data Overblik");
           alert.setTitle("Fejl");
           alert.setContentText("der er ikke nogen projekter \n"
                   + "at få vist Data fra");
           alert.showAndWait();}
    }
          
          
}
