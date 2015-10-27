package com.jadg.mydiabetes.ui.fragments;

import java.util.ArrayList;



import android.view.LayoutInflater;
import android.app.Fragment;
import android.widget.ListView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.ui.activities.TargetBG_detail;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.ui.listAdapters.TargetAdapter;
import com.jadg.mydiabetes.ui.listAdapters.TargetDataBinding;


public class TargetBG extends Fragment {


	ListView targetList;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater targetsmenu = getActivity().getMenuInflater();
        targetsmenu.inflate(R.menu.targets_menu, menu);
    }

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuItem_TargetsFragment_Add:
				Intent intent = new Intent(this.getActivity(), TargetBG_detail.class);
				startActivity(intent);
				//showTagDialog();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onResume() {
	    super.onResume();
	    targetList = (ListView)super.getActivity().findViewById(R.id.targetsFragmentList);
	    fillListView(targetList);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_target_bg, null);
		
		targetList = (ListView)v.findViewById(R.id.targetsFragmentList);
		
		fillListView(targetList);
		
		return v;
	}

	
	public void fillListView(ListView lv){
		DB_Read rdb = new DB_Read(getActivity());
		ArrayList<TargetDataBinding> allTags = rdb.Target_GetAll();
		
		rdb.close();
		
		lv.setAdapter(new TargetAdapter(allTags, getActivity()));
	}
}
