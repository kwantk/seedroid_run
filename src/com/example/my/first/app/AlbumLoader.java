package com.example.my.first.app;

import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class AlbumLoader extends AsyncTaskLoader<List<Album>>
{

	public AlbumLoader(Context context)
	{
		super(context);
	}
	
	@Override
	public List<Album> loadInBackground() {
		// TODO Auto-generated method stub
		
		// Perform query here and add results to return;
		return null;
	}
	

	
	
}
