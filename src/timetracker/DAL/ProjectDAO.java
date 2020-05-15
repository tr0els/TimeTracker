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
        try ( Connection con = dbCon.getConnection()) {

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
        try ( Connection con = dbCon.getConnection()) {

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
        try ( Connection con = dbCon.getConnection()) {

            String sql = "UPDATE Project SET Project_name = ?, project_rate = ?, client_id = ? WHERE project_id = ?";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, projectName);
            st.setInt(2, hourlyPay);
            st.setInt(3, clientID);
            st.setInt(4, projectID);

            st.executeQuery();

        } catch (SQLException e) {
            throw new DALException("Kunne ikke rette projektet");
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

        try ( Connection con = dbCon.getConnection()) {
            String sql = "SELECT project_id, project_name, project_rate, project.client_id, client_name\n"
                    + "FROM Project, Client\n"
                    + "WHERE Project.client_id = Client.client_id";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Project projects = new Project();
                projects.setProject_id(rs.getInt("project_id"));
                projects.setProject_name(rs.getString("project_name"));
                projects.setProject_rate(rs.getInt("project_rate"));
                projects.setClient_id(rs.getInt("client_id"));
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
        try ( Connection con = dbCon.getConnection()) {

            String sql = "SELECT * FROM Project WHERE project_name = ? AND project_rate = ? AND client_id = ?;";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, projectName);
            st.setInt(2, project_rate);
            st.setInt(3, client_id);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Project project = new Project();
                project.setProject_id(rs.getInt("project_id"));
                project.setProject_name(rs.getString("project_name"));
                project.setProject_rate(rs.getInt("project_rate"));
                project.setClient_id(rs.getInt("client_id"));
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

        try ( Connection con = dbCon.getConnection()) {
            String sql = "SELECT p.client_id, p.project_id, p.project_name, p.project_rate,\n"
                    + "CONVERT(VARCHAR(5),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))/60/60) + ':' +\n"
                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))/60%60), 2) + ':' +\n"
                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))%60),2)\n"
                    + "AS total_time\n"
                    + "FROM Project p\n"
                    + "JOIN Task t ON t.project_id = p.project_id\n"
                    + "JOIN Task_log tl ON t.task_id = tl.task_id\n"
                    + "WHERE t.person_id = ?\n"
                    + "GROUP BY p.project_id, p.project_name, p.client_id, p.project_rate;";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, person_id);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Project projects = new Project();

                projects.setProject_id(rs.getInt("project_id"));
                projects.setProject_name(rs.getString("project_name"));
                projects.setProject_rate(rs.getInt("project_rate"));
                projects.setClient_id(rs.getInt("client_id"));
                projects.setTotal_tid(rs.getString("total_time"));

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
        int client_ID = client.getClient_id();
        try ( Connection con = dbCon.getConnection()) {
            String sql = "SELECT * FROM Project WHERE client_id =  " + client_ID + ";";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Project projects = new Project();
                projects.setProject_id(rs.getInt("project_id"));
                projects.setProject_name(rs.getString("project_name"));
                projects.setProject_rate(rs.getInt("project_rate"));
                projects.setClient_id(rs.getInt("client_id"));

                allProjectswithClientID.add(projects);
            }
            return allProjectswithClientID;
        } catch (SQLException ex) {
            throw new DALException("kunne ikke finde projekter for klienten");
        }
        // return null;
    }

    public List<Project> getProjectsWithExtraData() throws DALException {
        ArrayList<Project> allProjectsWithExtraData = new ArrayList<>();

        try ( Connection con = dbCon.getConnection()) {
            String sql = "SELECT p.project_name,p.project_id, c.client_id,\n"
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
                    + "GROUP BY p.project_id, p.project_name, c.client_name, c.client_id;";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String billabletime;
                Project projects = new Project();
                projects.setProject_id(rs.getInt("project_id"));
                projects.setProject_name(rs.getString("project_name"));
                //projects.setProject_rate(rs.getInt("project_rate"));
                projects.setClient_id(rs.getInt("client_id"));
                projects.setClientName(rs.getString("client_name"));
                projects.setTotal_tid(rs.getString("total_time"));
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

    
        
        public List<Project> getProjectsToFilter(User comboUser, Client comboClient, String fradato, String tildato) throws DALException
    {
        ArrayList<Project> allProjectsWithExtraData = new ArrayList<>();

        try ( Connection con = dbCon.getConnection())
        {
            String client_clause = "";
            String user_clause = "";
            String fradato_caluse = "";
            String tildato_clause = "";
           // StringBuilder sb = new StringBuilder();
            
           if(comboUser != null)
               user_clause += "AND t.person_id = " +comboUser.getPerson_id()+"\n";
           
           if(comboClient != null)
               client_clause +=  comboClient.getClient_name();
           
           if( fradato != null )
               fradato_caluse += "AND t.task_start >= convert(date, '"+fradato+"', 103)\n";
           
           if( tildato != null )
               tildato_clause += "AND t.task_end <= convert(date, '"+tildato+"', 103)\n";
           
            
            String sql = 
                    "SELECT p.project_name,p.project_id, c.client_id,\n"                    + "CONVERT(VARCHAR(5),SUM(DATEDIFF(SECOND,t.task_start,t.task_end))/60/60) + ':' +\n"
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
                    + "GROUP BY p.project_id, p.project_name, c.client_name, c.client_id;";
            //sb.append(sql);
            
            
            
            
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String billabletime;
                Project projects = new Project();
                projects.setProject_id(rs.getInt("project_id"));
                projects.setProject_name(rs.getString("project_name"));
                //projects.setProject_rate(rs.getInt("project_rate"));
                projects.setClient_id(rs.getInt("client_id"));
                projects.setClientName(rs.getString("client_name"));
                projects.setTotal_tid(rs.getString("total_time"));
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


    public List<Project> getProjectsForEmploy(int medarbejder_id) throws DALException {
        ArrayList<Project> allProjectsWithExtraData = new ArrayList<>();

        try ( Connection con = dbCon.getConnection()) {
            String sql = "SELECT p.project_name,p.project_id, c.client_id,\n"
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
                    + "GROUP BY p.project_id, p.project_name, c.client_name, c.client_id;";
            //sb.append(sql);
            
            
            
            
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String billabletime;
                Project projects = new Project();
                projects.setProject_id(rs.getInt("project_id"));
                projects.setProject_name(rs.getString("project_name"));
                //projects.setProject_rate(rs.getInt("project_rate"));
                projects.setClient_id(rs.getInt("client_id"));
                projects.setClientName(rs.getString("client_name"));
                projects.setTotal_tid(rs.getString("total_time"));
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

}
