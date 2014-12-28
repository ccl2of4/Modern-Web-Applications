package cs378.assignment6.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import cs378.assignment6.domain.Meeting;
import cs378.assignment6.domain.Project;
import cs378.assignment6.domain.MeetingList;
import cs378.assignment6.etl.Loader;
import cs378.assignment6.etl.Reader;

public class MeetingDataMgrService implements Loader, Reader {
	private SessionFactory sessionFactory;
	
	public MeetingDataMgrService() {
		// A SessionFactory is set up once for an application
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
	}
	
	public void load(Object objectToLoad) throws Exception {
		String xml = (String)objectToLoad;
		Document document = Jsoup.parse (xml, "", Parser.xmlParser ());
		
		Elements linkElements = document.select ("link");
		
		for (Element linkElement : linkElements) {
			String link = linkElement.text ();
			Meeting meeting = new Meeting ();
			String[] components = link.split ("/");
			String name = components[components.length - 1];
			
			String resourceLink = "http://localhost:8080/myeavesdrop/projects/solum/meetings/" + name;
			
			meeting.setName (name);
			meeting.setLink (resourceLink);
			meeting.setYear ("2014");
			meeting.setDescription (link);
			
			insertMeetingRecord (meeting);
		}		
	}
	
	private void insertMeetingRecord(Object obj) {
		Meeting meeting = (Meeting)obj;
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		Criteria criteria = session.createCriteria(Project.class).
				add(Restrictions.eq("name",  "solum"));
		List<?> result = criteria.list();
		Project project;
		if (result.size() > 0)
			project = (Project)result.get (0);
		else {
			project = new Project ("solum");
			project.setDescription("Project for Solum");
		}
		
		project.addMeeting(meeting);
		
		session.save(project);
		session.getTransaction().commit();
		session.close();
	}

	public Object read(Object source) throws Exception {

		// Build the list of meetings
		
		// Use Hibernate to query meeting list from the table
		// Build MeetingList and send it back
		
		Project project;
		
		Session session = sessionFactory.openSession ();
		session.beginTransaction ();

		List<Object> projects = session.createQuery(
				"from Project p join fetch p.meetings m where m.project.name = 'solum'").list ();
		
		project = (Project)projects.get (0);
				
		session.getTransaction().commit ();
		session.close ();

		return project;
	}
}
