package assign.controllers;

import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import assign.services.AppServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class TestAppController {
	AppController appController;
	AppServices mockedAppServices;
	HttpServletRequest mockedRequest;
	HttpSession mockedSession;
	
	@Before
	public void setUp () {
		appController = new AppController ();
		mockedAppServices = mock (AppServices.class);
		mockedRequest = mock (HttpServletRequest.class);
		mockedSession = mock (HttpSession.class);
		appController.setAppServices(mockedAppServices);
		when (mockedRequest.getSession ()).thenReturn(mockedSession);
	}
	
	@Test
	public void test_search_1 () {
		when (mockedAppServices.createUrlStringFromTokens("", "", "")).thenThrow(new IllegalArgumentException());
		appController.search ("","","",mockedRequest);
		verify (mockedSession).setAttribute(AppController.PAGE_CONTENT, AppController.errorText ("Invalid query."));
	}
	
	public void test_search_2 () throws Exception {
		when (mockedAppServices.createUrlStringFromTokens("meetings", "barbican", "2013")).thenReturn("correct URL");
		when (mockedAppServices.createStringFromWebPage("correct URL")).thenReturn ("correct web page");
		appController.search ("meetings","barbican","2013",mockedRequest);
		verify (mockedAppServices).createUrlStringFromTokens("meetings", "barbican", "2013");
		verify (mockedAppServices).createStringFromWebPage("correct URL");
		verify (mockedAppServices).addToHistory(mockedSession, "correct URL");
		verify (mockedSession).setAttribute(AppController.PAGE_CONTENT, "correct web page");
	}
	
	public void test_search_3 () throws Exception {
		when (mockedAppServices.createUrlStringFromTokens("meetings", "barbican", "some year that doesn't work")).thenReturn("correct URL");
		when (mockedAppServices.createStringFromWebPage("correct URL")).thenThrow (new IOException ());
		appController.search ("meetings","barbican","some year that doesn't work",mockedRequest);
		verify (mockedAppServices).createUrlStringFromTokens("meetings", "barbican", "some year that doesn't work");
		verify (mockedAppServices).createStringFromWebPage("correct URL");
		verify (mockedAppServices, never()).addToHistory(mockedSession, "correct URL");
		verify (mockedSession).setAttribute(AppController.PAGE_CONTENT, AppController.errorText ("Query returned no result."));
	}
}
