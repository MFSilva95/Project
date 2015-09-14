package com.jadg.mydiabetes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.jadg.mydiabetes.database.CarbsDataBinding;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.GlycemiaDataBinding;
import com.jadg.mydiabetes.database.InsulinRegDataBinding;
import com.jadg.mydiabetes.database.NoteDataBinding;
import com.jadg.mydiabetes.database.TagDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;
import com.jadg.mydiabetes.dialogs.TimePickerFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class LogbookDetail extends Activity {

    //photo variables - start
    final private int CAPTURE_IMAGE = 2;
    Uri imgUri;
    Bitmap b;
    //photo variables - end
    ArrayList<String> allInsulins;

    private int id_ch = -1;
    private int id_ins = -1;
    private int id_bg = -1;
    private CarbsDataBinding ch = null;
    private InsulinRegDataBinding ins = null;
    private GlycemiaDataBinding bg = null;
    int tagId = -1;
    int insulinId = -1;
    int noteId = -1;
    int userId;

    EditText data;
    EditText hora;
    Spinner TagSpinner;
    EditText glycemia;
    ImageView img;
    EditText photopath;
    EditText carbs;
    Spinner InsulinSpinner;
    EditText target;
    EditText insulin;
    EditText note;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);
        // Show the Up button in the action bar.
        getActionBar();


        FillDateHour();
        FillTagSpinner();
        SetTagByTime();
        SetTargetByHour();
        FillInsulinSpinner();

        EditText hora = (EditText) findViewById(R.id.et_MealDetail_Hora);
        hora.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SetTagByTime();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        DB_Read rdb = new DB_Read(this);


        Bundle args = getIntent().getExtras();
        if (args != null) {
            String arg_ch = args.getString("ch");
            String arg_ins = args.getString("ins");
            String arg_bg = args.getString("bg");

            rdb = new DB_Read(this);

            if (!arg_ch.isEmpty()) {
                id_ch = Integer.parseInt(arg_ch);
                ch = rdb.CarboHydrate_GetById(id_ch);
            }
            if (!arg_ins.isEmpty()) {
                id_ins = Integer.parseInt(arg_ins);
                ins = rdb.InsulinReg_GetById(id_ins);
            }
            if (!arg_bg.isEmpty()) {
                id_bg = Integer.parseInt(arg_bg);
                bg = rdb.Glycemia_GetById(id_bg);
            }
            rdb.close();
            fillAllValues();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.logbook_detail_edit, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menuItem_LogbookDetail_EditSave:
                SaveRead();
                return true;
            case R.id.menuItem_LogbookDetail_Delete:
                DeleteRead();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //created by zeornelas
    public void fillAllValues() {
        DB_Read rdb = new DB_Read(this);
        //File file;

        data = (EditText) findViewById(R.id.et_MealDetail_Data);
        hora = (EditText) findViewById(R.id.et_MealDetail_Hora);

        TagSpinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
        glycemia = (EditText) findViewById(R.id.et_MealDetail_Glycemia);
        img = (ImageView) findViewById(R.id.iv_MealDetail_Photo);
        carbs = (EditText) findViewById(R.id.et_MealDetail_Carbs);
        InsulinSpinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
        target = (EditText) findViewById(R.id.et_MealDetail_TargetGlycemia);
        insulin = (EditText) findViewById(R.id.et_MealDetail_InsulinUnits);
        note = (EditText) findViewById(R.id.et_MealDetail_Notes);

        //coloca a photo
        if (ch != null) {
            EditText photopath = (EditText) findViewById(R.id.et_MealDetail_Photo);
            if (!ch.getPhotoPath().equals("")) {
                photopath.setText(ch.getPhotoPath());
                Log.d("foto path", "foto: " + ch.getPhotoPath());
                ImageView img = (ImageView) findViewById(R.id.iv_MealDetail_Photo);

                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int height = (int) (displaymetrics.heightPixels * 0.1);
                int width = (int) (displaymetrics.widthPixels * 0.1);
                b = decodeSampledBitmapFromPath(ch.getPhotoPath(), width, height);
                img.setImageBitmap(b);
            }
        }


        if (ch != null && ins != null && bg != null) {
            userId = ins.getIdUser();
            data.setText(ins.getDate());
            hora.setText(ins.getTime());
            tagId = ins.getIdTag();
            String aux = rdb.Tag_GetById(tagId).getName();
            SelectSpinnerItemByValue(TagSpinner, aux);
            glycemia.setText(bg.getValue().toString());
            carbs.setText(ch.getCarbsValue().toString());
            insulinId = ins.getIdInsulin();
            String aux1 = rdb.Insulin_GetById(insulinId).getName();
            SelectSpinnerItemByValue(InsulinSpinner, aux1);
            target.setText(ins.getTargetGlycemia().toString());
            insulin.setText(ins.getInsulinUnits().toString());
            noteId = ins.getIdNote();
            if (noteId != -1) {
                note.setText(rdb.Note_GetById(noteId).getNote());
            }
        } else if (ch != null && ins == null && bg == null) {//so hidratos carbono
            userId = ch.getId_User();
            data.setText(ch.getDate());
            hora.setText(ch.getTime());
            tagId = ch.getId_Tag();
            String aux = rdb.Tag_GetById(tagId).getName();
            SelectSpinnerItemByValue(TagSpinner, aux);
            glycemia.setText("");
            carbs.setText(ch.getCarbsValue().toString());
            target.setText("");
            insulin.setText("");
            noteId = ch.getId_Note();
            if (noteId != -1) {
                note.setText(rdb.Note_GetById(noteId).getNote());
            }

        } else if (ch == null && ins != null && bg != null) {//insulina com parametro da glicemia
            userId = ins.getIdUser();
            data.setText(ins.getDate());
            hora.setText(ins.getTime());
            tagId = ins.getIdTag();
            String aux = rdb.Tag_GetById(tagId).getName();
            SelectSpinnerItemByValue(TagSpinner, aux);
            glycemia.setText(bg.getValue().toString());
            carbs.setText("");
            insulinId = ins.getIdInsulin();
            String aux1 = rdb.Insulin_GetById(insulinId).getName();
            SelectSpinnerItemByValue(InsulinSpinner, aux1);
            target.setText(ins.getTargetGlycemia().toString());
            insulin.setText(ins.getInsulinUnits().toString());
            noteId = ins.getIdNote();
            if (noteId != -1) {
                note.setText(rdb.Note_GetById(noteId).getNote());
            }
        } else if (ch == null && ins == null && bg != null) {//so glicemia
            userId = bg.getIdUser();
            data.setText(bg.getDate());
            hora.setText(bg.getTime());
            tagId = bg.getIdTag();
            String aux = rdb.Tag_GetById(tagId).getName();
            SelectSpinnerItemByValue(TagSpinner, aux);
            glycemia.setText(bg.getValue().toString());
            carbs.setText("");
            target.setText("");
            insulin.setText("");
            noteId = bg.getIdNote();
            if (noteId != -1) {
                note.setText(rdb.Note_GetById(noteId).getNote());
            }

        } else if (ch == null && ins != null && bg == null) {//so insulina
            userId = ins.getIdUser();
            data.setText(ins.getDate());
            hora.setText(ins.getTime());
            tagId = ins.getIdTag();
            String aux = rdb.Tag_GetById(tagId).getName();
            SelectSpinnerItemByValue(TagSpinner, aux);
            glycemia.setText("");
            carbs.setText("");
            insulinId = ins.getIdInsulin();
            String aux1 = rdb.Insulin_GetById(insulinId).getName();
            SelectSpinnerItemByValue(InsulinSpinner, aux1);
            target.setText(ins.getTargetGlycemia().toString());
            insulin.setText(ins.getInsulinUnits().toString());
            noteId = ins.getIdNote();
            if (noteId != -1) {
                note.setText(rdb.Note_GetById(noteId).getNote());
            }
        } else if (ch != null && ins != null && bg == null) {
            userId = ins.getIdUser();
            data.setText(ins.getDate());
            hora.setText(ins.getTime());
            tagId = ins.getIdTag();
            String aux = rdb.Tag_GetById(tagId).getName();
            SelectSpinnerItemByValue(TagSpinner, aux);
            glycemia.setText("");
            carbs.setText(ch.getCarbsValue().toString());
            insulinId = ins.getIdInsulin();
            String aux1 = rdb.Insulin_GetById(insulinId).getName();
            SelectSpinnerItemByValue(InsulinSpinner, aux1);
            target.setText(ins.getTargetGlycemia().toString());
            insulin.setText(ins.getInsulinUnits().toString());
            noteId = ins.getIdNote();
            if (noteId != -1) {
                note.setText(rdb.Note_GetById(noteId).getNote());
            }
        } else if (ch != null && ins == null && bg != null) {
            userId = ch.getId_User();
            data.setText(ch.getDate());
            hora.setText(ch.getTime());
            tagId = ch.getId_Tag();
            String aux = rdb.Tag_GetById(tagId).getName();
            SelectSpinnerItemByValue(TagSpinner, aux);
            glycemia.setText(bg.getValue().toString());
            carbs.setText(ch.getCarbsValue().toString());
            target.setText("");
            insulin.setText("");
            noteId = ch.getId_Note();
            if (noteId != -1) {
                note.setText(rdb.Note_GetById(noteId).getNote());
            }
        }


        rdb.close();
    }


    @SuppressLint("SimpleDateFormat")
    public void FillDateHour() {
        EditText date = (EditText) findViewById(R.id.et_MealDetail_Data);
        final Calendar c = Calendar.getInstance();
        Date newDate = c.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(newDate);
        date.setText(dateString);

        EditText hour = (EditText) findViewById(R.id.et_MealDetail_Hora);
        formatter = new SimpleDateFormat("HH:mm:ss");
        String timeString = formatter.format(newDate);
        hour.setText(timeString);
    }

    public void FillTagSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
        ArrayList<String> allTags = new ArrayList<String>();
        DB_Read rdb = new DB_Read(this);
        ArrayList<TagDataBinding> t = rdb.Tag_GetAll();
        rdb.close();


        if (t != null) {
            for (TagDataBinding i : t) {
                allTags.add(i.getName());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allTags);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("textbox", R.id.et_MealDetail_Data);
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "DatePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putInt("textbox", R.id.et_MealDetail_Hora);
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "timePicker");

    }

    public void SetTagByTime() {
        Spinner tagSpinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
        EditText hora = (EditText) findViewById(R.id.et_MealDetail_Hora);
        DB_Read rdb = new DB_Read(this);
        String name = rdb.Tag_GetByTime(hora.getText().toString()).getName();
        rdb.close();
        SelectSpinnerItemByValue(tagSpinner, name);
    }

    public static void SelectSpinnerItemByValue(Spinner spnr, String value) {
        SpinnerAdapter adapter = spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).equals(value)) {
                spnr.setSelection(position);
                return;
            }
        }
    }


    public void ShowDialogAddInsulin() {
        final Context c = this;
        new AlertDialog.Builder(this)
                .setTitle("Informação")
                .setMessage("Antes de gravar deve adicionar a insulina a administrar!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Falta verificar se não está associada a nenhuma entrada da DB
                        //Rever porque não elimina o registo de glicemia
                        Intent intent = new Intent(c, Preferences.class);
                        intent.putExtra("tabPosition", 2);
                        startActivity(intent);
                        end();
                    }
                }).show();
    }

    public void end() {
        finish();
    }

    public void SetTargetByHour() {
        EditText target = (EditText) findViewById(R.id.et_MealDetail_TargetGlycemia);
        EditText hora = (EditText) findViewById(R.id.et_MealDetail_Hora);
        DB_Read rdb = new DB_Read(this);
        double d = rdb.Target_GetTargetByTime(hora.getText().toString());
        if (d != 0) {
            target.setText(String.valueOf(d));
        }
        rdb.close();
    }

    public void FillInsulinSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
        allInsulins = new ArrayList<String>();
        DB_Read rdb = new DB_Read(this);
        HashMap<Integer, String> val = rdb.Insulin_GetAllNames();
        rdb.close();

        if (val != null) {
            for (int i : val.keySet()) {
                allInsulins.add(val.get(i));
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allInsulins);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    //PHOTO - START

    public Uri setImageUri() {
        // Store image in /MyDiabetes
        File file = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes", new Date().getTime() + ".jpg");
        File dir = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes");
        if (!dir.exists()) {
            dir.mkdir();
        }
        imgUri = Uri.fromFile(file);
        return imgUri;
    }


    public void TakePhoto(View v) {
        photopath = (EditText) findViewById(R.id.et_MealDetail_Photo);
        if (!photopath.getText().toString().equals("")) {
            final Intent intent = new Intent(this, ViewPhoto.class);
            Bundle argsToPhoto = new Bundle();
            argsToPhoto.putString("Path", photopath.getText().toString());
            argsToPhoto.putInt("Id", id_ch);
            intent.putExtras(argsToPhoto);
            startActivityForResult(intent, 101010);
        } else {
            final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
            startActivityForResult(intent, CAPTURE_IMAGE);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        photopath = (EditText) findViewById(R.id.et_MealDetail_Photo);
        img = (ImageView) findViewById(R.id.iv_MealDetail_Photo);
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == CAPTURE_IMAGE) {
                Toast.makeText(getApplicationContext(), getString(R.string.photoSaved) + " " + imgUri.getPath(), Toast.LENGTH_LONG).show();
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int height = (int) (displaymetrics.heightPixels * 0.1);
                int width = (int) (displaymetrics.widthPixels * 0.1);
                b = decodeSampledBitmapFromPath(imgUri.getPath(), width, height);
                img.setImageBitmap(b);
                photopath.setText(imgUri.getPath());
                b = null;

            } else if (requestCode == 101010) {
                Log.d("Result:", resultCode + "");
                //se tivermos apagado a foto dá result code -1
                //se voltarmos por um return por exemplo o resultcode é 0
                if (resultCode == -1) {
                    photopath.setText("");
                    img.setImageDrawable(getResources().getDrawable(R.drawable.newphoto));
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imgUri != null) {
            outState.putString("cameraImageUri", imgUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("cameraImageUri")) {
            imgUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));
            EditText photopath = (EditText) findViewById(R.id.et_MealDetail_Photo);
            ImageView img = (ImageView) findViewById(R.id.iv_MealDetail_Photo);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = (int) (displaymetrics.heightPixels * 0.1);
            int width = (int) (displaymetrics.widthPixels * 0.1);
            b = decodeSampledBitmapFromPath(imgUri.getPath(), width, height);
            img.setImageBitmap(b);
            photopath.setText(imgUri.getPath());
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
        return adjustImageOrientation(BitmapFactory.decodeFile(path, options), path);
    }


    private static Bitmap adjustImageOrientation(Bitmap image, String picturePath) {
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


    //PHOTO - END


    //created by zeornelas
    public void DeleteRead() {
        final Context c = this;
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.deleteReading))
                .setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DB_Write wdb = new DB_Write(c);
                        try {
                            wdb.Logbook_Delete(id_ch, id_ins, id_bg, noteId);
                            goUp();
                        } catch (Exception e) {
                            Toast.makeText(c, getString(R.string.deleteException), Toast.LENGTH_LONG).show();
                        }
                        wdb.close();

                    }
                })
                .setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
    }

    public void goUp() {
        NavUtils.navigateUpFromSameTask(this);
    }


    public void SaveRead() {
        DB_Write reg = new DB_Write(this);
        boolean deleteCh = false;
        boolean deleteBg = false;
        boolean deleteIns = false;
        int itemsToDelete = 0;

        String d = data.getText().toString();
        String h = hora.getText().toString();

        //nota id nao existe e tem de ser criado
        if (noteId == -1 && !note.getText().toString().equals("")) {
            NoteDataBinding n = new NoteDataBinding();
            n.setNote(note.getText().toString());
            noteId = reg.Note_Add(n);
        }
        //nota id existe e o texto foi apagado/alterado
        else if (noteId != -1) {
            NoteDataBinding n = new NoteDataBinding();
            n.setNote(note.getText().toString());
            n.setId(noteId);
            reg.Note_Update(n);
        }


        //carbs
        //carbs id existe e foi apagado
        if (ch != null && carbs.getText().toString().equals("")) {
            deleteCh = true;
            itemsToDelete++;

        }
        //carbs id nao existe e tem de ser criado
        else if (ch == null && !carbs.getText().toString().equals("") && !deleteCh) {
            photopath = (EditText) findViewById(R.id.et_MealDetail_Photo);
            ch = new CarbsDataBinding();
            if (noteId != -1) {
                ch.setId_Note(noteId);
            }


            ch.setId_User(userId);
            ch.setCarbsValue(Double.parseDouble(carbs.getText().toString()));
            DB_Read rdb = new DB_Read(this);
            String tagSelected = TagSpinner.getSelectedItem().toString();
            Log.d("selected Spinner", tagSelected);
            tagId = rdb.Tag_GetIdByName(tagSelected);
            ch.setId_Tag(tagId);
            ch.setPhotoPath(photopath.getText().toString()); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
            ch.setDate(d);
            ch.setTime(h);

            rdb.close();
            reg.Carbs_Save(ch);
        }
        //carbs id existe e valor está igual ou foi alterado
        else if (ch != null && !carbs.getText().toString().equals("") && !deleteCh) {
            photopath = (EditText) findViewById(R.id.et_MealDetail_Photo);
            if (noteId != -1) {
                ch.setId_Note(noteId);
            }

            ch.setId(id_ch);
            ch.setId_User(userId);
            ch.setCarbsValue(Double.parseDouble(carbs.getText().toString()));
            DB_Read rdb = new DB_Read(this);
            String tagSelected = TagSpinner.getSelectedItem().toString();
            Log.d("selected Spinner", tagSelected);
            tagId = rdb.Tag_GetIdByName(tagSelected);
            ch.setId_Tag(tagId);
            ch.setPhotoPath(photopath.getText().toString()); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
            ch.setDate(d);
            ch.setTime(h);

            rdb.close();
            reg.Carbs_Update(ch);
        }

        //bg
        //bg id existe e foi apagado
        if (bg != null && glycemia.getText().toString().equals("")) {
            deleteBg = true;
            itemsToDelete++;

        }
        //bg id nao existe e tem de ser criado
        else if (bg == null && !glycemia.getText().toString().equals("") && !deleteBg) {
            bg = new GlycemiaDataBinding();
            if (noteId != -1) {
                bg.setIdNote(noteId);
            }
            bg.setIdUser(userId);
            bg.setValue(Double.parseDouble(glycemia.getText().toString()));
            bg.setDate(d);
            bg.setTime(h);

            DB_Read rdb = new DB_Read(this);
            String tagSelected = TagSpinner.getSelectedItem().toString();
            Log.d("selected Spinner", tagSelected);
            tagId = rdb.Tag_GetIdByName(tagSelected);
            bg.setIdTag(tagId);

            rdb.close();
            id_bg = reg.Glycemia_Save(bg);
        }
        //bg id existe e valor está igual ou foi alterado
        else if (bg != null && !glycemia.getText().toString().equals("") && !deleteBg) {
            if (noteId != -1) {
                bg.setIdNote(noteId);
            }
            bg.setIdUser(userId);
            bg.setValue(Double.parseDouble(glycemia.getText().toString()));
            bg.setDate(d);
            bg.setTime(h);

            DB_Read rdb = new DB_Read(this);
            String tagSelected = TagSpinner.getSelectedItem().toString();
            Log.d("selected Spinner", tagSelected);
            tagId = rdb.Tag_GetIdByName(tagSelected);
            bg.setIdTag(tagId);

            rdb.close();
            reg.Glycemia_Update(bg);
        }


        //ins
        //ins id existe e foi apagado
        if (ins != null && insulin.getText().toString().equals("")) {
            deleteIns = true;
            itemsToDelete++;

        }
        //ins id nao existe e tem de ser criado
        else if (ins == null && !insulin.getText().toString().equals("") && !deleteIns) {
            if (target.getText().toString().equals("")) {
                target.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT);
                return;
            }
            //spinner das insulinas tem de ter valores
            if (allInsulins.isEmpty()) {
                ShowDialogAddInsulin();
                return;
            }


            ins = new InsulinRegDataBinding();
            if (noteId != -1) {
                ins.setIdNote(noteId);
            }

            String insulinSelected = InsulinSpinner.getSelectedItem().toString();
            Log.d("selected Spinner", insulinSelected);
            DB_Read rdb = new DB_Read(this);
            insulinId = rdb.Insulin_GetByName(insulinSelected).getId();


            ins.setIdUser(userId);
            ins.setIdInsulin(insulinId);
            ins.setIdBloodGlucose(id_bg != -1 ? id_bg : -1);
            ins.setDate(d);
            ins.setTime(h);
            ins.setTargetGlycemia(Double.parseDouble(target.getText().toString()));
            ins.setInsulinUnits(Double.parseDouble(insulin.getText().toString()));

            String tagSelected = TagSpinner.getSelectedItem().toString();
            Log.d("selected Spinner", tagSelected);
            tagId = rdb.Tag_GetIdByName(tagSelected);
            ins.setIdTag(tagId);

            rdb.close();
            reg.Insulin_Save(ins);
        }
        //ins id existe e valor está igual ou foi alterado
        else if (ins != null && !insulin.getText().toString().equals("") && !deleteIns) {
            if (target.getText().toString().equals("")) {
                target.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT);
                return;
            }
            //spinner das insulinas tem de ter valores
            if (allInsulins.isEmpty()) {
                ShowDialogAddInsulin();
                return;
            }

            if (noteId != -1) {
                ins.setIdNote(noteId);
            }

            String insulinSelected = InsulinSpinner.getSelectedItem().toString();
            Log.d("selected Spinner", insulinSelected);
            DB_Read rdb = new DB_Read(this);
            insulinId = rdb.Insulin_GetByName(insulinSelected).getId();


            ins.setId(id_ins);
            ins.setIdUser(userId);
            ins.setIdInsulin(insulinId);
            ins.setIdBloodGlucose(id_bg != -1 ? id_bg : -1);
            ins.setDate(d);
            ins.setTime(h);
            ins.setTargetGlycemia(Double.parseDouble(target.getText().toString()));
            ins.setInsulinUnits(Double.parseDouble(insulin.getText().toString()));

            String tagSelected = TagSpinner.getSelectedItem().toString();
            Log.d("selected Spinner", tagSelected);
            tagId = rdb.Tag_GetIdByName(tagSelected);
            ins.setIdTag(tagId);

            rdb.close();
            reg.Insulin_Update(ins);
            Log.d("AQUI", "AQUIIII");
        }
        reg.close();

        if (deleteCh || deleteBg || deleteIns) {
            final Context c = this;
            String message;
            if (itemsToDelete == 1) {
                message = getString(R.string.deleteConfirmationSingular);
            } else {
                message = getString(R.string.deleteConfirmationPlural);
            }

            if (deleteBg) {
                message += "\n" + "- " + getString(R.string.deleteGlycemiaReg);
            }
            if (deleteCh) {
                message += "\n" + "- " + getString(R.string.deleteCarbsReg);
            }
            if (deleteIns) {
                message += "\n" + "- " + getString(R.string.deleteInsulinReg);
            }

            final boolean delCh = deleteCh;
            final boolean delBg = deleteBg;
            final boolean delIns = deleteIns;

            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.deleteAlert))
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            try {
                                DB_Write wdb = new DB_Write(c);
                                if (delCh && delBg && delIns) {
                                    wdb.Logbook_Delete(id_ch, id_ins, id_bg, noteId);
                                } else {
                                    if (delCh) {
                                        wdb.Logbook_DeleteOnSave(id_ch, -1, -1, -1);
                                    }
                                    if (delBg) {
                                        if (ins != null) {
                                            wdb.Logbook_DeleteOnSave(-1, -1, id_bg, id_ins);

                                        } else {
                                            wdb.Logbook_DeleteOnSave(-1, -1, id_bg, -1);

                                        }


                                    }
                                    if (delIns) {
                                        wdb.Logbook_DeleteOnSave(-1, id_ins, -1, -1);
                                    }
                                }
                                wdb.close();
                                goUp();
                            } catch (Exception e) {

                                Toast.makeText(c, getString(R.string.deleteException), Toast.LENGTH_LONG).show();
                                Log.d("Excepção", e.getMessage());
                                //Log.d("LocalizedMessage",e.getLocalizedMessage());
                                //Log.d("trace",e.getStackTrace().toString());
                                //e.printStackTrace();

                            }


                        }
                    })
                    .setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Do nothing.
                        }
                    }).show();
        } else {
            goUp();
        }
    }


}
