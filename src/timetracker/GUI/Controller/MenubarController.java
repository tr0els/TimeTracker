/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import timetracker.GUI.Controller.FxmlLoader;

/**
 * FXML Controller class
 *
 * @author Charlotte
 */
public class MenubarController implements Initializable {

  
    @FXML
    private JFXButton opgaverbtb;
    @FXML
    private JFXButton minTidbtb;
    @FXML
    private JFXButton projekterbtb;
    @FXML
    private JFXButton logudbtb;
    @FXML
    private AnchorPane menupane;
    @FXML
    private AnchorPane viewpane;
    @FXML
    private JFXDrawer drawer;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void loadOpgaverView(ActionEvent event) {
    FxmlLoader.loadWindow(getClass().getResource("/timetracker/GUI/View/TaskView.fxml"),  viewpane);    
    
    }

    @FXML
    private void loadMinTidView(ActionEvent event) {
        FxmlLoader.loadWindow(getClass().getResource("/timetracker/GUI/View/MinTid.fxml"), viewpane);    
    }

    @FXML
    private void loadProjekterView(ActionEvent event) {
         FxmlLoader.loadWindow(getClass().getResource("/timetracker/GUI/View/ProjektView.fxml"), viewpane);   
    }

    @FXML
    private void handelLogout(ActionEvent event) {
        //  todo
    }

 

    
    

  
}
