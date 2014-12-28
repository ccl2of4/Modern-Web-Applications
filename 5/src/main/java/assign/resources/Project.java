package assign.resources;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jsoup.select.Elements;

@XmlRootElement
public class Project {
	private String name;
	private String description;
	private Meetings meetings;
	
	public Project () {}
	
	public Project (String name, String description) {
		this.name = name;
		this.description = description;
	}

	@XmlElement
	public String getName () {
		return name;
	}
	public void setName (String name) {
		this.name = name;
	}
	
	@XmlElement
	public String getDescription () {
		return description;
	}	
	public void setDescription (String description) {
		this.description = description;
	}
	
	@XmlElement
	public Meetings getMeetings () {
		return meetings;
	}
	public void setMeetings (Meetings meetings) {
		this.meetings = meetings;
	}
}