/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import timetracker.BE.Client;
import timetracker.BE.Project;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.ClientModel;

/**
 * FXML Controller class
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class KlientManagerAdminController implements Initializable
{

    private Client selectedClient;
    private static ClientModel Cmodel;

    @FXML
    private AnchorPane root;
    @FXML
    private JFXListView<Client> listviewClients;
    @FXML
    private JFXListView<Project> listviewProjects;
    @FXML
    private Label lblClientName;
    @FXML
    private Label lblDefaultRate;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private JFXButton btbNewClient;
    @FXML
    private AnchorPane paneCreateClient;
    @FXML
    private JFXTextField newClientName;
    @FXML
    private JFXTextField newDefaultRate;
    @FXML
    private JFXButton btbCreateClient;
    @FXML
    private JFXButton btbCancelNewClient;
    @FXML
    private AnchorPane paneEditClient;
    @FXML
    private JFXTextField txtEditName;
    @FXML
    private JFXTextField txtEditDefaultRate;
    @FXML
    private JFXButton btbEditClient;
    @FXML
    private JFXButton btbCloseEdit;
    @FXML
    private JFXButton btbEditChosenClient;
    @FXML
    private Label lblAlertnewClient;
    @FXML
    private Label lblAlertEdit;

    /**
     * Constructor for KlientManagerAdminController
     *
     * @throws DALException
     * @throws SQLException
     */
    public KlientManagerAdminController() throws DALException, SQLException
    {
        Cmodel = ClientModel.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        newDefaultRate.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue)
            {
                if (!newValue.matches("\\d*"))
                {
                    newDefaultRate.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        txtEditDefaultRate.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue)
            {
                if (!newValue.matches("\\d*"))
                {
                    txtEditDefaultRate.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        try
        {
            populateClientList();
            drawer.close();
            btbEditClient.setVisible(false);
            btbEditChosenClient.setVisible(false);
            listviewProjects.setFocusTraversable(false);
            listviewProjects.setTooltip(tooltipForProjectList());
        } catch (DALException | SQLException ex)
        {
        }
    }

    /**
     * Sætter alle klienterne ind i klient listviewet
     *
     * @throws DALException
     * @throws SQLException
     */
    private void populateClientList() throws DALException, SQLException
    {
        listviewClients.setItems(Cmodel.getClients());
    }

    /**
     * Håndterer den valgte klient fra klient listviwet, og tranportere dens
     * info til relevandte lables, og arktivere "Ret klient knappen" mm.
     *
     * @param event
     * @throws DALException
     * @throws SQLException
     */
    @FXML
    private void getSelectedClient(MouseEvent event) throws DALException, SQLException
    {
        selectedClient = listviewClients.getSelectionModel().getSelectedItem();

        lblClientName.setText(selectedClient.getClientName());
        lblDefaultRate.setText(selectedClient.getDefaultRate() + " DKK");

        // Gør btbEditClient og btbEditChosenClient synlige
        btbEditClient.setVisible(true);
        btbEditChosenClient.setVisible(true);

        // Tilføjer projeketer til listviewet for den valgte klient
        addprojektstolistview(selectedClient);
    }

    /**
     * finder ud af hvilket tooltip der skal vises
     *
     * @return
     */
    private Tooltip tooltipForProjectList()
    {
        Tooltip tip = new Tooltip();

        if (istheresomthinginprojectview() == 1)
        {
            tip.setText("Vælg en klient for at få vist deres projekter");
            return tip;
        } else if (istheresomthinginprojectview() == 2)
        {
            tip.setText("Klienten har ingen projekter");
            return tip;
        }
        return null;
    }

    /**
     * Hjælpe metode til at finde ud af hvad der er i projekt listviewet, og
     * retunere en int som bruges i tooltipforprojektview() for at sætte den
     * korrekte tooltip.
     *
     * @return
     */
    public int istheresomthinginprojectview()
    {

        if (selectedClient == null)
        {
            return 1;
        }
        if (Bindings.isEmpty(listviewProjects.getItems()).get() && selectedClient != null)
        {
            return 2;
        }

        return 0;
    }

    /**
     * Tilføjer den valgte klients projekter til listviewet
     *
     * @param client
     * @throws DALException
     * @throws SQLException
     */
    private void addprojektstolistview(Client client) throws DALException, SQLException
    {
        listviewProjects.setCellFactory(param -> new ListCell<Project>()
        {
            @Override
            protected void updateItem(Project item, boolean empty)
            {
                super.updateItem(item, empty);
                if (item == null && empty)
                {
                    setText(null);
                    setGraphic(null);

                } else
                {
                    setText(item.getProjectName() + " - " + item.getProjectRate() + " DKK");
                }
            }

        });

        listviewProjects.setItems(Cmodel.getClientProjcts(client));

    }

    /**
     * Tooltip eventhandler, tjekker om musen hover over projektlistview for at
     * sætte det rigtige tooltip
     *
     * @param event
     */
    @FXML
    private void tooltip(MouseEvent event)
    {

        listviewProjects.setTooltip(tooltipForProjectList());
    }

    /**
     * opret ny klient knap, som åbner draweren med txtinput felter til at
     * oprette en klient
     *
     * @param event
     */
    @FXML
    private void showPaneWithNewClient(ActionEvent event)
    {
        drawer.setSidePane(paneCreateClient);
        drawer.open();
        drawer.toFront();
    }

    /**
     * Knap til at håndtere oprettelsen af en ny klient, knappen ligger i
     * anchorpane, som ikke kan ses når draweren ikke er åben.
     *
     * @param event
     * @throws DALException
     */
    @FXML
    private void handleCreateClient(ActionEvent event) throws DALException
    {
        String newName = newClientName.getText().trim();
        String newRate = newDefaultRate.getText().trim();

        if (checkinputinTxtfields(newName, newRate) == true)
        {
            return;
        }

        Client newClient = new Client();
        int defaultRate = Integer.parseInt(newRate);
        newClient = Cmodel.createClient(newName, defaultRate);
        Cmodel.getClients().add(newClient);
        listviewClients.refresh();
        drawer.close();
    }

    /**
     * Sletter input i txtfelterne og lukker drawer.
     *
     * @param event
     */
    @FXML
    private void handleCancelNewClient(ActionEvent event)
    {
        lblAlertnewClient.setText("");
        newDefaultRate.clear();
        newClientName.clear();
        drawer.close();
    }

    /**
     * Håndtere EditClient, knappen ligger i anchorpane som kun kan ses når
     * draweren med EditClient er åben.
     *
     * @param event
     * @throws DALException
     */
    @FXML
    private void handleEditClient(ActionEvent event) throws DALException
    {
        String editName = txtEditName.getText().trim();
        String editrate = txtEditDefaultRate.getText().trim().replace(" DKK", " ").trim();

        if (checkinputinTxtfields(editName, editrate) == true)
        {
            return;
        }

        int editDefaultRate = Integer.parseInt(editrate);

        selectedClient.setClientName(editName);
        selectedClient.setDefaultRate(editDefaultRate);
        Cmodel.editClient(selectedClient);
        lblDefaultRate.setText(editDefaultRate + " DKK");
        lblClientName.setText(editName);
        listviewClients.refresh();
        drawer.close();

    }

    /**
     * Lukker for drawer til at edit klient.
     *
     * @param event
     */
    @FXML
    private void handleCancelEditClient(ActionEvent event)
    {
        drawer.close();
        lblAlertEdit.setText("");
        txtEditName.clear();
        txtEditDefaultRate.clear();

    }

    /**
     * Åbner draweren med ret klien txtinputfelter
     *
     * @param event
     */
    @FXML
    private void drawerOpenEditClient(ActionEvent event)
    {
        drawer.setSidePane(paneEditClient);
        drawer.open();
        txtEditName.setText(selectedClient.getClientName());
        txtEditDefaultRate.setText(selectedClient.getDefaultRate() + " DKK");
    }

    /**
     * Laver tjek på txtfelterne når man retter eller opretter en klient, for at
     * være sikker på felterne ikke er tomme.
     *
     * @param name
     * @param rate
     * @return
     */
    public boolean checkinputinTxtfields(String name, String rate)
    {
        //String stringtocheck = txtEditName.getText().trim();
        //String timeprischeck = txtEditDefaultRate.getText().trim();
        String alertText = "Navn og/eller pris må ikke være tom";
        String alertStyle = "-fx-text-fill: red;"
                + "-fx-font-size: 12;";

        if (name.length() < 1 || rate.length() < 1)
        {

            if (drawer.getSidePane().contains(paneEditClient) == true)
            {
                lblAlertEdit.setText(alertText);
                lblAlertEdit.setStyle(alertStyle);
                return true;
            } else
            {
                lblAlertnewClient.setText(alertText);
                lblAlertnewClient.setStyle(alertStyle);
                return true;
            }

        }
        return false;
    }
}
