package assign.resources;

import java.util.List;
import java.util.HashSet;
import java.util.HashMap;

import assign.services.Assignment7Services;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@Path ("")
public class Assignment7Resources {
	public Assignment7Resources () {
		return;
	}
	
	@GET
	@PathParam ("")
	public String getMeetingInfo () throws Exception {
		final String urlString = "http://eavesdrop.openstack.org/meetings/";
		Assignment7Services services = Assignment7Services.sharedServices ();
		HashMap<String,HashSet<String>> meetingInfo = new HashMap<String,HashSet<String>> ();
		try {
			Document doc = services.getDocument (urlString);
			List<String> links = services.retrieveLinks (doc, urlString);
			for (String meetingLink : links) {
				String meetingName = meetingLink.replace(urlString, "").split("/")[0];
				HashSet<String> meetingSet = new HashSet<String> ();
				meetingInfo.put(meetingName, meetingSet);
				Document meetingDoc = services.getDocument (meetingLink);
				List<String> meetingLinks = services.retrieveLinks (meetingDoc, meetingLink);
				for (String yearLink : meetingLinks) {
					Document meetingYearDoc = services.getDocument (yearLink);
					List<String> meetingYearLinks = services.retrieveLinks (meetingYearDoc, yearLink);
					for (String meetingThingLink : meetingYearLinks) {
						String parts[] = meetingThingLink.split ("/");
						String lastPart = parts[parts.length - 1];
						String lastPartNameThing = lastPart
								.replace(".txt", "")
								.replace(".log", "")
								.replace(".html", "");
						meetingSet.add(lastPartNameThing);
					}
				}
			}
			
		} catch (Exception e) {	
			e.printStackTrace ();
		}
        String json = new ObjectMapper().writeValueAsString(meetingInfo);
		return json;
	}
}
