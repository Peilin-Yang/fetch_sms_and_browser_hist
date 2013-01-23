package com.example.buttontest;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class ButtonTest extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_button_test);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_button_test, menu);
		return true;
	}
	
	/** Called when the user clicks the Send button */
	public void ShowBrowserHistory(View view) {
	    Intent intent = new Intent(this, BrowserHistory.class);
	    startActivity(intent);
	}
	
	public void ShowFetchedSMS(View view) {
	    Intent intent = new Intent(this, FetchSMS.class);
	    startActivity(intent);
	}
	
}
