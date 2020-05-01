/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
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
    @FXML
    private JFXButton overblikbtb;
    @FXML
    private JFXButton projektbtb;
    @FXML
    private JFXButton brugermanagerbtb;
    @FXML
    private JFXButton klientmanagerbtb;
    

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
    private void loadoverblik(ActionEvent event) {
         //FxmlLoader.loadWindow(getClass().getResource("/timetracker/GUI/View/.fxml"), viewpane);   
    }

    @FXML
    private void loadprojektview(ActionEvent event) {
        
         FxmlLoader.loadWindow(getClass().getResource("/timetracker/GUI/View/ProjektManagerAdmin.fxml"), viewpane);   
    }

    @FXML
    private void loadbrugermanager(ActionEvent event) {
             FxmlLoader.loadWindow(getClass().getResource("/timetracker/GUI/View/UserManagerAdmin.fxml"), viewpane);   
    }

    @FXML
    private void loadklientmanager(ActionEvent event) {
        FxmlLoader.loadWindow(getClass().getResource("/timetracker/GUI/View/KlientManagerAdmin.fxml"), viewpane);   
    }
    

    @FXML
    private void handelLogout(ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/timetracker/GUI/View/MainView.fxml"));
            loader.load();
            Parent root = loader.getRoot();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Time Tracker login");
            stage.getIcons().add(new Image("/timetracker/GUI/Icons/grumsen.png"));
            Stage Currentstage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Currentstage.close();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
    }

    /**
     * Get på projektbutton, så vi kan bruge "setVisible()" i main controlleren
     * @return 
     */
    public JFXButton getProjektbtb() {
        return projektbtb;
    }
     /**
     * Get på projektbutton, så vi kan bruge "setVisible()" i main controlleren
     * @return 
     */
    public JFXButton getKlientmanagerbtb() {
        return klientmanagerbtb;
    }
    /**
     * Get på projektbutton, så vi kan bruge "setVisible()" i main controlleren
     * @return 
     */
    public JFXButton getBrugermanagerbtb() {
        return brugermanagerbtb;
    }

  
}
