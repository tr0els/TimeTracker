/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import timetracker.BE.Project;
import timetracker.BE.TaskChild;
import timetracker.BE.TaskGroup;
import timetracker.BE.TaskParent;
import timetracker.GUI.Model.TaskModel;

/**
 * 
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class TaskUtil {
    private List<TaskGroup> tasks;
    private ObservableList<Project> projects;
    private TaskModel taskModel;
    
    public TaskUtil(List<TaskGroup> tasks, ObservableList<Project> projects, TaskModel taskModel) {
        this.tasks = tasks;
        this.projects = projects;
        this.taskModel = taskModel;
    }

    /**
     * Builds the view for the given list of grouped tasks
     * 
     * @param tasks
     * @param projects
     * @param taskModel
     * @return a pane with the tasks
     */    

}
