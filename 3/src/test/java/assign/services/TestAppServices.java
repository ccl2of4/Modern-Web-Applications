package assign.services;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpSession;

public class TestAppServices {
	AppServices appServices;
	HttpSession mockSession;
	
	@Before
	public void setUp () {
		appServices = AppServices.sharedAppServices ();
		mockSession = mock (HttpSession.class);
	}
	
	@Test
	public void test_createUrlStringFromTokens () {
		String result = null;
		
		try {
			result = null;
			result = appServices.createUrlStringFromTokens ("foo", "bar", "baz");
			assertTrue (false);
		} catch (IllegalArgumentException e) {
			assertTrue (true);
		} catch (Exception e) {
			assertTrue (false);
		} assertNull (result);
		
		try {
			result = null;
			result = appServices.createUrlStringFromTokens ("meetingss", "barbican", "2013");
			assertTrue (false);
		} catch (IllegalArgumentException e) {
			assertTrue (true);
		} catch (Exception e) {
			assertTrue (false);
		} assertNull (result);
		
		try {
			result = null;
			result = appServices.createUrlStringFromTokens (null, null, null);
			assertEquals (true, false);
		} catch (IllegalArgumentException e) {
			assertEquals (true, true);
		} catch (Exception e) {
			assertEquals (true, false);
		} assertNull (result);
		
		try {
			result = null;
			result = appServices.createUrlStringFromTokens ("irclogs", "", "");
		} catch (Exception e) {
			assertTrue (false);
		} assertEquals (AppServices.EAVESDROP_PREFIX + "irclogs/", result);
		
		try {
			result = null;
			result = appServices.createUrlStringFromTokens ("irclogs", null, null);
		} catch (Exception e) {
			assertTrue (false);
		} assertEquals (AppServices.EAVESDROP_PREFIX + "irclogs/", result);
		
		try {
			result = null;
			result = appServices.createUrlStringFromTokens ("irclogs", "heat", "2014");
		} catch (Exception e) {
			assertTrue (false);
		} assertEquals (AppServices.EAVESDROP_PREFIX + "irclogs/%23heat/", result);
		
		try {
			result = null;
			result = appServices.createUrlStringFromTokens ("irclogs", "heat", "");
		} catch (Exception e) {
			assertTrue (false);
		} assertEquals (AppServices.EAVESDROP_PREFIX + "irclogs/%23heat/", result);
		
		try {
			result = null;
			result = appServices.createUrlStringFromTokens ("irclogs", "heat", null);
		} catch (Exception e) {
			assertTrue (false);
		} assertEquals (AppServices.EAVESDROP_PREFIX + "irclogs/%23heat/", result);
		
		try {
			result = null;
			result = appServices.createUrlStringFromTokens ("meetings", "barbican", "2013");
		} catch (Exception e) {
			assertTrue (false);
		} assertEquals (AppServices.EAVESDROP_PREFIX + "meetings/barbican/2013/", result);
		
		try {
			result = null;
			result = appServices.createUrlStringFromTokens ("meetings", "barbican", "");
		} catch (Exception e) {
			assertTrue (false);
		} assertEquals (AppServices.EAVESDROP_PREFIX + "meetings/barbican/", result);
		
		try {
			result = null;
			result = appServices.createUrlStringFromTokens ("meetings", "barbican", null);
		} catch (Exception e) {
			assertTrue (false);
		} assertEquals (AppServices.EAVESDROP_PREFIX + "meetings/barbican/", result);
	}
	
	@Test
	public void test_addToHistory () {
		ArrayList<String> tempHistory = new ArrayList<String>();
		when (mockSession.getAttribute (AppServices.HISTORY_STRING)).thenReturn(tempHistory);
		
		String[] strings = {"foo", "bar", "baz", "quxx", "foo"};
		
		for (String str : strings)
			appServices.addToHistory (mockSession, str);
		
		assertEquals (5, tempHistory.size ());
		
		for (int i = 0; i < strings.length; ++i)
			assertEquals (strings[i], tempHistory.get(i));
	}
	
	@Test
	public void test_retrieveHistory () {
		ArrayList<String> tempHistory = new ArrayList<String>();
		ArrayList<String> result;
		when (mockSession.getAttribute (AppServices.HISTORY_STRING)).thenReturn(tempHistory);
		
		tempHistory.addAll(Arrays.asList ("foo", "bar"));
		
		result = appServices.retrieveHistory (mockSession);
		assertEquals (tempHistory, result);
	}
	
	@Test
	public void test_extractLinks () {
		String page;
		String resultPage;
		StringBuilder builder;
		
		page = "<a href=\"foo\"></a>";
		resultPage = "<a href=\"foo\"></a><br>";
		builder = new StringBuilder (page);
		appServices.extractLinks(builder);
		assertEquals(resultPage, builder.toString());
		
		page = "<a href=\"foo\">foo bar baz quxx</a>";
		resultPage = "<a href=\"foo\">foo bar baz quxx</a><br>";
		builder = new StringBuilder (page);
		appServices.extractLinks(builder);
		assertEquals(resultPage, builder.toString());
		
		page = "<a href=\"\"></a>";
		resultPage = "<a href=\"\"></a><br>";
		builder = new StringBuilder (page);
		appServices.extractLinks(builder);
		assertEquals(resultPage, builder.toString());
		
		page = "foo<a href=\"foo\"></a>bar<a href=\"foo\">baz</a><a href=\"foo\"></a>quxx";
		resultPage = "<a href=\"foo\"></a><br><a href=\"foo\">baz</a><br><a href=\"foo\"></a><br>";
		builder = new StringBuilder (page);
		appServices.extractLinks(builder);
		assertEquals(resultPage, builder.toString());
	}
	
	@Test
	public void test_convertRelativeLinksToAbsolute () {
		String page;
		String resultPage;
		StringBuilder builder;
		
		page = "<a href=\"foo\"></a>";
		resultPage = "<a href=\"" + AppServices.EAVESDROP_PREFIX + "foo\"></a>";
		builder = new StringBuilder (page);
		appServices.convertRelativeLinksToAbsolute(builder);
		assertEquals(resultPage, builder.toString());
		
		page = "<a href=\"foo\">foo bar baz quxx</a>";
		resultPage = "<a href=\"" + AppServices.EAVESDROP_PREFIX + "foo\">foo bar baz quxx</a>";
		builder = new StringBuilder (page);
		appServices.convertRelativeLinksToAbsolute(builder);
		assertEquals(resultPage, builder.toString());
		
		page = "<a href=\"\"></a>";
		resultPage = "<a href=\"" + AppServices.EAVESDROP_PREFIX + "\"></a>";
		builder = new StringBuilder (page);
		appServices.convertRelativeLinksToAbsolute(builder);
		assertEquals(resultPage, builder.toString());
		
		page = "foo<a href=\"foo\"></a>bar<a href=\"foo\">baz</a><a href=\"foo\"></a>quxx";
		resultPage = "foo<a href=\"" + AppServices.EAVESDROP_PREFIX + "foo\"></a>bar<a href=\"" + AppServices.EAVESDROP_PREFIX + "foo\">baz</a><a href=\"" + AppServices.EAVESDROP_PREFIX + "foo\"></a>quxx";
		builder = new StringBuilder (page);
		appServices.convertRelativeLinksToAbsolute(builder);
		assertEquals(resultPage, builder.toString());
	}
}