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

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
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
     * sket ændringer, ved at kopiere den allerede oprettet Task og give det et
     * personId og tidspunktet ændringen er sket.
     *
     * @param task
     * @param user
     * @throws DALException
     */
    public void changelogTask(int taskId, int person_id) throws DALException
    {
        try ( Connection con = dbCon.getConnection())
        {
            String sql = "INSERT INTO Changelog_Task (changelog_person, changelog_time , task_id, task_name, billable, project_id, person_id, task_start, task_end)\n"
                    + "SELECT ?, CURRENT_TIMESTAMP, task_id, task_name, billable, project_id, person_id, task_start, task_end FROM Tasklog\n"
                    + "WHERE Tasklog.task_id = ?";

            PreparedStatement st = con.prepareStatement(sql);

            st.setInt(1, person_id);
            st.setInt(2, taskId);

            st.executeQuery();

        } catch (SQLException e)
        {
//            throw new DALException("Kunne ikke tilføje Changelog");
        }
    }
}
