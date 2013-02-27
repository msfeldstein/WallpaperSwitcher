package com.mijoro.wallpaperswitcher;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
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
	Typeface thin;
	Typeface light;
	OnSharedPreferenceChangeListener mPrefsListener = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if (WallpaperSwitcherModel.FEED_SLUG_KEY.equals(key)) {
				setupCurrentSyncingRow(false);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_rss);

		thin = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
		light = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

		mUrlField = (EditText) findViewById(R.id.urlField);
		mUrlField.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					String blogName = v.getText().toString();
					Feed feed = new Feed();
					feed.urlslug = blogName;
					feed.title = blogName;
					Intent i = new Intent(SelectRSSActivity.this,
							FeedViewActivity.class);
					i.putExtra("feed", feed);
					startActivity(i);
				}
				return false;
			}
		});
		final TextView suffix = (TextView) findViewById(R.id.suffix_label);
		suffix.addOnLayoutChangeListener(new OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right,
					int bottom, int oldLeft, int oldTop, int oldRight,
					int oldBottom) {
				int endPadding = suffix.getMeasuredWidth()
						+ suffix.getPaddingRight() - 12;
				mUrlField.setPadding(mUrlField.getPaddingLeft(),
						mUrlField.getPaddingTop(), endPadding,
						mUrlField.getPaddingBottom());

			}
		});

		mSuggestedList = (ListView) findViewById(R.id.suggestedList);
		mAdapter = new SuggestedSourceAdapter(this);
		mSuggestedList.setAdapter(mAdapter);
		mSuggestedList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Feed feed = (Feed) mAdapter.getItem(position);
				Intent i = new Intent(SelectRSSActivity.this,
						FeedViewActivity.class);
				i.putExtra("feed", feed);
				startActivity(i);
			}
		});

		// ((TextView)
		// findViewById(R.id.favorite_blog_label)).setTypeface(light);
		// ((TextView) findViewById(R.id.enter_blog_label)).setTypeface(light);

		setupCurrentSyncingRow(true);
	}

	private void setupCurrentSyncingRow(boolean initial) {

		SharedPreferences settings = getSharedPreferences("WallpaperSwitcher",
				0);
		String urlString = settings.getString("feed", "");
		if (initial) {
			settings.registerOnSharedPreferenceChangeListener(mPrefsListener);
			findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					WallpaperSwitcherModel.cancelRecurringAlarm(SelectRSSActivity.this);
				}
			});
		}
		if (urlString != null && !urlString.isEmpty()) {
			findViewById(R.id.current_sync_row).setVisibility(View.VISIBLE);
			TextView blogName = (TextView) findViewById(R.id.current_syncing_blog);
			blogName.setText(urlString);
			blogName.setTypeface(light);
			TextView label = (TextView) findViewById(R.id.currently_syncing);
			label.setTypeface(thin);
		} else {
			findViewById(R.id.current_sync_row).setVisibility(View.GONE);
		}
	}

}
