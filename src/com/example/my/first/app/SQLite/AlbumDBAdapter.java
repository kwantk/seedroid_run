package com.example.my.first.app.SQLite;

import java.util.ArrayList;
import java.util.List;

import com.example.my.first.app.Album;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AlbumDBAdapter extends SQLiteOpenHelper {

	

	public AlbumDBAdapter(Context context) {
		super(context, AlbumContract.DATABASE_NAME, null, AlbumContract.DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
						
		Log.d("SQL", AlbumContract.CREATE_ALBUM_TABLE);
		// create column then assign foreign key on it:  FOREIGN KEY (COLUMN) REFERENCES TABLE(COLUMN)
		db.execSQL(AlbumContract.CREATE_ALBUM_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(AlbumContract.DELETE_ALBUM_TABLE);
		onCreate(db);
	}
	
	public void resetAlbum()
	{
		SQLiteDatabase db = this.getWritableDatabase();
				
		db.execSQL(AlbumContract.DELETE_ALBUM_TABLE);
		onCreate(db);
	}
	
	public void addAlbum(Album album)
	{
		ContentValues values = new ContentValues();
		values.put(AlbumContract.AlbumEntry.COL_UID, album.ID);
		values.put(AlbumContract.AlbumEntry.COL_NAME, album.Name);		
		values.put(AlbumContract.AlbumEntry.COL_URI, album.URI);
		values.put(AlbumContract.AlbumEntry.COL_THUMB_URI, album.URIThumb);
				
		SQLiteDatabase db = this.getWritableDatabase();
		if (db.insert(AlbumContract.AlbumEntry.TABLE_NAME, null, values) == -1)
		{
			Log.d("DATABASE", album.Name + " insert failed.");
		}
		else
		{
		
		}
		db.close();
	}
	
	public Album findAlbumByUid(String uid)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Album result = null;
		
		Cursor cursor = db.query(AlbumContract.AlbumEntry.TABLE_NAME, 
				new String[] {
					AlbumContract.AlbumEntry.COL_NAME,  
					AlbumContract.AlbumEntry.COL_URI,  
					AlbumContract.AlbumEntry.COL_THUMB_URI }, 
				AlbumContract.AlbumEntry.COL_UID + "= ?", 
				new String[] { uid },
				null,
				null,
				null,
				null);
		
		if (cursor.moveToFirst())
		{
			// album exists
			Log.d("DATABASE", "Existing Album: " + cursor.getString(0));
			result = new Album(cursor.getString(0),
					cursor.getString(1),
					cursor.getString(2)
					);
			result.ID = uid;			
		}
		else			
		{
			// album doesn't exist
			Log.d("DATABASE", "Album doesn't exist... " + uid);
		}
		
		db.close();
		
		return result;
	}
	
	public List<Album> getAllAlbums()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		List<Album> result = new ArrayList<Album>();
		
		String sqlQuery = "SELECT " 
				+ AlbumContract.AlbumEntry.COL_NAME + "," 
				+ AlbumContract.AlbumEntry.COL_URI + "," 
				+ AlbumContract.AlbumEntry.COL_THUMB_URI
				+ " FROM " + AlbumContract.AlbumEntry.TABLE_NAME;
			
		Cursor cursor = db.rawQuery(sqlQuery, null);
		
		if (cursor.moveToFirst())
		{
			do
			{
			result.add(new Album(cursor.getString(0),
					cursor.getString(1),
					cursor.getString(2)
					));
			} while (cursor.moveToNext());
		}
		
		db.close();
		return result;
	}
	
	public void deleteAlbum(String uid)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(AlbumContract.AlbumEntry.TABLE_NAME,
				AlbumContract.AlbumEntry.COL_UID + "= ?", 
				new String[] {uid});
		db.close();
	}
}


