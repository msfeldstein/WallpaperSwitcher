package com.mijoro.wallpaperswitcher;

import java.io.IOException;
import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class SelectRSSActivity extends Activity implements LatestImageFetcher.ImageFetcherDelegate {
	Button mSetFeedButton;
	EditText mUrlField;
	String mFeedUrl;
	Bitmap mLatestBitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_rss);
		mSetFeedButton = (Button) findViewById(R.id.setfeedbutton);
		mSetFeedButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				commitFeedUrl();
			}
		});
		mUrlField = (EditText)findViewById(R.id.urlField);
		mUrlField.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					//setFeedUrl();
					String blogName = v.getText().toString();
					mFeedUrl = "http://"+blogName+".tumblr.com/rss";
					setFeedUrl(mFeedUrl);
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
	}
	
	private void commitFeedUrl() {
		SharedPreferences settings = getSharedPreferences("WallpaperSwitcher", 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putString("rssURL", mFeedUrl);
	    editor.commit();
	    setupRecurringSwitching();
	    
	    WallpaperManager wm = WallpaperManager.getInstance(this);
		try {
			wm.setBitmap(mLatestBitmap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		mLatestBitmap = b;
		mSetFeedButton.setVisibility(View.VISIBLE);
	}

	@Override
	public void imageFetched(Bitmap b) {
		setLatestImage(b);
	}

	@Override
	public void errorFetchingFeed() {
		runOnUiThread(new ErrorShower());
	}
	
	private class ErrorShower implements Runnable {
		@Override
		public void run() {
			Toast.makeText(SelectRSSActivity.this, "Error Fetching Feed", Toast.LENGTH_SHORT).show();
			mUrlField.requestFocusFromTouch();
			mUrlField.selectAll();
		}
		
	}
	
}
