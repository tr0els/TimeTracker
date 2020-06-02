/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import timetracker.BE.Client;
import timetracker.BE.Project;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.ClientModel;
import timetracker.GUI.Model.ProjectModel;

/**
 * FXML Controller class
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class ProjektManagerAdminController implements Initializable
{

    private ProjectModel model;
    private ClientModel cModel;
    private static ProjektManagerAdminController projektController = null;
    ObservableList<Client> clients;
    TreeItem<Project> project;

    @FXML
    private AnchorPane root;
    @FXML
    private JFXComboBox<Client> combobox;
    @FXML
    private JFXTreeTableView<Project> treeView;
    @FXML
    private AnchorPane editProjectPane;
    @FXML
    private AnchorPane createProjectPane;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private JFXComboBox<Client> comboboxEdit;
    @FXML
    private JFXTextField hourlyPriceEdit;
    @FXML
    private JFXTextField projectNameEdit;
    @FXML
    private JFXTextField hourlyPrice;
    @FXML
    private JFXTextField projectName;

    /**
     * Constructor for ProjektManagerAdminController
     *
     * @throws DALException
     * @throws SQLException
     */
    public ProjektManagerAdminController() throws DALException, SQLException
    {
        model = ProjectModel.getInstance();
        cModel = ClientModel.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        hourlyPrice.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue)
            {
                if (!newValue.matches("\\d*"))
                {
                    hourlyPrice.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        hourlyPriceEdit.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue)
            {
                if (!newValue.matches("\\d*"))
                {
                    hourlyPriceEdit.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        try
        {
            drawer.close();
            clients = cModel.getClients();
            combobox.setItems(clients);
            comboboxEdit.setItems(clients);

        } catch (DALException ex)
        {
            Logger.getLogger(ProjektManagerAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
        populateTreeTable();
    }

    /**
     * sætter det rigtige panel til drawer og åbner den for brugeren.
     *
     * @param event
     */
    @FXML
    private void handleGetCreateAction(ActionEvent event)
    {
        drawer.close();
        drawer.setSidePane(createProjectPane);
        drawer.open();
        drawer.toFront();
    }

    /**
     * Kalder createprojekt når brugeren er klar til at oprette et projekt også
     * lukker den draweren.
     *
     * @param event
     * @throws DALException
     */
    @FXML
    void handleCreateAction(ActionEvent event) throws DALException
    {
        createProject();

    }

    /**
     * Kalder deleteprojekt når brugeren trykker på slet knappen og lukker
     * draweren
     *
     * @param event
     * @throws DALException
     */
    @FXML
    private void handleDeleteAction(ActionEvent event) throws DALException
    {
        deleteProject();
        drawer.close();
    }

    /**
     * Kalder editproject når brugeren trykker på edit knappen og lukker
     * draweren
     *
     * @param event
     * @throws DALException
     */
    @FXML
    private void handleEditAction(ActionEvent event) throws DALException
    {
        editProject();
        drawer.close();
    }

    /**
     * Sætter data på textfields når man klikker på et projekt.
     *
     * @param event
     * @throws DALException
     */
    @FXML
    private void handleEditSetup(MouseEvent event) throws DALException
    {
        drawer.setSidePane(editProjectPane);

        project = treeView.getSelectionModel().getSelectedItem();
        int clientID = project.getValue().getClientId();

        for (int i = 0; i < clients.size(); i++)
        {
            int cli = clients.get(i).getClientId();
            if (clientID == cli)
            {
                comboboxEdit.getSelectionModel().select(clients.get(i));
            }
        }
        projectNameEdit.setText(project.getValue().getProjectName());
        hourlyPriceEdit.setText(project.getValue().getProjectRate() + "");

        drawer.open();
        drawer.toFront();
    }

    /**
     * Tager det info som admin har puttet ind i "Projekt Manager" menuen og
     * sender det ned for at blive gemt på serveren
     *
     * @throws DALException
     */
    public void createProject() throws DALException
    {
        int clientID = combobox.getSelectionModel().selectedItemProperty().get().getClientId();
        String projectNameString = projectName.getText();
        int hourlyPay = Integer.parseInt(hourlyPrice.getText());

        if (projectNameString.equals(""))
        {
            projectName.setText("Navn kan ikke være tomt");
            projectName.setStyle("-fx-text-inner-color: red");
        } else
        {
            model.createProject(clientID, projectNameString, hourlyPay);
            populateTreeTable();
            drawer.close();
        }

    }

    /**
     * Tager det projekt som Admin har valgt i "projekt Manager" menuen og
     * sender det ned til DAL så det kan fjernes fra serveren. projekter kan
     * ikke slettes hvis projekter har tasks tilføjet til sig.
     *
     * @throws DALException
     */
    public void deleteProject() throws DALException
    {
        project = treeView.getSelectionModel().getSelectedItem();
        project.getParent().getChildren().remove(project);

        model.deleteProject(project.getValue().getProjectId());
    }

    /**
     * Henter projectID fra det projekt som admin har valgt. Tager så det
     * updateret data som er inputet og sender det ned til DAL så infoen på
     * databasen kan updateres
     *
     * @throws DALException
     */
    public void editProject() throws DALException
    {
        int clientID = comboboxEdit.getSelectionModel().getSelectedItem().getClientId();
        String projectName = projectNameEdit.getText();
        int hourlyPay = Integer.parseInt(hourlyPriceEdit.getText());
        int projectID = project.getValue().getProjectId();

        model.editProject(clientID, projectName, hourlyPay, projectID);

        treeView.getSelectionModel().getSelectedItem().getValue().setProjectName(projectName);

        populateTreeTable();
    }

    /**
     * oprette coloner i treetableview og sætter listen af projekter fra
     * databasen ind.
     */
    private void populateTreeTable()
    {
        //opretter kolonerne
        JFXTreeTableColumn<Project, String> projectName = new JFXTreeTableColumn<>("Projekt");
        projectName.setPrefWidth(250);
        JFXTreeTableColumn<Project, String> projectClient = new JFXTreeTableColumn<>("Klient");
        projectClient.setPrefWidth(150);
        JFXTreeTableColumn<Project, String> projectRate = new JFXTreeTableColumn<>("Timepris");
        projectRate.setPrefWidth(150);

        //vælger hvilket data der skal vises (i dette tilfælde projekt navnet) og hvilken colone det skal vises i
        projectName.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Project, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Project, String> param)
            {
                return new SimpleStringProperty(param.getValue().getValue().getProjectName());
            }
        });

        projectClient.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Project, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Project, String> param)
            {
                return new SimpleStringProperty(param.getValue().getValue().getClientName());
            }
        });

        projectRate.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Project, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Project, String> param)
            {
                return new SimpleStringProperty(Integer.toString(param.getValue().getValue().getProjectRate()));
            }
        });

        //opretter listen som skal indenholde alle de projekter som skal vises
        ObservableList<Project> projects = FXCollections.observableArrayList();

        //henter det data der skal ind i listen fra databasen
        try
        {
            projects.addAll(model.getProjects());

        } catch (DALException ex)
        {
            Logger.getLogger(ProjektManagerAdminController.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex)
        {
            Logger.getLogger(ProjektManagerAdminController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        //sætter dataen ind i selve treetableviewet
        final TreeItem<Project> root = new RecursiveTreeItem<Project>(projects, RecursiveTreeObject::getChildren);

        treeView.getColumns().setAll(projectName, projectClient, projectRate);
        treeView.setRoot(root);
        treeView.setShowRoot(false);
    }

    @FXML
    private void handleEditClearName(MouseEvent event)
    {
        projectNameEdit.selectAll();
        projectNameEdit.setStyle("-fx-text-inner-color: black");
    }

    @FXML
    private void handleCreateClearName(MouseEvent event)
    {
        projectName.selectAll();
        projectName.setStyle("-fx-text-inner-color: black");
    }

}
