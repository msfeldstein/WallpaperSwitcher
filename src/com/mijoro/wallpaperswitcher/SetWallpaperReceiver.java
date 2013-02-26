package com.mijoro.wallpaperswitcher;

import java.io.IOException;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.PowerManager;

public class SetWallpaperReceiver extends BroadcastReceiver implements
		LatestImageFetcher.ImageFetcherDelegate {
	Context c;
	PowerManager.WakeLock wl;

	@Override
	public void onReceive(Context context, Intent intent) {
		c = context;
		LatestImageFetcher fetcher = new LatestImageFetcher();
		fetcher.delegate = this;
		SharedPreferences settings = c.getSharedPreferences(
				"WallpaperSwitcher", 0);
		String urlString = settings.getString("rssURL", "");
		fetcher.fetchFirstImageAt(urlString);
		PowerManager pm = (PowerManager) c
				.getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "WallpaperSwitcher");
		wl.acquire();
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
		wl.release();
	}

	@Override
	public void errorFetchingFeed() {
		wl.release();
		System.out
				.println("Automatic wallpaper fetcher has an error fetching feed");
	}

	public static void setupRecurringAlarm(Context c) {
		Intent intent = new Intent(c, SetWallpaperReceiver.class);
		intent.setAction("com.mijoro.wallpaperswitcher.CHANGE_WALLPAPER");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(c, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.HOUR, 1);
		AlarmManager alarm = (AlarmManager) c
				.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pendingIntent);
		alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(),
				AlarmManager.INTERVAL_HOUR, pendingIntent);
	}

}
