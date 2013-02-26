package com.mijoro.wallpaperswitcher;

import android.os.Parcel;
import android.os.Parcelable;

public class Feed implements Parcelable {
	public String urlslug;
	public String title;
	public int resourceID;
	
	public Feed() {}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(urlslug);
		dest.writeString(title);
		dest.writeInt(resourceID);
	}
	
	public static final Parcelable.Creator<Feed> CREATOR = new Parcelable.Creator<Feed>() {

		@Override
		public Feed createFromParcel(Parcel source) {
			Feed f = new Feed();
			f.urlslug = source.readString();
			f.title = source.readString();
			f.resourceID = source.readInt();
			return f;
		}

		@Override
		public Feed[] newArray(int size) {
			return new Feed[size];
		}
	};
}