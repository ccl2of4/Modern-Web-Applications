package assign.resources;

import javax.ws.rs.client.*;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.jsoup.nodes.*;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.*;

import static org.junit.Assert.*;

public class TestEavesdrop {
	
	@Before
	public void setUp () {
		
	}
	
	@Test
	public void test1 () {
		Client client = ClientBuilder.newClient();
		try {
			Response response = client.target("http://localhost:8080/assignment5/myeavesdrop/projects/cs345").request().buildDelete().invoke ();

			Project project = new Project ();
			project.setName("cs345");
			project.setDescription("foo");
			response = client.target("http://localhost:8080/assignment5/myeavesdrop/projects/")
                .request("application/xml").buildPost(Entity.entity(project, "application/xml")).invoke ();
			
			assertFalse (response.getStatus() == 404);

			project = client.target("http://localhost:8080/assignment5/myeavesdrop/projects/cs345")
	         .request().get(Project.class);
			
			assertEquals ("cs345", project.getName ());
		} catch (Exception e) {
			assertTrue (false);
		} finally {
			client.close();
		}
	}
	
	@Test
	public void test2 () {
		Client client = ClientBuilder.newClient();
		try {
			Response response = client.target("http://localhost:8080/assignment5/myeavesdrop/projects/cs347").request().buildDelete().invoke ();

			Project project = new Project ();
			project.setName("cs347");
			project.setDescription("foo2");
			response = client.target("http://localhost:8080/assignment5/myeavesdrop/projects/")
                .request("application/xml").buildPost(Entity.entity(project, "application/xml")).invoke ();
			
			assertFalse (response.getStatus() == 404);

			project = client.target("http://localhost:8080/assignment5/myeavesdrop/projects/cs347")
	         .request().get(Project.class);
			
			assertEquals ("cs347", project.getName ());
		}
		catch (Exception e) {
			assertFalse (true);
		} finally {
			client.close();
		}
	}
	
	@Test
	public void test3 () {
		Client client = ClientBuilder.newClient();
		try {
			Response response = client.target("http://localhost:8080/assignment5/myeavesdrop/projects/cs331").request().buildDelete().invoke ();

			Project project = new Project ();
			project.setName("cs331");
			project.setDescription("foo");
			response = client.target("http://localhost:8080/assignment5/myeavesdrop/projects/")
                .request("application/xml").buildPost(Entity.entity(project, "application/xml")).invoke ();
			
			assertFalse (response.getStatus() == 404);

			project = client.target("http://localhost:8080/assignment5/myeavesdrop/projects/cs331")
	         .request().get(Project.class);
			
			assertTrue (project.getName ().equals ("cs331"));
			assertEquals ("foo", project.getDescription ());

			project = new Project ();
			project.setDescription("foo2");
			response = client.target("http://localhost:8080/assignment5/myeavesdrop/projects/cs331")
                .request("application/xml").buildPut(Entity.entity(project, "application/xml")).invoke ();
			
			assertFalse (response.getStatus() == 404);

			project = client.target("http://localhost:8080/assignment5/myeavesdrop/projects/cs331")
			         .request().get(Project.class);
			
			assertEquals ("cs331", project.getName ());
			assertEquals ("foo2", project.getDescription ());
		} catch (Exception e) {
			assertTrue (false);
		} finally {
			client.close();
		}
	}
}