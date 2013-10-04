package com.example.my.first.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.my.first.app.AsyncDownload.AsyncResponse;
import com.example.my.first.app.AsyncDownload.AsyncResponseXML;
import com.example.my.first.app.AsyncDownload.DownloadFileTask;
import com.example.my.first.app.AsyncDownload.DownloadXMLTask;
import com.example.my.first.app.OAuth.AuthPreferences;
import com.example.my.first.app.OAuth.OAuth;
import com.example.my.first.app.SQLite.AlbumDBAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

@SuppressWarnings("unused")
public class MainActivity extends Activity implements AsyncResponseXML, AsyncResponse {
	
	public final static String EXTRA_MESSAGE = "com.example.my.first.app.MESSAGE";
	
	private AlbumDBAdapter mDBAlbumHelper;
	
	private AuthPreferences authPreferences;
	private AccountManager accountManager;
	
	private final String SCOPE = "https://picasaweb.google.com/data/";
	private final String ACC_TYPE = "com.google";
	private static final int AUTHORIZATION_CODE = 1993;
	private static final int ACCOUNT_CODE = 1601;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        mDBAlbumHelper = new AlbumDBAdapter(getBaseContext());
        
        accountManager = AccountManager.get(this);
        authPreferences = new AuthPreferences(this);
        
        invalidateToken();
        
        Log.e("AUTHENTICATE", "start");
        
        if (authPreferences.getUser() != null && authPreferences.getToken() != null)
        {
        	// authenticated
        	doAuthenticate();
        }
        else
        {
        	Log.e("AUTHENTICATE", "failed");
        	chooseAccount();
        }
    }
    
    
    private void doAuthenticate()
    {
    	Log.e("Auth", authPreferences.getToken());
    }
    
    private void chooseAccount()
    {
    	
    	Intent intent = AccountManager.newChooseAccountIntent(null, null, new String[] {ACC_TYPE}, false, null, null, null, null);
    	startActivityForResult(intent, ACCOUNT_CODE);
    }
    
    private void requestToken() {
		Account userAccount = null;
		String user = authPreferences.getUser();
		for (Account account : accountManager.getAccountsByType(ACC_TYPE)) {
			if (account.name.equals(user)) {
				userAccount = account;
 
				break;
			}
		}
 
		accountManager.getAuthToken(userAccount, "oauth2:" + SCOPE, null, this,
				new OnTokenAcquired(), null);
	}
    
	private void invalidateToken() {
		AccountManager accountManager = AccountManager.get(this);
		accountManager.invalidateAuthToken(ACC_TYPE,
				authPreferences.getToken());
 
		authPreferences.setToken(null);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
 
		if (resultCode == RESULT_OK) {
			if (requestCode == AUTHORIZATION_CODE) {
				requestToken();
			} else if (requestCode == ACCOUNT_CODE) {
				String accountName = data
						.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				authPreferences.setUser(accountName);
 
				// invalidate old tokens which might be cached. we want a fresh
				// one, which is guaranteed to work
				invalidateToken();
 
				requestToken();
			}
		}
	}
	
	
	private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
		 
		public void run(AccountManagerFuture<Bundle> result) {
			try {
				Bundle bundle = result.getResult();
 
				Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if (launch != null) {
					startActivityForResult(launch, AUTHORIZATION_CODE);
				} else {
					String token = bundle
							.getString(AccountManager.KEY_AUTHTOKEN);
 
					authPreferences.setToken(token);
 
					// authenticated -- now what?
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
 
	
	public void processFinishXML(ArrayList<Album> output)
	{
		Log.d("ALBUM LOG", output.size() + "");
		for (int i=0; i<output.size(); i++)
		{
			Album album = output.get(i);
			String url = album.URIThumb;
			
			Album result = mDBAlbumHelper.findAlbumByUid(album.ID);			
			if (result == null)
			{
				Log.d("DATABASE", "ADDING" + album.ID + " - " +album.Name);
				mDBAlbumHelper.addAlbum(album);
			}
			
			DownloadFileTask dft = new DownloadFileTask();
			dft.delegate = this;
			dft.execute(url);

		}
				
    	Intent intent = new Intent(this, DisplayAlbumActivity.class);
    	intent.putParcelableArrayListExtra(EXTRA_MESSAGE, output);
		startActivity(intent);
	}
	
	public void processFinishFile(String result)
	{
		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void sendMessage(View view) {
//    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	EditText editText = (EditText) findViewById(R.id.edit_message);
    	String message = editText.getText().toString();
    	
//    	this sends message to the next intention
//    	intent.putExtra(EXTRA_MESSAGE, message);
//    	startActivity(intent);
    	    	    	

    	String stringURL = "https://picasaweb.google.com/data/feed/api/user/default?access_token=" + authPreferences.getToken();
    	
    	ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	if (networkInfo != null && networkInfo.isConnected())
    	{
    		DownloadXMLTask dtf = new DownloadXMLTask();
    		dtf.delegate = this;
    		dtf.execute(stringURL);
    	}
    	else    		
    	{
    		Log.d("ERROR", "No Network Connection.");
    	}

    	// toast
    	/**
    	Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		
		Toast toast = Toast.makeText(context, "No User Account.  Please enter a username.", duration);
		toast.show();
		**/
    }   
    
    public void clearCache(View view)
    {
    	Log.d("Clear Cache", "START");
    	
    	String path = Environment.getExternalStorageDirectory()+ "/AlbumApp/";
    	File file = new File(path);
    	File fileList[] = file.listFiles();

    	boolean isDeleted = false;
    	for (int i=0; i<fileList.length; i++)
    	{
    		isDeleted = fileList[i].delete();
    		if (isDeleted)
    		{
    			Log.d("Delete", fileList[i].getPath());
    		}
    		else
    		{
    			Log.d("Delete Error", fileList[i].getPath());
    		}
    	}    	
    }
    
    
    public void getUserAccounts(View view)
    {
    	Log.d("Accounts", "Trigger");
    	
    	Account[] accounts = accountManager.getAccountsByType(ACC_TYPE);
    	
    	if (accounts.length!=0)
    	{
	    	for (int i=0; i<accounts.length; i++)
	    	{
	    		Log.d("Accounts", accounts[i].name);
	    	}
    	}
    	else    		
    	{
    		Log.e("Accounts", "No Accounts on device");
    	}
    	
    }
    
    
    public void resetDatabase(View view)
    {
    	mDBAlbumHelper.resetAlbum();
    }
    
    public static boolean isDownloadManagerAvailable(Context context)
    {
	  try {
	        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
	            return false;
	        }
	        Intent intent = new Intent(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
	        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
	                PackageManager.MATCH_DEFAULT_ONLY);
	        return list.size() > 0;
	    } catch (Exception e) {
	        return false;
	    }
    }
}
