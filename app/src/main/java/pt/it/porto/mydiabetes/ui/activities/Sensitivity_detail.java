package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.Sensitivity;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


public class Sensitivity_detail extends BaseActivity {

	public static final String BUNDLE_GOAL = "GOAL";
	public static final String BUNDLE_ID = "Id";
	public static final String BUNDLE_DATA = "Data";

	int idTarget = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ratio_detail);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		EditText name = (EditText) findViewById(R.id.et_TargetBG_Nome);
		EditText from = (EditText) findViewById(R.id.et_TargetBG_HourFrom);
		//EditText to = (EditText) findViewById(R.id.et_TargetBG_HourTo);
		EditText value = (EditText) findViewById(R.id.et_TargetBG_Glycemia);
		//to.setEnabled(false);

		Bundle args = getIntent().getExtras();
		if (args != null) {

			Sensitivity toFill = null;
			String lastTime;
			if (args.containsKey(BUNDLE_DATA)) {
				toFill = args.getParcelable(BUNDLE_DATA);
			}

			if (args.containsKey(BUNDLE_ID)) {
				String id = args.getString("Id");
				DB_Read rdb = new DB_Read(this);
				toFill = rdb.Sensitivity_GetByID(""+Integer.parseInt(id));
				if(toFill!=null){
					lastTime = rdb.getNextSensTime(toFill);
					//if(lastTime!=null){to.setText(lastTime);}
				}
//				Log.i("cenas", "onCreate: ID is -> -> "+id);
				rdb.close();
			}

			if (toFill != null) {
				idTarget = toFill.getId();
				name.setText(toFill.getName());
				from.setText(toFill.getStart());
				//to.setText(toFill.getEnd());

				value.setText(String.valueOf((int) toFill.getSensitivity()));
			}

			if (args.containsKey(BUNDLE_GOAL)) {
				float goal = args.getFloat(BUNDLE_GOAL);
				((EditText) findViewById(R.id.et_TargetBG_Glycemia)).setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) goal));
			}

		}else{
			//to.setVisibility(View.INVISIBLE);
		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();

		Bundle args = getIntent().getExtras();
		if (args != null) {
			inflater.inflate(R.menu.target_bg_detail_delete, menu);
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					VerifyTargetBeforeUpdate();
				}
			});
		} else {
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					AddNewTarget();
				}
			});
		}

		//getSupportMenuInflater().inflate(R.menu.tag_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.menuItem_TargetBGDetail_Delete:
				DeleteTarget();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void showTimePickerDialogFrom(View v) {

		String currentTime = "";
		if(((EditText) v).getText().toString().equals("")){
			Calendar registerDate = Calendar.getInstance();
			currentTime = DateUtils.getFormattedTimeSec(registerDate);
		}
		else{
			currentTime = ((EditText) v).getText().toString();
		}
		android.support.v4.app.DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_TargetBG_HourFrom,
				DateUtils.getTimeCalendar(currentTime));
		newFragment.show(getSupportFragmentManager(), "timePicker");

		TextView errorLabel = (TextView) findViewById(R.id.ratioError);
		errorLabel.setText("");
		errorLabel.setVisibility(View.GONE);
	}

