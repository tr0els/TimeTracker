/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Model;

/**
 *
 * @author Troels Klein
 */
public class TaskModel {
    
    private static TaskModel model = null;
    
    private TaskModel(){}
    
    public static TaskModel getInstance(){
        if (model == null){
            model = new TaskModel();
        }
        return model;
    }
}
