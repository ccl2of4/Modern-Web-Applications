package cs378.assignment6.service;

import java.net.URL;

import org.jsoup.Jsoup;

import cs378.assignment6.etl.Reader;

public class EavesdropReaderService implements Reader {

	public Object read(Object source) throws Exception {
		String urlString = ((URL)source).toString ();
		return Jsoup.connect (urlString).get ();

	}
}
