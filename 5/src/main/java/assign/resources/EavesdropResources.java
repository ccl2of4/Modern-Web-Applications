package assign.resources;

import assign.services.EavesdropServices;

import java.util.List;
import java.util.LinkedList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

@Path("")
public class EavesdropResources {

	public EavesdropResources () {
		final String dbURL = "jdbc:mysql://localhost:3306/assignment5";
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace ();
		}
		EavesdropServices.sharedServices ().setDataSource (setupDataSource(dbURL));
	}
	
	public static DataSource setupDataSource (String url) {
        BasicDataSource ds = new BasicDataSource();
        ds.setUsername("ccl2of4");
        ds.setPassword("");
        ds.setUrl(url);
        return ds;
    }
	
	@POST
	@Path("/projects")
	@Consumes ("application/xml")
	public void postProject (Project project) throws Exception {
		String query = "insert into project(name, description) values(?,?);";
		
		List<String> params = new LinkedList<String> ();
		params.add (project.getName ());
		params.add (project.getDescription ());
		
		EavesdropServices services = EavesdropServices.sharedServices ();
		PreparedStatement statement = services.prepareStatement (query, params);
		if (statement != null) {
			services.executeUpdate (statement);
			statement.close ();
		}
	}
	
	@PUT
	@Path("/projects/{projectName}")
	@Consumes("application/xml")
	public void putProject (Project project, @PathParam("projectName") String projectName) throws Exception {		
		String description = project.getDescription ();
		if (description == null) {
			return;
		}

		String query = "update project set description=? where name=?;";
		List<String> params = new LinkedList<String>();
		params.add (description);
		params.add (projectName);
		
		EavesdropServices services = EavesdropServices.sharedServices ();
		PreparedStatement statement = services.prepareStatement (query, params);
		if (statement != null) {
			services.executeUpdate (statement);
			statement.close ();
		}
	}
	
	@POST
	@Path("/projects/{projectName}/meetings")
	@Consumes ("application/xml")
	public void postMeeting (Meeting meeting, @PathParam("projectName") String projectName) throws Exception {
		String query = "insert into meeting(name, project_name, link, year) values(?,?,?,?);";
		
		List<String> params = new LinkedList<String>();
		params.add (meeting.getName ());
		params.add (projectName);
		params.add (meeting.getLink ());
		params.add (meeting.getYear ());
		
		EavesdropServices services = EavesdropServices.sharedServices ();
		PreparedStatement statement = services.prepareStatement (query, params);
		if (statement != null) {
			services.executeUpdate (statement);
			statement.close ();
		}
	}
	
	@GET
	@Path("/projects/{projectName}")
	@Produces("application/xml")
	public Project getProject (@PathParam("projectName") String projectName) throws Exception {
		String query = "select project.name as p_name, project.description, meeting.name as m_name, meeting.link, meeting.year from project left join meeting on project.name = meeting.project_name where project.name = ? order by p_name;";
		List<String> params = new LinkedList<String> ();
		params.add (projectName);
		
		EavesdropServices services = EavesdropServices.sharedServices ();
		PreparedStatement ps = services.prepareStatement (query, params);
		
		Project project = null;
		if (ps != null) {	
			ResultSet results = services.executeQuery(ps);
			if (results != null) while (results.next ()) {
				if (project == null) {
					project = new Project ();
					project.setName (results.getString ("p_name"));
					project.setDescription (results.getString ("description"));
					project.setMeetings (new Meetings ());
					project.getMeetings().setAll (new LinkedList<Meeting> ());
				}
				if (results.getString ("m_name") != null) {
					Meeting meeting = new Meeting ();
					meeting.setName (results.getString ("m_name"));
					meeting.setLink (results.getString ("link"));
					meeting.setYear (results.getString ("year"));
					project.getMeetings ().getAll ().add (meeting);
				}
			}
			ps.close ();
		}
		return project;
	}
		
	@DELETE
	@Path("/projects/{projectName}")
	public void deleteProject (@PathParam("projectName") String projectName) throws Exception {
		String query = "delete from project where name = ?;";
		List<String> params = new LinkedList<String> ();
		params.add (projectName);
		
		EavesdropServices services = EavesdropServices.sharedServices ();
		PreparedStatement ps = services.prepareStatement (query, params);
		if (ps != null) {
			services.executeUpdate (ps);
			ps.close ();
		}
	}
}