package assign.resources;

import org.jsoup.nodes.*;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import assign.services.EavesdropServices;


public class TestEavesdropResources {
	EavesdropServices mockedServices = mock (EavesdropServices.class);
	EavesdropResources resources = new EavesdropResources ();
	
	@Before
	public void setup () {
		resources.setServices(mockedServices);
	}
	
	@Test
	public void testGetMeetings () throws Exception {
		final String str = "http://eavesdrop.openstack.org/meetings/foo/2014/";
		when (mockedServices.getDocument (str)).thenReturn (null);
		when (mockedServices.retrieveLinks (null, str)).thenReturn (null);
		when (mockedServices.makeProjectXML ("foo", null)).thenReturn (null);
		String result = resources.getMeetings("foo", "2014");
		assertNull (result);
	}
	
	@Test
	public void testGetIRClogs () throws Exception {
		final String str = "http://eavesdrop.openstack.org/irclogs/#foo/";
		when (mockedServices.getDocument (str)).thenReturn (null);
		when (mockedServices.retrieveLinks (null, str)).thenReturn (null);
		when (mockedServices.makeProjectXML ("foo", null)).thenReturn (null);
		String result = resources.getIRClogs("foo");
		assertNull (result);
	}
	
	@Test
	public void testGetProjects () throws Exception {
		final String str = "http://eavesdrop.openstack.org/irclogs/#foo/";
		when (mockedServices.getDocument (str)).thenReturn (null);
		when (mockedServices.retrieveLinks (null, str)).thenReturn (null);
		when (mockedServices.makeProjectXML ("foo", null)).thenReturn (null);
		String result = resources.getMeetings("foo", "2014");
		assertNull (result);
	}
}