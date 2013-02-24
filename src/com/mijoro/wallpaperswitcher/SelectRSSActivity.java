package com.mijoro.wallpaperswitcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
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
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SelectRSSActivity extends Activity implements LatestImageFetcher.ImageFetcherDelegate {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_rss);
		EditText urlField = (EditText)findViewById(R.id.urlField);
		urlField.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					//setFeedUrl(v.getText().toString());
					setFeedUrl("http://geometrydaily.tumblr.com/rss");
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
		LatestImageFetcher imageFetcher = new LatestImageFetcher();
		imageFetcher.delegate = this;
		imageFetcher.fetchFirstImageAt(surl);
		SharedPreferences settings = getSharedPreferences("WallpaperSwitcher", 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putString("rssURL", surl);
	    editor.commit();
	    setupRecurringSwitching();
	}
	
	private void setupRecurringSwitching() {
		Intent intent = new Intent(this, SetWallpaperReceiver.class);
		intent.setAction("com.mijoro.wallpaperswitcher.CHANGE_WALLPAPER");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
		            0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pendingIntent);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, pendingIntent);
	}
	
	private void setLatestImage(Bitmap b) {
		ImageView image = (ImageView)findViewById(R.id.imageView1);
		image.setImageBitmap(b);
		WallpaperManager wm = WallpaperManager.getInstance(this);
		try {
			wm.setBitmap(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void imageFetched(Bitmap b) {
		setLatestImage(b);
	}
	
}
