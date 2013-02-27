package com.mijoro.wallpaperswitcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

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
		System.out.println("Mijoro new image fetcher at " + new Date().toLocaleString());
	}
	
	public void fetchFirstImageAt(String tumblrSlug) {
		String urlString = "http://" + tumblrSlug + ".tumblr.com/api/read";
		System.out.println("Mijoro fetch " + urlString + " at " + new Date().toLocaleString());
		urlStringToFetch = urlString;
		new Thread(feedParser).start();
	}
	
	private void sendErrorOnMainThread() {
		h.post(new Runnable() {
			@Override
			public void run() {
				delegate.errorFetchingFeed();
			}
		});
	}
	
	private void sendBitmapOnMainThread(final Bitmap b) {
		h.post(new Runnable() {
			@Override
			public void run() {
				delegate.imageFetched(b);
			}
		});
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
				System.out.println("Done fetching a bitmap!");
			} catch (Exception e) {
				System.out.println("Mijoro Exception at " + new Date().toLocaleString() + e.getMessage());
				e.printStackTrace();
			    sendErrorOnMainThread();
			}
			if (myXMLHandler.result != null) {
				System.out.println(" Mijoro Found image " + myXMLHandler.result + " at " + new Date().toLocaleString());
				URL url;
				try {
					url = new URL(myXMLHandler.result);
					final Bitmap b = BitmapFactory.decodeStream(url.openStream());
					sendBitmapOnMainThread(b);
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
		
		public XmlHandler() {
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
	    	if (tagName.toLowerCase().equals("photo-url")) {
	    		String res = currentStringData.toString();
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
	    	if (tagName.toLowerCase().equals("photo-url")) {
	    		currentStringData.append(ch, start, length);
	    	}
	    }
	}
}
