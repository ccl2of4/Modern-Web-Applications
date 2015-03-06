package assign.controllers;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import assign.services.AppServices;

@Controller
public class AppController {

	public static final String PAGE_CONTENT = "pageContent";
	private AppServices appServices;
	
	public void setAppServices (AppServices appServices) {
		this.appServices = appServices;}
	public AppServices getAppServices () {
		return appServices == null ? (appServices = AppServices.sharedAppServices()) : appServices;}
	
	@RequestMapping(value = "", method=RequestMethod.GET)
	public String index (HttpServletRequest request, HttpServletResponse response)
	{
		return "/main.jsp";
	}
	
	@RequestMapping(value = "/search", params = {"type","project","year"}, method=RequestMethod.POST)
	public String search (
		@RequestParam("type") String type,
		@RequestParam("project") String project,
		@RequestParam("year") String year,
		HttpServletRequest request)
	{
		HttpSession session = request.getSession ();
		AppServices appServices = getAppServices ();
	    
		try {
			String urlString = appServices.createUrlStringFromTokens (type, project, year);
			String pageContent = appServices.createStringFromWebPage (urlString);
			session.setAttribute (PAGE_CONTENT, pageContent);
			appServices.addToHistory (session, urlString);
			request.getSession ().setAttribute (AppServices.HISTORY_STRING, appServices.getHistory (session));

		} catch (IllegalArgumentException e) {
			session.setAttribute (PAGE_CONTENT, errorText ("Invalid query."));
		} catch (IOException e) {
			session.setAttribute (PAGE_CONTENT, errorText ("Query returned no result."));
		}
		return "/main.jsp";
	}

	@RequestMapping(value = "/closeSession", method=RequestMethod.POST)
	public String closeSession (HttpServletRequest request) {
		request.getSession ().invalidate ();
		return "/main.jsp";
	}

	public static String errorText (String text) {
		return "<div style='color:red'>" + text + "</div>";
	}
}
