/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
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

public class TaskUtil {
    
    /**
     * Builds the view for the given list of grouped tasks
     * @return a pane with the tasks
     * 
     * note: another method could be made to handle tasks with no group
     */
    
    public static Pane getView(List<TaskGroup> tasks, ObservableList<Project> projects) {
        
        // pane to wrap the final result in
        Pane taskPane = new Pane();
        
        // vbox to keep nodes below each other
        VBox wrapper = new VBox();
        wrapper.getStyleClass().add("wrapper");
        taskPane.getChildren().add(wrapper);

        for (TaskGroup taskGroup : tasks) {
            
            // build nodes for each task group
            HBox groupNode = buildTaskGroup(taskGroup);
            wrapper.getChildren().add(groupNode);
            
            // build nodes for each parent (including any children)
            for (TaskParent taskParent : taskGroup.getParents()) {
                TitledPane taskNode = buildTask(taskParent, projects);
                wrapper.getChildren().add(taskNode);
            }
            
            // add spacing between groups here
            Pane spacer = new Pane();
            spacer.getStyleClass().add("spacer");
            wrapper.getChildren().add(spacer);
        }
        
        return taskPane;
    }
    
    private static HBox buildTaskGroup(TaskGroup taskGroup) {
        
        // hbox
        HBox groupNode = new HBox();
        groupNode.getStyleClass().add("group");
        
        // name
        Label name = new Label(taskGroup.getName());
        name.getStyleClass().add("name");
        groupNode.getChildren().add(name);
        
        // empty filler
        Pane filler = new Pane();
        groupNode.setHgrow(filler, Priority.ALWAYS); // virker ikke!
        name.getStyleClass().add("name");
        groupNode.getChildren().add(filler);
        
        // total
        Label total = new Label("Total:  ");
        total.getStyleClass().add("total");
        groupNode.getChildren().add(total);
        
        // time
        Label time = new Label(taskGroup.getTime());
        time.getStyleClass().add("time");
        groupNode.getChildren().add(time);

        return groupNode;
    }
    
