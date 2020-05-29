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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
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
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
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
    private BrugerModel bModel;
    private final ClientModel cModel;
    private double totalhouersForPiechart;
    private double billaableHouersForPiechart;
    private ObservableList<Project> listeAfProjekter;
    private final String europeanDatePattern = "dd-MM-yyyy";
    private final String europeanDatePatternYearMonth = "MM-yyyy";

    private User UserLoggedInForMinTid = null;
    private Stage popupStage;

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
            btbPopupData.setVisible(false);

            //hvis userloggedin er null skal vi initalisere tabelleren og piechart i denne metode
            //hvis der er en userloggetin så bliver det initaliseret i metoden getCurrentUserForMinTidView
            if (UserLoggedInForMinTid == null) {
                populatetable();
                handlePieChart();
                populatecombobox();

            }

        } catch (DALException ex) {
            Logger.getLogger(OverviewForAdminsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(OverviewForAdminsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * håndtere Filterknappen som åbner skuffen med filter mulighederne
     *
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
        if (UserLoggedInForMinTid == null) {
            listeAfProjekter = pModel.getProjectsToFilter(null, null, null, null, null, null);
        } else {
            listeAfProjekter = pModel.getProjectsToFilter(UserLoggedInForMinTid, null, null, null, null, null);

        }
        colprojekts.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProjectName()));
        coltotaltid.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTotalTime()));
        colKlient.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClientName()));
        colBillable.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBillableTime()));
        tableview.setItems(listeAfProjekter);

    }

    /**
     * denne metode bruges til at beregne tiden der er brugt på et projekt, i
     * billable og total værdier som vi allere har i vores listeAfPorjekter vi
     * renger dem om til sekunder, for at converetere dem til timer i double så
     * 1.5 timer er 90 minutter som er 5400 sekunder.
     */
    public void populatepieChart() {

        int getmetimeHHBill;
        int getmetimeMMBill;
        double getmetotalHHMMBill = 0.0;
        int getmetimeHHTotal;
        int getmetimeMMTotal;
        double getmetotalHHMMTotal = 0.0;

        for (Project project : listeAfProjekter) {
            String timeBil = project.getBillableTime();
            String[] timesplitupBil = timeBil.split(":");

            String hhBil = timesplitupBil[0];
            String mmBil = timesplitupBil[1];
            String secBil = timesplitupBil[2];

            String time = project.getTotalTime();
            String[] timesplitup = time.split(":");

            String hhTotal = timesplitup[0];
            String mmTotal = timesplitup[1];
            String secTotal = timesplitup[2];

            //henter billable timer ud fra projekt listen
            getmetimeHHBill = (Integer.parseInt(hhBil) * 3600);
            getmetimeMMBill = (Integer.parseInt(mmBil) * 60);

            //henter total tidud fra projekt listen
            //vi * med 3600, for det er skeunder der er i en time, og vi * med 60 for det er det antal sekunder der er i et minut
            getmetimeHHTotal = (Integer.parseInt(hhTotal) * 3600);
            getmetimeMMTotal = (Integer.parseInt(mmTotal) * 60);

            //her tager vi bill HH og MM som er konverteret til sekunder og lægger over i en variabel  for at give os et total for projektet.        
            getmetotalHHMMBill += getmetimeHHBill + getmetimeMMBill + (Integer.parseInt(secBil));
            getmetotalHHMMTotal += getmetimeHHTotal + getmetimeMMTotal + (Integer.parseInt(secTotal));
        }
        //når vi er færdig med loopet, dividere vi med 3600, for at få konvereteret vore sekunder til timer i decimaltal
        billaableHouersForPiechart = (getmetotalHHMMBill / 3600);
        totalhouersForPiechart = (getmetotalHHMMTotal / 3600);

    }

    /**
     * gøre Comboboxene klar med lister
     *
     * @throws DALException
     * @throws SQLException
     */
    private void populatecombobox() throws DALException, SQLException {

        ComboMedarbejder.setItems(bModel.getUsers());
        comboKlienter.setItems(cModel.getClients());
        comboPerioder.setItems(bModel.getListOfPeriods());

    }

    /**
     * Håndtere de valgte søge kritier, og sender værdierne ind i
     * ProjektModellen og opdatere ListeafProjekter med filteret
     *
     * @throws DALException
     * @throws SQLException
     */
    public void getProjectsForfilter() throws DALException, SQLException {

        User comboUser = null;
        Client comboKlient = null;
        String fradatoSelected = null;
        String tildatoSelected = null;
        String MonthStart = null;
        String MonthEnd = null;
        first:
        {
            if (UserLoggedInForMinTid != null) {
                comboUser = UserLoggedInForMinTid;
            }

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
            if (comboPerioder.getValue() != null) {
                YearMonth getmonth = comboPerioder.getValue();
                MonthEnd = getmonth.atEndOfMonth().format(DateTimeFormatter.ofPattern(europeanDatePattern));
                int lengthOfMonth = getmonth.lengthOfMonth();
                MonthStart = getmonth.atEndOfMonth().minusDays(lengthOfMonth - 1).format(DateTimeFormatter.ofPattern(europeanDatePattern));

            }

            if (checkFilter() == true) {
                break first;
            }

            listeAfProjekter = pModel.getProjectsToFilter(comboUser, comboKlient, fradatoSelected, tildatoSelected, MonthStart, MonthEnd);
            handlePieChart();
        }
    }

    /**
     * Håndtere søgningen, og lukker draweren
     *
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
     *
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
    public void handlePieChart() {
        String comboMedarbejder = "";
        String periode ="";
        String comboKlient="";
       
        
        populatepieChart();
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
        
        if (ComboMedarbejder.getSelectionModel().getSelectedItem() != null)
                comboMedarbejder = "'"+ComboMedarbejder.getSelectionModel().getSelectedItem().toString()+"'";
        if(comboKlienter.getSelectionModel().getSelectedItem() != null)
                comboKlient =  "'"+comboKlienter.getSelectionModel().getSelectedItem().getClientName()+"'";
        if(comboPerioder.getSelectionModel().getSelectedItem() != null){
                periode = "'"+comboPerioder.getValue().format(DateTimeFormatter.ofPattern(europeanDatePatternYearMonth))+"'";}
        if(comboPerioder.getSelectionModel().getSelectedItem() != null ||  comboKlienter.getSelectionModel().getSelectedItem() != null || ComboMedarbejder.getSelectionModel().getSelectedItem() != null)
        piechart.setTitle("Overblik over: " + periode + "  " +  comboKlient + " " + comboMedarbejder );
        else 
        piechart.setTitle("Overblik over alle projekter\n som har tilknyttet opgaver");
    }

    /**
     * tjekker Filter vaglene,
     *
     * @throws timetracker.DAL.DALException
     * @throws java.sql.SQLException
     */
    public boolean checkFilter() throws DALException, SQLException {

        if (fradato.getValue() != null && tildato.getValue() != null && tildato.getValue().isBefore(fradato.getValue()) && fradato.getValue().isAfter(tildato.getValue())) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("Fejl i datoer");
            alert.setTitle("Fejl i valg af til- og fradato");
            alert.setContentText("fradato må ikke ligge efter tildato \n og tildato må ikke ligge før fradatoen");
            tildato.setValue(null);
            fradato.setValue(null);
            tildato.getEditor().clear();
            fradato.getEditor().clear();
            populatetable();
            handlePieChart();
            alert.showAndWait();
            return true;
        }

        if (fradato.getValue() != null && comboPerioder.getValue() != null) {
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
            return true;
        }

        if (tildato.getValue() != null && comboPerioder.getValue() != null) {
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
            return true;
        }

        return false;
    }

    /**
     * metoden bruges i menubarcontroller, når vi klikker ind på min tid, på den
     * måde får vi sat den nuværende bruger som er logget ind.
     *
     * @param currentUser
     * @throws DALException
     * @throws SQLException
     */
    public void getCurrentUserForMinTidView(User currentUser) throws DALException, SQLException {
        UserLoggedInForMinTid = currentUser;
        initalizePopulatethings();

    }

    /**
     * bruges i menubarcontroller til at disable medarbejdercomboboxen i min
     * tid.
     *
     * @return
     */
    public JFXComboBox<User> getComboMedarbejder() {
        return ComboMedarbejder;
    }

    /**
     * initalisere min tid viewet, hvis der er en user som er logget ind bruges
     * i getcurrentuserformintidview
     *
     * @throws DALException
     * @throws SQLException
     */
    public void initalizePopulatethings() throws DALException, SQLException {
        if (UserLoggedInForMinTid != null) {
            populatetable();
            handlePieChart();
            populatecombobox();
        }

    }

    /**
     * Håndtere knappen til at åbne et overbilk over taks i et projekt.
     *
     * @throws IOException
     * @throws DALException
     */
    private void handelPopupDataView() throws IOException, DALException, SQLException {

        User transferUser = null;
        Project selectedProject = null;
        String fradatoSelected = null;
        String tildatoSelected = null;
        String MonthStart = null;
        String MonthEnd = null;

        //vi tjekker at vores tableview ikke er tomt
        first:
        if (!listeAfProjekter.isEmpty()) {   
            
            second:
            if (tableview.getSelectionModel().getSelectedItem() != null) {
                selectedProject = tableview.getSelectionModel().getSelectedItem();
            }

            //hvis der er valgt et projekt, tjekker vi om det er brugeren som er logget ind eller om det et en bruger fra comboboxen vi skal bruge i dataoverblikker 
            //hvis ikke det er nogen af dem er det en admin som er logget ind og har ikke brugt comboboxen til at filtere på.
            //hvis det er tilfældet sætter vi tranferuser til null, på den mådet kommer alle bruger med ud
            if (UserLoggedInForMinTid != null) {
                transferUser = UserLoggedInForMinTid;
            } else if (ComboMedarbejder.getValue() != null) {
                transferUser = ComboMedarbejder.getValue();
            } else {
                transferUser = null;
            }

            if (fradato.getValue() != null) {
                fradatoSelected = fradato.getValue().format(DateTimeFormatter.ofPattern(europeanDatePattern));
            }

            if (tildato.getValue() != null) {
                tildatoSelected = tildato.getValue().format(DateTimeFormatter.ofPattern(europeanDatePattern));
            }

            if (comboPerioder.getValue() != null) {
                YearMonth getmonth = comboPerioder.getValue();
                MonthEnd = getmonth.atEndOfMonth().format(DateTimeFormatter.ofPattern(europeanDatePattern));
                int lengthOfMonth = getmonth.lengthOfMonth();
                MonthStart = getmonth.atEndOfMonth().minusDays(lengthOfMonth - 1).format(DateTimeFormatter.ofPattern(europeanDatePattern));

            }
            if (checkFilter() == true) {
                break first;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/timetracker/GUI/View/popUpDataView.fxml"));
            Parent popup = loader.load();
            PopUpDataViewController controller = loader.<PopUpDataViewController>getController();
            controller.TransferInfoForPopup(selectedProject, transferUser, fradatoSelected, tildatoSelected, MonthStart, MonthEnd);

            if (popupStage == null) {
                Scene scene = new Scene(popup);

                popupStage = new Stage();
                popupStage.getIcons().add(new Image("/timetracker/GUI/Icons/grumsen.png"));
                popupStage.setScene(scene);
                popupStage.setResizable(false);
                if(transferUser == null)
                popupStage.setTitle(selectedProject.getProjectName().toUpperCase() + " - " + selectedProject.getProjectRate() + " DKK");
                else {popupStage.setTitle(selectedProject.getProjectName().toUpperCase());}
                popupStage.show();
            }
            popupStage.show();
            popupStage.toFront();
            if(transferUser == null)
            popupStage.setTitle(selectedProject.getProjectName().toUpperCase() + " - " + selectedProject.getProjectRate() + " DKK");
            else {popupStage.setTitle(selectedProject.getProjectName().toUpperCase());}
        }

    }
    /**
     * håndere hvis man dobbeltklikker på et projekt  
     * @param event
     * @throws IOException
     * @throws DALException
     * @throws SQLException 
     */
    @FXML
    private void handelSelectedProject(MouseEvent event) throws IOException, DALException, SQLException {
        if (event.getClickCount() > 1) {
            handelPopupDataView();
        }
    }
    /**
     * eventhandler til når musen er inde for tableviewet, for at vise tooltip
     * @param event 
     */
    @FXML
    private void handelTooltipForTableView(MouseEvent event) {

        tableview.setTooltip(getToolTipForTableView());

    }
    /**
     * bruges til at sætte tooltip for tableviewet 
     * @return 
     */
    public Tooltip getToolTipForTableView() {

        Tooltip tip = new Tooltip();

        if (listeAfProjekter.isEmpty()) {
            tip.setText("Det er ikke nogle Projekter at vise");
            return tip;
        } else {
            tip.setText("Dobbel klik på et Projekt, for at få vist dens tilhørende opgaver");
            return tip;
        }

    }

}
