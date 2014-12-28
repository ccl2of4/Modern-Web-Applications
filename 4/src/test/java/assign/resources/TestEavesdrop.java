package assign.resources;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Response;

import org.jsoup.nodes.*;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestEavesdrop {
	
	@Before
	public void setUp () {
		
	}

	
   @Test
   public void testGetProjects() throws Exception {
      Client client = ClientBuilder.newClient();
      try {
         String result = client.target("http://localhost:8080/assignment4/myeavesdrop/projects/")
                 .request().get(String.class);
       
	      assertTrue (result.contains ("<projects>"));
	      assertTrue (result.contains ("</projects>"));
	      assertTrue (result.contains ("<project>"));
	      assertTrue (result.contains ("</project>"));
	      assertTrue (result.contains ("_trove"));
	      assertTrue (result.contains ("#heat"));

      } finally {
         client.close();
      }
   }
   
   public void testGetMeetings () throws Exception {
	      Client client = ClientBuilder.newClient();
	      try {
	         String result = client.target("http://localhost:8080/assignment4/myeavesdrop/projects/barbican/meetings/2014/")
	                 .request().get(String.class);
	       
		      assertTrue (result.contains ("<project name=\"barbican\">"));
		      assertTrue (result.contains ("http://eavesdrop.openstack.org/meetings/barbican/2014/barbican.2014-01-27-20.00.html"));
		      assertTrue (result.contains ("<link>"));
		      assertTrue (result.contains ("</link>"));
		      assertTrue (result.contains ("</project>"));

	      } finally {
	         client.close();
	      }
   }
   
   public void testGetIRClogs () throws Exception {
	      Client client = ClientBuilder.newClient();
	      try {
	         String result = client.target("http://localhost:8080/assignment4/myeavesdrop/projects/%23heat/irclogs/")
	                 .request().get(String.class);
	       
		      assertTrue (result.contains ("<project name=\"#heat\">"));
		      assertTrue (result.contains ("http://eavesdrop.openstack.org/irclogs/%23heat/%23heat.2013-12-10.log"));
		      assertTrue (result.contains ("<link>"));
		      assertTrue (result.contains ("</link>"));
		      assertTrue (result.contains ("</project>"));

	      } finally {
	         client.close();
	      }
   }
}