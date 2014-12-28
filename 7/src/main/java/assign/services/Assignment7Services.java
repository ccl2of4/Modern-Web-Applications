package assign.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.nodes.*;
import org.jsoup.parser.*;
import org.jsoup.select.*;
import org.jsoup.*;

public class Assignment7Services {
	
	static Assignment7Services singleton;
	
	public static Assignment7Services sharedServices () {
		return singleton == null ? singleton = new Assignment7Services () : singleton;
	}
	
	public Document getDocument (String urlString) throws Exception {
		return Jsoup.connect (urlString).get ();
	}
	
	public Elements retrieveProjectNames (Document doc) {
		Elements elements = new Elements ();
		Elements anchorElements = doc.getElementsByTag("a");
		
		for (Element anchorElement : anchorElements) {
			String anchorHtml = anchorElement.html ();
			
			if (anchorHtml.equals("Name") ||
				anchorHtml.equals("Last modified") ||
				anchorHtml.equals("Size") ||
				anchorHtml.equals("Description") ||
				anchorHtml.equals("Parent Directory"))
				continue;
			
			Element element = new Element (Tag.valueOf ("Project"), "");
			element.html (anchorElement.html ().replace ("/", ""));
			elements.add (element);
		}
		return elements;
	}
	
	public List<String> retrieveLinks (Document doc, String prependString) {
		ArrayList<String> result = new ArrayList<String> ();
		Elements anchorElements = doc.getElementsByTag("a");
		for (Element anchorElement : anchorElements) {
			String anchorHtml = anchorElement.html ();
			
			if (anchorHtml.equals("Name") ||
				anchorHtml.equals("Last modified") ||
				anchorHtml.equals("Size") ||
				anchorHtml.equals("Description") ||
				anchorHtml.equals("Parent Directory"))
				continue;
			
			result.add (( (prependString == null ? "" : prependString) + anchorElement.attr ("href")));
		}
		return result;
	}
	
	public String makeXML (String rootElementName, Map<String, String> attributes, Elements elements) {
		Document doc = Jsoup.parse ("");
		doc.html ("");
		
		Element rootElement = doc.appendElement(rootElementName);
		if (attributes != null) for (String key : attributes.keySet ()){
			String value = attributes.get (key);
			rootElement.attr (key, value);
		}
		if (elements != null) for (Element element : elements) {
			rootElement.appendChild(element);
		}
		return doc.html ();
	}
	public String makeProjectXML (String projectName, Elements elements) {
		Map<String, String> attributes = new TreeMap<String, String> ();
		attributes.put ("name", projectName);
		return makeXML ("project", attributes, elements);
	}
	public String makeProjectsXML (Elements elements) {
		return makeXML ("projects", null, elements);
	}
	
}