package com.mijoro.wallpaperswitcher;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SelectRSSActivity extends Activity {
	Button mSetFeedButton;
	EditText mUrlField;
	ListView mSuggestedList;
	SuggestedSourceAdapter mAdapter;
	String mFeedUrl;
	Bitmap mLatestBitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_rss);

		mUrlField = (EditText)findViewById(R.id.urlField);
		mUrlField.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					String blogName = v.getText().toString();
					Feed feed = new Feed();
					feed.urlslug = blogName;
					feed.title = blogName;
					Intent i = new Intent(SelectRSSActivity.this, FeedViewActivity.class);
					i.putExtra("feed", feed);
					startActivity(i);
				}
				return false;
			}
		});
		
		mSuggestedList = (ListView)findViewById(R.id.suggestedList);
		mAdapter = new SuggestedSourceAdapter(this);
		mSuggestedList.setAdapter(mAdapter);
		mSuggestedList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Feed feed = (Feed)mAdapter.getItem(position);
				Intent i = new Intent(SelectRSSActivity.this, FeedViewActivity.class);
				i.putExtra("feed", feed);
				startActivity(i);
			}
		});
		
		setupCurrentSyncingRow();
	}
	
	private void setupCurrentSyncingRow() {
		SharedPreferences settings = getSharedPreferences(
				"WallpaperSwitcher", 0);
		String urlString = settings.getString("feed", "");
		if (urlString != null && !urlString.isEmpty()) {
			((TextView)findViewById(R.id.current_syncing_blog)).setText("Syncing: " + urlString);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_select_rss, menu);
		return true;
	}
	
}
