package com.example.bluefile;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;

import com.example.bluefile.fragment.BlueToothClientFragment;

public class BTClientActivity extends ActionBarActivity {

	private BTClientAdapter clientPageAdapter;
	private BlueToothClientFragment btFrag;
	private ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.client_main);
		btFrag = new BlueToothClientFragment();
		
		clientPageAdapter = new BTClientAdapter(getSupportFragmentManager());		
		clientPageAdapter.addFragment(btFrag);

		// Set up the ViewPager, attaching the adapter.
		mViewPager = (ViewPager) findViewById(R.id.pagerClient);
		mViewPager.setAdapter(clientPageAdapter);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}
	
	public void clientTransferStartBtnClick(View v) {
		btFrag.startClientRecieve();
	}
	
	private class BTClientAdapter extends FragmentPagerAdapter {
		private List<Fragment> mFragmentList;

		public BTClientAdapter(FragmentManager fm) {
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
