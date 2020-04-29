/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import timetracker.App;

/**
 *
 * @author Charlotte
 */
public class FxmlLoader {

    
    
       public static Object loadWindow(URL loc, AnchorPane pane) {
        Object controller = null;
        try {
            FXMLLoader loader = new FXMLLoader(loc);
            Parent parent = loader.load();
            controller = loader.getController();
            pane.getChildren().clear();
            pane.getChildren().add(parent);
            
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return controller;
    }
    
     
}

//
//  public Pane getPage(String filename){
//    
//        try{
//          URL fileUrl = App.class.getResource("/timetracker/GUI/View/" + filename + ".fxml");
//          if(fileUrl == null)
//          { throw new java.io.FileNotFoundException("FXML filen kan ikke findes");
//          }
//            view = new FXMLLoader().load(fileUrl);
//            
//        } catch (IOException ex) {
//            System.out.println("no page found");
//     }
//    
//    return view;
//}