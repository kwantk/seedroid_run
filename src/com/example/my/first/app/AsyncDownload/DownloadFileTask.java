package com.example.my.first.app.AsyncDownload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class DownloadFileTask extends AsyncTask<String, Void, String>{

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
		delegate.processFinishFile("");
	}

	
	private String downloadUrl(String uri) throws IOException
	{
		String result = "";
		
		InputStream input = null;
		
		try
		{
			URL url = new URL(uri);
			
			String PATH = Environment.getExternalStorageDirectory()+ "/AlbumApp/" ;
			File file = new File(PATH); 
			file.mkdirs();
			
			String filename = Uri.parse(uri).getLastPathSegment();
			File outputFile = new File(file, filename);			
			
			if (!outputFile.exists())
			{
			
				FileOutputStream fos = new FileOutputStream(outputFile);			
				
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
				
				int dataRead = 0;
				
				byte[] inputBuffer = new byte[BUFFER_SIZE];
							
				while ((dataRead = input.read(inputBuffer)) != -1)
				{
					fos.write(inputBuffer, 0, dataRead);		
				}
				fos.flush();
				
				fos.close();
				input.close();
			}
			else
			{
				Log.d("Download", "File already exists, skipping.");
			}
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
