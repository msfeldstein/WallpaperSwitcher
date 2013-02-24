package com.mijoro.wallpaperswitcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;


public class LatestImageFetcher {
	public interface ImageFetcherDelegate {
		public void imageFetched(Bitmap b);
		public void errorFetchingFeed();
	}
	
	private String urlStringToFetch;
	
	public ImageFetcherDelegate delegate;
	Handler h;
	
	public LatestImageFetcher() {
		h = new Handler();
	}
	
	public void fetchFirstImageAt(String urlString) {
		urlStringToFetch = urlString;
		new Thread(feedParser).start();
	}
	
	Runnable feedParser = new Runnable() {
		@Override
		public void run() {
			XmlHandler myXMLHandler = new XmlHandler();
			try {
			    /**
			    * Create a new instance of the SAX parser
			    **/
			    SAXParserFactory saxPF = SAXParserFactory.newInstance();
			    SAXParser saxP = saxPF.newSAXParser();
			    XMLReader xmlR = saxP.getXMLReader();
			    URL url = new URL(urlStringToFetch); // URL of the XML
			    
			    xmlR.setContentHandler(myXMLHandler);
			    xmlR.parse(new InputSource(url.openStream()));
			    
			    
			} catch (MySAXTerminatorException e) {
				System.out.println("Done!");
			} catch (Exception e) {
				e.printStackTrace();
			    System.out.println(e);
			    delegate.errorFetchingFeed();
			}
			if (myXMLHandler.result != null) {
				System.out.println("Found image " + myXMLHandler.result);
				URL url;
				try {
					url = new URL(myXMLHandler.result);
					final Bitmap b = BitmapFactory.decodeStream(url.openStream());
					h.post(new Runnable() {
						@Override
						public void run() {
							delegate.imageFetched(b);
						}
					});
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	};
	
	public class MySAXTerminatorException extends SAXException {
		private static final long serialVersionUID = 1L;
	}
	
	public class XmlHandler extends DefaultHandler {
		
		private String tagName;
		private StringBuilder currentStringData;
		public String result; 
		Pattern pattern;
		Matcher matcher;
		
		public XmlHandler() {
			pattern = Pattern.compile("^(http|https|ftp)\\://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~])*$");
		}
		
	    /**
	     * This will be called when the tags of the XML starts.
	     **/
	    @Override
	    public void startElement(String uri, String localName, String qName,
	            Attributes attributes) throws SAXException {
	        tagName = localName;
	        currentStringData = new StringBuilder();
	    }
	    /**
	     * This will be called when the tags of the XML end.
	     **/
	    @Override
	    public void endElement(String uri, String localName, String qName)
	            throws SAXException {
	    	if (tagName.toLowerCase().equals("description")) {
	    		String res = searchForURL(currentStringData.toString());
	    		if (res != null && !res.isEmpty()) {
	    			result = res;
	    			throw new MySAXTerminatorException();
	    		}
	    	}
	    }
	    /**
	     * This is called to get the tags value
	     **/
	    @Override
	    public void characters(char[] ch, int start, int length)
	            throws SAXException {
	    	if (tagName.toLowerCase().equals("description")) {
	    		currentStringData.append(ch, start, length);
	    	}
	    }
	    
	    private String searchForURL(String s) {
	    	String [] parts = s.split("\"");

	        // Attempt to convert each item into an URL.   
	        for( String item : parts ) try {
	            URL url = new URL(item);
	            // If possible then replace with anchor...
	            System.out.print(url.toString()); 
	            return url.toString();
	        } catch (MalformedURLException e) {
	        	System.out.println("No url in ");
	        }
	        return null;

	    }
	}
}
