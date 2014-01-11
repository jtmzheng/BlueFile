package com.example.bluefile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.bluefile.fragment.ConfigFragment;


/**
 * 
 * @author Max
 *
 */
public class MainActivity extends FragmentActivity {

	private static FragmentTransaction fragmentTransaction;
	private static FragmentManager fragmentManager;
	
	private static Fragment configFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		configFragment = new ConfigFragment();
		
		fragmentManager = this.getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.commit();
		
		fragmentTransaction.add(R.id.frame, configFragment);
		
		setContentView(R.layout.activity_main);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * 
	 * @param v
	 */
	public void hostTransferStart(View v) {
		Intent intent = new Intent(this, BTHostActivity.class);
        startActivity(intent);
	}

	/**
	 * 
	 * @param v
	 */
	public void connectTransferStart(View v) {

	}

}
