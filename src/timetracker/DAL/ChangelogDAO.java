/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import timetracker.BE.Task;
import timetracker.BE.User;

/**
 *
 * @author René Jørgensen
 */
public class ChangelogDAO
{

    private DatabaseConnector dbCon;

    public ChangelogDAO() throws DALException
    {
        dbCon = new DatabaseConnector();
    }

    /**
     * Opretter en Changelog_Task i vores database så man kan se hvornår der er
     * sket ændringer.
     *
     * @param task
     * @param user
     * @throws DALException
     */
    public void changelogTask(Task task, int person_id) throws DALException
    {
        try ( Connection con = dbCon.getConnection())
        {
            String sql = "INSERT INTO Changelog_Task (changelog_person, changelog_time , task_id, task_name, billable, project_id, person_id, task_start, task_end)\n"
                    + "SELECT ?, CURRENT_TIMESTAMP, task_id, task_name, billable, project_id, person_id, task_start, task_end FROM Tasklog\n"
                    + "WHERE Tasklog.task_id = ?";

            PreparedStatement st = con.prepareStatement(sql);

            st.setInt(1, person_id);
            st.setInt(2, task.getTaskId());

            st.executeQuery();

        } catch (SQLException e)
        {
//            throw new DALException("Kunne ikke tilføje Changelog");
        }
    }
}