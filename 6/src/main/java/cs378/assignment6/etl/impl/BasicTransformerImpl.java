package cs378.assignment6.etl.impl;

import java.util.Map;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import cs378.assignment6.etl.Transformer;

public class BasicTransformerImpl implements Transformer {

	public static final String EAVESDROP_PREFIX = "http://eavesdrop.openstack.org/meetings/solum_team_meeting/2014/";
	
	public Object transform(Object source) throws Exception {
		Document document = (Document)source;
		Elements elements = retrieveLinks (document, EAVESDROP_PREFIX);
		String xml = makeProjectXML ("solum", elements);
		return xml;
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
