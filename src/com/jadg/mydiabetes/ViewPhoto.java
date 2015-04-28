package com.jadg.mydiabetes;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.Toast;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import android.view.Menu;
import android.view.MenuItem;
import com.jadg.mydiabetes.database.DB_Write;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")

public class ViewPhoto extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewphoto);
		// Show the Up button in the action bar.
		getActionBar();
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			String path = args.getString("Path");
			
			File dir = new File(Environment.getExternalStorageDirectory() + path);
			ImageView img = (ImageView)findViewById(R.id.viewphoto_img);
			img.setImageURI(Uri.fromFile(dir));
		}
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.view_photo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			//NavUtils.navigateUpFromSameTask(this);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	public void DeletePhoto(View v){
		final Context c = this;
		//Toast.makeText(this, "xfgdsf", Toast.LENGTH_LONG).show();
		final Bundle args = getIntent().getExtras();
		final int id = args.getInt("Id");
		new AlertDialog.Builder(v.getContext())
	    .setTitle("Eliminar foto?")
	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	        	 
	        	 try {
	        		 if(id!=-1){
	        			 DB_Write wdb = new DB_Write(c);
	        			 wdb.Carbs_DeletePhoto(id);
	        			 wdb.close();
	        		 }
	        		 
	        		 String path = args.getString("Path");
	        		 File file = new File(Environment.getExternalStorageDirectory() + path);
	        		 boolean deleted = file.delete();
	        		 if (deleted) {
	        			 Log.d("apagado", file.getAbsolutePath());
	        		 }else {
	        			 Log.d("n�o apagado", file.getAbsolutePath());
	        		 }
	        		 finish();
	        		 
	        	 }catch (Exception e) {
	        		 Toast.makeText(c, "N�o pode eliminar esta leitura!", Toast.LENGTH_LONG).show();
	     		 }
	         }
	    })
	    .setNegativeButton("N�o", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	                // Do nothing.
	         }
	    }).show();
	}
	
	public void DeleteCarbsRead(final int id){
		final Context c = this;
		new AlertDialog.Builder(this)
	    .setTitle("Eliminar leitura?")
	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	             //Falta verificar se n�o est� associada a nenhuma entrada da DB
	        	 DB_Write wdb = new DB_Write(c);
	        	 try {
	        		 wdb.Carbs_Delete(id);
	        		 goUp();
	        	 }catch (Exception e) {
	        		 Toast.makeText(c, "N�o pode eliminar esta leitura!", Toast.LENGTH_LONG).show();
	     		 }
	             wdb.close();
	             
	         }
	    })
	    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	                // Do nothing.
	         }
	    }).show();
	}
	
	public void goUp(){
		NavUtils.navigateUpFromSameTask(this);
	}
}
