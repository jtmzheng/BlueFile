package com.example.bluefile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.bluefile.fragment.AddFileDialog.NoticeDialogListener;
import com.example.bluefile.fragment.BlueToothFragment;
import com.example.bluefile.fragment.FileFragment;


public class BTHostActivity extends FragmentActivity implements NoticeDialogListener {
	
	private BTHostAdapter hostPageAdapter;
	private ViewPager mViewPager;

	private FileFragment fileFrag;
	private BlueToothFragment btFrag;
	
	private Set<BTFile> filesToSend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.host_main);

		fileFrag = new FileFragment();
		btFrag = new BlueToothFragment();

		hostPageAdapter = new BTHostAdapter(getSupportFragmentManager());		
		hostPageAdapter.addFragment(fileFrag);
		hostPageAdapter.addFragment(btFrag);
		
		// Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(hostPageAdapter);
		
		filesToSend = new HashSet<BTFile>();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host_main, menu);
		return true;
	}

	/**
	 * Callback for affirmative for adding file to the list of files to send
	 */
	@Override
	public void onDialogPositiveClick(File file, int id) {
		BTFile btFile = null;
		try {
			btFile = BTFileManager.readFile(file);
		} catch (IOException e) {
			btFile = null;
			e.printStackTrace();
		}
		
		if(btFile != null) {
			filesToSend.add(btFile);
		}
		
		System.out.println(filesToSend);
	}

	/**
	 * Callback for negative for adding file to the list of files to send
	 */
	@Override
	public void onDialogNegativeClick(File file, int id) {

	}
	
	public void updateConnectionsBtnClick(View v) {
		
	}
	
	
	private class BTHostAdapter extends FragmentPagerAdapter {

		private List<Fragment> mFragmentList;

	    public BTHostAdapter(FragmentManager fm) {
	        super(fm);
	        mFragmentList = new ArrayList<Fragment>();
	    }

	    public void addFragment(Fragment fragment) {
	        mFragmentList.add(fragment);
	    }

	    @Override
	    public int getCount() {
	        return mFragmentList.size();
	    }

	    @Override
	    public Fragment getItem(int position) {
	        return mFragmentList.get(position);
	    }
		
	}
}
