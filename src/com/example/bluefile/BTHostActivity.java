package com.example.bluefile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.bluefile.fragment.AddFileDialog.NoticeDialogListener;
import com.example.bluefile.fragment.BlueToothHostFragment;
import com.example.bluefile.fragment.FileFragment;


public class BTHostActivity extends ActionBarActivity implements NoticeDialogListener {

	private BTHostAdapter hostPageAdapter;
	private ViewPager mViewPager;
	private ActionBar mActionBar;
	private TextView mTextView;

	private FileFragment fileFrag;
	private BlueToothHostFragment btFrag;

	private Set<BTFile> filesToSend;
	private BTFile fileToSend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.host_main);

		fileFrag = new FileFragment();
		btFrag = new BlueToothHostFragment();

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

		mActionBar = getSupportActionBar();

		// Specify that tabs should be displayed in the action bar.
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create a tab listener that is called when the user changes tabs.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				mViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabReselected(Tab arg0, FragmentTransaction arg1) {}

			@Override
			public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {}
		};

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When swiping between pages, select the corresponding tab.
				getActionBar().setSelectedNavigationItem(position);
			}
		});

		// Add tabs for files, devices
		mActionBar.addTab(mActionBar.newTab().setText(R.string.action_fileexplorer).setTabListener(tabListener));
		mActionBar.addTab(mActionBar.newTab().setText(R.string.action_btexplorer).setTabListener(tabListener));

		mTextView = (TextView)findViewById(R.id.file);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * Callback for the menu items
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.startHosting:	{
			System.out.println("Test");
			return btFrag.startHostTransfer();
		}
		default: {
			return super.onOptionsItemSelected(item);
		}
		}
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
			fileToSend = btFile;
			mTextView.setText(getResources().getString(R.string.selectedFile) + btFile.fileName);
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
	
	public void startTransferHostBtnClick(View v) {
	
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