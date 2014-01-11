package com.example.bluefile.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.bluefile.R;
import com.example.bluefile.view.ConfigView;

public class ConfigFragment extends Fragment {

	@SuppressWarnings("unused")
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LinearLayout layout = new LinearLayout(this.getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout configLayout = new LinearLayout(this.getActivity());
		configLayout.setGravity(Gravity.CENTER);

		ConfigView configView = new ConfigView(this.getActivity());
		configLayout.addView(configView);

		view = inflater.inflate(R.layout.config_view, layout, true);

		layout.addView(configLayout);

		return layout;
	}


}
