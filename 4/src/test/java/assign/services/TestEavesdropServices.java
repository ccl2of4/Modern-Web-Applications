package assign.services;

import java.util.TreeMap;

import org.jsoup.nodes.*;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestEavesdropServices {

	EavesdropServices services = new EavesdropServices ();
	
	public TestEavesdropServices () {}
	
	@Before
	public void setUp () {
		
	}
	
	@Test
	public void testRetrieveProjectNames () {
		Document mockedDocument = mock (Document.class);
		Elements elements = new Elements ();
		Element mockedElement;
		
		mockedElement = mock (Element.class);
		elements.add (mockedElement);
		when (mockedElement.html ()).thenReturn ("foo");
		
		when (mockedDocument.getElementsByTag ("a")).thenReturn (elements);
		
		elements = services.retrieveProjectNames (mockedDocument);
		
		assertTrue (elements.size() == 1);
	}
	
	@Test
	public void testRetrieveProjectNames2 () {
		Document mockedDocument = mock (Document.class);
		Elements elements = new Elements ();
		Element mockedElement;
		
		for (int i = 0; i < 5; ++i) {
			mockedElement = mock (Element.class);
			elements.add (mockedElement);
			switch (i) {
				case 0:
					when (mockedElement.html ()).thenReturn ("foo"); break;
				case 1:
					when (mockedElement.html ()).thenReturn ("bar"); break;
				case 2:
					when (mockedElement.html ()).thenReturn ("baz"); break;
				case 3:
					when (mockedElement.html ()).thenReturn ("Last modified"); break;
				case 4:
					when (mockedElement.html ()).thenReturn ("Name"); break;
			}
		}
		when (mockedDocument.getElementsByTag ("a")).thenReturn (elements);
		
		elements = services.retrieveProjectNames (mockedDocument);

		assertTrue (elements.size() == 3);
	}
	
	@Test
	public void restRetrieveLinks () {
		Document mockedDocument = mock (Document.class);
		Elements elements = new Elements ();
		Element mockedElement;
		
		mockedElement = mock (Element.class);
		elements.add (mockedElement);
		when (mockedElement.html ()).thenReturn ("foo");
		
		when (mockedDocument.getElementsByTag ("a")).thenReturn (elements);
		
		elements = services.retrieveProjectNames (mockedDocument);
		
		assertTrue (elements.size() == 1);
	}
	
	@Test
	public void testRetrieveLinks2 () {
		Document mockedDocument = mock (Document.class);
		Elements elements = new Elements ();
		Element mockedElement;
		
		for (int i = 0; i < 5; ++i) {
			mockedElement = mock (Element.class);
			elements.add (mockedElement);
			switch (i) {
				case 0:
					when (mockedElement.html ()).thenReturn ("foo"); break;
				case 1:
					when (mockedElement.html ()).thenReturn ("bar"); break;
				case 2:
					when (mockedElement.html ()).thenReturn ("baz"); break;
				case 3:
					when (mockedElement.html ()).thenReturn ("Last modified"); break;
				case 4:
					when (mockedElement.html ()).thenReturn ("Name"); break;
			}
		}
		when (mockedDocument.getElementsByTag ("a")).thenReturn (elements);
		
		elements = services.retrieveProjectNames (mockedDocument);

		assertTrue (elements.size() == 3);
	}
	
	@Test
	public void testMakeXML () {
		TreeMap<String, String> map = new TreeMap<String, String> ();
		map.put ("foo", "bar");
		
		Elements elements = new Elements ();
		Element element = new Element (Tag.valueOf ("baz"), "");
		element.html ("qux");
		elements.add (element);
		
		String result = services.makeXML("foo", map, elements);
		
		assertTrue (result.contains ("foo=\"bar\""));
		assertTrue (result.contains ("baz"));
		assertTrue (result.contains ("qux"));
	}
	
}