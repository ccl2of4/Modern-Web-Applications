package cs378.assignment6.domain;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table (name = "project")
@XmlRootElement
@XmlType (propOrder={"name","description","meetingList"})
public class Project {

	private String name;
	private String description;
	private Set<Meeting> meetings;
	private MeetingList meetingList;
	
	public Project () {
		this.meetings = new HashSet<Meeting>();
	}
	
	public Project (String name) {
		this.name = name;
		this.meetings = new HashSet<Meeting>();
	}
	
	@Id
	@XmlElement
	public String getName () {
		return this.name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	@XmlElement
	public String getDescription () {
		return this.description;
	}
	
	public void setDescription (String description) {
		this.description = description;
	}
	
	public void addMeeting (Meeting meeting) {
		this.meetings.add(meeting);
		if (meeting.getProject () == null || !meeting.getProject ().equals(this)) {
			meeting.setProject (this);
		}
	}
	
	@OneToMany(mappedBy="project")
	@Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
	@XmlTransient
	public Set<Meeting> getMeetings() {
		return meetings;
	}
	
	public void setMeetings (Set<Meeting> meetings) {
		this.meetings = meetings;
	}
	
	@XmlElement (name = "meetings")
	@Transient
	public MeetingList getMeetingList () {
		if (meetingList != null)
			return meetingList;
		
		meetingList = new MeetingList ();
		List<Meeting> list = new LinkedList<Meeting> (this.getMeetings ());
		meetingList.setMeetings (list);
		return meetingList;
	}
	
	public void setMeetingList (MeetingList meetingList) {
		this.meetingList = meetingList;
	}
}