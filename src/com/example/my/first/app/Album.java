package com.example.my.first.app;

import android.os.Parcel;
import android.os.Parcelable;

public class Album  implements Parcelable {
	public enum SERVICE
	{
		PICASA,
		FLICKR,
		LOCAL		
	}
	
	public String Name;	
	public String URI;
	public String URIThumb;
	public String ID;
	public int NumberOfPictures;
	
	
	public Album(String name, String Uri, String UriThumb)
	{				
		this.Name = name;
		this.URI = Uri;
		this.URIThumb = UriThumb;
	}
	
	public Album(Parcel in)
	{
		this.Name = in.readString();
		this.URI = in.readString();
		this.URIThumb = in.readString();
		this.ID = in.readString();
		this.NumberOfPictures = in.readInt();
	}
	
	
	
	public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>()
	{
			public Album createFromParcel(Parcel in)
			{
				return new Album(in);		
			}
			
			public Album[] newArray(int size)
			{
				return new Album[size];
			}
	};
	
	public int describeContents()
	{
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(Name);
		dest.writeString(URI);
		dest.writeString(URIThumb);
		dest.writeString(ID);
		dest.writeInt(NumberOfPictures);
	}
	
	public boolean equals(Album album)
	{
		boolean result = false;	
		result = this.ID.equals(album.ID);
		return result;
	}
	
	public boolean isUpdated(Album album)
	{
		boolean result = false;
		result = this.Name.equals(album.Name);
		result &= this.NumberOfPictures == album.NumberOfPictures;
		result &= this.URI.equals(album.URI);
		result &= this.URIThumb.equals(album.URIThumb);
		
		return !result;
	}
}
