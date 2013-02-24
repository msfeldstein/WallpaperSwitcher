package com.mijoro.wallpaperswitcher;

import java.io.IOException;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

public class SetWallpaperReceiver extends BroadcastReceiver implements LatestImageFetcher.ImageFetcherDelegate {
	Context c;
	@Override
	public void onReceive(Context context, Intent intent) {
		c = context;
		LatestImageFetcher fetcher = new LatestImageFetcher();
		fetcher.delegate = this;
		SharedPreferences settings = c.getSharedPreferences("WallpaperSwitcher", 0);
	    String urlString = settings.getString("rssURL", "");
		fetcher.fetchFirstImageAt(urlString);
	}

	@Override
	public void imageFetched(Bitmap b) {
		WallpaperManager wm = WallpaperManager.getInstance(c);
		try {
			wm.setBitmap(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void errorFetchingFeed() {
		System.out.println("Automatic wallpaper fetcher has an error fetching feed");
	}

}
