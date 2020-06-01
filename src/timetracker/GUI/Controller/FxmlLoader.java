/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class FxmlLoader
{

    /**
     * Loader vinduet og sætter den rigtige scene.
     *
     * @param loc
     * @param pane
     */
    public static void loadWindow(URL loc, AnchorPane pane)
    {
       
        try
        {
            FXMLLoader loader = new FXMLLoader(loc);
            Parent parent = loader.load();
            pane.getChildren().clear();
            pane.getChildren().add(parent);
        } catch (IOException ex)
        {
            
        }
   
    }
}
