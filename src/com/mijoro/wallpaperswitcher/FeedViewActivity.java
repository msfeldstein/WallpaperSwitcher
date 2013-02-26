package com.mijoro.wallpaperswitcher;

import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FeedViewActivity extends Activity implements LatestImageFetcher.ImageFetcherDelegate {
	private String mFeedUrl;
	private Bitmap mLatestBitmap;
	private Feed mFeed;
	private Button mSetFeedButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_view);
		
		Feed feed = getIntent().getExtras().getParcelable("feed");
		mFeed = feed;
		mFeedUrl = "http://"+feed.urlslug+".tumblr.com/rss";
		
		mSetFeedButton = (Button)findViewById(R.id.setfeedbutton);
		mSetFeedButton.setEnabled(false);
		mSetFeedButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				commitFeedUrl();
			}
		});
		
		LatestImageFetcher imageFetcher = new LatestImageFetcher();
		imageFetcher.delegate = this;
		imageFetcher.fetchFirstImageAt(mFeedUrl);
		System.out.println("Fetching " + mFeedUrl);
		((TextView)findViewById(R.id.blogname)).setText(feed.title);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void commitFeedUrl() {
		SharedPreferences settings = getSharedPreferences("WallpaperSwitcher", 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putString("rssURL", mFeedUrl);
	    editor.putString("feed", mFeed.urlslug);
	    editor.commit();
	    setupRecurringSwitching();
	    
	    WallpaperManager wm = WallpaperManager.getInstance(this);
		try {
			wm.setBitmap(mLatestBitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
		finish();
	}
	
	private void setupRecurringSwitching() {
		SetWallpaperReceiver.setupRecurringAlarm(this);
	}
	
	@Override
	public void errorFetchingFeed() {
		Toast.makeText(FeedViewActivity.this, "Error Fetching Feed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void imageFetched(Bitmap b) {
		mLatestBitmap = b;
		ImageView iv = (ImageView)findViewById(R.id.thumbnail);
		iv.setImageBitmap(b);
		mSetFeedButton.setEnabled(true);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    if(item.getItemId()==android.R.id.home){
	        finish();//finish your activity
	    }
	    return super.onMenuItemSelected(featureId, item);
	}
	

}
