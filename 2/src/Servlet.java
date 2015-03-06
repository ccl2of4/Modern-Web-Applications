import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.logging.Level;
import java.util.logging.Logger;;

public class Servlet extends HttpServlet {
	
	/* constants */
	static final long serialVersionUID = 1L;
	static final int FORMTYPEINITIAL = 1;
	static final int FORMTYPELOGGEDIN = 2;
	static final int BUTTONTYPELOGIN = 1;
	static final int BUTTONTYPEREGISTER = 2;
	static final int BUTTONTYPESUBMIT = 3;
	static final int BUTTONTYPELOGOUT = 4;
	static final String PROJECTNAME = "Project_2";
	static final String BUTTONTYPE = "buttonType";
	static final String FORMTYPE = "formType";
	static final String CONSTRUCTEDURL = "constructedURL";
	static final String USERHISTORYPATH = "userhistory.txt";
	static final String USERLOGINSPATH = "userInfo.txt";
	
	/* instance variables */
	HashMap<String,String> userLogins;
	HashMap<String,ArrayList<String>> userHistory;
	String currentUsername;
	Logger logger = Logger.getLogger (Servlet.class.getName());
	
	public Servlet () {
		logger.log (Level.CONFIG, "Starting up Servlet.");
		userLogins = retrieveHashMap (USERLOGINSPATH);
		if (userLogins == null) userLogins = new HashMap<String,String>();
		userHistory = retrieveHashMap (USERHISTORYPATH);
		if (userHistory == null) userHistory = new HashMap<String,ArrayList<String>>();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException
	{
		Cookie cookies [] = request.getCookies();
		if (cookies != null) for(Cookie c : cookies) {
			if (c.getName().equals("username"))
				currentUsername = c.getValue();
			if(userLogins.get (currentUsername) == null)
				currentUsername = null;
			break;
		}
		else logger.log (Level.CONFIG, "No cookies found.");
		
		logger.log (Level.INFO, (currentUsername == null ? "No user currently" : currentUsername) + " logged in.");
		
		if (currentUsername == null)
			response.getWriter().print(initialForm ());
		else
			response.getWriter().print(loggedInForm ());
	}
		
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
		{
		int formType = Integer.parseInt (request.getParameter (FORMTYPE));
		
		if (formType == FORMTYPEINITIAL)
			doPostInitialForm (request, response);
		else if (formType == FORMTYPELOGGEDIN)
			doPostLoggedInForm (request, response);
		
	}
	void doPostInitialForm (HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		int buttonType = Integer.parseInt (request.getParameter (BUTTONTYPE));
		String username = request.getParameter ("username");
		String password = request.getParameter ("password");
		String loginError = null;

		loginError = 	username.equals("") ? "Please enter a username." :
						(password.equals("") ? "Please enter a password." : null);
		
		if (loginError == null) {
			
			if (buttonType == BUTTONTYPELOGIN) {
				logger.log (Level.INFO, "Handling login attempt.");
				loginError = checkLogIn(username, password) ? null : "Incorrect login credentials.";}
			else if (buttonType == BUTTONTYPEREGISTER) {
				logger.log (Level.INFO, "Handling registration attempt.");
				loginError = addUser (username, password) ? null : "Username already taken.";}
			
			if (loginError == null) {
				logger.log (Level.INFO, "Login/register successful.");
				currentUsername = username;
				Cookie cookie = new Cookie("username",currentUsername);
				cookie.setMaxAge (1000);
				response.addCookie (cookie);
				response.getWriter().print(loggedInForm ());}}
		
		if (loginError != null) {
			logger.log (Level.WARNING, "Error with login/registration.");
			response.getWriter().print(initialForm (loginError,username,password));}
	}
	void doPostLoggedInForm (HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		int buttonType = Integer.parseInt (request.getParameter (BUTTONTYPE));
		
		if (buttonType == BUTTONTYPESUBMIT) {
			logger.log (Level.INFO, "Handling search submission attempt.");
			String urlString = request.getParameter (CONSTRUCTEDURL);
			addHistory (currentUsername, urlString);
			String html = getHTML (urlString);
			String error = html != null ? null : "There is no page available for the specified parameters";
			if (error == null) {
				logger.log (Level.INFO, "Found result for search attempt.");
				response.getWriter().print (html);}
			else
				logger.log (Level.WARNING, "No page found for given search parameters.");
			response.getWriter().print(loggedInForm (error));
		}
		
		else if (buttonType == BUTTONTYPELOGOUT) {
			logger.log (Level.INFO, "Logging out user.");
			Cookie cookie = new Cookie ("username","");
			cookie.setMaxAge (1000);
			response.addCookie (cookie);
			currentUsername = null;
			response.getWriter().print(initialForm ());
		}
	}
	
	String initialForm () {return initialForm(null, null, null);}
	String initialForm (String errorMessage, String username, String password) {
		String result =
				"<html>" +
				"<head>" +
				"<script type='text/javascript'>" +
				"	function clickedButton(button,form) {" +
				"		document.getElementById('"+BUTTONTYPE+"').value=button;" +
				"		document.getElementById('"+FORMTYPE+"').value=form;" +
				"		document.getElementById('form').submit();" +
				"	}" +
				"</script>" +
				"</head>" +
				"	<body>" +
				"		<form action='/"+PROJECTNAME+"/queryparam' method='post' id='form'/>" +
				"			Username:&nbsp<input type='text' name='username' value='"+ (username != null ? username : "") + "'/>" +
				"			<br>Password:&nbsp<input type='text' name='password' value='"+ (password != null ? password : "") + "'/>" +
				"			&nbsp<input type='hidden' value='' id='"+BUTTONTYPE+"' name='"+BUTTONTYPE+"'/>" +
				"			&nbsp<input type='hidden' value='' id='"+FORMTYPE+"' name ='"+FORMTYPE+"'/>" +
				"			<br><button type='button' onclick='clickedButton("+BUTTONTYPELOGIN+","+FORMTYPEINITIAL+")'>Log in</button>" +
				"			&nbsp<button type='button' onclick='clickedButton("+BUTTONTYPEREGISTER+","+FORMTYPEINITIAL+")'>Register</button>" +
				"		</form>" +
				"		<div style='color:red'>"+ (errorMessage != null ? errorMessage : "") +"</div>" +
				"	</body>" +
				"</html>";
		return result;
	}
	
	String loggedInForm () {return loggedInForm (null);}
	String loggedInForm (String errorMessage) {
		String result =
				"<html>" +
				"	<head>" +
				"<script type='text/javascript'>" +
				"	function clickedButton(button,form) {" +
				"		var type = document.getElementById('type').value;" +
				"		var project = document.getElementById('project').value;" +
				"		var year = document.getElementById('year').value;" +
				"		if (type == 'irclogs') {" +
				"			project = project != '' ? '%23' + project : project;" +
				"			year = '';" +
				"		}" +
				"		document.getElementById('"+CONSTRUCTEDURL+"').value = 'http://eavesdrop.openstack.org/'+type+'/'+project+'/'+year;" +
				"		document.getElementById('"+BUTTONTYPE+"').value=button;" +
				"		document.getElementById('"+FORMTYPE+"').value=form;" +
				"		document.getElementById('form').submit();" +
				"	}" +
				"</script>" +
				"	</head>" +
				"	<body>" +
				"		<div style='color:red'>"+(errorMessage != null ? errorMessage : "")+"</div> " +
				"		Hello, <b>"+currentUsername+"</b>" +
				"		<form action='/"+PROJECTNAME+"/queryparam' method='post' id='form'/>" +
				"			Type:<input type='text' id='type' name='type'/>" +
				"			<br>Project:<input type='text' id='project' name='project'/>" +
				"			<br>Year:<input type='text' id='year' name='year'/>" +
				"			<input type='hidden' id='constructedURL' name='constructedURL' value=''/>" +
				"			&nbsp<input type='hidden' value='' id='"+BUTTONTYPE+"' name='"+BUTTONTYPE+"'/>" +
				"			&nbsp<input type='hidden' value='' id='"+FORMTYPE+"' name ='"+FORMTYPE+"'/>" +
				"			<br><button type='button' onclick='clickedButton("+BUTTONTYPESUBMIT+","+FORMTYPELOGGEDIN+")'>Submit</button>" +
				" 			<br><button type='button' onclick='clickedButton("+BUTTONTYPELOGOUT+","+FORMTYPELOGGEDIN+")'>Log Out</button>" +
				"		</form>" +
				"	History:";
		
		ArrayList<String> currentUserHistory = userHistory.get(currentUsername);
		if (currentUserHistory == null)
			logger.log (Level.SEVERE, "The state for the current user is corrupt. This is a fatal error.");
		
		if (currentUserHistory.size() == 0)
			result += "<br>There's nothing here.";
		
		for (String str : currentUserHistory)
			result += "<br> <a href ="+str+">"+str+"</a>";
		
		result +=
			"	</body>" +
			"</html>";
		
		return result;
	}
	boolean checkLogIn (String username, String password) {
		String checkPassword = userLogins.get (username);
		return checkPassword != null && checkPassword.equals (password);
	}
	boolean addUser (String username, String password) {
		String checkPassword = userLogins.get (username);
		boolean result = checkPassword == null;
		if (result) {
			logger.log (Level.INFO, "Adding user " + username);
			userLogins.put (username, password);
			userHistory.put (username, new ArrayList<String>());
			writeHashMap (userLogins, USERLOGINSPATH);
		}
		else
			logger.log (Level.INFO, "User " + username + " already exists");
		return result;
	}
	boolean addHistory (String username, String urlString) {
		ArrayList<String> list;
		boolean result = (list = userHistory.get (currentUsername)) != null;
		if (result) {
			logger.log (Level.INFO, "History for " + username + " is being added.");
			list.add (urlString);
			writeHashMap (userHistory, USERHISTORYPATH);
		}
		else {
			logger.log (Level.INFO, "History for " + username + " could not be added. Either the user doesn't exist or the user's state is corrupt.");
		}
		return result;
	}
	void clearUserInfo () {
		logger.log (Level.CONFIG, "Clearing user logins and histories.");
		userLogins.clear ();
		userHistory.clear ();
		writeHashMap (userLogins, USERLOGINSPATH);
		writeHashMap (userHistory, USERHISTORYPATH);
	}
	void writeHashMap (HashMap userInfo, String path) {
		try {
      		FileOutputStream fileOut = new FileOutputStream(path);
      		ObjectOutputStream out = new ObjectOutputStream(fileOut);
      		out.writeObject(userInfo);
      		out.close();
      		fileOut.close();
    	} catch(IOException i) {
    		logger.log (Level.SEVERE, "The file at path " + " could not be written. This is a fatal error.");
 		}
	}
	HashMap retrieveHashMap (String path) {
		HashMap result = null;
    	try {
    		FileInputStream fileIn = new FileInputStream(path);
	      	ObjectInputStream in = new ObjectInputStream(fileIn);
      		Object obj = in.readObject ();
      		result = (HashMap) obj;
      		in.close();
      		fileIn.close();
      	} catch(IOException i) {
      		logger.log (Level.CONFIG, "No hashmap found at path " + path + ".");
    	} catch(ClassNotFoundException c) {
    		logger.log (Level.SEVERE, "Invalid data for hashmap found at " + path + ".");
	      	c.printStackTrace();
    	}
    	return result;
	}
	String getHTML(String urlToRead) {
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = null;
		try {
			url = new URL(urlToRead);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			result = "";
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (IOException e) {
			logger.log (Level.INFO, "reading contents of URL at " + urlToRead + " was unsuccessful.");
		}
		return result;
	}
}