//	public void showTimePickerDialogTo(View v) {
//		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_TargetBG_HourTo, DateUtils.getTimeCalendar(((TextView) v).getText().toString()));
//		newFragment.show(getFragmentManager(), "timePicker");
//	}

	public void AddNewTarget() {

		EditText name = (EditText) findViewById(R.id.et_TargetBG_Nome);
		EditText hourFrom = (EditText) findViewById(R.id.et_TargetBG_HourFrom);
		//EditText hourTo = (EditText) findViewById(R.id.et_TargetBG_HourTo);
		EditText value = (EditText) findViewById(R.id.et_TargetBG_Glycemia);

		if (name.getText().toString().equals("")) {
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (hourFrom.getText().toString().equals("")) {
			hourFrom.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(hourFrom, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
//		if (hourTo.getText().toString().equals("")) {
//			hourTo.requestFocus();
//			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.showSoftInput(hourTo, InputMethodManager.SHOW_IMPLICIT);
//			return;
//		}
		if (value.getText().toString().equals("")) {
			value.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(value, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		Sensitivity target = new Sensitivity();
		target.setName(name.getText().toString());
		target.setStart(hourFrom.getText().toString());
		target.setEnd(target.getStart(), 30);
		//target.setEnd(hourTo.getText().toString());
		DB_Read read = new DB_Read(this);
		target.setUser_id(read.getUserId());
		//ArrayList<Sensitivity> all_sensitivities = read.Sensitivity_GetAll();
//		boolean sensExists = read.Sensitivity_exists(idTarget);
		boolean sensExists = read.sensitivityTimeStartExists(target.getStart(),idTarget+"");
		read.close();
		if(sensExists){
			TextView errorLabel = ((TextView) findViewById(R.id.ratioError));
			errorLabel.setText(R.string.error_time_overlaps);
			errorLabel.setVisibility(View.VISIBLE);
			return;
		}

//		Sensitivity sens = null;
//		Sensitivity next = null;
//		for(int index=0;index<all_sensitivities.size();index++){
//			sens = all_sensitivities.get(index);
//			if(sens.getStartInMinutes() < target.getStartInMinutes()){
//				sens.setEnd(target.getStart());
//				if(index+1 < all_sensitivities.size()){
//					next = all_sensitivities.get(index+1);
//					next.setStart(target.getEnd());
//				}
//
//				break;
//			}
//		}
//		if(sens !=null){
//			DB_Write write = new DB_Write(this);
//			write.Sensitivity_Reg_Update(sens);
//			if(next!=null){write.Sensitivity_Reg_Update(next);}
//			write.close();
//		}

		target.setSensitivity(Double.valueOf(value.getText().toString()));
		DB_Write wdb = new DB_Write(this);
		wdb.Sensitivity_Reg_Add(target);
		wdb.close();
		finish();
	}

	public int getDuration(int start, int end){
		if (start>end){return start-end;}
		return end-start;
	}



    Sensitivity target = new Sensitivity();

	public void VerifyTargetBeforeUpdate() {

        String TAG = "cenas";

		EditText name = (EditText) findViewById(R.id.et_TargetBG_Nome);
		EditText hourFrom = (EditText) findViewById(R.id.et_TargetBG_HourFrom);
		//EditText hourTo = (EditText) findViewById(R.id.et_TargetBG_HourTo);
		EditText value = (EditText) findViewById(R.id.et_TargetBG_Glycemia);

		if (name.getText().toString().equals("")) {
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (hourFrom.getText().toString().equals("")) {
			hourFrom.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(hourFrom, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
//		if (hourTo.getText().toString().equals("")) {
//			hourTo.requestFocus();
//			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.showSoftInput(hourTo, InputMethodManager.SHOW_IMPLICIT);
//			return;
//		}
		if (value.getText().toString().equals("")) {
			value.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(value, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		//^^^^ verification that every field is filled

		target.setId(idTarget);

        final DB_Read read = new DB_Read(this);
		target = read.Sensitivity_GetByID(target.getId()+"");//get Base target
        target.setName(name.getText().toString());//update name
        target.setSensitivity(Double.valueOf(value.getText().toString()));//update value

//		String[] time = hourFrom.getText().toString().split(":");
//		int hour = Integer.parseInt(time[0]);
//		int minute = Integer.parseInt(time[1]);
//		int thisStart = hour*60+minute;
//		if(thisStart > target.getEndInMinutes()){
//
//			//goin for the past...
//			hourFrom.requestFocus();
//			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.showSoftInput(hourFrom, InputMethodManager.SHOW_IMPLICIT);
//
//			TextView errorLabel = ((TextView) findViewById(R.id.ratioError));
//
//			errorLabel.setVisibility(View.VISIBLE);
//
//			return;
//		}
		target.setStart(hourFrom.getText().toString());//update start time
//        final LinkedList<Integer> overlapCount = read.countSensOverlap(target.getStart(),target.getEnd(),idTarget);
//
//		if(overlapCount!=null){
//            Log.i(TAG, "VerifyTargetBeforeUpdate: Count = "+overlapCount.size() );
//            final Context c = this;
//			new AlertDialog.Builder(this)
//					.setTitle(getString(R.string.targetbg_info))
//					.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int whichButton) {
//							DB_Write wdb = new DB_Write(c);
//							try {
//                                for(int id:overlapCount){
//                                    wdb.Sensitivity_Reg_Remove(id);
//                                }
//
//                                Sensitivity previous = read.getPreviousRatio(target);
//                                Sensitivity next = read.getNextRatio(target);
//                                read.close();
//                                updateTarget(previous, next, wdb);
//
//								goUp();
//							} catch (Exception e) {
//								Toast.makeText(c, getString(R.string.targetbg_delete_exception), Toast.LENGTH_LONG).show();
//							}
//                            wdb.close();
//						}
//					})
//					.setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int whichButton) {
//                            read.close();
//							// Do nothing.
//						}
//					}).show();
//		}else{
//            Sensitivity previous = read.getPreviousRatio(target);
//            Sensitivity next = read.getNextRatio(target);
		boolean sensExists = read.sensitivityTimeStartExists(target.getStart(),idTarget+"");//Sensitivity_exists(idTarget);
		//LinkedList overlapList = read.countSensOverlap(target.getStart(),target.getEnd(),target.getUserId());
		read.close();

		if(sensExists){
			TextView errorLabel = ((TextView) findViewById(R.id.ratioError));
			errorLabel.setText(R.string.error_time_overlaps);
			errorLabel.setVisibility(View.VISIBLE);
			return;
		}
//		if(overlapList!=null){
//			final Context c = this;
//			new AlertDialog.Builder(this)
//					.setMessage(getString(R.string.sensitivity_overlap, overlapList))
//					.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int whichButton) {
//							//Falta verificar se não está associada a nenhuma entrada da DB
//							//Rever porque não elimina o registo de glicemia
//							DB_Write wdb = new DB_Write(c);
//							try {
//								DB_Read read = new DB_Read(c);
//								target.setId(idTarget);
//								target = read.Sensitivity_GetByID(target.getUserId()+"");//get Base target
////							Sensitivity previous = read.getPreviousRatio(target);
////							Sensitivity next = read.getNextRatio(target);
//								read.close();
//								wdb.Sensitivity_Reg_Remove(idTarget);
////							updateTarget(previous, next, wdb);
//								wdb.close();
//								goUp();
//							} catch (Exception e) {
//								Toast.makeText(c, getString(R.string.targetbg_delete_exception), Toast.LENGTH_LONG).show();
//							}
//							wdb.close();
//						}
//					})
//					.setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int whichButton) {
//							// Do nothing.
//						}
//					}).show();
		//}
		else{

			DB_Write wdb = new DB_Write(this);
			wdb.Sensitivity_Reg_Update(target);
			wdb.close();

//            updateTarget(previous, target,  next);
			finish();
		}


//        }
	}

    private void updateTarget(Sensitivity previous, Sensitivity current, Sensitivity next){

        previous.setEnd(current.getStart());
        next.setStart(current.getEnd());

        DB_Write wdb = new DB_Write(this);
        wdb.Sensitivity_Reg_Update(previous);
        wdb.Sensitivity_Reg_Update(current);
        wdb.Sensitivity_Reg_Update(next);
        wdb.close();
        DB_Read read = new DB_Read(this);
        ArrayList<Sensitivity> sensitivities = read.Sensitivity_GetAll();
        Log.i("cenas", "updateTarget: -------------------------------------------");
        for(Sensitivity s:sensitivities){
            Log.i("cenas", s.toString());
        }
    }

	private void updateTarget(Sensitivity previous, Sensitivity next, DB_Write wdb){

		previous.setEnd(next.getStart());
		next.setStart(previous.getEnd());

		wdb.Sensitivity_Reg_Update(previous);
		wdb.Sensitivity_Reg_Update(next);
	}

    private void updateTarget(Sensitivity previous, Sensitivity current, Sensitivity next, DB_Write wdb){

        previous.setEnd(current.getStart());
        next.setStart(current.getEnd());

        wdb.Sensitivity_Reg_Update(previous);
        wdb.Sensitivity_Reg_Update(current);
        wdb.Sensitivity_Reg_Update(next);
    }



	public void DeleteTarget() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.targetbg_info))
				.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						DB_Write wdb = new DB_Write(c);
						try {
							DB_Read read = new DB_Read(c);
							target.setId(idTarget);
							target = read.Sensitivity_GetByID(target.getId()+"");//get Base target
//							Sensitivity previous = read.getPreviousRatio(target);
//							Sensitivity next = read.getNextRatio(target);
							read.close();
							wdb.Sensitivity_Reg_Remove(idTarget);
//							updateTarget(previous, next, wdb);
							wdb.close();
							goUp();
						} catch (Exception e) {
							Toast.makeText(c, getString(R.string.targetbg_delete_exception), Toast.LENGTH_LONG).show();
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
		//NavUtils.navigateUpFromSameTask(this);
		finish();
	}
}
