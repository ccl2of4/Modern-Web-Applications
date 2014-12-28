package cs378.assignment6.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MeetingList {
	List<Meeting> meetings;
	
	@XmlElement (name = "meeting")
	public List<Meeting> getMeetings () {
		return meetings;
	}
	public void setMeetings (List<Meeting> meetings) {
		this.meetings = meetings;
	}
}