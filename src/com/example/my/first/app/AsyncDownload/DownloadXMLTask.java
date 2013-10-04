package com.example.my.first.app.AsyncDownload;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.example.my.first.app.Album;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

public class DownloadXMLTask extends AsyncTask<String, String, ArrayList<Album>>
{
	public AsyncResponseXML delegate = null;
	public static final String ns = null;

	@Override
	protected ArrayList<Album> doInBackground(String... params) {
		ArrayList<Album> result = null;
		InputStream input = null;
		
		try
		{
			input = downloadUrl(params[0]);
			result = ProcessXML(input);			
		} catch (IOException e)
		{			
			
		} catch (XmlPullParserException eXml)
		{
		
		}
		return result;
	}

	@Override
	protected void onPostExecute(ArrayList<Album> result)
	{
		delegate.processFinishXML(result);		
	}

	
	private InputStream downloadUrl(String uri) throws IOException
	{
		InputStream input = null;
			
		URL url = new URL(uri);			
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
							
		conn.connect();
		int response = conn.getResponseCode();						
		Log.d("DEBUG", "Response is: " + response);
		if (response == 200)
		{
			input = conn.getInputStream();
		}
		 
		return input;					
	}
	
	private ArrayList<Album> ProcessXML(InputStream xmlData) throws XmlPullParserException, IOException
	{
		ArrayList<Album> albums = new ArrayList<Album>();

		try
		{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(xmlData, ns);
			parser.nextTag();			
			albums = readFeed(parser);			
		}
		finally
		{
		
		}		
		return albums;
		
	}
	
	private ArrayList<Album> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		ArrayList<Album> entries = new ArrayList<Album>();
		Album album = null;
		
		parser.require(XmlPullParser.START_TAG, ns, "feed");
		
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}

			String name = parser.getName();
			
			if (name.equals("entry"))
			{
				album = readEntry(parser);
				entries.add(album);
				Log.d("Album Identified", album.Name + " (" + album.ID + ")");
			}			
			else
			{
				skip(parser);
			}
		}
		return entries;
	}
	
	private Album readEntry(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		Album result = new Album(null, null, null);
		parser.require(XmlPullParser.START_TAG, ns, "entry");
		
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() == XmlPullParser.START_TAG)
			{
				String name = parser.getName();
								
				if (name.equals("title"))
				{
					result.Name = readTag(parser, "title");						
				} 
				else if (name.equals("gphoto:id"))
				{
					result.ID = readTag(parser, "gphoto:id");					
				}
				else if (name.equals("gphoto:numphotos"))
				{
					result.NumberOfPictures = Integer.parseInt(readTag(parser, "gphoto:numphotos"));
				}
				else if (name.equals("media:group"))
				{
					readMediaGroup(parser, result);
				}
				else
				{			
					skip(parser);
				}
			}
		}
		
		return result;
	}
	
	private String readTag(XmlPullParser parser,  String tagName) throws XmlPullParserException, IOException 
	{
		parser.require(XmlPullParser.START_TAG, ns, tagName);
		String result = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, tagName);
		return result;
	}
	 
	
	private void readMediaGroup(XmlPullParser parser, Album album) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, ns, "media:group");
		
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() == XmlPullParser.START_TAG)
			{
				String name = parser.getName();
								
				if (name.equals("media:thumbnail"))
				{
					album.URIThumb = parser.getAttributeValue(ns, "url");
					parser.nextTag();
				}
				else
				{
					skip(parser);					
				}
			}
		}
	}
	
		
	private String readText(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String result = "";
		if (parser.next() == XmlPullParser.TEXT)
		{
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    
	    while (depth != 0) {
	        switch (parser.next()) {	        
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;	        
	        }
	        
	    }
	 }
	
	
}
