/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;
import timetracker.BE.Client;
import timetracker.BE.Project;
import timetracker.BE.User;

/**
 *
 * @author Charlotte
 */
public class ProjectDAO {

    private DatabaseConnector dbCon;

    public ProjectDAO() throws DALException {
        dbCon = new DatabaseConnector();
    }

    /**
     * Tager imod infoen fra BLLManagerens "createProject" og sætter det ind i
     * en prepared statement så det vil blive gemt på databasen.
     *
     * @param clientID
     * @param projectName
     * @param hourlyPay
     */
    public void createProject(int clientID, String projectName, int hourlyPay) {
        try (Connection con = dbCon.getConnection()) {

            String sql = "INSERT INTO Project (project_name, project_rate, client_id) VALUES (?,?,?)";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, projectName);
            st.setInt(2, hourlyPay);
            st.setInt(3, clientID);

            st.executeQuery();

        } catch (Exception e) {
        }
    }

    /**
     * tager imod infoen fra BLLManagerens "deleteProject" og sætter det ind i
     * en prepared statement så det vil blive slettet fra databasen.
     *
     * @param projectID
     * @throws timetracker.DAL.DALException
     */
    public void deleteProject(int projectID) throws DALException {
        try (Connection con = dbCon.getConnection()) {

            String sql = "DELETE FROM Project WHERE project_id = ?";

            PreparedStatement st = con.prepareStatement(sql);

            st.setInt(1, projectID);

            st.execute();

        } catch (SQLException e) {
            throw new DALException("Kunne ikke slette projektet");
        }
    }

    /**
     * tager imod infoen fra BLLManagerens "editProject" og sætter det ind i en
     * prepared statement så infoen kan updateres i databasen.
     *
     * @param clientID
     * @param projectName
     * @param hourlyPay
     * @param projectID
     * @throws timetracker.DAL.DALException
     */
    public void editProject(int clientID, String projectName, int hourlyPay, int projectID) throws DALException {
        try (Connection con = dbCon.getConnection()) {

            String sql = "UPDATE Project SET Project_name = ?, project_rate = ?, client_id = ? WHERE project_id = ?;";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, projectName);
            st.setInt(2, hourlyPay);
            st.setInt(3, clientID);
            st.setInt(4, projectID);

            st.executeQuery();

        } catch (SQLException e) {
            //throw new DALException("Kunne ikke rette projektet" +e);
        }
    }

    /**
     * Henter alt projekt data som ligger gemt på serveren og putter det ind i
     * en ArrayList.
     *
     * @return
     * @throws DALException
     */
    public List<Project> getProjects() throws DALException {
        ArrayList<Project> allProjects = new ArrayList<>();

        try (Connection con = dbCon.getConnection()) {
            String sql = "SELECT project_id, project_name, project_rate, project.client_id, client_name\n"
                    + "FROM Project, Client\n"
                    + "WHERE Project.client_id = Client.client_id";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Project projects = new Project();
                projects.setProjectId(rs.getInt("project_id"));
                projects.setProjectName(rs.getString("project_name"));
                projects.setProjectRate(rs.getInt("project_rate"));
                projects.setClientId(rs.getInt("client_id"));
                projects.setClientName(rs.getString("client_name"));

                allProjects.add(projects);
            }
            return allProjects;
        } catch (SQLException ex) {
            throw new DALException("Kunne ikke hente projekter fra databasen");
        }
        //return null;
    }

    /**
     *
     * @param projectName
     * @param project_rate
     * @param client_id
     * @return
     */
    public Project getProject(String projectName, int project_rate, int client_id) throws DALException {
        try (Connection con = dbCon.getConnection()) {

            String sql = "SELECT * FROM Project WHERE project_name = ? AND project_rate = ? AND client_id = ?;";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, projectName);
            st.setInt(2, project_rate);
            st.setInt(3, client_id);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Project project = new Project();
                project.setProjectId(rs.getInt("project_id"));
                project.setProjectName(rs.getString("project_name"));
                project.setProjectRate(rs.getInt("project_rate"));
                project.setClientId(rs.getInt("client_id"));
                return project;
            }

        } catch (SQLException ex) {
            throw new DALException("kunne ikke hente proejktet fra databasen");
        }

        return null;
    }

    /**
     * returnere en liste af projecter hvor person_id har lavet tasks på.
     *
     * @param person_id
     * @return
     */
    public List<Project> getProjectsbyID(int person_id) throws DALException {
        ArrayList<Project> projectsbyID = new ArrayList<>();

        try (Connection con = dbCon.getConnection()) {
            String sql = "SELECT p.client_id, c.client_name, p.project_id, p.project_name, p.project_rate,\n"
                    + "CONVERT(VARCHAR(5),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))/60/60) + ':' +\n"
                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))/60%60), 2) + ':' +\n"
                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))%60),2)\n"
                    + "AS total_time FROM Project p\n"
                    + "JOIN Client c on p.client_id = c.client_id\n"
                    + "JOIN Tasklog tl on p.project_id = tl.project_id\n"
                    + "WHERE tl.person_id = ?\n"
                    + "GROUP BY p.project_id, p.project_name, p.client_id, p.project_rate, c.client_name\n"
                    + "ORDER BY p.project_name;";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, person_id);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Project projects = new Project();

                projects.setProjectId(rs.getInt("project_id"));
                projects.setProjectName(rs.getString("project_name"));
                projects.setProjectRate(rs.getInt("project_rate"));
                projects.setClientId(rs.getInt("client_id"));
                projects.setTotalTime(rs.getString("total_time"));
                projects.setClientName(rs.getString("client_name"));

                projectsbyID.add(projects);
            }

        } catch (SQLException ex) {
            throw new DALException("Kunne ikke hente dine projekter");
        }

        return projectsbyID;
    }

    /**
     * henter en liste ud, med projekter for en klient
     *
     * @param client
     * @return
     */
    public List<Project> getProjectsbyClientID(Client client) throws DALException {
        ArrayList<Project> allProjectswithClientID = new ArrayList<>();
        int client_ID = client.getClientId();
        try (Connection con = dbCon.getConnection()) {
            String sql = "SELECT * FROM Project p WHERE p.client_id =  " + client_ID + "order by p.project_name ;";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Project projects = new Project();
                projects.setProjectId(rs.getInt("project_id"));
                projects.setProjectName(rs.getString("project_name"));
                projects.setProjectRate(rs.getInt("project_rate"));
                projects.setClientId(rs.getInt("client_id"));

                allProjectswithClientID.add(projects);
            }
           
            return allProjectswithClientID;
        } catch (SQLException ex) {
            throw new DALException("kunne ikke finde projekter for klienten");
        }
        // return null;
    }
