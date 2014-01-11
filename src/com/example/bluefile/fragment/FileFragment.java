package com.example.bluefile.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bluefile.R;
import com.example.bluefile.view.BlueToothFileView;

/**
 * 
 * @author Max
 *
 */
public class FileFragment extends Fragment {

	private static final String ROOT_DIR = "/";

	private AddFileDialog addFileFrag;
	
	private List<String> itemsList = null;
	private List<String> pathsList = null;
	
	private TextView currentPathTextView;
	private ListView currentFileListView;
	
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LinearLayout layout = new LinearLayout(this.getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout fileLayout = new LinearLayout(this.getActivity());
		fileLayout.setGravity(Gravity.CENTER);

		BlueToothFileView blueToothView = new BlueToothFileView(this.getActivity());
		fileLayout.addView(blueToothView);

		view = inflater.inflate(R.layout.file_view, layout, true); 
		
		addFileFrag = new AddFileDialog();

		layout.addView(fileLayout);

		return layout;
	}
	
	@Override 
	public void onStart() {
		super.onStart();
		
		currentPathTextView = (TextView)view.findViewById(R.id.path);
		currentFileListView = (ListView)view.findViewById(R.id.list);
				
		itemsList = new ArrayList<String>();
		pathsList = new ArrayList<String>();
		
		this.getDirectoryContents(ROOT_DIR);
		
		currentFileListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final File file = new File(pathsList.get(position));
				if (file.isDirectory())	{
					if(file.canRead()) {
						getDirectoryContents(pathsList.get(position));
					}
				} else {
					if(file.canRead()) {
						addFileFrag.setFileToAdd(file);
						addFileFrag.show(getFragmentManager(), "AddFileDialog");
						
						// Add a hook
						addFileFrag.onDismiss(new DialogInterface() {
							@Override
							public void cancel() {
								addFileFrag.setFileToAdd(null);
							}

							@Override
							public void dismiss() {
								System.out.println("Added a file!");
								addFileFrag.setFileToAdd(null);
							}
							
						});
					}
				}
			}
		});
	}

	private void getDirectoryContents(String dirPath)
	{
		currentPathTextView.setText("Location: " + dirPath);
		itemsList = new ArrayList<String>();
		pathsList = new ArrayList<String>();
		
		File f = new File(dirPath);
		File[] files = f.listFiles();

		if(!dirPath.equals(ROOT_DIR)) {
			itemsList.add(ROOT_DIR);
			pathsList.add(ROOT_DIR);
			itemsList.add("../");
			pathsList.add(f.getParent());
		}
		
		for(File file : files) {
			pathsList.add(file.getPath());
			if(file.isDirectory()) {
				itemsList.add(file.getName() + "/"); 
			} else {
				itemsList.add(file.getName());
			}
		}
		

		ArrayAdapter<String> fileList = new ArrayAdapter<String>(view.getContext(), R.layout.row, itemsList);
		currentFileListView.setAdapter(fileList);
	}
}
