package com.example.my.first.app.AsyncDownload;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class DownloadTextFileTask extends AsyncTask<String, String, String> {
		
	public AsyncResponse delegate = null;
	

	@Override
	protected String doInBackground(String... uri)
	{
		String result = "";
		try
		{
			result = downloadUrl(uri[0]);
		} catch (IOException e)
		{
			result = "Unable to retrieve file";
		}
		return result;
	}
	
	@Override
	protected void onPostExecute(String result)
	{
		delegate.processFinishFile(result);
	}

	
	private String downloadUrl(String uri) throws IOException
	{
		String result = "";
		StringBuilder content = new StringBuilder();
		
		InputStream input = null;
		
		try
		{
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(100000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
						
			conn.connect();
			int response = conn.getResponseCode();
			int BUFFER_SIZE = 1024;
										
			Log.d("DEBUG", "Response is: " + response);
			
			input = conn.getInputStream();			
			Reader reader = new InputStreamReader(new BufferedInputStream(input), "UTF-8");
			
			
			int dataRead = 0;
			String readString;
			char[] inputBuffer = new char[BUFFER_SIZE];
			
			while ((dataRead = reader.read(inputBuffer)) != -1)
			{
				readString = String.copyValueOf(inputBuffer, 0, dataRead);
				content.append(readString);				
			}
			
			result = content.toString();
			
		} finally
		{
			if (input != null)
			{
				input.close();
			}			
		}
				
		return result;		
	}	
}
