package com.example.my.first.app;

import java.io.File;

import com.example.my.first.app.R.id;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumAdapter extends ArrayAdapter<Album> {
	Context context;
	int layoutResourceId;
	Album[] data = null;
	
	public AlbumAdapter(Context context, int layoutResourceId, Album[] data)
	{
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		AlbumHolder holder = null;
		
		if (row == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new AlbumHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.albumImage);
			holder.txtTitle = (TextView) row.findViewById(id.albumTitle);
			holder.txtDescr = (TextView) row.findViewById(id.albumDescription);			
			
			row.setTag(holder);			
		}
		else
		{
			holder = (AlbumHolder) row.getTag();
		}
			
		Album album = data[position];
		holder.txtTitle.setText(album.Name);
		holder.txtTitle.setSelected(true);		
		holder.txtDescr.setText(album.ID);		
		
		String path = Environment.getExternalStorageDirectory() + "/AlbumApp/" + Uri.parse(album.URIThumb).getLastPathSegment();
		File file = new File(path);
		
		if (file.exists())
		{
			holder.imgIcon.setImageURI(Uri.fromFile(file));
		}
		
		return row;
	}
	
	private static class AlbumHolder
	{
		ImageView imgIcon;
		TextView txtTitle;
		TextView txtDescr;
	}
	
}
