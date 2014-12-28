package assign.services;

import java.io.IOException;
import java.util.TreeMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

public class EavesdropServices {
	
	static EavesdropServices singleton;
	
	public static EavesdropServices sharedServices () {
		return singleton == null ? singleton = new EavesdropServices () : singleton;
	}
	
	public Document getDocument (String urlString) throws IOException {
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
	
	public Elements retrieveLinks (Document doc, String prependString) {
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
			
			Element element = new Element (Tag.valueOf ("link"), "");
			element.html ( (prependString == null ? "" : prependString) + anchorElement.attr ("href"));
			elements.add(element);
		}
		return elements;
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