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

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SelectRSSActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_rss);
		EditText urlField = (EditText)findViewById(R.id.urlField);
		urlField.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					setFeedUrl(v.getText().toString());
				}
				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_select_rss, menu);
		return true;
	}
	
	private void setFeedUrl(String surl) {
		new Thread(feedParser).start();
	}
	
	private void setLatestImage(Bitmap b) {
		ImageView image = (ImageView)findViewById(R.id.imageView1);
		image.setImageBitmap(b);
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
			    URL url = new URL("http://geometrydaily.tumblr.com/rss"); // URL of the XML
			    
			    xmlR.setContentHandler(myXMLHandler);
			    xmlR.parse(new InputSource(url.openStream()));
			    
			    
			} catch (MySAXTerminatorException e) {
				System.out.println("Done!");
			} catch (Exception e) {
				e.printStackTrace();
			    System.out.println(e);
			}
			if (myXMLHandler.result != null) {
				System.out.println("Found image " + myXMLHandler.result);
				URL url;
				try {
					url = new URL(myXMLHandler.result);
					final Bitmap b = BitmapFactory.decodeStream(url.openStream());
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setLatestImage(b);
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
