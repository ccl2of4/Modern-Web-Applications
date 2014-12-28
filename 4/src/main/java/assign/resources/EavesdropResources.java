package assign.resources;

import java.io.IOException;

import assign.services.EavesdropServices;

import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@Path("")
public class EavesdropResources {
	
	EavesdropServices services = null;
	EavesdropServices getServices () {
		return services == null ? EavesdropServices.sharedServices () : services;
	}
	void setServices (EavesdropServices services) {
		this.services = services;
	}
	
	@GET
	@Path("/projects/{project}/meetings/{year}")
	@Produces("application/xml")
	public String getMeetings (@PathParam("project") String project, @PathParam("year") String year) {
		EavesdropServices services = getServices ();
		String urlString = "http://eavesdrop.openstack.org/meetings/";
		urlString += project + "/";
		urlString += year + "/";
		String result;
		try {
			Document doc = services.getDocument (urlString);
			Elements elements = services.retrieveLinks (doc, urlString);
			result = services.makeProjectXML (project, elements);
		} catch (IOException e) {	
			result = null;
		}
		return result;
	}
	
	@GET
	@Path("/projects/{project}/irclogs")
	@Produces("application/xml")
	public String getIRClogs (@PathParam("project") String project) {
		EavesdropServices services = getServices ();
		String urlString = "http://eavesdrop.openstack.org/irclogs/";
		project = project.replace("#", "%23");
		urlString += project + "/";
		project = project.replace ("%23", "#");
		String result;
		try {
			Document doc = services.getDocument (urlString);
			Elements elements = services.retrieveLinks (doc, urlString);
			result = services.makeProjectXML (project, elements);
		} catch (IOException e) {	
			result = null;
		}
		return result;
	}

	@GET
	@Path("/projects")
	@Produces("application/xml")
	public String getProjects () {
		final String irclogs = "http://eavesdrop.openstack.org/irclogs/";
		final String meetings = "http://eavesdrop.openstack.org/meetings/";
		EavesdropServices services = getServices ();
		String result;
		try {
			Document doc = services.getDocument (irclogs);
			Elements elements = services.retrieveProjectNames (doc);
			doc = services.getDocument (meetings);
			elements.addAll (services.retrieveProjectNames (doc));
			result = services.makeProjectsXML (elements);
		} catch (IOException e) {	
			result = null;
		}
		return result;
	}
	
}