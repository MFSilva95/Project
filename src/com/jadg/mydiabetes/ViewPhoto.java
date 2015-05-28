package com.jadg.mydiabetes;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.jadg.mydiabetes.database.DB_Write;





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
			Log.d("DIRPATH", path);
			ImageView img = (ImageView)findViewById(R.id.viewphoto_img);
			Bitmap b = decodeFile(path);
			//b = adjustImageOrientation(b,path);
			img.setImageBitmap(b);
			
		}
	}
	
	
	
	
	public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            Display display = getWindowManager().getDefaultDisplay();
    		Point size = new Point();
    		display.getSize(size);
    		int newWidth = size.x;
    		int newHeight = size.y;
    		
    		final int REQUIRED_SIZE = newWidth;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

//	private Bitmap adjustImageOrientation(Bitmap image, String picturePath ) {
//        ExifInterface exif;
//        try {
//            exif = new ExifInterface(picturePath);
//            int exifOrientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL);
//
//            int rotate = 0;
//            switch (exifOrientation) {
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                rotate = 90;
//                break;
//
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                rotate = 180;
//                break;
//
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                rotate = 270;
//                break;
//            }
//
//            if (rotate != 0) {
//                int w = image.getWidth();
//                int h = image.getHeight();
//
//                // Setting pre rotate
//                Matrix mtx = new Matrix();
//                mtx.preRotate(rotate);
//
//                // Rotating Bitmap & convert to ARGB_8888, required by tess
//                image = Bitmap.createBitmap(image, 0, 0, w, h, mtx, false);
//
//            }
//        } catch (IOException e) {
//                 return null;
//        }
//        return image.copy(Bitmap.Config.ARGB_8888, true);
//    }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_photo, menu);
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
		case R.id.menuItem_ViewPhoto_delete:
			DeletePhoto();
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}

	
	public void DeletePhoto(){
		final Context c = this;
		//Toast.makeText(this, "xfgdsf", Toast.LENGTH_LONG).show();
		final Bundle args = getIntent().getExtras();
		final int id = args.getInt("Id");
		new AlertDialog.Builder(c)
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
	        		 File file = new File(Uri.parse(path).getPath());
	        		 boolean deleted = file.delete();
	        		 if (deleted) {
	        			 Log.d("apagado", file.getAbsolutePath());
	        		 }else {
	        			 Log.d("não apagado", file.getAbsolutePath());
	        		 }
	        		 setResult(RESULT_OK);
	        		 finish();
	        		 
	        	 }catch (Exception e) {
	        		 Toast.makeText(c, "Não pode eliminar esta leitura!", Toast.LENGTH_LONG).show();
	     		 }
	         }
	    })
	    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
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
	             //Falta verificar se não está associada a nenhuma entrada da DB
	        	 DB_Write wdb = new DB_Write(c);
	        	 try {
	        		 wdb.Carbs_Delete(id);
	        		 goUp();
	        	 }catch (Exception e) {
	        		 Toast.makeText(c, "Não pode eliminar esta leitura!", Toast.LENGTH_LONG).show();
	     		 }
	             wdb.close();
	             
	         }
	    })
	    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	                // Do nothing.
	         }
	    }).show();
	}
	
	public void goUp(){
		NavUtils.navigateUpFromSameTask(this);
	}
}
