package assign.services;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppServices {
	
	private static AppServices singleton;
	
	public static final String HISTORY_STRING = "history";
	public static final String EAVESDROP_PREFIX = "http://eavesdrop.openstack.org/";
	
	public static AppServices sharedAppServices () {
		return singleton == null ? (singleton = new AppServices ()) : singleton;
	}
	
	public String createStringFromWebPage (String urlString)
			throws IOException
	{	
        URLConnection urlConnection = new URL (urlString).openConnection ();
        InputStream inputStream = urlConnection.getInputStream ();
        Scanner input = new Scanner (new BufferedReader (new InputStreamReader (inputStream)) );
        StringBuilder builder = new StringBuilder();
        
        while(input.hasNext ())
        	builder.append (input.nextLine());
        input.close ();
        
		extractLinks (builder);
		convertRelativeLinksToAbsolute (builder, urlString);
		return builder.toString ();
	}
	
	public String createUrlStringFromTokens (String type, String project, String year)
			throws IllegalArgumentException
	{
		String result;
		
		if (type != null && type.equals ("irclogs")) {
			type = type != null && !type.equals ("") ? type + "/" : "";
			project = project != null && !project.equals ("") ? "%23" + project + "/" : "";
			year = "";}
		
		else if (type != null && type.equals ("meetings")) {
			type = type != null && !type.equals ("") ? type + "/" : "";
			project = project != null && !project.equals ("") ? project + "/" : "";
			year = year != null && !year.equals ("") ? year + "/" : "";}
		
		else throw new IllegalArgumentException ("type must be either irclogs or meetings");
		
		result = EAVESDROP_PREFIX + type + project + year;
		
		return result;
	}
	
	public void addToHistory (HttpSession session, String urlString) {
		ArrayList<String> list = retrieveHistory (session);
		list.add (urlString);
	}
	
	public ArrayList<String> getHistory (HttpSession session) {
		return retrieveHistory (session);
	}
	
	ArrayList<String> retrieveHistory (HttpSession session) {
		ArrayList<String> list = null;
		
		if (session != null) {
			 list = (ArrayList)session.getAttribute (HISTORY_STRING);
			 
			if (list == null) {
				list = new ArrayList<String>();
				session.setAttribute (HISTORY_STRING, list);}}
		
		else list = new ArrayList<String>();
		
		return list;
	}
	
	void extractLinks (StringBuilder contentStrBuilder) {
		Pattern pattern = Pattern.compile("<a href=(.*?)</a>");
		Matcher matcher = pattern.matcher(contentStrBuilder.toString ());
		contentStrBuilder.setLength (0);
		while (matcher.find ()) {
			String sub = matcher.group ();
			if (
					sub.contains ("Name") ||
					sub.contains ("Last modified") ||
					sub.contains ("Size") ||
					sub.contains ("Description") ||
					sub.contains ("Parent Directory")
				) continue;
			contentStrBuilder.append (sub);
			contentStrBuilder.append ("<br>");
		}
	}
	
	void convertRelativeLinksToAbsolute (StringBuilder contentStrBuilder) {
		convertRelativeLinksToAbsolute (contentStrBuilder, EAVESDROP_PREFIX);
	}
	
	void convertRelativeLinksToAbsolute (StringBuilder contentStrBuilder, String prependString) {
		int startIndex = 0;
		int matchIndex;
		final int appendOffset = 9;
		while ( (matchIndex = contentStrBuilder.indexOf ("<a href=\"", startIndex)) != -1 ) {
			contentStrBuilder.insert(matchIndex + appendOffset, prependString);
			startIndex = matchIndex + 1;
		}
	}	
}
