import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class TestServlet {
	Servlet servlet;
	HttpServletRequest mockRequest;
	HttpServletResponse mockResponse;
	
	@Before
	public void setUp () {
		servlet = new Servlet ();
		mockRequest = mock (HttpServletRequest.class);
		mockResponse = mock (HttpServletResponse.class);
	}
	
	@Test
	public void test_clearUserInfo_1 () {
		servlet.clearUserInfo ();
		servlet.addUser ("user", "foo");
		servlet.addUser ("foo", "bar");
		servlet.addUser ("foo", "foofoo");
		servlet.addUser ("bar", "foobar");
		servlet.clearUserInfo ();
		
		assertEquals (servlet.userHistory.size (), 0);
		assertEquals (servlet.userLogins.size (), 0);
	}
	
	@Test
	public void test_addUser_1 () {
		servlet.clearUserInfo ();
		assertEquals (servlet.addUser ("user1", "passw"), true);
		
		assertEquals (servlet.userLogins.size (), 1);
		assertEquals (servlet.userHistory.size (), 1);
	}
	
	@Test
	public void test_addUser_2 () {
		servlet.clearUserInfo ();
		assertEquals (servlet.addUser ("user1", "passw"), true);
		assertEquals (servlet.addUser ("user2", "pass2"), true);
		assertEquals (servlet.addUser ("user3", "fooo"), true);
		assertEquals (servlet.addUser ("user1", "pls work"), true);
		
		assertEquals (servlet.userLogins.size (), 3);
		assertEquals (servlet.userHistory.size (), 3);
	}
	
	@Test
	public void test_addHistory_1 () {
		servlet.clearUserInfo ();
		servlet.addUser ("user", "pass");
		
		assertEquals (servlet.addHistory("user", "foo"), true);
		assertEquals (servlet.userHistory.size (), 1);
		assertNotEquals (servlet.userHistory.get ("user"), null);
		assertEquals (servlet.userHistory.get ("user").size (), 1);
	}
	
	@Test
	public void test_addHistory_2 () {
		servlet.clearUserInfo ();
		servlet.addUser ("user", "pass");
		
		assertEquals (servlet.addHistory("user2", "foo"), false);
		assertEquals (servlet.userHistory.size (), 1);
		assertEquals (servlet.userHistory.get ("user2"), null);
	}
	
	@Test
	public void test_addHistory_3 () {
		servlet.clearUserInfo ();
		servlet.addUser ("user", "pass");
		
		assertEquals (servlet.addHistory("user", "foo"), true);
		assertEquals (servlet.addHistory("user", "foo"), true);
		assertEquals (servlet.addHistory("user", "fooagain"), true);
		assertEquals (servlet.userHistory.size (), 1);
		assertNotEquals (servlet.userHistory.get ("user"), null);
		assertEquals (servlet.userHistory.get ("user").size (), 3);
	}
	
	@Test
	public void test_checkLogIn_1 () {
		servlet.clearUserInfo ();
		servlet.addUser ("user", "pass");
		
		assertEquals (servlet.checkLogIn("user", "pass"), true);
	}
	
	@Test
	public void test_checkLogIn_2 () {
		servlet.clearUserInfo ();
		servlet.addUser ("user", "pass");
		
		assertEquals (servlet.checkLogIn("user", "wrong"), false);
	}
	
	@Test
	public void test_checkLogIn_3 () {
		servlet.clearUserInfo ();
		servlet.addUser ("user", "pass");
		
		assertEquals (servlet.checkLogIn ("other_guy", "foo"), false);
	}
	
	@Test
	public void test_doGet_1 () {
		servlet.clearUserInfo ();
		Cookie[] cookies = new Cookie[]{new Cookie("username", "connor")};
		when (mockRequest.getCookies ()).thenReturn(cookies);
		
		assertEquals (servlet.currentUsername, null);
	}
	
	@Test
	public void test_doGet_2 () {
		servlet.clearUserInfo ();
		servlet.addUser("connor", "foo");
		Cookie[] cookies = new Cookie[]{new Cookie("username", "connor")};
		when (mockRequest.getCookies ()).thenReturn(cookies);
		
		assertEquals (servlet.currentUsername.equals ("connor"), true);
	}
	
	@Test
	public void test_doPost_1 () {
		servlet.clearUserInfo ();
		when (mockRequest.getParameter (Servlet.FORMTYPE)).thenReturn (Integer.toString (Servlet.FORMTYPEINITIAL));
		when (mockRequest.getParameter (Servlet.BUTTONTYPE)).thenReturn (Integer.toString (Servlet.BUTTONTYPELOGIN));
		when (mockRequest.getParameter ("username")).thenReturn ("foo");
		when (mockRequest.getParameter ("password")).thenReturn ("bar");
		
		try {
			servlet.doPost (mockRequest, mockResponse);
		} catch (Exception e) {
			e.printStackTrace ();
		}
		
		assertEquals (servlet.currentUsername, null);
		assertEquals (servlet.userHistory.size (), 0);
		assertEquals (servlet.userLogins.size (), 0);
	}
	
	@Test
	public void test_doPost_2 () {
		servlet.clearUserInfo ();
		when (mockRequest.getParameter (Servlet.FORMTYPE)).thenReturn (Integer.toString (Servlet.FORMTYPEINITIAL));
		when (mockRequest.getParameter (Servlet.BUTTONTYPE)).thenReturn (Integer.toString (Servlet.BUTTONTYPEREGISTER));
		when (mockRequest.getParameter ("username")).thenReturn ("foo");
		when (mockRequest.getParameter ("password")).thenReturn ("bar");
		
		try {
			servlet.doPost (mockRequest, mockResponse);
		} catch (Exception e) {
			e.printStackTrace ();
		}
		
		assertEquals (servlet.currentUsername.equals("foo"), true);
		assertEquals (servlet.userHistory.size (), 1);
		assertEquals (servlet.userLogins.size (), 1);
	}
	
	@Test
	public void test_doPost_3 () {
		servlet.clearUserInfo ();		
		servlet.addUser ("connor", "something");
		
		when (mockRequest.getParameter (Servlet.FORMTYPE)).thenReturn (Integer.toString (Servlet.FORMTYPEINITIAL));
		when (mockRequest.getParameter (Servlet.BUTTONTYPE)).thenReturn (Integer.toString (Servlet.BUTTONTYPEREGISTER));
		when (mockRequest.getParameter ("username")).thenReturn ("connor");
		when (mockRequest.getParameter ("password")).thenReturn ("something");
		
		try {
			servlet.doPost (mockRequest, mockResponse);
		} catch (Exception e) {
			e.printStackTrace ();
		}
		
		assertEquals (servlet.currentUsername, null);
		assertEquals (servlet.userHistory.size (), 1);
		assertEquals (servlet.userLogins.size (), 1);
	}
	
	@Test
	public void test_doPost_4 () {
		servlet.clearUserInfo ();		
		servlet.addUser ("connor", "something");
		
		when (mockRequest.getParameter (Servlet.FORMTYPE)).thenReturn (Integer.toString (Servlet.FORMTYPEINITIAL));
		when (mockRequest.getParameter (Servlet.BUTTONTYPE)).thenReturn (Integer.toString (Servlet.BUTTONTYPELOGIN));
		when (mockRequest.getParameter ("username")).thenReturn ("connor");
		when (mockRequest.getParameter ("password")).thenReturn ("something");
		
		try {
			servlet.doPost (mockRequest, mockResponse);
		} catch (Exception e) {
			e.printStackTrace ();
		}
		
		assertEquals (servlet.currentUsername.equals ("connor"), true);
		assertEquals (servlet.userHistory.size (), 1);
		assertEquals (servlet.userLogins.size (), 1);
	}
	
	@Test
	public void test_doPost_5 () {
		servlet.clearUserInfo ();		
		servlet.addUser ("connor", "something");
		servlet.currentUsername = "connor";
		
		when (mockRequest.getParameter (Servlet.FORMTYPE)).thenReturn (Integer.toString (Servlet.FORMTYPELOGGEDIN));
		when (mockRequest.getParameter (Servlet.BUTTONTYPE)).thenReturn (Integer.toString (Servlet.BUTTONTYPESUBMIT));
		when (mockRequest.getParameter (Servlet.CONSTRUCTEDURL)).thenReturn ("fooooURL");

		try {
			servlet.doPost (mockRequest, mockResponse);
		} catch (Exception e) {
			e.printStackTrace ();
		}
		
		assertNotEquals (servlet.userHistory.get ("connor"), null);
		assertEquals (servlet.userHistory.get ("connor").size (), 1);
	}
}
