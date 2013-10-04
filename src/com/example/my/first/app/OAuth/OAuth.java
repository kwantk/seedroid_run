package com.example.my.first.app.OAuth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class OAuth {
	
	private AccountManager am;
	private Bundle options;
	
	public OAuth(Context context)
	{
		am = AccountManager.get(context);	       
	    options = new Bundle();
	}
	
	
	public void getAccounts()
	{
		Account[] accounts = am.getAccountsByType("com.google");
		for (int i=0; i<accounts.length; i++)
		{
			Log.d("Accounts", accounts[i].name);
		}
	}
	


	
}
