package assign.resources;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Meetings {
	private List<Meeting> meetings;
	
	@XmlElement(name = "meeting")
	public List<Meeting> getAll () {
		return meetings;
	}
	public void setAll (List<Meeting> meetings) {
		this.meetings = meetings;
	}
}