/**
 * 
 * @return
 * @throws DALException 
 */
    public List<Project> getProjectsWithExtraData() throws DALException {
        ArrayList<Project> allProjectsWithExtraData = new ArrayList<>();

        try (Connection con = dbCon.getConnection()) {
            String sql = "SELECT p.project_name,p.project_id, c.client_id, p.project_rate,\n"
                    + "CONVERT(VARCHAR(5),SUM(DATEDIFF(SECOND,t.task_start,t.task_end))/60/60) + ':' +\n"
                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,t.task_start,t.task_end))/60%60), 2) + ':' +\n"
                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,t.task_start,t.task_end))%60),2)\n"
                    + "AS total_time,\n"
                    + "convert(Varchar(5),sum(case when t1.billable = 1  then (DATEDIFF(SECOND,t1.task_start,t1.task_end))end)/60/60)+':'+\n"
                    + "right('0' + convert(Varchar(2),sum(case when t1.billable = 1 then (DATEDIFF(SECOND,t1.task_start,t1.task_end))end)/60%60),2)+ ':'+\n"
                    + "right('0' + convert(Varchar(2),sum(case when t1.billable = 1 then (DATEDIFF(SECOND,t1.task_start,t1.task_end))end)%60),2) as billabletime, c.client_name\n"
                    + "FROM Tasklog t, Project p, Tasklog t1, Client c\n"
                    + "where t.project_id = p.project_id\n"
                    + "and c.client_id = p.client_id\n"
                    + "and t.task_id = t1.task_id\n"
                    + "GROUP BY p.project_id, p.project_name, c.client_name, c.client_id, p.project_rate;";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String billabletime;
                Project projects = new Project();
                projects.setProjectId(rs.getInt("project_id"));
                projects.setProjectName(rs.getString("project_name"));
                projects.setProjectRate(rs.getInt("project_rate"));
                projects.setClientId(rs.getInt("client_id"));
                projects.setClientName(rs.getString("client_name"));
                projects.setTotalTime(rs.getString("total_time"));
                billabletime = rs.getString("billabletime");

                if (billabletime != null) {
                    projects.setBillableTime(billabletime);
                } else {
                    projects.setBillableTime("00:00:00");
                }
              
                allProjectsWithExtraData.add(projects);
            }
                      
            return allProjectsWithExtraData;
        } catch (SQLException ex) {
            throw new DALException("Kunne ikke hente projekter fra databasen med ekstra data" + ex);
        }
        //return null;
    }
    /**
     * denne metode bruges til at lave vores sql, til filteringen på overbliksbilledet og min tid. 
     * @param comboUser
     * @param comboClient
     * @param fradato
     * @param tildato
     * @param monthStart
     * @param monthEnd
     * @return
     * @throws DALException 
     */
    public List<Project> getProjectsToFilter(User comboUser, Client comboClient, String fradato, String tildato, String monthStart, String monthEnd) throws DALException {
        ArrayList<Project> allProjectsWithExtraData = new ArrayList<>();

        try (Connection con = dbCon.getConnection()) {
            String client_clause = "";
            String user_clause = "";
            String fradato_caluse = "";
            String tildato_clause = "";
            String periode_clause = "";

            if (comboUser != null) {
                user_clause += "AND t.person_id = " + comboUser.getPersonId()+ "\n";
            }

            if (comboClient != null) {
                client_clause += comboClient.getClientName();
            }

            if (fradato != null) {
                fradato_caluse += "AND t.task_start >= convert(date, '" + fradato + "', 103)\n";
            }

            if (tildato != null) {
                tildato_clause += "AND t.task_end <= convert(date, '" + tildato + "', 103)\n";
            }

            if (monthStart != null && monthEnd != null) {
                periode_clause += "AND t.task_start Between convert(date, '" + monthStart + "', 103) and convert(date, '" + monthEnd + "', 103)";
            }

            String sql
                    = "SELECT p.project_name,p.project_id, c.client_id, p.project_rate,\n"
                    + "CONVERT(VARCHAR(5),SUM(DATEDIFF(SECOND,t.task_start,t.task_end))/60/60) + ':' +\n"
                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,t.task_start,t.task_end))/60%60), 2) + ':' +\n"
                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,t.task_start,t.task_end))%60),2)\n"
                    + "AS total_time,\n"
                    + "convert(Varchar(5),sum(case when t1.billable = 1  then (DATEDIFF(SECOND,t1.task_start,t1.task_end))end)/60/60)+':'+\n"
                    + "right('0' + convert(Varchar(2),sum(case when t1.billable = 1 then (DATEDIFF(SECOND,t1.task_start,t1.task_end))end)/60%60),2)+ ':'+\n"
                    + "right('0' + convert(Varchar(2),sum(case when t1.billable = 1 then (DATEDIFF(SECOND,t1.task_start,t1.task_end))end)%60),2) as billabletime, c.client_name\n"
                    + "FROM Tasklog t, Project p, Tasklog t1, Client c\n"
                    + "where t.project_id = p.project_id\n"
                    + "and c.client_id = p.client_id\n"
                    + "and t.task_id = t1.task_id\n"
                    + "and c.client_name LIKE '%" + client_clause + "%'\n"
                    + user_clause
                    + fradato_caluse
                    + tildato_clause
                    + periode_clause
                    + "GROUP BY p.project_id, p.project_name, c.client_name, c.client_id, p.project_rate;";
    

            Statement statement = con.createStatement();
            
         
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String billabletime;
                Project projects = new Project();
                projects.setProjectId(rs.getInt("project_id"));
                projects.setProjectName(rs.getString("project_name"));
                projects.setProjectRate(rs.getInt("project_rate"));
                projects.setClientId(rs.getInt("client_id"));
                projects.setClientName(rs.getString("client_name"));
                projects.setTotalTime(rs.getString("total_time"));
                billabletime = rs.getString("billabletime");

                if (billabletime != null) {
                    projects.setBillableTime(billabletime);
                } else {
                    projects.setBillableTime("00:00:00");
                }

                allProjectsWithExtraData.add(projects);
                  
            } 
            return allProjectsWithExtraData;
             
        } catch (SQLException ex) {
            throw new DALException("Kunne ikke hente projekter fra databasen med ekstra data" + ex);
        }
        
    }


}
