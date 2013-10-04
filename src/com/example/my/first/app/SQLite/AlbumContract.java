package com.example.my.first.app.SQLite;

import android.provider.BaseColumns;

public final class AlbumContract 
{
	public AlbumContract() {}
	
	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "pictureAlbum.db";
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	
	public static final String CREATE_ALBUM_TABLE =
			"CREATE TABLE " + AlbumEntry.TABLE_NAME + " (" +
		AlbumEntry._ID + " INTEGER PRIMARY KEY," +
		AlbumEntry.COL_UID + TEXT_TYPE + COMMA_SEP +
		AlbumEntry.COL_NAME + TEXT_TYPE + COMMA_SEP + 
		AlbumEntry.COL_URI + TEXT_TYPE + COMMA_SEP +
		AlbumEntry.COL_THUMB_URI + TEXT_TYPE +
		" )";
		
	public static final String DELETE_ALBUM_TABLE = 
			"DROP TABLE IF EXISTS " + AlbumEntry.TABLE_NAME;

	public static abstract class AlbumEntry implements BaseColumns 
	{
		public static final String TABLE_NAME = "ALBUM";
		public static final String COL_UID = "album_uid";
		public static final String COL_NAME = "album_name";
		public static final String COL_URI = "album_uri";
		public static final String COL_THUMB_URI = "album_thumb_uri";
	}
}