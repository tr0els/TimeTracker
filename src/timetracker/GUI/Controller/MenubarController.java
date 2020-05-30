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
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.UserModel;

/**
 * FXML Controller class
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
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
    @FXML
    private Label loggedInUser;
    
    private static UserModel model;
        public MenubarController() throws DALException, SQLException {
        model = UserModel.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loggedInUser.setText(model.getUser().getName() + " " + model.getUser().getSurname());
        FxmlLoader.loadWindow(getClass().getResource("/timetracker/GUI/View/TaskView.fxml"), viewpane);
    }

    @FXML
    private void loadOpgaverView(ActionEvent event) {
        FxmlLoader.loadWindow(getClass().getResource("/timetracker/GUI/View/TaskView.fxml"), viewpane);

    }

    @FXML
    private void loadMinTidView(ActionEvent event) throws IOException, DALException, SQLException {
       // FxmlLoader.loadWindow(getClass().getResource("/timetracker/GUI/View/MinTid.fxml"), viewpane);
        
        FXMLLoader loader = new FXMLLoader();
        
        loader.setLocation(getClass().getResource("/timetracker/GUI/View/OverviewForAdmins.fxml"));
        Parent parent = loader.load();
      
        
        OverviewForAdminsController controller = loader.getController();
        
        controller.getComboMedarbejder().setVisible(false);
        controller.getCurrentUserForMinTidView(model.getUser());
        
  
        viewpane.getChildren().clear();
        viewpane.getChildren().add(parent);
            
    
    }

    @FXML
    private void loadProjekterView(ActionEvent event) {
        FxmlLoader.loadWindow(getClass().getResource("/timetracker/GUI/View/ProjektView.fxml"), viewpane);
    }

    @FXML
    private void loadoverblik(ActionEvent event) {
        FxmlLoader.loadWindow(getClass().getResource("/timetracker/GUI/View/OverviewForAdmins.fxml"), viewpane);
                       
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
     *
     * @return
     */
    public JFXButton getProjektbtb() {
        return projektbtb;
    }

    /**
     * Get på projektbutton, så vi kan bruge "setVisible()" i main controlleren
     *
     * @return
     */
    public JFXButton getKlientmanagerbtb() {
        return klientmanagerbtb;
    }

    /**
     * Get på projektbutton, så vi kan bruge "setVisible()" i main controlleren
     *
     * @return
     */
    public JFXButton getBrugermanagerbtb() {
        return brugermanagerbtb;
    }
    /**
     * get overbliks button for overbliks view, så vi kan bruge setVisible() i main controlleren 
     * @return 
     */
    public JFXButton getOverblikforAdmin() {
        return overblikbtb;
    }

}
