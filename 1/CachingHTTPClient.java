import java.net.*;
import java.util.*;
import java.io.*;
import java.text.*;

public class CachingHTTPClient {

	private static final String ContentEncodingKey = "Content-Encoding";
	private static final String ContentLengthKey = "Content-Length";
	private static final String ContentTypeKey = "Content-Type";
	private static final String DateKey = "Date";
	private static final String TransferEncodingKey = "Transfer-Encoding";
	private static final String TransferLengthKey = "Transfer-Length";
	private static final String ExpiresKey = "Expires";
	private static final String CacheControlKey = "Cache-Control";
	private static final String ConnectionKey = "Connection";
	private static final String AgeKey = "Age";

	private static final String MetaDataExtension = ".dat";

	public static void main(String args[]) {
				
		if (args.length < 1) {
			System.out.println("Usage:");
			System.out.println("java TestUrlConnection <url>");
			System.exit(0);
		}
		
		URL url = null;
		try {
			url = new URL(args[0]);
		} catch (MalformedURLException e) {
			System.out.println ("Invalid URL.");
			System.exit (1);
		}
		
		assert url != null;
		String path = fileSystemPathForURL (url);
		File file = new File (path);

		HashMap<String,String> metaData = retrieveMetaData (path + MetaDataExtension);
		boolean fileCached = file.exists () && cachedFileValidForUse (metaData);
		
		if (fileCached) {
			addAgeHeader (metaData);
		} else {
			file = retrieveFromNetworkAndCache (url, path);
			metaData = retrieveMetaData (path + MetaDataExtension);
		}

		try { 
			final String start = fileCached ? "***** Serving from the cache – start *****" : "***** Serving from the source – start *****";
			final String end = fileCached ? "***** Serving from the cache – end *****" : "***** Serving from the source – end *****";
			Scanner scanner = new Scanner (new BufferedReader (new FileReader (file)));

			System.out.println (start);

			for (String key : metaData.keySet ()) {
				System.out.println (key + ": " + metaData.get (key));
			}

			System.out.println ();
			while (scanner.hasNextLine()) 
				System.out.println(scanner.nextLine());

			System.out.println (end);
			

		} catch (IOException e) {
			System.out.println ("Error reading file.");
			System.exit (1);
		}
	}

	public static boolean cachedFileValidForUse (HashMap<String,String> metaData) {
		boolean result = true;
		CacheControlParser parser = new CacheControlParser (metaData.get (CacheControlKey));
		SimpleDateFormat dateFormat = new SimpleDateFormat ("EEE, dd MMM yyyy HH:mm:ss z");

		int iteration = 1;
		loop:
		for (;;++iteration) {
			switch (iteration) {
				case 1: {

					result = !parser.isNull ();
					if (!result) break loop;

					break;

				} case 2: {

					result = !parser.noCache ();
					if (!result) break loop;

					break;

				} case 3: {
					
					String expiresString = metaData.get (ExpiresKey);
					if (expiresString != null) {
						Date expires = dateFormat.parse (expiresString, new ParsePosition(0));
						Date today = new Date ();

						result = today.before (expires);
					} else result = false;

					if (!result) break loop;

					break;
				
				} case 4: {

					long maxAge = parser.maxAge ();
					if (maxAge != Integer.MIN_VALUE) {
						maxAge *= 1000;
						String dateString = metaData.get (DateKey);
						Date date = dateFormat.parse (dateString, new ParsePosition (0));
						Date expires = new Date (date.getTime () + maxAge);
						Date today = new Date ();

						result = today.before (expires);
						if (!result) break loop;
					}

					break;

				} case 5: {

					long sMaxAge = parser.sMaxAge ();
					if (sMaxAge != Integer.MIN_VALUE) {
						sMaxAge *= 1000;
						String dateString = metaData.get (DateKey);
						Date date = dateFormat.parse (dateString, new ParsePosition (0));
						Date expires = new Date (date.getTime () + sMaxAge);
						Date today = new Date ();

						result = today.before (expires);
						if (!result) break loop;
					}
					break;

				}
				default: break loop;
			}
		}
		return result;
	}