    private static TitledPane buildTask(TaskParent taskParent, ObservableList<Project> projects) {
        
        // titledpane
        TitledPane parentNode = new TitledPane();
        parentNode.setExpanded(false);
        
        // hbox wrapper
        HBox hbox = new HBox();
        
        // name + num children wrapper
        HBox hboxName = new HBox();
        hboxName.getStyleClass().add("name");

        // show number of children
        if(taskParent.getChildren().size() > 1) {
            Label numChildren = new Label(taskParent.getChildren().size() + "");
            numChildren.getStyleClass().add("numChildren");
            hboxName.getChildren().add(numChildren);
        }
        
        // name
        JFXTextField name = new JFXTextField(taskParent.getName());
        name.getStyleClass().add("name");
        hboxName.getChildren().add(name);
        hbox.getChildren().add(hboxName);

        // edit name on enter pressed
        name.setOnAction(e -> {
            taskParent.setName(name.getText());
            //updateTaskName(taskParent);
            System.out.println("name test 2");
        });
        
        // edit name on unfocus
        name.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal == false) {
                taskParent.setName(name.getText());
                //updateTaskName(taskParent);
                System.out.println("name test");
            }
        });
 
        // project
        JFXComboBox<Project> project = new JFXComboBox();
        project.setItems(projects);
        
        // select the project in combobox that matches the tasks project id
        for (Project p : projects) {
            if(p.getProjectId() == taskParent.getProjectId()) {
                project.getSelectionModel().select(p);
            }
        }
                    
        project.getStyleClass().add("project");
        hbox.getChildren().add(project);
        
        // edit project
        project.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            taskParent.setProjectId(newVal.getProjectId());
            //updateTaskProject(taskParent);
            System.out.println("Project: id=" + newVal.getProjectId() + " - "+newVal.getProjectName() + " (" + newVal.getClientName() + ")");
        });
        
        // billable
        Label billable = new Label("$");
        billable.getStyleClass().add("billable");
        
        if(taskParent.isBillable()) {
            billable.getStyleClass().add("true");
        } else {
            billable.getStyleClass().add("false");
        }
        
        // edit billable
        billable.setOnMouseClicked(e -> {
            taskParent.setBillable(!taskParent.isBillable()); // funny
            //updateTaskBillable(taskParent);
            System.out.println("Task billable is: " + taskParent.isBillable());
        });        
        
        hbox.getChildren().add(billable);
        
        // start
        JFXTextField start = new JFXTextField(taskParent.getStart().toLocalTime().toString());
        start.getStyleClass().add("start");
        hbox.getChildren().add(start);
        
        // dash
        hbox.getChildren().add(new Label("-"));
        
        // end
        JFXTextField end = new JFXTextField(taskParent.getEnd().toLocalTime().toString());
        end.getStyleClass().add("end");
        hbox.getChildren().add(end);
        
        // datepicker
        hbox.getChildren().add(new JFXDatePicker());
        
        // time
        Label time = new Label(taskParent.getTime());
        time.getStyleClass().add("time");
        hbox.getChildren().add(time);
        
        // continue timer button
        Button continueTimer = new Button();
        continueTimer.getStyleClass().add("continueTimer");
        hbox.getChildren().add(continueTimer);
              
        // stack each child under the parents content
        VBox childrenWrapper = new VBox();
        
        if(taskParent.getChildren().size() > 1) {
            for (TaskChild taskChild : taskParent.getChildren()) {
                childrenWrapper.getChildren().add(buildTask(taskChild, projects));
            }
        }
        
        parentNode.setContent(childrenWrapper);
        
        // add hbox to titledpane
        parentNode.setGraphic(hbox);

        return parentNode;
    }

     private static TitledPane buildTask(TaskChild taskChild, ObservableList<Project> projects) {
        
        // titledpane
        TitledPane childNode = new TitledPane();
        
        // hbox wrapper
        HBox hbox = new HBox();
        
        // name + num children wrapper
        HBox hboxName = new HBox();
        hboxName.getStyleClass().add("name");

        // name
        JFXTextField name = new JFXTextField(taskChild.getName());
        name.getStyleClass().add("name");
        hboxName.getChildren().add(name);
        hbox.getChildren().add(hboxName);

        // edit name on enter pressed
        name.setOnAction(e -> {
            taskChild.setName(name.getText());
            //updateTaskName(taskChild);
            System.out.println("name test 2");
        });
        
        // edit name on unfocus
        name.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal == false) {
                taskChild.setName(name.getText());
                //updateTaskName(taskChild);
                System.out.println("name test");
            }
        });
 
        // project
        JFXComboBox<Project> project = new JFXComboBox();
        project.setItems(projects);
        
        // select the project in combobox that matches the tasks project id
        for (Project p : projects) {
            if(p.getProjectId() == taskChild.getProjectId()) {
                project.getSelectionModel().select(p);
            }
        }
                    
        project.getStyleClass().add("project");
        hbox.getChildren().add(project);
        
        // edit project
        project.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            taskChild.setProjectId(newVal.getProjectId());
            //updateTaskProject(taskParent);
            System.out.println("Project: id=" + newVal.getProjectId() + " - "+newVal.getProjectName() + " (" + newVal.getClientName() + ")");
        });
        
        // billable
        Label billable = new Label("$");
        billable.getStyleClass().add("billable");
        
        if(taskChild.isBillable()) {
            billable.getStyleClass().add("true");
        } else {
            billable.getStyleClass().add("false");
        }
        
        // edit billable
        billable.setOnMouseClicked(e -> {
            taskChild.setBillable(!taskChild.isBillable()); // funny
            //updateTaskBillable(taskParent);
            System.out.println("Task billable is: " + taskChild.isBillable());
        });        
        
        hbox.getChildren().add(billable);
        
        // start
        JFXTextField start = new JFXTextField(taskChild.getStart().toLocalTime().toString());
        start.getStyleClass().add("start");
        hbox.getChildren().add(start);
        
        // dash
        hbox.getChildren().add(new Label("-"));
        
        // end
        JFXTextField end = new JFXTextField(taskChild.getEnd().toLocalTime().toString());
        end.getStyleClass().add("end");
        hbox.getChildren().add(end);
        
        // datepicker
        hbox.getChildren().add(new JFXDatePicker());
        
        // time
        Label time = new Label(taskChild.getTime());
        time.getStyleClass().add("time");
        hbox.getChildren().add(time);
        
        // continue timer button
        Button continueTimer = new Button();
        continueTimer.getStyleClass().add("continueTimer");
        hbox.getChildren().add(continueTimer);
        
        // add hbox to titledpane
        childNode.setGraphic(hbox);
        
        return childNode;
    }
}
