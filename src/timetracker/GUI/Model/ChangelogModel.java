/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Model;

import java.sql.SQLException;
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

    /**
     * Constructor for ChangelogModel
     *
     * @throws DALException
     */
    public ChangelogModel() throws DALException
    {
        bll = BLLManager.getInstance();
    }

    /**
     * Returnerer den allerede startet instans af klassen, eller starter en ny.
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public static ChangelogModel getInstance() throws DALException, SQLException
    {
        if (model == null)
        {
            model = new ChangelogModel();
        }
        return model;
    }

    /**
     * Opretter en changelog på det medsendte taskId og personId
     *
     * @param taskId
     * @param person_id
     * @throws DALException
     */
    public void changelogTask(int taskId, int person_id) throws DALException
    {
        bll.changelogTask(taskId, person_id);
    }

}