	public static File retrieveFromNetworkAndCache (URL url, String path) {
		File file = null;
		try {
			file = new File (path);
			URLConnection connection = url.openConnection();
			InputStream input = connection.getInputStream();
			MyFileOutputStream output = new MyFileOutputStream (file);
			byte[] buffer = new byte[4096];
			int n = - 1;

			writeMetaData (connection, path + MetaDataExtension);

			while ( (n = input.read(buffer)) != -1)
			  if (n > 0) output.write(buffer, 0, n);

		} catch (IOException e2) {
			System.out.println("Failed to write cache file.");
			System.exit (1);
		}
		return file;
	}

	public static void writeMetaData (URLConnection connection, String path) {
		try {
		 	HashMap<String, String> map = new HashMap<String, String>();

		 	map.put (ContentEncodingKey, connection.getContentEncoding ());
		 	map.put (ContentLengthKey, Integer.toString (connection.getContentLength()));
			map.put (ContentTypeKey, connection.getContentType());
			map.put (DateKey, connection.getHeaderField("Date"));
			map.put (TransferEncodingKey, connection.getHeaderField("Transfer-Encoding"));
			map.put (TransferLengthKey, connection.getHeaderField("Transfer-Length"));
			map.put (ExpiresKey, connection.getHeaderField("Expires"));
			map.put (CacheControlKey, connection.getHeaderField("Cache-Control"));
			map.put (ConnectionKey, connection.getHeaderField("Connection"));

      		FileOutputStream fileOut = new FileOutputStream(path + MetaDataExtension);
      		ObjectOutputStream out = new ObjectOutputStream(fileOut);
      		out.writeObject(map);
      		out.close();
      		fileOut.close();

    	} catch(IOException i) {
    		System.out.println ("Failed to write metadata file.");
    		System.exit (1);
 		}
	}

	public static HashMap<String,String> retrieveMetaData (String path) {
		HashMap<String, String> map = null;
    	try {
    		FileInputStream fileIn = new FileInputStream(path + MetaDataExtension);
	      	ObjectInputStream in = new ObjectInputStream(fileIn);
      		Object obj = in.readObject ();
      		map = (HashMap<String,String>) obj;
      		in.close();
      		fileIn.close();
    	} catch(IOException i) {
    	} catch(ClassNotFoundException c) {
	      	c.printStackTrace();
	    }
	    return map;
  	}

  	public static void addAgeHeader (HashMap<String, String> metaData) {
 		SimpleDateFormat dateFormat = new SimpleDateFormat ("EEE, dd MMM yyyy HH:mm:ss z");
 		String creationDateString = metaData.get (DateKey);
 		Date creationDate = dateFormat.parse (creationDateString, new ParsePosition(0));
		Date today = new Date ();
		long age = (today.getTime () - creationDate.getTime ())/1000;
		metaData.put (AgeKey, Long.toString(age));
  	}

	public static String fileSystemPathForURL(URL url) {
		final String directory = "/tmp/ccl676/assignment1/";
	  File dir = new File(directory);

	  if (!dir.exists()) {
	    try {
	    	dir.mkdirs();
	    } catch (SecurityException se) {
	    	System.out.println ("Couldn't create cache directory.");
	    	System.exit (1);
	    }
	  }

		return directory + ( url.toString ().replace ('/','-').replace ('.','-').replace (':','-') );
	}
}

class MyFileOutputStream extends FileOutputStream {
	public MyFileOutputStream (File file) throws FileNotFoundException  {
		super (file);
	}
	public void writeln(String s) throws IOException{
		int index = 0;
		while (index < s.length())
			this.write ((byte)(s.charAt(index++)));
		this.write ((byte)'\n');
	}
}

class CacheControlParser extends Object {
	private String[] components;
	private boolean nullComponents;
	public CacheControlParser (String data) {
		super ();
		this.nullComponents = data == null;
		this.components = nullComponents ? new String[]{""} : data.split (",");
	}
	public boolean isNull () {
		return this.nullComponents;
	}
	public boolean noCache () {
		return containsKey ("no-cache");
	}
	public boolean isPublic () {
		return containsKey ("public");
	}
	public int maxAge () {
		return valueForKey ("max-age");
	}
	public int sMaxAge () {
		return valueForKey ("s-max-age");
	}
	private boolean containsKey (String key) {
		for (String str : components)
			if (str.contains (key)) return true;
		return false;
	}
	private int valueForKey (String key) {
		for (String str : components) {
			if (str.contains (key)) {
				String[] tokens = str.split ("=");
				if (tokens.length != 2) break;
				return Integer.parseInt (tokens[1]);
			}
		}
		return Integer.MIN_VALUE;
	}
}