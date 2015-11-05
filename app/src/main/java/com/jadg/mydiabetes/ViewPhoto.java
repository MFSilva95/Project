package com.jadg.mydiabetes;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.jadg.mydiabetes.database.DB_Write;





public class ViewPhoto extends Activity {
	ImageView img;

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
			img = (ImageView)findViewById(R.id.viewphoto_img);

			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int height = (int)(displaymetrics.heightPixels * 0.3);
			int width = (int)(displaymetrics.widthPixels * 0.3);
			Log.d("IMAGE WIDTH:", "" + width);
			Log.d("IMAGE HEIGHT:", "" + height);
			
			
			Bitmap b = decodeSampledBitmapFromPath(path, width, height);
	
			img.setImageBitmap(b);
		}
	}
	
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	
	    return inSampleSize;
	}
	
	
	public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    //BitmapFactory.decodeResource(res, resId, options);
	    BitmapFactory.decodeFile(path, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return adjustImageOrientation(BitmapFactory.decodeFile(path, options),path);
	}
	
	
	private static Bitmap adjustImageOrientation(Bitmap image, String picturePath ) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(picturePath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int rotate = 0;
            switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            }

            if (rotate != 0) {
                int w = image.getWidth();
                int h = image.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap & convert to ARGB_8888, required by tess
                image = Bitmap.createBitmap(image, 0, 0, w, h, mtx, false);

            }
        } catch (IOException e) {
                 return null;
        }
        return image.copy(Bitmap.Config.ARGB_8888, true);
    }
	

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
