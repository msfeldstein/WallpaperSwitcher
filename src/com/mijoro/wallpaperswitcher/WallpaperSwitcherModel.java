package com.mijoro.wallpaperswitcher;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class WallpaperSwitcherModel {
	public static String URL_KEY = "rssURL";
	public static String FEED_SLUG_KEY = "feed";
	
	public static String getFeedUrl(Context c) {
		SharedPreferences settings = c.getSharedPreferences(
				"WallpaperSwitcher", 0);
		String urlString = settings.getString(WallpaperSwitcherModel.URL_KEY, "");
		return urlString;
	}
	
	public static void setupRecurringAlarm(Context c) {
		PendingIntent pendingIntent = getPendingIntent(c);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.HOUR, 1);
		AlarmManager alarm = (AlarmManager) c
				.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pendingIntent);
		alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR,
				pendingIntent);
	}

	public static void cancelRecurringAlarm(Context c) {
		PendingIntent pendingIntent = getPendingIntent(c);
		pendingIntent.cancel();
		AlarmManager alarm = (AlarmManager) c
				.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pendingIntent);

		SharedPreferences settings = c.getSharedPreferences(
				"WallpaperSwitcher", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(FEED_SLUG_KEY);
		editor.remove(URL_KEY);
		editor.commit();
	}
	
	private static PendingIntent getPendingIntent(Context c) {
		Intent intent = new Intent(c, SetWallpaperService.class);
		intent.setAction("com.mijoro.wallpaperswitcher.CHANGE_WALLPAPER");
		PendingIntent pendingIntent = PendingIntent.getService(c, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}
}
