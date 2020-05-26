/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Model;

import java.sql.SQLException;
import timetracker.BE.Task;
import timetracker.BE.User;
import timetracker.BLL.BLLManager;
import timetracker.DAL.DALException;

/**
 *
 * @author René Jørgensen
 */
public class ChangelogModel
{
    private static BLLManager bll;
    private static ChangelogModel model = null;

    public ChangelogModel() throws DALException
    {
        bll = BLLManager.getInstance();        
    }
    
    public static ChangelogModel getInstance() throws DALException, SQLException {
        if (model == null) {
            model = new ChangelogModel();
        }
        return model;
    }
    
    public void changelogTask(Task task, User user){
        bll.changelogTask(task, user);
    }
    
}
