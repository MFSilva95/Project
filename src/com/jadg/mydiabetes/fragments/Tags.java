package com.jadg.mydiabetes.fragments;



import java.util.ArrayList;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ListView;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.TagDetail;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.TagAdapter;
import com.jadg.mydiabetes.database.TagDataBinding;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link Tags.OnFragmentInteractionListener} interface to handle interaction
 * events. Use the {@link Tags#newInstance} factory method to create an instance
 * of this fragment.
 * 
 */
public class Tags extends Fragment {

	ListView tagList;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        
    }
	
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater tagsmenu = getSupportActivity().getSupportMenuInflater();
        tagsmenu.inflate(R.menu.tags_menu, menu);
    }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_tags, null);
		
		tagList = (ListView)v.findViewById(R.id.tagsFragmentList);
		
		fillListView(tagList);
		
		return v;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuItem_TagsFragment_Add:
				Intent intent = new Intent(this.getActivity(), TagDetail.class);
				startActivity(intent);
				//showTagDialog();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	public void showTagDialog(){
		LayoutInflater inflater = getLayoutInflater();
		final View v = inflater.inflate(R.layout.dialog_new_tag);
		
    	new AlertDialog.Builder(getActivity())
    	    .setView(v)
    	    .setPositiveButton("Gravar", new DialogInterface.OnClickListener() {
    	         public void onClick(DialogInterface dialog, int whichButton) {
    	             // deal with the editable
    	             DB_Write wdb = new DB_Write(getActivity());
    	             EditText tagname = (EditText)v.findViewById(R.id.et_dialog_new_tag_Name);
    	             wdb.Tag_Add(tagname.getText().toString());
    	             wdb.close();
    	             fillListView(tagList);
    	         }
    	    })
    	    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
    	         public void onClick(DialogInterface dialog, int whichButton) {
    	                // Do nothing.
    	         }
    	    }).show();
	}
	
	public void fillListView(ListView lv){
		DB_Read rdb = new DB_Read(getActivity());
		ArrayList<TagDataBinding> allTags = rdb.Tag_GetAll();
		
		rdb.close();
		
		lv.setAdapter(new TagAdapter(allTags, getActivity()));
	}

}
