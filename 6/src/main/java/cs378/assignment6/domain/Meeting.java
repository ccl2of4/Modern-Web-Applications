package cs378.assignment6.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table( name = "meeting" )
@XmlRootElement
@XmlType (propOrder={"name","link","year","description"})
public class Meeting {

	private String name;
	private Project project;
	private String link;
	private String year;
	private String description;
	
	public Meeting () {
	}
	
	public Meeting (String name) {
		this.name = name;
	}
	
	@Id
	@XmlElement
	public String getName () {
		return this.name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	@ManyToOne
	@JoinColumn(name="project_name")
	@XmlTransient
	public Project getProject () {
		return project;
	}
	
	public void setProject (Project project) {
		this.project = project;
	}
	
	@XmlElement
	public String getLink () {
		return link;
	}
	
	public void setLink (String link) {
		this.link = link;
	}
	
	@XmlElement
	public String getYear () {
		return year;
	}
	
	public void setYear (String year) {
		this.year = year;
	}
	
	@XmlElement
	public String getDescription () {
		return description;
	}
	
	public void setDescription (String description) {
		this.description = description;
	}
}