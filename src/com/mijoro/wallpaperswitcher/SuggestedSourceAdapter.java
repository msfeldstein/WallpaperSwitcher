package com.mijoro.wallpaperswitcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SuggestedSourceAdapter extends BaseAdapter {
	private String[] feeds = new String[3];
	private String[] feedNames = new String[3];
	private int[] imageIDs = new int[3];
	
	private Context mContext;
	
	public SuggestedSourceAdapter(Context c) {
		mContext = c;
		
		feeds[0] = "geometrydaily";
		feeds[1] = "travelingcolors";
		feeds[2] = "earthlynation";
		
		imageIDs[0] = R.drawable.geometrydaily;
		imageIDs[1] = R.drawable.travelingcolors;
		imageIDs[2] = R.drawable.earthlynation;
		
		feedNames[0] = "Geometry Daily";
		feedNames[1] = "Traveling Colors";
		feedNames[2] = "Earthly Nation";
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return feeds.length;
	}

	@Override
	public Object getItem(int position) {
		Feed feed = new Feed();
		feed.resourceID = imageIDs[position];
		feed.title = feedNames[position];
		feed.urlslug = feeds[position];
		return feed;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (convertView == null) {
			row = LayoutInflater.from(mContext).inflate(R.layout.feed_row, parent, false);
		}
		
		ImageView thumb = (ImageView) row.findViewById(R.id.thumb);
		thumb.setImageResource(imageIDs[position]);
		TextView title = (TextView) row.findViewById(R.id.title);
		title.setText(feedNames[position]);
		
		return row;
	}

}
