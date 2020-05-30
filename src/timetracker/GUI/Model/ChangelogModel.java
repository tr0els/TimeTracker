/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Model;

import java.sql.SQLException;
import timetracker.BE.Task;
import timetracker.BLL.BLLManager;
import timetracker.DAL.DALException;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
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
    
    public void changelogTask(int taskId, int person_id) throws DALException{
        bll.changelogTask(taskId, person_id);
    }
    
}
