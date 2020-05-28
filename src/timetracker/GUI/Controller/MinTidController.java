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
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import timetracker.BE.Project;
import timetracker.BE.User;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.BrugerModel;
import timetracker.GUI.Model.ClientModel;
import timetracker.GUI.Model.ProjektModel;

/**
 * FXML Controller class
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class MinTidController implements Initializable {

    @FXML
    private JFXDrawer filterskuffe;
    @FXML
    private TableView<?> tableview;
    @FXML
    private TableColumn<?, ?> colprojekts;
    @FXML
    private TableColumn<?, ?> coltotaltid;
    @FXML
    private TableColumn<?, ?> colBillable;
    @FXML
    private TableColumn<?, ?> colKlient;
    @FXML
    private BarChart<?, ?> barchart;
    @FXML
    private JFXButton Filterkanp;
    @FXML
    private PieChart piechart;
    @FXML
    private AnchorPane searchAnchorpane;
    @FXML
    private JFXComboBox<?> comboPerioder;
    @FXML
    private JFXDatePicker fradato;
    @FXML
    private JFXDatePicker tildato;
    @FXML
    private JFXButton seekbtb;
    @FXML
    private JFXComboBox<?> comboKlienter;
    @FXML
    private JFXButton clearFilterbtb;
    @FXML
    private Label lblforPiechart;
    
    
    private User loggedInUser;
    private final ProjektModel pModel;
    private final BrugerModel bModel;
    private final ClientModel cModel;
    private ObservableList<Project> listeAfProjekter;
    
    
    public MinTidController() throws DALException, SQLException{
      pModel = ProjektModel.getInstance();
      bModel = BrugerModel.getInstance();
      cModel = ClientModel.getInstance();
    
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        filterskuffe.setSidePane(searchAnchorpane);
            filterskuffe.toFront();
            filterskuffe.close();
    
    }

    @FXML
    private void handleFilteropen(ActionEvent event) {
        if (filterskuffe.isOpened()) {
            filterskuffe.close();
        } else {
            filterskuffe.open();
        }
    }

    @FXML
    private void handleSeekPressed(ActionEvent event) {
    }

    @FXML
    private void handleClearFilter(ActionEvent event) {
    }

}
